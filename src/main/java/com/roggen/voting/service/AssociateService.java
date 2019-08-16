package com.roggen.voting.service;

import com.roggen.voting.data.Associate;
import com.roggen.voting.data.repository.AssociateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AssociateService {

    @Autowired
    private AssociateRepository repository;

    public Mono<Associate> save(Associate discussion) {
        return repository.save(discussion);
    }

    public Mono<Associate> findById(String id) {
        return this.repository.findById(id);
    }

    public Flux<Associate> findAll() {
        return this.repository.findAll();
    }
}
