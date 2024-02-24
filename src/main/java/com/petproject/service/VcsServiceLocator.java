package com.petproject.service;

import com.petproject.model.VCS;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class VcsServiceLocator {

    private final GithubService githubService;

    public Optional<VcsService> getService(String vcsType) {
        try {
            VCS vcs = VCS.valueOf(vcsType.toUpperCase());
            if (vcs == VCS.GITHUB) {
                return Optional.of(githubService);
            }
        } catch (IllegalArgumentException e) {
            log.error("Unsupported vcs {}", vcsType);
        }
        return Optional.empty();
    }
}
