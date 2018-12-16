package com.wonderprints.isomorphic.example.actions;

import lombok.*;

@Data
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(callSuper=false)
public class SetVisibilityFilter extends Action {
    @SuppressWarnings("unused")
    public SetVisibilityFilter(String filter) {
        super("SET_VISIBILITY_FILTER");
        this.filter = filter;
    }
    private String filter;
}
