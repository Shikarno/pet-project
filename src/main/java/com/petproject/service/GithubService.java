package com.petproject.service;

import com.petproject.client.dto.GithubBranch;
import com.petproject.model.Branch;
import com.petproject.model.Repository;
import com.petproject.model.VCS;
import com.petproject.service.exception.UserNotFoundException;
import com.petproject.client.GithubClient;
import com.petproject.client.dto.GithubRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Service
@AllArgsConstructor
public class GithubService implements VcsService{

    private GithubClient githubClient;

    public Flux<Repository> repositories(String userName) {
        Flux<Repository> repositoryFlux = getRepositoryFlux(userName);
        return githubClient.getUser(userName)
                .flux()
                .flatMap(githubUser -> repositoryFlux)
                .onErrorResume(ResponseStatusException.class,
                        ex -> {
                            throw new UserNotFoundException(userName, ex);
                        }
                );
    }

    private Flux<Repository> getRepositoryFlux(String userName) {
        return githubClient.getRepos(userName)
                .filter(githubRepository -> !githubRepository.getFork())
                .publishOn(Schedulers.boundedElastic())
                .flatMap(githubRepository -> {
                    Mono<List<Branch>> branchesMono = githubClient.getBranches(userName, githubRepository.getName())
                            .map(this::convert)
                            .collectList();
                    return branchesMono.map(branches -> {
                        Repository convertedRepo = convert(githubRepository);
                        convertedRepo.setBranches(branches);
                        return convertedRepo;
                    });
                });
    }

    public Repository convert(GithubRepository githubRepository) {
        return Repository.builder().name(githubRepository.getName()).vcs(VCS.GITHUB).build();
    }

    public Branch convert(GithubBranch githubBranch) {
        return new Branch(githubBranch.getName(), githubBranch.getCommit().getSha());
    }
}
