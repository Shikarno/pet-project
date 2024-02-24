package com.petproject.service;

import com.petproject.model.Repository;
import reactor.core.publisher.Flux;

public interface VcsService {

    Flux<Repository> repositories(String userName);
}
