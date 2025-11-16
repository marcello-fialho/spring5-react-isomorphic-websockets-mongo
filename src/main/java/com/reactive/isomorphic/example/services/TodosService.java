package com.reactive.isomorphic.example.services;

import com.reactive.isomorphic.example.model.Todo;
import com.reactive.isomorphic.example.model.VisibilityFilter;

public interface TodosService {
    VisibilityFilter setVisibilityFilter(String visibilityFilter);
    void completeAllTodos();
    void clearCompleted();
    Todo addTodo(Todo todo);
    void deleteTodo(String id);
    Todo updateTodo(String id, Todo todo);
}
