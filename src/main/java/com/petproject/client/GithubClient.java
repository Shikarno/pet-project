package com.petproject.client;

import com.petproject.client.dto.GithubBranch;
import com.petproject.client.dto.GithubUser;
import com.petproject.client.dto.GithubRepository;
import lombok.AllArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@AllArgsConstructor
public class GithubClient {

    private static final Pattern LINK_PATTERN = Pattern.compile(".*<(.*)>; rel=\"next\".*");

    private WebClient webClient;

    public Flux<GithubRepository> getRepos(String userName) {
        Flux<ResponseEntity<Flux<GithubRepository>>> response = getFirstPage(userName)
                .expand(fluxResponseEntity -> getNextPages(fluxResponseEntity, GithubRepository.class));

        return response.flatMap(responseEntity -> {
            Flux<GithubRepository> body = responseEntity.getBody();
            return body != null ? body : Flux.empty();
        });

    }

    private Mono<ResponseEntity<Flux<GithubRepository>>> getFirstPage(String userName) {
        return webClient.get()
                .uri(getRepositoriesUri(userName))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntityFlux(GithubRepository.class);
    }

    private <T> Publisher<ResponseEntity<Flux<T>>> getNextPages(ResponseEntity<Flux<T>> fluxResponseEntity, Class<T> entityClass) {
        List<String> links = fluxResponseEntity.getHeaders().get("link");
        if (links != null) {
            for (String link : links) {
                Matcher m = LINK_PATTERN.matcher(link);
                if (m.matches()) {
                    return webClient.get()
                            .uri(URI.create(m.group(1)))
                            .retrieve()
                            .toEntityFlux(entityClass);
                }
            }
        }
        return Flux.empty();
    }

    public Flux<GithubBranch> getBranches(String userName, String repository) {
        Flux<ResponseEntity<Flux<GithubBranch>>> response = webClient.get()
                .uri(getBranchesUri(userName, repository))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntityFlux(GithubBranch.class)
                .expand(fluxResponseEntity -> getNextPages(fluxResponseEntity, GithubBranch.class));

        return response.flatMap(responseEntity -> {
            Flux<GithubBranch> body = responseEntity.getBody();
            return body != null ? body : Flux.empty();
        });

    }

    public Mono<GithubUser> getUser(String userName) {
        return getEntity(getUserUri(userName), GithubUser.class)
                .filter(githubUserResponseEntity -> githubUserResponseEntity.getStatusCode().isSameCodeAs(HttpStatus.OK))
                .mapNotNull(HttpEntity::getBody);
    }

    private <T> Mono<ResponseEntity<T>> getEntity(Function<UriBuilder, URI> uriFunction, Class<T> entityClass) {
        return webClient.get()
                .uri(uriFunction)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(
                        httpStatusCode -> httpStatusCode.isSameCodeAs(HttpStatus.NOT_FOUND),
                        clientResponse -> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found"))
                )
                .toEntity(entityClass);
    }

    private static Function<UriBuilder, URI> getRepositoriesUri(String userName) {
        return uriBuilder -> uriBuilder.path("users/{userName}/repos").build(userName);
    }

    private static Function<UriBuilder, URI> getBranchesUri(String userName, String repo) {
        return uriBuilder -> uriBuilder.path("repos/{userName}/{repo}/branches").build(userName, repo);
    }

    private static Function<UriBuilder, URI> getUserUri(String userName) {
        return uriBuilder -> uriBuilder.path("users/{userName}").build(userName);
    }

}
