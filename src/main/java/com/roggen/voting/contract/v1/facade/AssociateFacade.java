package com.roggen.voting.contract.v1.facade;

import com.roggen.voting.client.AssociateClient;
import com.roggen.voting.config.exception.GenericException;
import com.roggen.voting.contract.v1.mapper.AssociateMapper;
import com.roggen.voting.contract.v1.request.AssociateRequest;
import com.roggen.voting.contract.v1.response.AssociateResponse;
import com.roggen.voting.data.Associate;
import com.roggen.voting.service.AssociateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class AssociateFacade {

    @Autowired
    private AssociateService service;
    @Autowired
    private AssociateClient client;

    public Mono<AssociateResponse> save(AssociateRequest request) {
        return this.service
                   .save(AssociateMapper.map(request))
                   .map(AssociateMapper::map);
    }

    public Mono<AssociateResponse> findById(String id) {
        return this.service
                   .findById(id)
                   .map(AssociateMapper::map)
                   .switchIfEmpty(Mono.defer(() -> Mono.error(new GenericException("associate.not.found", HttpStatus.NOT_FOUND))));
    }

    public Flux<AssociateResponse> findAll() {
        return this.service
                   .findAll()
                   .map(AssociateMapper::map);
    }

    public Mono<String> findAvailableAssociateId(String id) {
        return this.service
                   .findById(id)
                   .map(Associate::getCpf)
                   .flatMap(this.client::verifyIfAssociateCanVote)
                   .thenReturn(id);
    }
}
