package com.castrapet.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

// Serve arquivos HTML/CSS/JS do classpath /static/
public class StaticHandler implements HttpHandler {

    private static final Map<String, String> MIME = Map.of(
            ".html", "text/html; charset=UTF-8",
            ".css",  "text/css",
            ".js",   "application/javascript",
            ".png",  "image/png",
            ".jpg",  "image/jpeg"
    );

    @Override
    public void handle(HttpExchange ex) throws IOException {
        String path = ex.getRequestURI().getPath();

        if ("/".equals(path) || path.isEmpty()) path = "/index.html";

        // Bloqueia acesso a diretórios fora de /static/
        String recurso = "/static" + path;
        InputStream is = StaticHandler.class.getResourceAsStream(recurso);

        if (is == null) {
            // SPA fallback: qualquer rota desconhecida serve index.html
            is = StaticHandler.class.getResourceAsStream("/static/index.html");
            if (is == null) {
                byte[] msg = "404 Not Found".getBytes(StandardCharsets.UTF_8);
                ex.sendResponseHeaders(404, msg.length);
                ex.getResponseBody().write(msg);
                ex.getResponseBody().close();
                return;
            }
            path = "/index.html";
        }

        String ext = path.contains(".") ? path.substring(path.lastIndexOf('.')) : "";
        String mime = MIME.getOrDefault(ext, "application/octet-stream");

        byte[] bytes = is.readAllBytes();
        ex.getResponseHeaders().set("Content-Type", mime);
        ex.sendResponseHeaders(200, bytes.length);
        ex.getResponseBody().write(bytes);
        ex.getResponseBody().close();
    }
}
