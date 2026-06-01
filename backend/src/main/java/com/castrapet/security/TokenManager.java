package com.castrapet.security;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TokenManager {

    private static final Map<String, Sessao> sessoes = new ConcurrentHashMap<>();

    private static class Sessao {
        final long usuarioId;
        final LocalDateTime expiracao;

        Sessao(long usuarioId) {
            this.usuarioId = usuarioId;
            this.expiracao = LocalDateTime.now().plusHours(24);
        }
    }

    public static String criarToken(long usuarioId) {
        String token = UUID.randomUUID().toString();
        sessoes.put(token, new Sessao(usuarioId));
        return token;
    }

    // Retorna o ID do usuário se token válido, ou null se inválido/expirado
    public static Long validarToken(String token) {
        if (token == null) return null;
        Sessao sessao = sessoes.get(token);
        if (sessao == null) return null;
        if (LocalDateTime.now().isAfter(sessao.expiracao)) {
            sessoes.remove(token);
            return null;
        }
        return sessao.usuarioId;
    }

    public static void removerToken(String token) {
        if (token != null) sessoes.remove(token);
    }
}
