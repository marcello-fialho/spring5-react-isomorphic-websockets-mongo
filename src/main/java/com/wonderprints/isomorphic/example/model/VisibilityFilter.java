package com.wonderprints.isomorphic.example.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
@Document
public class VisibilityFilter {
    @NonNull
    @Id
    private String value;
}
