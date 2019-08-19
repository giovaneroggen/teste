package com.roggen.voting;

import okhttp3.mockwebserver.MockWebServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfiguration{
    @Bean
    public MockWebServer mockWebServer(){
        return new MockWebServer();
    }
    @Bean
    public WebClient webClient(MockWebServer mockWebServer){
        return WebClient.create(mockWebServer.url("/").toString());
    }
}
