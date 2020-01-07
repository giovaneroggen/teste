package com.roggen.voting.contract.v1.controller;

import com.roggen.voting.WebFluxTest;
import com.roggen.voting.contract.v1.request.DiscussionRequest;
import com.roggen.voting.contract.v1.request.DiscussionSessionRequest;
import com.roggen.voting.contract.v1.request.VoteRequest;
import com.roggen.voting.data.Associate;
import com.roggen.voting.data.Discussion;
import com.roggen.voting.data.repository.AssociateRepository;
import com.roggen.voting.data.repository.DiscussionRepository;
import com.roggen.voting.enumeration.VoteEnum;
import okhttp3.mockwebserver.MockResponse;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

public class DiscussionControllerTest extends WebFluxTest {

    @Autowired
    private DiscussionRepository repository;

    @Autowired
    private AssociateRepository associateRepository;

    @Override
    @BeforeEach
    protected void beforeEach(ApplicationContext applicationContext, RestDocumentationContextProvider restDocumentation) {
        super.beforeEach(applicationContext, restDocumentation);
        this.repository.deleteAll().block();
        this.associateRepository.deleteAll().block();
    }

    @Test
    public void save_success(){
        DiscussionRequest request = DiscussionRequest.builder()
                .discussion("Teste 123?")
                .build();

        this.webTestClient
                .post()
                .body(Mono.just(request), DiscussionRequest.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .consumeWith(document("discussion/save_success"))
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.createdDate").isNotEmpty()
                .jsonPath("$.discussion").value(equalTo(request.getDiscussion()));

        StepVerifier.create(this.repository.findAll())
                .consumeNextWith(d -> {
                    assertNotNull(d.getId());
                    assertEquals(request.getDiscussion(), d.getDiscussion());
                    assertEquals(0L, d.getYesQuantity().longValue());
                    assertEquals(0L, d.getNoQuantity().longValue());
                    assertEquals(0, d.getAssociatedList().size());
                    assertNull(d.getTimeout());
                    assertFalse(d.getActive());
                    assertFalse(d.getClosed());
                    assertNotNull(d.getCreatedDate());
                }).verifyComplete();
    }

    @Test
    public void save_error_discussion_notnull(){
        DiscussionRequest request = new DiscussionRequest();
        this.webTestClient
                .post()
                .body(Mono.just(request), DiscussionRequest.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .consumeWith(document("discussion/save_error_discussion_notnull"));
        StepVerifier.create(this.repository.findAll())
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    public void save_error_discussion_notempty(){
        DiscussionRequest request = DiscussionRequest.builder()
                .discussion("")
                .build();
        this.webTestClient
                .post()
                .body(Mono.just(request), DiscussionRequest.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .consumeWith(document("discussion/save_error_discussion_notempty"));
        StepVerifier.create(this.repository.findAll())
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    public void find_all_success() {
        Discussion build = Discussion.builder()
                .discussion("Teste 01719462003")
                .build();
        this.repository
                .save(build).block();
        this.webTestClient
                .get()
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(document("discussion/find_all_success"));
    }

    @Test
    public void find_all_success_empty() {
        this.webTestClient
                .get()
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(document("discussion/find_all_success_empty"));
    }

    @Test
    public void start_success(){
        Discussion discussion = Discussion.builder().discussion("Teste 01719462003").build();
        Discussion block = this.repository
                .save(discussion)
                .block();

        DiscussionSessionRequest request = DiscussionSessionRequest.builder()
                .timeout(1L)
                .build();

        this.webTestClient
                .post()
                .uri("/{id}/sessions", block.getId())
                .body(Mono.just(request), DiscussionSessionRequest.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(document("discussion/start_success"));

        StepVerifier.create(this.repository.findAll())
                    .consumeNextWith(d -> {
                        assertTrue(d.getActive());
                        assertEquals(d.getTimeout(), request.getTimeout());
                        assertNotNull(d.getActivatedDate());
                    }).verifyComplete();
    }

    @Test
    public void start_error_notavailable(){
        DiscussionSessionRequest request = DiscussionSessionRequest.builder()
                .timeout(1L)
                .build();
        this.webTestClient
                .post()
                .uri("/{id}/sessions", "1902um0132i1290e1")
                .body(Mono.just(request), DiscussionSessionRequest.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .consumeWith(document("discussion/start_error_notavailable"));

        StepVerifier.create(this.repository.findAll())
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    public void verify_expiration_success(){
        Discussion discussion = Discussion.builder()
                                          .discussion("Teste 01719462003")
                                          .active(true)
                                          .closed(false)
                                          .timeout(1L)
                                          .createdDate(LocalDateTime.now().minusMinutes(10L))
                                          .activatedDate(LocalDateTime.now().minusMinutes(5L))
                                          .build();
        this.repository
                .save(discussion)
                .block();

        this.webTestClient
                .get()
                .uri("/sessions")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(document("discussion/verify_expiration_success"));

        StepVerifier.create(this.repository.findAll())
                .consumeNextWith(d -> {
                    assertFalse(d.getActive());
                    assertTrue(d.getClosed());
                }).verifyComplete();
    }

    @Test
    public void vote_success(){
        super.mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setBody("{\"status\": \"ABLE_TO_VOTE\"}")
        );
        Associate associate = Associate.builder()
                .name("Teste 01719462003")
                .cpf("01719462003")
                .build();
        associate = this.associateRepository.save(associate).block();

        Discussion discussion = Discussion.builder()
                                            .discussion("Teste 01719462003")
                                            .active(true)
                                            .closed(false)
                                            .timeout(2L)
                                            .createdDate(LocalDateTime.now().minusMinutes(10L))
                                            .activatedDate(LocalDateTime.now().minusMinutes(1L))
                                            .build();

        discussion = this.repository.save(discussion).block();

        VoteRequest build = VoteRequest.builder()
                .vote(VoteEnum.YES)
                .associateId(associate.getId())
                .build();

        this.webTestClient
                .post()
                .uri("/{id}", discussion.getId())
                .body(Mono.just(build), VoteRequest.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(document("discussion/vote_success"));

        StepVerifier.create(this.repository.findAll())
                .consumeNextWith(d -> {
                    assertEquals(1, d.getAssociatedList().size());
                    assertEquals(1L, d.getYesQuantity().longValue());
                    assertEquals(0L, d.getNoQuantity().longValue());
                }).verifyComplete();
    }

    @Test
    public void vote_error_discussion_notavailabletovote(){
        VoteRequest build = VoteRequest.builder()
                .vote(VoteEnum.YES)
                .associateId("JIioerwjr08j209e2")
                .build();

        this.webTestClient
                .post()
                .uri("/{id}", "JIioerwjr08j209e2")
                .body(Mono.just(build), VoteRequest.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .consumeWith(document("discussion/vote_error_discussion_notavailabletovote"));
    }

    @Test
    public void vote_error_associate_notfound(){
        Discussion discussion = Discussion.builder()
                .discussion("Teste 01719462003")
                .active(true)
                .closed(false)
                .timeout(2L)
                .createdDate(LocalDateTime.now().minusMinutes(10L))
                .activatedDate(LocalDateTime.now().minusMinutes(1L))
                .build();

        discussion = this.repository.save(discussion).block();

        VoteRequest build = VoteRequest.builder()
                .vote(VoteEnum.YES)
                .associateId("JIioerwjr08j209e2")
                .build();

        this.webTestClient
                .post()
                .uri("/{id}", discussion.getId())
                .body(Mono.just(build), VoteRequest.class)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .consumeWith(document("discussion/vote_error_associate_notfound"));

        StepVerifier.create(this.repository.findAll())
                .consumeNextWith(d -> {
                    assertEquals(0, d.getAssociatedList().size());
                    assertEquals(0L, d.getYesQuantity().longValue());
                    assertEquals(0L, d.getNoQuantity().longValue());
                }).verifyComplete();
    }

    @Test
    public void vote_error_associate_unable(){
        super.mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setBody("{\"status\": \"UNABLE_TO_VOTE\"}")
        );
        Associate associate = Associate.builder()
                .name("Teste 01719462003")
                .cpf("01719462003")
                .build();
        associate = this.associateRepository.save(associate).block();

        Discussion discussion = Discussion.builder()
                .discussion("Teste 01719462003")
                .active(true)
                .closed(false)
                .timeout(2L)
                .createdDate(LocalDateTime.now().minusMinutes(10L))
                .activatedDate(LocalDateTime.now().minusMinutes(1L))
                .build();

        discussion = this.repository.save(discussion).block();

        VoteRequest build = VoteRequest.builder()
                .vote(VoteEnum.YES)
                .associateId(associate.getId())
                .build();

        this.webTestClient
                .post()
                .uri("/{id}", discussion.getId())
                .body(Mono.just(build), VoteRequest.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .consumeWith(document("discussion/vote_error_associate_unable"));

        StepVerifier.create(this.repository.findAll())
                .consumeNextWith(d -> {
                    assertEquals(0, d.getAssociatedList().size());
                    assertEquals(0L, d.getYesQuantity().longValue());
                    assertEquals(0L, d.getNoQuantity().longValue());
                }).verifyComplete();
    }

    @Test
    public void vote_error_associate_repeatednotallowed(){
        super.mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setBody("{\"status\": \"ABLE_TO_VOTE\"}")
        );
        Associate associate = Associate.builder()
                .name("Teste 01719462003")
                .cpf("01719462003")
                .build();
        associate = this.associateRepository.save(associate).block();

        List<String> associatedList = new ArrayList<>();
        associatedList.add(associate.getId());
        Discussion discussion = Discussion.builder()
                .discussion("Teste 01719462003")
                .associatedList(associatedList)
                .active(true)
                .closed(false)
                .timeout(2L)
                .createdDate(LocalDateTime.now().minusMinutes(10L))
                .activatedDate(LocalDateTime.now().minusMinutes(1L))
                .build();
        discussion = this.repository.save(discussion).block();

        VoteRequest build = VoteRequest.builder()
                .vote(VoteEnum.YES)
                .associateId(associate.getId())
                .build();

        this.webTestClient
                .post()
                .uri("/{id}", discussion.getId())
                .body(Mono.just(build), VoteRequest.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .consumeWith(document("discussion/vote_error_associate_repeatednotallowed"));

        StepVerifier.create(this.repository.findAll())
                .consumeNextWith(d -> {
                    assertEquals(1, d.getAssociatedList().size());
                    assertEquals(0L, d.getYesQuantity().longValue());
                    assertEquals(0L, d.getNoQuantity().longValue());
                }).verifyComplete();
    }

    @Test
    public void result_success(){
        Associate associate = Associate.builder()
                .name("Teste 01719462003")
                .cpf("01719462003")
                .build();
        associate = this.associateRepository.save(associate).block();

        List<String> associatedList = new ArrayList<>();
        associatedList.add(associate.getId());
        Discussion discussion = Discussion.builder()
                .discussion("Teste 01719462003")
                .associatedList(associatedList)
                .active(false)
                .closed(true)
                .timeout(2L)
                .yesQuantity(1L)
                .noQuantity(0L)
                .createdDate(LocalDateTime.now().minusMinutes(10L))
                .activatedDate(LocalDateTime.now().minusMinutes(1L))
                .build();
        discussion = this.repository.save(discussion).block();

        this.webTestClient
                .get()
                .uri("/{id}", discussion.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(document("discussion/result_success"))
                .jsonPath("$.id").value(equalTo(discussion.getId()))
                .jsonPath("$.discussion").value(equalTo(discussion.getDiscussion()))
                .jsonPath("$.total").value(equalTo(1))
                .jsonPath("$.yesQuantity").value(equalTo(1))
                .jsonPath("$.noQuantity").value(equalTo(0));
    }

    @Test
    public void result_error_discussion_notavailable(){
        this.webTestClient
                .get()
                .uri("/{id}", "1io2j3091j3129j302j1j13")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .consumeWith(document("discussion/result_error_discussion_notavailable"));
    }

    @Override
    public String baseUrl() {
        return "/v1/discussions";
    }
}