package com.petproject.client;

import com.petproject.client.dto.GithubBranch;
import com.petproject.client.dto.GithubRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import reactor.test.StepVerifier;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class GithubClientContractTest {

    @Autowired
    private GithubClient githubClient;

    @Test
    public void testGetRepos() {
        StepVerifier.create(githubClient.getRepos("Shikarno"))
                .expectNext(GithubRepository.builder().name("gitza").fork(false).build())
                .expectNext(GithubRepository.builder().name("pet-project").fork(false).build())
                .verifyComplete();
    }

    @Test
    public void testGetBranches() {
        GithubBranch.Commit commit = new GithubBranch.Commit(
                "https://api.github.com/repos/Shikarno/gitza/commits/ff890efb391b267e55636e0865a24ce527adc805",
                "ff890efb391b267e55636e0865a24ce527adc805"
        );
        StepVerifier.create(githubClient.getBranches("Shikarno", "gitza"))
                .expectNext(GithubBranch.builder().name("master").commit(commit).build())
                .verifyComplete();
    }

    @Test
    public void testGetUser() {
        StepVerifier.create(githubClient.getUser("Shikarno"))
                .expectNextCount(1)
                .verifyComplete();
    }
}
