package com.wonderprints.isomorphic.example.actions;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AddTodo(
    @JsonProperty("type") String type,
    @JsonProperty("id") String id,
    @JsonProperty("text") String text
) implements Action {
}
