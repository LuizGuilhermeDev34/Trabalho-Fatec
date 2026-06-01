package com.castrapet.handler;

import com.castrapet.exception.BusinessException;
import com.castrapet.exception.NotFoundException;
import com.castrapet.exception.UnauthorizedException;
import com.castrapet.model.Role;
import com.castrapet.model.Usuario;
import com.castrapet.repository.UsuarioRepository;
import com.castrapet.security.TokenManager;
import com.castrapet.util.Json;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public abstract class BaseHandler implements HttpHandler {

    private final UsuarioRepository usuarioRepo = new UsuarioRepository();

    @Override
    public final void handle(HttpExchange ex) throws IOException {
        try {
            configurarCors(ex);
            if ("OPTIONS".equalsIgnoreCase(ex.getRequestMethod())) {
                enviarResposta(ex, 204, "");
                return;
            }
            rotear(ex);
        } catch (UnauthorizedException e) {
            enviarErro(ex, 401, e.getMessage());
        } catch (NotFoundException e) {
            enviarErro(ex, 404, e.getMessage());
        } catch (BusinessException e) {
            enviarErro(ex, 400, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            enviarErro(ex, 500, "Erro interno do servidor");
        }
    }

    protected abstract void rotear(HttpExchange ex) throws Exception;

    // ── Autenticação ──────────────────────────────────────────────────────────

    protected Usuario getUsuarioLogado(HttpExchange ex) {
        String auth = ex.getRequestHeaders().getFirst("Authorization");
        if (auth == null || !auth.startsWith("Bearer "))
            throw new UnauthorizedException("Token não fornecido");
        String token = auth.substring(7).trim();
        Long userId = TokenManager.validarToken(token);
        if (userId == null) throw new UnauthorizedException("Token inválido ou expirado");
        return usuarioRepo.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("Usuário não encontrado"));
    }

    protected void exigirRole(Usuario usuario, Role... roles) {
        for (Role r : roles) {
            if (usuario.getRole() == r) return;
        }
        throw new BusinessException("Acesso não autorizado para este recurso");
    }

    // ── HTTP helpers ──────────────────────────────────────────────────────────

    protected String lerCorpo(HttpExchange ex) throws IOException {
        return new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    protected Map<String, Object> lerJson(HttpExchange ex) throws IOException {
        return Json.parseObject(lerCorpo(ex));
    }

    protected void enviarResposta(HttpExchange ex, int status, Object body) throws IOException {
        String json = (body instanceof String s) ? s : Json.toJson(body);
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        ex.sendResponseHeaders(status, bytes.length);
        ex.getResponseBody().write(bytes);
        ex.getResponseBody().close();
    }

    protected void enviarErro(HttpExchange ex, int status, String mensagem) throws IOException {
        enviarResposta(ex, status, Map.of("erro", mensagem, "status", status));
    }

    // ── URL helpers ───────────────────────────────────────────────────────────

    protected String[] segmentos(HttpExchange ex, String prefixo) {
        String path = ex.getRequestURI().getPath();
        String resto = path.substring(prefixo.length());
        if (resto.startsWith("/")) resto = resto.substring(1);
        if (resto.isBlank()) return new String[0];
        return resto.split("/");
    }

    protected String queryParam(HttpExchange ex, String nome) {
        String query = ex.getRequestURI().getQuery();
        if (query == null) return null;
        for (String par : query.split("&")) {
            String[] kv = par.split("=", 2);
            if (kv.length == 2 && kv[0].equals(nome)) return kv[1];
        }
        return null;
    }

    protected int queryParamInt(HttpExchange ex, String nome, int padrao) {
        String v = queryParam(ex, nome);
        if (v == null) return padrao;
        try { return Integer.parseInt(v); } catch (NumberFormatException e) { return padrao; }
    }

    private void configurarCors(HttpExchange ex) {
        ex.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        ex.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        ex.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }
}
