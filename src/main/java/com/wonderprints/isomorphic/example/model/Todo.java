package com.wonderprints.isomorphic.example.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
@Document
public class Todo {
    @NonNull
    @Id
    private String id;
    @NonNull
    private String text;
    @NonNull
    private boolean completed;

    public static Todo cp(Todo original) {
        return new Todo(original.getId(), original.getText(), original.isCompleted());
    }
}
