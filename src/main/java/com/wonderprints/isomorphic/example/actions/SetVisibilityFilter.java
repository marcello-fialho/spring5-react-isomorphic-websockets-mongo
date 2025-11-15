package com.wonderprints.isomorphic.example.actions;

public class SetVisibilityFilter extends Action {
    private String filter;

    public SetVisibilityFilter() {
        super();
    }

    @SuppressWarnings("unused")
    public SetVisibilityFilter(String filter) {
        super("SET_VISIBILITY_FILTER");
        this.filter = filter;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SetVisibilityFilter that = (SetVisibilityFilter) o;
        return filter != null ? filter.equals(that.filter) : that.filter == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (filter != null ? filter.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SetVisibilityFilter{" +
                "filter='" + filter + '\'' +
                '}';
    }
}
