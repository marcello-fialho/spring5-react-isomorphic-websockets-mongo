package com.wonderprints.isomorphic.example.actions;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SetVisibilityFilter(
    @JsonProperty("type") String type,
    @JsonProperty("filter") String filter
) implements Action {
}
