package com.roggen.voting.contract.v1.controller;

import com.roggen.voting.contract.v1.facade.AssociateFacade;
import com.roggen.voting.contract.v1.request.AssociateRequest;
import com.roggen.voting.contract.v1.response.AssociateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/associates")
public class AssociateController {

    @Autowired
    private AssociateFacade facade;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<AssociateResponse> save(@Valid @RequestBody AssociateRequest request){
        return this.facade.save(request);
    }

    @GetMapping("/{id}")
    public Mono<AssociateResponse> findById(@PathVariable("id") String id){
        return this.facade.findById(id);
    }

    @GetMapping
    public Flux<AssociateResponse> findAll(){
        return this.facade.findAll();
    }
}
