package com.wonderprints.isomorphic.example.actions;

public sealed interface Action permits AddTodo, DeleteTodo, UpdateTodo, SetVisibilityFilter {
    String type();
}
