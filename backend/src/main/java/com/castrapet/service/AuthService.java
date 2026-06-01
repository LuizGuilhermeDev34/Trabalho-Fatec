package com.castrapet.service;

import com.castrapet.exception.BusinessException;
import com.castrapet.model.Role;
import com.castrapet.model.Usuario;
import com.castrapet.repository.UsuarioRepository;
import com.castrapet.security.PasswordUtil;
import com.castrapet.security.TokenManager;

import java.util.LinkedHashMap;
import java.util.Map;

public class AuthService {

    private final UsuarioRepository usuarioRepo = new UsuarioRepository();

    public Map<String, Object> registrar(String nome, String email, String senha, String telefone) {
        if (nome == null || nome.isBlank()) throw new BusinessException("Nome é obrigatório");
        if (email == null || email.isBlank()) throw new BusinessException("E-mail é obrigatório");
        if (senha == null || senha.length() < 6) throw new BusinessException("Senha deve ter ao menos 6 caracteres");

        if (usuarioRepo.existsByEmail(email)) {
            throw new BusinessException("E-mail já cadastrado: " + email);
        }

        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email.toLowerCase().trim());
        usuario.setSenha(PasswordUtil.hash(senha));
        usuario.setTelefone(telefone);
        usuario.setRole(Role.TUTOR);
        usuario.setAtivo(true);

        usuarioRepo.save(usuario);
        String token = TokenManager.criarToken(usuario.getId());
        return montarResposta(token, usuario);
    }

    public Map<String, Object> login(String email, String senha) {
        if (email == null || senha == null) throw new BusinessException("E-mail e senha são obrigatórios");

        Usuario usuario = usuarioRepo.findByEmail(email.toLowerCase().trim())
                .orElseThrow(() -> new BusinessException("E-mail ou senha inválidos"));

        if (!PasswordUtil.verificar(senha, usuario.getSenha())) {
            throw new BusinessException("E-mail ou senha inválidos");
        }

        if (!usuario.isAtivo()) throw new BusinessException("Conta desativada");

        String token = TokenManager.criarToken(usuario.getId());
        return montarResposta(token, usuario);
    }

    private Map<String, Object> montarResposta(String token, Usuario u) {
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("token", token);
        resp.put("tipo", "Bearer");
        resp.put("id", u.getId());
        resp.put("nome", u.getNome());
        resp.put("email", u.getEmail());
        resp.put("role", u.getRole().name());
        return resp;
    }
}
