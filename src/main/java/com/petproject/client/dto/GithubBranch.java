package com.petproject.client.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@EqualsAndHashCode
public class GithubBranch {

    private String name;

    private Commit commit;

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @EqualsAndHashCode
    public static class Commit {
        private String url;

        private String sha;
    }
}
