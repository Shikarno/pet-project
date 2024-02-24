package com.petproject.service;

import com.petproject.client.dto.GithubBranch;
import com.petproject.client.dto.GithubUser;
import com.petproject.client.GithubClient;
import com.petproject.client.dto.GithubRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GithubServiceTest {

    private static final String USER_NAME = "testUser";
    private static final String TEST_REPO = "testRepo";

    private static final String MAIN_BRANCH = "main";

    private static final String COMMIT_URL = "url";

    private static final String COMMIT_SHA = "sha";

    @Mock
    private GithubClient githubClient;

    @InjectMocks
    private GithubService githubService;

    @Test
    public void testRepositories() {
        GithubUser githubUser = GithubUser.builder().login(USER_NAME).build();
        GithubRepository githubRepository = GithubRepository.builder().name(TEST_REPO).fork(false).build();
        GithubBranch githubBranch = GithubBranch.builder().name(MAIN_BRANCH).commit(
                new GithubBranch.Commit(COMMIT_URL, COMMIT_SHA)).build();
        List<GithubBranch> branches = Collections.singletonList(githubBranch);

        when(githubClient.getUser(USER_NAME)).thenReturn(Mono.just(githubUser));
        when(githubClient.getRepos(USER_NAME)).thenReturn(Flux.just(githubRepository));
        when(githubClient.getBranches(USER_NAME, githubRepository.getName())).thenReturn(Flux.fromIterable(branches));

        StepVerifier.create(githubService.repositories(USER_NAME))
                .expectNextMatches(repository -> repository.getName().equals(githubRepository.getName())
                        && repository.getBranches().size() == 1
                        && repository.getBranches().getFirst().getName().equals(githubBranch.getName()))
                .verifyComplete();

        verify(githubClient).getUser(USER_NAME);
        verify(githubClient).getRepos(USER_NAME);
        verify(githubClient).getBranches(USER_NAME, githubRepository.getName());
    }
}