package com.wonderprints.isomorphic.example.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public record VisibilityFilter(
    @Id String value
) {
}
