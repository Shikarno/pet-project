package com.petproject.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class VcsServiceLocatorTest {

    @Mock
    private GithubService githubClient;

    @InjectMocks
    private VcsServiceLocator vcsServiceLocator;

    @Test
    void getService() {
        Optional<VcsService> github = vcsServiceLocator.getService("github");
        assertThat(github).contains(githubClient);
    }

    @Test
    void getService_NotSupported() {
        assertThat(vcsServiceLocator.getService("bitbucket")).isEmpty();
    }

    @Test
    void getService_Invalid() {
        assertThat(vcsServiceLocator.getService("invalid")).isEmpty();
    }
}