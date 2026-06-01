package com.castrapet.handler;

import com.castrapet.service.AuthService;
import com.castrapet.util.Json;
import com.sun.net.httpserver.HttpExchange;

import java.util.Map;

// /api/auth
public class AuthHandler extends BaseHandler {

    private final AuthService authService = new AuthService();

    @Override
    protected void rotear(HttpExchange ex) throws Exception {
        String[] seg = segmentos(ex, "/api/auth");
        String metodo = ex.getRequestMethod();

        // POST /api/auth/registrar
        if ("POST".equals(metodo) && seg.length == 1 && "registrar".equals(seg[0])) {
            Map<String, Object> body = lerJson(ex);
            Map<String, Object> resp = authService.registrar(
                    Json.getString(body, "nome"),
                    Json.getString(body, "email"),
                    Json.getString(body, "senha"),
                    Json.getString(body, "telefone")
            );
            enviarResposta(ex, 201, resp);

        // POST /api/auth/login
        } else if ("POST".equals(metodo) && seg.length == 1 && "login".equals(seg[0])) {
            Map<String, Object> body = lerJson(ex);
            Map<String, Object> resp = authService.login(
                    Json.getString(body, "email"),
                    Json.getString(body, "senha")
            );
            enviarResposta(ex, 200, resp);

        } else {
            enviarErro(ex, 404, "Endpoint não encontrado");
        }
    }
}
