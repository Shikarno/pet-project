package com.petproject.model;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode
public class Repository {

    private String name;

    private VCS vcs;

    private List<Branch> branches;
}
