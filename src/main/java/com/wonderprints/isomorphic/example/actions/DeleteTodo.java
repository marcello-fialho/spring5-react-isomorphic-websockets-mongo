package com.wonderprints.isomorphic.example.actions;

public class DeleteTodo extends Action {
    private String id;

    public DeleteTodo() {
        super();
    }

    @SuppressWarnings("unused")
    public DeleteTodo(String id) {
        super("DELETE_TODO");
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DeleteTodo that = (DeleteTodo) o;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DeleteTodo{" +
                "id='" + id + '\'' +
                '}';
    }
}
