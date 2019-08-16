package com.roggen.voting.data.repository;

import com.roggen.voting.data.Associate;
import com.roggen.voting.data.Discussion;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface AssociateRepository extends ReactiveMongoRepository<Associate, String> {
}
