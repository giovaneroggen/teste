package com.roggen.voting.service;

import com.roggen.voting.data.Discussion;
import com.roggen.voting.data.repository.DiscussionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class DiscussionService {

    @Autowired
    private DiscussionRepository repository;

    public Mono<Discussion> save(Discussion discussion) {
        return repository.save(discussion);
    }

    public Flux<Discussion> findAll() {
        return this.repository.findAll();
    }

    public Mono<Discussion> findAvailableToStart(String id) {
        return this.repository.findByIdAndActiveFalseAndClosedFalse(id);
    }

    public Mono<Discussion> findAvailableToVote(String id) {
        return this.repository.findByIdAndActiveTrueAndClosedFalse(id);
    }

    public Flux<Discussion> findAllExpiredAndCloseSession() {
        return this.repository
                   .findAllByActiveTrueAndClosedFalse()
                   .filter(this::discussionIsExpired)
                   .flatMap(this::closeSession);
    }

    public Mono<Discussion> findClosedSession(String id) {
        return this.repository
                   .findAllByActiveFalseAndClosedTrue(id);
    }

    private Mono<Discussion> closeSession(Discussion d) {
        d.setClosed(Boolean.TRUE);
        d.setActive(Boolean.FALSE);
        return this.repository.save(d);
    }

    private boolean discussionIsExpired(Discussion d) {
        return d.getActivatedDate()
                .plusMinutes(d.getTimeout())
                .isBefore(LocalDateTime.now());
    }

}
