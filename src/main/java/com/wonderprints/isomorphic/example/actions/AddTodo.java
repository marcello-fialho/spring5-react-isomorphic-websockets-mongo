package com.wonderprints.isomorphic.example.actions;

import lombok.*;

@Data
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(callSuper=false)
public class AddTodo extends Action {
    @SuppressWarnings("unused")
    public AddTodo(String id, String text) {
        super("ADD_TODO");
        this.id = id;
        this.text = text;
    }
    private String id;
    private String text;
}
