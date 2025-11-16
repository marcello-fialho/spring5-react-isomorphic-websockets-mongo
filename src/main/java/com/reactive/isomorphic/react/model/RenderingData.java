package com.reactive.isomorphic.react.model;

public record RenderingData(String data, String content) {
    // Compact constructor to ensure data is always prefixed
    public RenderingData {
        if (data != null && !data.startsWith("window.__PRELOADED_STATE__=")) {
            data = "window.__PRELOADED_STATE__=" + data;
        }
    }
    
    // Static factory method matching original single-parameter constructor
    public static RenderingData of(String rawData) {
        return new RenderingData(rawData, "");
    }
    
    // Static factory method matching original two-parameter constructor  
    public static RenderingData of(String rawData, String content) {
        return new RenderingData(rawData, content);
    }
}
