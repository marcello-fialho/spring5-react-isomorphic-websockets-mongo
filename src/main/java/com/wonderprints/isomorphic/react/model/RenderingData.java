package com.wonderprints.isomorphic.react.model;

import lombok.*;

@Getter
public class RenderingData {
    @NonNull
    private String data;
    @NonNull
    private String content;

    public RenderingData(String data) {
        this.content = "";
        this.data = "window.__PRELOADED_STATE__=" + data;
    }
    public RenderingData(String data, String content) {
        this.content = content;
        this.data = "window.__PRELOADED_STATE__=" + data;
    }
}
