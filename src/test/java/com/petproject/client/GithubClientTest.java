package com.petproject.client;

import com.petproject.client.dto.GithubBranch;
import com.petproject.client.dto.GithubRepository;
import com.petproject.client.dto.GithubUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GithubClientTest {

    private static final String LINK_HEADER_VALUE = "<https://api.github.com/user/420065/repos?page=2>; rel=\"next\"," +
            "<https://api.github.com/user/420065/repos?page=2>; rel=\"last\"";
    private static final String REPO_NAME = "Repo";
    private static final String REPO2_NAME = "Repo2";

    private static final String BRANCH_NAME = "Branch";
    private static final String BRANCH2_NAME = "Branch2";
    private static final String USER_NAME = "UserName";
    private static final String LINK_HEADER = "link";
    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpecMock;

    @Mock
    private WebClient.ResponseSpec responseSpecMock;

    @InjectMocks
    private GithubClient githubClient;

    @Test
    void getRepos_NoLinksForNextPage() {
        webClientMockCommon();
        when(responseSpecMock.toEntityFlux(GithubRepository.class)).thenReturn(
                Mono.just(
                        new ResponseEntity<>(Flux.just(GithubRepository.builder().name(REPO_NAME).build()), HttpStatus.OK)
                )
        );

        Flux<GithubRepository> repos = githubClient.getRepos(USER_NAME);
        StepVerifier.create(repos)
                .expectNextMatches(repository -> repository.getName().equals(REPO_NAME))
                .verifyComplete();
    }

    @Test
    void getRepos_WithLinksForNextPage() {
        webClientMockCommon();
        when(requestHeadersUriSpecMock.uri(any(URI.class))).thenReturn(requestHeadersSpecMock);
        when(responseSpecMock.toEntityFlux(GithubRepository.class)).thenReturn(
                Mono.just(
                        new ResponseEntity<>(
                                Flux.just(GithubRepository.builder().name(REPO_NAME).build()),
                                new LinkedMultiValueMap<>() {{
                                    add(LINK_HEADER, LINK_HEADER_VALUE);
                                }},
                                HttpStatus.OK
                        )
                ),
                Mono.just(
                        new ResponseEntity<>(
                                Flux.just(GithubRepository.builder().name(REPO2_NAME).build()),
                                HttpStatus.OK
                        )
                )
        );

        Flux<GithubRepository> repos = githubClient.getRepos(USER_NAME);
        StepVerifier.create(repos)
                .expectNextMatches(repository -> repository.getName().equals(REPO_NAME))
                .expectNextMatches(repository -> repository.getName().equals(REPO2_NAME))
                .verifyComplete();
    }

    @Test
    void getBranches_NoLinksForNextPage() {
        webClientMockCommon();
        when(responseSpecMock.toEntityFlux(GithubBranch.class)).thenReturn(
                Mono.just(
                        new ResponseEntity<>(Flux.just(GithubBranch.builder().name(BRANCH_NAME).build()), HttpStatus.OK)
                )
        );

        Flux<GithubBranch> repos = githubClient.getBranches(USER_NAME, REPO_NAME);
        StepVerifier.create(repos)
                .expectNextMatches(branch -> branch.getName().equals(BRANCH_NAME))
                .verifyComplete();
    }

    @Test
    void getBranches_WithLinksForNextPage() {
        webClientMockCommon();
        when(requestHeadersUriSpecMock.uri(any(URI.class))).thenReturn(requestHeadersSpecMock);
        when(responseSpecMock.toEntityFlux(GithubBranch.class)).thenReturn(
                Mono.just(
                        new ResponseEntity<>(
                                Flux.just(GithubBranch.builder().name(BRANCH_NAME).build()),
                                new LinkedMultiValueMap<>() {{
                                    add(LINK_HEADER, LINK_HEADER_VALUE);
                                }},
                                HttpStatus.OK
                        )
                ),
                Mono.just(
                        new ResponseEntity<>(Flux.just(GithubBranch.builder().name(BRANCH2_NAME).build()), HttpStatus.OK)
                )
        );

        Flux<GithubBranch> repos = githubClient.getBranches(USER_NAME, REPO_NAME);
        StepVerifier.create(repos)
                .expectNextMatches(branch -> branch.getName().equals(BRANCH_NAME))
                .expectNextMatches(branch -> branch.getName().equals(BRANCH2_NAME))
                .verifyComplete();
    }

    @Test
    void getUser() {
        webClientMockCommon();
        when(responseSpecMock.onStatus(any(), any())).thenReturn(responseSpecMock);
        when(responseSpecMock.toEntity(GithubUser.class)).thenReturn(
                Mono.just(
                        new ResponseEntity<>(GithubUser.builder().login("login").build(), HttpStatus.OK)
                )
        );

        Mono<GithubUser> userResponse = githubClient.getUser(USER_NAME);
        StepVerifier.create(userResponse)
                .expectNextMatches(user -> user.getLogin().equals("login"))
                .verifyComplete();
    }

    private void webClientMockCommon() {
        when(webClient.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri(any(Function.class))).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.accept(any())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
    }
}