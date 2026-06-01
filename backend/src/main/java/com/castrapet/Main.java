package com.castrapet;

import com.castrapet.handler.*;
import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) throws Exception {
        // Railway usa PORT, localmente usa SERVER_PORT, padrao 8080
        String portaEnv = System.getenv("PORT") != null ? System.getenv("PORT")
                        : System.getenv().getOrDefault("SERVER_PORT", "8080");
        int porta = Integer.parseInt(portaEnv);

        HttpServer server = HttpServer.create(new InetSocketAddress(porta), 0);

        server.createContext("/api/auth", new AuthHandler());
        server.createContext("/api/pets", new PetHandler());
        server.createContext("/api/clinicas", new ClinicaHandler());
        server.createContext("/api/agendamentos", new AgendamentoHandler());
        server.createContext("/", new StaticHandler());

        server.setExecutor(Executors.newFixedThreadPool(10));
        server.start();

        System.out.println("[CastraPet] Servidor rodando na porta " + porta);
        System.out.println("[CastraPet] Acesse: http://localhost:" + porta);
        System.out.println("[CastraPet] Login: admin@castrapet.com / admin123");
    }
}
