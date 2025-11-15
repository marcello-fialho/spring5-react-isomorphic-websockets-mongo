package com.wonderprints.isomorphic.example.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public record Todo(
    @Id String id,
    String text,
    boolean completed
) {
    public static Todo cp(Todo original) {
        return new Todo(original.id, original.text, original.completed);
    }
}
