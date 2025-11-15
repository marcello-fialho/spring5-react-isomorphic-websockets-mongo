package com.wonderprints.isomorphic.react.renderer;

import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.concurrent.Executors;

public class React {

    private ThreadLocal<ScriptEngine> engineHolder = ThreadLocal.withInitial(() -> {
        var scriptEngine = new ScriptEngineManager().getEngineByName("graal.js");
        if (scriptEngine == null) {
            throw new IllegalStateException("GraalJS engine not found. " +
                "Please install GraalVM JDK and set it as your JAVA_HOME. " +
                "Download from: https://www.graalvm.org/downloads/");
        }
        var globalScheduledThreadPool = Executors.newScheduledThreadPool(20);
        var ctx = new SimpleScriptContext();
        ctx.setAttribute("__NASHORN_POLYFILL_TIMER__", globalScheduledThreadPool, ScriptContext.ENGINE_SCOPE);
        ctx.setAttribute("__HTTP_SERVLET_REQUEST__", globalScheduledThreadPool, ScriptContext.ENGINE_SCOPE);
        scriptEngine.setContext(ctx);
        try {
            scriptEngine.eval(read("static/nashorn-polyfill.js"));
            scriptEngine.eval(read("static/server.js"));
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
        return scriptEngine;
    });

    public String render(String initialState, String request) {
        try {
            var engine = engineHolder.get();
            var html = ((Invocable) engine).invokeFunction("render", initialState, request);
            return String.valueOf(html);
        }
        catch (Exception e) {
            throw new IllegalStateException("failed to render isomorphic component", e);
        }
    }

    private Reader read(String path) {
        var in = getClass().getClassLoader().getResourceAsStream(path);
        return new InputStreamReader(in);
    }
}
