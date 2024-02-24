package com.petproject.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@EqualsAndHashCode
public class Branch {

    private String name;

    private String lastCommitSha;
}
