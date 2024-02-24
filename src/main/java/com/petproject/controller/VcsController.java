package com.petproject.controller;

import com.petproject.service.VcsServiceLocator;
import com.petproject.service.exception.UserNotFoundException;
import com.petproject.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")
@Slf4j
public class VcsController {

    @Autowired
    VcsServiceLocator vcsServiceLocator;

    @GetMapping(value = "/{vcs}/{userName}/repositories",  produces = {"application/json"})
    public Mono<ResponseEntity<Response>> getAllRepositories(@PathVariable String vcs, @PathVariable String userName) {
        return vcsServiceLocator.getService(vcs)
                .map(service -> service.repositories(userName)
                        .collectList()
                        .map(listOfDocuments -> {
                            return ResponseEntity.ok(Response.builder().repositories(listOfDocuments).build());
                        }))
                .orElse(Mono.just(ResponseEntity
                        .badRequest()
                        .body(Response.builder().errorMessage(String.format("Vcs %s is not supported", vcs)).build())));

    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Response> handleUserNotFoundException(UserNotFoundException ex) {
        log.error("User with user name {} not found", ex.getUserName(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Response.builder().errorMessage(String.format("User with user name %s not found", ex.getUserName())).build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response> handleOtherExceptions(Exception ex) {
        log.error("An error occurred while fetching repositories", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.builder().errorMessage("An error occurred while fetching repositories").build());
    }
}
