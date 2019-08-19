package com.roggen.voting.client;

import com.roggen.voting.client.response.AssociateResponse;
import com.roggen.voting.config.exception.GenericException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class AssociateClient {

    @Autowired
    private WebClient webClient;

    public Mono<Void> verifyIfAssociateCanVote(String cpf) {
        return this.webClient
                   .get()
                   .uri("/users/{cpf}", cpf)
                   .retrieve()
                   .onStatus(HttpStatus.NOT_FOUND::equals, response -> Mono.error(new GenericException("associate.not.found", HttpStatus.NOT_FOUND)))
                   .bodyToMono(AssociateResponse.class)
                   .map(AssociateResponse::getStatus)
                   .filter("ABLE_TO_VOTE"::equals)
                   .switchIfEmpty(Mono.error(new GenericException("associate.unable", HttpStatus.BAD_REQUEST)))
                   .then();
    }
}
