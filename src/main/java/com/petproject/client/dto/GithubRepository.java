package com.petproject.client.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@EqualsAndHashCode
public class GithubRepository {

    private String name;

    private Boolean fork;

}

