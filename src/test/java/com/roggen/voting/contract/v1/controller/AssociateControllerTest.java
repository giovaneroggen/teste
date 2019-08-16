package com.roggen.voting.contract.v1.controller;

import com.roggen.voting.WebFluxTest;
import com.roggen.voting.contract.v1.request.AssociateRequest;
import com.roggen.voting.data.Associate;
import com.roggen.voting.data.repository.AssociateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.restdocs.RestDocumentationContextProvider;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

public class AssociateControllerTest extends WebFluxTest {

    @Autowired
    private AssociateRepository repository;

    @Override
    @BeforeEach
    protected void beforeEach(ApplicationContext applicationContext, RestDocumentationContextProvider restDocumentation) {
        super.beforeEach(applicationContext, restDocumentation);
        repository.deleteAll()
                  .subscribe();
    }

    @Test
    public void save_success() {
        AssociateRequest request = AssociateRequest.builder()
                .cpf("01719462003")
                .name("TESTE 01719462003")
                .build();

        this.webTestClient
            .post()
            .body(Mono.just(request), AssociateRequest.class)
            .exchange()
            .expectStatus().isCreated()
            .expectBody()
            .consumeWith(document("associate/save_success"))
            .jsonPath("$.name").value(equalTo(request.getName()))
            .jsonPath("$.cpf").value(equalTo(request.getCpf()))
            .jsonPath("$.id").isNotEmpty();

        StepVerifier.create(this.repository.findAll())
                    .consumeNextWith(a -> {
                        assertEquals(a.getCpf(), request.getCpf());
                        assertEquals(a.getName(), request.getName());
                        assertNotNull(a.getId());
                    }).verifyComplete();
    }

    @Test
    public void save_error_cpf_notnull() {
        AssociateRequest request = AssociateRequest.builder()
                .cpf(null)
                .name("TESTE 01719462003")
                .build();

        super.webTestClient
                .post()
                .body(Mono.just(request), AssociateRequest.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .consumeWith(document("associate/save_error_cpf_notnull"));

        StepVerifier.create(this.repository.findAll())
                    .expectNextCount(0)
                    .verifyComplete();
    }

    @Test
    public void save_error_name_notnull() {
        AssociateRequest request = AssociateRequest.builder()
                .cpf("01719462003")
                .name(null)
                .build();

        this.webTestClient
                .post()
                .body(Mono.just(request), AssociateRequest.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .consumeWith(document("associate/save_error_name_notnull"));

        StepVerifier.create(this.repository.findAll())
                    .expectNextCount(0)
                    .verifyComplete();
    }

    @Test
    public void save_error_name_notempty() {
        AssociateRequest request = AssociateRequest.builder()
                .cpf("01719462003")
                .name("")
                .build();

        this.webTestClient
                .post()
                .body(Mono.just(request), AssociateRequest.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .consumeWith(document("associate/save_error_name_notempty"));

        StepVerifier.create(this.repository.findAll())
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    public void save_error_cpf_invalid() {
        AssociateRequest request = AssociateRequest.builder()
                .cpf("01719462002")
                .name("TESTE 01719462003")
                .build();

        this.webTestClient
                .post()
                .body(Mono.just(request), AssociateRequest.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .consumeWith(document("associate/save_error_cpf_invalid"));

        StepVerifier.create(this.repository.findAll())
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    public void find_by_id_success() {
        Associate build = Associate.builder()
                                   .name("Teste 01719462003")
                                   .name("01719462003")
                                   .build();
        Associate block = this.repository
                              .save(build).block();

        this.webTestClient
            .get()
            .uri("/{id}", block.getId())
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .consumeWith(document("associate/find_by_id_success"));
    }


    @Test
    public void find_by_id_not_found() {
        this.webTestClient
                .get()
                .uri("/{id}", "u9012jhrfh90u3r1j")
                .exchange()
                .expectStatus().isNotFound().expectBody()
                .consumeWith(document("associate/find_by_id_not_found_error"));
    }

    @Test
    public void find_all_success() {
        Associate build = Associate.builder()
                                   .name("Teste 01719462003")
                                   .name("01719462003")
                                   .build();
        this.repository
                .save(build).subscribe();

        this.webTestClient
                .get()
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(document("associate/find_all_success"));
    }


    @Test
    public void find_all_success_empty() {
        this.webTestClient
                .get()
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(document("associate/find_all_success_empty"));
    }

    @Override
    public String baseUrl() {
        return "/v1/associates";
    }
}