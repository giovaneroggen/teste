package com.roggen.voting.contract.v1.facade;


import com.roggen.voting.config.exception.GenericException;
import com.roggen.voting.contract.v1.mapper.DiscussionMapper;
import com.roggen.voting.contract.v1.request.DiscussionRequest;
import com.roggen.voting.contract.v1.request.DiscussionSessionRequest;
import com.roggen.voting.contract.v1.request.VoteRequest;
import com.roggen.voting.contract.v1.response.DiscussionResponse;
import com.roggen.voting.contract.v1.response.DiscussionResultResponse;
import com.roggen.voting.data.Discussion;
import com.roggen.voting.messaging.queue.DiscussionResultQueue;
import com.roggen.voting.service.DiscussionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class DiscussionFacade {

    @Autowired
    private DiscussionService service;

    @Autowired
    private AssociateFacade associateFacade;

    @Autowired
    private DiscussionResultQueue queue;

    public Mono<DiscussionResponse> save(DiscussionRequest request) {
        return this.service
                   .save(DiscussionMapper.map(request))
                   .map(DiscussionMapper::map);
    }

    public Mono<Void> start(String id, DiscussionSessionRequest request) {
        return this.findAvailableToStart(id)
                   .map(d -> DiscussionMapper.map(d, request))
                   .flatMap(this.service::save)
                   .then();
    }

    public Flux<DiscussionResponse> findAll() {
        return this.service
                   .findAll()
                   .map(DiscussionMapper::map);
    }

    public Mono<Void> verifyExpiration() {
        return this.service
                   .findAllExpiredAndCloseSession()
                   .map(DiscussionMapper::mapResult)
                   .map(it -> MessageBuilder.withPayload(it).build())
                   .doOnNext(this.queue.output()::send)
                   .then();
    }

    public Mono<Void> vote(String id, VoteRequest request) {
        return this.findAvailableToVote(id)
                   .flatMap(it ->
                       this.associateFacade
                           .findAvailableAssociateId(request.getAssociateId())
                           .filter(associateId -> !it.getAssociatedList().contains(associateId))
                           .flatMap(associateId -> this.service.save(DiscussionMapper.map(it, request)))
                           .switchIfEmpty(Mono.defer(() -> Mono.error(new GenericException("repeated.vote.not.allowed", HttpStatus.BAD_REQUEST))))
                   )
                   .then();
    }

    public Mono<DiscussionResultResponse> result(String id) {
        return this.service
                   .findClosedSession(id)
                   .map(DiscussionMapper::mapResult)
                   .switchIfEmpty(Mono.defer(() -> Mono.error(new GenericException("discussion.not.available", HttpStatus.BAD_REQUEST))));
    }

    private Mono<Discussion> findAvailableToVote(String id) {
        return this.service
                .findAvailableToVote(id)
                    .switchIfEmpty(Mono.defer(() -> Mono.error(new GenericException("discussion.not.available", HttpStatus.BAD_REQUEST))));
    }

    private Mono<Discussion> findAvailableToStart(String id) {
        return this.service
                .findAvailableToStart(id)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new GenericException("discussion.not.available", HttpStatus.BAD_REQUEST))));
    }
}