package com.wonderprints.isomorphic.example.actions;

import com.wonderprints.isomorphic.example.model.Todo;

public class UpdateTodo extends Action {
    private Todo todo;

    public UpdateTodo() {
        super();
    }

    @SuppressWarnings("unused")
    public UpdateTodo(Todo todo) {
        super("UPDATE_TODO");
        this.todo = todo;
    }

    public Todo getTodo() {
        return todo;
    }

    public void setTodo(Todo todo) {
        this.todo = todo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        UpdateTodo that = (UpdateTodo) o;
        return todo != null ? todo.equals(that.todo) : that.todo == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (todo != null ? todo.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UpdateTodo{" +
                "todo=" + todo +
                '}';
    }
}
