package com.wonderprints.isomorphic.example.actions;

public class AddTodo extends Action {
    private String id;
    private String text;

    public AddTodo() {
        super();
    }

    @SuppressWarnings("unused")
    public AddTodo(String id, String text) {
        super("ADD_TODO");
        this.id = id;
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AddTodo addTodo = (AddTodo) o;
        return (id != null ? id.equals(addTodo.id) : addTodo.id == null) &&
                (text != null ? text.equals(addTodo.text) : addTodo.text == null);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AddTodo{" +
                "id='" + id + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
