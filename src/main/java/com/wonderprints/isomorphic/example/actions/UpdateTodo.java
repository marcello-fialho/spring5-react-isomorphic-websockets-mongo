package com.wonderprints.isomorphic.example.actions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wonderprints.isomorphic.example.model.Todo;

public record UpdateTodo(
    @JsonProperty("type") String type,
    @JsonProperty("todo") Todo todo
) implements Action {
}
