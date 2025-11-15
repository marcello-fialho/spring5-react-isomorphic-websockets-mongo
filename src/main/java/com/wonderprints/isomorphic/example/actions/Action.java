package com.wonderprints.isomorphic.example.actions;

public class Action {
    private String type;

    public Action() {
    }

    public Action(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Action action = (Action) o;
        return type != null ? type.equals(action.type) : action.type == null;
    }

    @Override
    public int hashCode() {
        return type != null ? type.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Action{" +
                "type='" + type + '\'' +
                '}';
    }
}
