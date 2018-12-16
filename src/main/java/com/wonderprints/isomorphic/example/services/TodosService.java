package com.wonderprints.isomorphic.example.services;

import com.wonderprints.isomorphic.example.model.Todo;
import com.wonderprints.isomorphic.example.model.VisibilityFilter;
import reactor.core.publisher.Mono;

public interface TodosService {
    Mono<VisibilityFilter> setVisibilityFilter(String visibilityFilter);
    Mono<Void> completeAllTodos();
    Mono<Void> clearCompleted();
    Mono<Todo> addTodo(Todo todo);
    Mono<Void> deleteTodo(String id);
    Mono<Todo> updateTodo(String id, Todo todo);
}
