package com.wonderprints.isomorphic.react.renderer;

import org.openjdk.nashorn.api.scripting.NashornScriptEngine;
import javax.script.ScriptContext;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.concurrent.Executors;

public class React {

    private ThreadLocal<NashornScriptEngine> engineHolder = ThreadLocal.withInitial(() -> {
        var nashornScriptEngine = (NashornScriptEngine) new ScriptEngineManager().getEngineByName("nashorn");
        var globalScheduledThreadPool = Executors.newScheduledThreadPool(20);
        var ctx = new SimpleScriptContext();
        ctx.setAttribute("__NASHORN_POLYFILL_TIMER__", globalScheduledThreadPool, ScriptContext.ENGINE_SCOPE);
        ctx.setAttribute("__HTTP_SERVLET_REQUEST__", globalScheduledThreadPool, ScriptContext.ENGINE_SCOPE);
        nashornScriptEngine.setContext(ctx);
        try {
            nashornScriptEngine.eval(read("static/nashorn-polyfill.js"));
            nashornScriptEngine.eval(read("static/server.js"));
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
        return nashornScriptEngine;
    });

    public String render(String initialState, String request) {
        try {
            var html = engineHolder.get().invokeFunction("render", initialState, request);
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
