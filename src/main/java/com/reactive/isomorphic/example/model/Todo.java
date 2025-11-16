package com.reactive.isomorphic.example.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public record Todo(
    @Id @JsonProperty("id") String id,
    @JsonProperty("text") String text,
    @JsonProperty("completed") boolean completed
) {
    public static Todo cp(Todo original) {
        return new Todo(original.id, original.text, original.completed);
    }
}
