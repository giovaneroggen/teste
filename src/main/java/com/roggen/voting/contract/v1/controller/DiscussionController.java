package com.roggen.voting.contract.v1.controller;

import com.roggen.voting.contract.v1.facade.DiscussionFacade;
import com.roggen.voting.contract.v1.request.DiscussionRequest;
import com.roggen.voting.contract.v1.request.DiscussionSessionRequest;
import com.roggen.voting.contract.v1.request.VoteRequest;
import com.roggen.voting.contract.v1.response.DiscussionResponse;
import com.roggen.voting.contract.v1.response.DiscussionResultResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/v1/discussions")
public class DiscussionController {

    @Autowired
    private DiscussionFacade facade;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<DiscussionResponse> save(@RequestBody DiscussionRequest request){
        return this.facade.save(request);
    }

    @GetMapping
    public Flux<DiscussionResponse> findAll(){
        return facade.findAll();
    }

    @PostMapping("/{id}/sessions")
    public Mono<Void> start(@PathVariable("id") String id,
                            @RequestBody DiscussionSessionRequest request){
        return this.facade.start(id, request);
    }

    @GetMapping("/sessions")
    public Mono<Void> verifyExpiration(){
        return this.facade
                   .verifyExpiration()
                   .doOnTerminate(() -> log.warn("DiscussionController.verifyExpiration"));
    }

    @PostMapping("/{id}")
    public Mono<Void> vote(@PathVariable("id") String id,
                           @RequestBody VoteRequest request){
        return this.facade.vote(id, request);
    }

    @GetMapping("/{id}")
    public Mono<DiscussionResultResponse> result(@PathVariable("id") String id){
        return this.facade.result(id);
    }

}
