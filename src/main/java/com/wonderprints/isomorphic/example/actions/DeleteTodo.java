package com.wonderprints.isomorphic.example.actions;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DeleteTodo(
    @JsonProperty("type") String type,
    @JsonProperty("id") String id
) implements Action {
}
