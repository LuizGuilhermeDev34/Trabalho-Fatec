package com.castrapet.repository;

import com.castrapet.model.Role;
import com.castrapet.model.Usuario;
import com.castrapet.security.PasswordUtil;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class UsuarioRepository {

    private static final Map<Long, Usuario> banco = new ConcurrentHashMap<Long, Usuario>();
    private static final AtomicLong contador = new AtomicLong(2);

    static {
        Usuario admin = new Usuario();
        admin.setId(1L);
        admin.setNome("Administrador");
        admin.setEmail("admin@castrapet.com");
        admin.setSenha(PasswordUtil.hash("admin123"));
        admin.setRole(Role.ADMIN);
        admin.setAtivo(true);
        banco.put(1L, admin);
    }

    public Optional<Usuario> findByEmail(String email) {
        return banco.values().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    public Optional<Usuario> findById(long id) {
        return Optional.ofNullable(banco.get(id));
    }

    public boolean existsByEmail(String email) {
        return banco.values().stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
    }

    public Usuario save(Usuario u) {
        if (u.getId() == null) u.setId(contador.getAndIncrement());
        banco.put(u.getId(), u);
        return u;
    }
}
