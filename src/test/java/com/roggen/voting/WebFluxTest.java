package com.roggen.voting;

import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration;

@SpringBootTest
@AutoConfigureDataMongo
@ExtendWith({SpringExtension.class, RestDocumentationExtension.class})
public abstract class WebFluxTest {

    protected WebTestClient webTestClient;

    protected final MockWebServer mockWebServer = new MockWebServer();

    @BeforeEach
    protected void beforeEach(ApplicationContext applicationContext,
                    RestDocumentationContextProvider restDocumentation) {
        this.webTestClient =
                WebTestClient.bindToApplicationContext(applicationContext)
                        .configureClient()
                        .baseUrl(this.baseUrl())
                        .filter(documentationConfiguration(restDocumentation))
                        .build();
    }

    @AfterEach
    protected void afterEach() throws IOException {
        mockWebServer.shutdown();
    }
    public abstract String baseUrl();



    @Configuration
    class WebClientConfiguration{
        @Bean
        public WebClient webClient(){
            return WebClient.create(mockWebServer.url("/").toString());
        }
    }

}
