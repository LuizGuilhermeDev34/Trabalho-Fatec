package com.castrapet.handler;

import com.castrapet.model.Role;
import com.castrapet.model.Usuario;
import com.castrapet.service.ClinicaService;
import com.sun.net.httpserver.HttpExchange;

import java.time.LocalDate;
import java.util.Map;

// /api/clinicas
public class ClinicaHandler extends BaseHandler {

    private final ClinicaService clinicaService = new ClinicaService();

    @Override
    protected void rotear(HttpExchange ex) throws Exception {
        String[] seg = segmentos(ex, "/api/clinicas");
        String metodo = ex.getRequestMethod();

        // GET /api/clinicas  — público (não exige login para ver clínicas)
        if ("GET".equals(metodo) && seg.length == 0) {
            enviarResposta(ex, 200, clinicaService.listar());

        // GET /api/clinicas/{id}
        } else if ("GET".equals(metodo) && seg.length == 1) {
            long id = Long.parseLong(seg[0]);
            enviarResposta(ex, 200, clinicaService.buscarPorId(id));

        // GET /api/clinicas/{id}/vagas?data=YYYY-MM-DD
        } else if ("GET".equals(metodo) && seg.length == 2 && "vagas".equals(seg[1])) {
            long id = Long.parseLong(seg[0]);
            String dataStr = queryParam(ex, "data");
            LocalDate data = dataStr != null ? LocalDate.parse(dataStr) : LocalDate.now();
            enviarResposta(ex, 200, clinicaService.vagasDisponiveis(id, data));

        // POST /api/clinicas  — apenas ADMIN
        } else if ("POST".equals(metodo) && seg.length == 0) {
            Usuario usuario = getUsuarioLogado(ex);
            exigirRole(usuario, Role.ADMIN);
            Map<String, Object> body = lerJson(ex);
            enviarResposta(ex, 201, clinicaService.cadastrarClinica(body));

        // POST /api/clinicas/{id}/vagas  — ADMIN ou VET
        } else if ("POST".equals(metodo) && seg.length == 2 && "vagas".equals(seg[1])) {
            Usuario usuario = getUsuarioLogado(ex);
            exigirRole(usuario, Role.ADMIN, Role.VET);
            long id = Long.parseLong(seg[0]);
            Map<String, Object> body = lerJson(ex);
            enviarResposta(ex, 201, clinicaService.criarVaga(id, body));

        } else {
            enviarErro(ex, 404, "Endpoint não encontrado");
        }
    }
}
