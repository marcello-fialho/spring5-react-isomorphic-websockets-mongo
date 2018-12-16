package com.wonderprints.isomorphic.example.actions;

import com.wonderprints.isomorphic.example.model.Todo;
import lombok.*;

@Data
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(callSuper=false)
public class UpdateTodo extends Action {
    private Todo todo;
    @SuppressWarnings("unused")
    public UpdateTodo(Todo todo) {
        super("UPDATE_TODO");
        this.todo = todo;
    }
}
