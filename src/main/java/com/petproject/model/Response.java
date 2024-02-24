package com.petproject.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {

    private List<Repository>  repositories;


    private String errorMessage;
}
