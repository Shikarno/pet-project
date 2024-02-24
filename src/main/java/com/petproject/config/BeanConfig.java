package com.petproject.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class BeanConfig {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    private static final String AUTHORIZATION_HEADER_VALUE = "Bearer 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";

    private static final String X_GIT_HUB_API_VERSION_HEADER = "X-GitHub-Api-Version";

    private static final String X_GIT_HUB_API_VERSION_HEADER_VALUE = "2022-11-28";

    @Value("${github.url}")
    private String githubUrl;

    @Bean
    public WebClient githubWebClient() {
        return WebClient.builder()
                .baseUrl(githubUrl)
                .defaultHeader(AUTHORIZATION_HEADER, AUTHORIZATION_HEADER_VALUE)
                .defaultHeader(X_GIT_HUB_API_VERSION_HEADER, X_GIT_HUB_API_VERSION_HEADER_VALUE)
                .build();
    }
}
