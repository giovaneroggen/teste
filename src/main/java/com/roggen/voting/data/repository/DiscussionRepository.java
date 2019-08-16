package com.roggen.voting.data.repository;

import com.roggen.voting.data.Discussion;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DiscussionRepository extends ReactiveMongoRepository<Discussion, String> {

    Mono<Discussion> findByIdAndActiveFalseAndClosedFalse(String id);

    Mono<Discussion> findByIdAndActiveTrueAndClosedFalse(String id);

    Flux<Discussion> findAllByActiveTrueAndClosedFalse();

    Mono<Discussion> findAllByActiveFalseAndClosedTrue(String id);
}
