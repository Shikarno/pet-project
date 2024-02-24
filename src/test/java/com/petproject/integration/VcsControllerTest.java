package com.petproject.integration;

import com.petproject.client.GithubClient;
import com.petproject.client.dto.GithubBranch;
import com.petproject.client.dto.GithubRepository;
import com.petproject.client.dto.GithubUser;
import com.petproject.model.Branch;
import com.petproject.model.Repository;
import com.petproject.model.Response;
import com.petproject.model.VCS;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class VcsControllerTest {

    private static final String USER_NAME = "testUser";

    private static final String TEST_REPO = "testRepo";

    private static final String MAIN_BRANCH = "main";

    private static final String COMMIT_URL = "url";

    private static final String COMMIT_SHA = "sha";

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private GithubClient githubClient;

    @Test
    public void testGetResource() throws Exception {
        GithubUser githubUser = GithubUser.builder().login(USER_NAME).build();
        GithubRepository githubRepository = GithubRepository.builder().name(TEST_REPO).fork(false).build();
        GithubBranch githubBranch = GithubBranch.builder().name(MAIN_BRANCH).commit(
                new GithubBranch.Commit(COMMIT_URL, COMMIT_SHA)).build();
        List<GithubBranch> branches = Collections.singletonList(githubBranch);

        Branch branch = Branch.builder()
                .name(MAIN_BRANCH)
                .lastCommitSha(COMMIT_SHA)
                .build();
        Repository repository = Repository.builder()
                .name(TEST_REPO)
                .vcs(VCS.GITHUB)
                .branches(Collections.singletonList(branch))
                .build();

        Response expectedResponse = new Response(Collections.singletonList(repository), null);

        when(githubClient.getUser(USER_NAME)).thenReturn(Mono.just(githubUser));
        when(githubClient.getRepos(USER_NAME)).thenReturn(Flux.just(githubRepository));
        when(githubClient.getBranches(USER_NAME, githubRepository.getName())).thenReturn(Flux.fromIterable(branches));

        webTestClient.get().uri("/api/github/" + USER_NAME + "/repositories")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Response.class)
                .value(response -> assertThat(response).isEqualTo(expectedResponse));

        verify(githubClient).getUser(USER_NAME);
        verify(githubClient).getRepos(USER_NAME);
        verify(githubClient).getBranches(USER_NAME, githubRepository.getName());
    }

    @Test
    public void testGetResource_NotFound() throws Exception {
        when(githubClient.getUser(USER_NAME)).thenReturn(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
        when(githubClient.getRepos(USER_NAME)).thenReturn(Flux.empty());

        webTestClient.get().uri("/api/github/" + USER_NAME + "/repositories")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();

        verify(githubClient).getUser(USER_NAME);
        verify(githubClient).getRepos(USER_NAME);
        verifyNoMoreInteractions(githubClient);
    }

    @Test
    public void testGetResource_UnexpectedException() throws Exception {
        when(githubClient.getUser(USER_NAME)).thenReturn(Mono.error(new RuntimeException()));
        when(githubClient.getRepos(USER_NAME)).thenReturn(Flux.empty());

        webTestClient.get().uri("/api/github/" + USER_NAME + "/repositories")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

        verify(githubClient).getUser(USER_NAME);
        verify(githubClient).getRepos(USER_NAME);
        verifyNoMoreInteractions(githubClient);
    }
}
