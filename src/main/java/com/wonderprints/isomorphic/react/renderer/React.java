package com.wonderprints.isomorphic.react.renderer;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.concurrent.Executors;

public class React {

    private ThreadLocal<Context> contextHolder = ThreadLocal.withInitial(() -> {
        try {
            var context = Context.newBuilder("js")
                .allowAllAccess(true)
                .build();
            
            // Load server.js
            var serverSource = Source.newBuilder("js", read("static/server.js"), "server.js").build();
            context.eval(serverSource);
            
            return context;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize GraalJS context. " +
                "Please ensure GraalVM JDK with JavaScript support is installed and set as JAVA_HOME.", e);
        }
    });

    public String render(String initialState, String request) {
        try {
            var context = contextHolder.get();
            var renderFunction = context.getBindings("js").getMember("render");
            if (renderFunction == null || !renderFunction.canExecute()) {
                throw new IllegalStateException("render function not found in JavaScript context");
            }
            var html = renderFunction.execute(initialState, request);
            return html.asString();
        } catch (Exception e) {
            throw new IllegalStateException("failed to render isomorphic component", e);
        }
    }

    private Reader read(String path) {
        var in = getClass().getClassLoader().getResourceAsStream(path);
        if (in == null) {
            throw new IllegalStateException("Resource not found: " + path);
        }
        return new InputStreamReader(in);
    }
}
