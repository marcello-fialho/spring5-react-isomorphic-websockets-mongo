package com.wonderprints.isomorphic.example.actions;

import lombok.*;

@Data
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(callSuper=false)
public class DeleteTodo extends Action {
    @SuppressWarnings("unused")
    public DeleteTodo(String id) {
        super("DELETE_TODO");
        this.id = id;
    }
    private String id;
}
