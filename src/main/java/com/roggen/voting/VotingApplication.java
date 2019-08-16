package com.roggen.voting;

import com.roggen.voting.contract.v1.response.DiscussionResultResponse;
import com.roggen.voting.data.Associate;
import com.roggen.voting.data.repository.AssociateRepository;
import com.roggen.voting.messaging.queue.DiscussionResultQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@EnableBinding(DiscussionResultQueue.class)
@EnableMongoAuditing
@SpringBootApplication
@EnableReactiveMongoRepositories
public class VotingApplication implements CommandLineRunner {

    @Autowired
    private AssociateRepository repository;

    @Autowired
    private DiscussionResultQueue queue;

    public static void main(String[] args) {
        SpringApplication.run(VotingApplication.class, args);
    }

    @Bean
    public WebClient webClient(){
        return WebClient.create();
    }

    @Override
    public void run(String... args) throws Exception {
        this.repository
            .deleteAll()
            .thenMany(Flux.just("1231231", "01719462003", "19839091069", "62289608068"))
            .map(cpf -> Associate.builder().cpf(cpf).name("Name " + cpf).build())
            .flatMap(this.repository::save)
            .doOnTerminate(() ->
                queue.output().send(
                    MessageBuilder.withPayload(DiscussionResultResponse.builder().id("TESTE").build()).build()
                )
            )
            .subscribe(System.out::println);
    }
}
