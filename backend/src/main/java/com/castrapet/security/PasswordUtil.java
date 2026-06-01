package com.castrapet.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class PasswordUtil {

    // Gera hash SHA-256 com salt aleatório. Formato armazenado: "hash:salt"
    public static String hash(String senha) {
        String salt = UUID.randomUUID().toString().replace("-", "");
        String hash = sha256(salt + senha);
        return hash + ":" + salt;
    }

    // Verifica senha contra o hash armazenado no formato "hash:salt"
    public static boolean verificar(String senha, String hashArmazenado) {
        if (hashArmazenado == null || !hashArmazenado.contains(":")) return false;
        String[] partes = hashArmazenado.split(":", 2);
        String hashCalculado = sha256(partes[1] + senha);
        return hashCalculado.equals(partes[0]);
    }

    private static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 não disponível", e);
        }
    }
}
