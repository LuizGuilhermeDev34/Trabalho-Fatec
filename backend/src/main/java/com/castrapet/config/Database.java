package com.castrapet.config;

import com.castrapet.security.PasswordUtil;

import java.sql.*;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class Database {

    private static final String URL = System.getenv().getOrDefault(
            "DB_URL", "jdbc:postgresql://localhost:5432/castrapet");
    private static final String USER = System.getenv().getOrDefault("DB_USER", "postgres");
    private static final String PASSWORD = System.getenv().getOrDefault("DB_PASSWORD", "castrapet@2024");

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void inicializar() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            InputStream is = Database.class.getResourceAsStream("/schema.sql");
            if (is == null) {
                System.err.println("[DB] schema.sql não encontrado no classpath");
                return;
            }
            String sql = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            // Executa cada statement separado por ponto-e-vírgula
            for (String s : sql.split(";")) {
                String trimmed = s.trim();
                if (!trimmed.isEmpty()) {
                    try {
                        stmt.execute(trimmed);
                    } catch (SQLException e) {
                        // Ignora erros de "já existe" (tabelas/índices duplicados)
                        if (!e.getMessage().contains("already exists")
                                && !e.getMessage().contains("já existe")) {
                            System.err.println("[DB] Aviso SQL: " + e.getMessage());
                        }
                    }
                }
            }
            // Inicializa a senha do admin com hash real via PasswordUtil
            inicializarSenhaAdmin(conn);

            System.out.println("[DB] Banco de dados inicializado com sucesso.");
        } catch (Exception e) {
            System.err.println("[DB] Erro ao inicializar banco: " + e.getMessage());
            throw new RuntimeException("Falha na inicialização do banco de dados", e);
        }
    }

    private static void inicializarSenhaAdmin(Connection conn) throws SQLException {
        String sql = "SELECT senha FROM usuarios WHERE email = 'admin@castrapet.com'";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                String senhaAtual = rs.getString("senha");
                // Placeholder gerado no SQL começa com zeros — substitui pelo hash real
                if (senhaAtual != null && senhaAtual.startsWith("00000000000000000000")) {
                    String novoHash = PasswordUtil.hash("admin123");
                    try (PreparedStatement ps = conn.prepareStatement(
                            "UPDATE usuarios SET senha = ? WHERE email = 'admin@castrapet.com'")) {
                        ps.setString(1, novoHash);
                        ps.executeUpdate();
                    }
                    System.out.println("[DB] Admin: admin@castrapet.com / senha: admin123");
                }
            }
        }
    }
}
