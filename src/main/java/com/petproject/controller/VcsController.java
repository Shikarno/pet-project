package com.petproject.controller;

import com.petproject.model.Response;
import com.petproject.service.VcsServiceLocator;
import com.petproject.service.exception.UserNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
@Slf4j
@AllArgsConstructor
public class VcsController {

    VcsServiceLocator vcsServiceLocator;

    @Operation(summary = "Get all user's repositories names which are not forks along with branches names")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved data"),
            @ApiResponse(responseCode = "404", description = "Data not found"),
            @ApiResponse(responseCode = "406", description = "Media type is not supported", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal error")
    })
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
