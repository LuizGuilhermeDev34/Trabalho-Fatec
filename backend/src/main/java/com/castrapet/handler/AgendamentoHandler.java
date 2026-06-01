package com.castrapet.handler;

import com.castrapet.model.Role;
import com.castrapet.model.Usuario;
import com.castrapet.service.AgendamentoService;
import com.sun.net.httpserver.HttpExchange;

import java.util.Map;

// /api/agendamentos
public class AgendamentoHandler extends BaseHandler {

    private final AgendamentoService agService = new AgendamentoService();

    @Override
    protected void rotear(HttpExchange ex) throws Exception {
        Usuario usuario = getUsuarioLogado(ex);
        String[] seg = segmentos(ex, "/api/agendamentos");
        String metodo = ex.getRequestMethod();

        // GET /api/agendamentos/admin/estatisticas
        if ("GET".equals(metodo) && seg.length == 2
                && "admin".equals(seg[0]) && "estatisticas".equals(seg[1])) {
            exigirRole(usuario, Role.ADMIN, Role.VET);
            enviarResposta(ex, 200, agService.estatisticas());

        // GET /api/agendamentos/admin/todos?page=0&size=20
        } else if ("GET".equals(metodo) && seg.length == 2
                && "admin".equals(seg[0]) && "todos".equals(seg[1])) {
            exigirRole(usuario, Role.ADMIN, Role.VET);
            int page = queryParamInt(ex, "page", 0);
            int size = queryParamInt(ex, "size", 20);
            enviarResposta(ex, 200, agService.listarTodos(page, size));

        // GET /api/agendamentos
        } else if ("GET".equals(metodo) && seg.length == 0) {
            enviarResposta(ex, 200, agService.listarMeusAgendamentos(usuario));

        // GET /api/agendamentos/{id}
        } else if ("GET".equals(metodo) && seg.length == 1) {
            long id = Long.parseLong(seg[0]);
            enviarResposta(ex, 200, agService.buscarPorId(id, usuario));

        // POST /api/agendamentos
        } else if ("POST".equals(metodo) && seg.length == 0) {
            Map<String, Object> body = lerJson(ex);
            enviarResposta(ex, 201, agService.agendar(body, usuario));

        // PUT /api/agendamentos/{id}/cancelar
        } else if ("PUT".equals(metodo) && seg.length == 2 && "cancelar".equals(seg[1])) {
            long id = Long.parseLong(seg[0]);
            enviarResposta(ex, 200, agService.cancelar(id, usuario));

        // PUT /api/agendamentos/{id}/status  — ADMIN ou VET
        } else if ("PUT".equals(metodo) && seg.length == 2 && "status".equals(seg[1])) {
            exigirRole(usuario, Role.ADMIN, Role.VET);
            long id = Long.parseLong(seg[0]);
            Map<String, Object> body = lerJson(ex);
            enviarResposta(ex, 200, agService.atualizarStatus(id, body));

        } else {
            enviarErro(ex, 404, "Endpoint não encontrado");
        }
    }
}
