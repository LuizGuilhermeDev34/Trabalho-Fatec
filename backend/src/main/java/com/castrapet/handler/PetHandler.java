package com.castrapet.handler;

import com.castrapet.model.Usuario;
import com.castrapet.service.PetService;
import com.sun.net.httpserver.HttpExchange;

import java.util.List;
import java.util.Map;

// /api/pets
public class PetHandler extends BaseHandler {

    private final PetService petService = new PetService();

    @Override
    protected void rotear(HttpExchange ex) throws Exception {
        Usuario usuario = getUsuarioLogado(ex);
        String[] seg = segmentos(ex, "/api/pets");
        String metodo = ex.getRequestMethod();

        // GET /api/pets
        if ("GET".equals(metodo) && seg.length == 0) {
            List<Map<String, Object>> pets = petService.listarMeusPets(usuario);
            enviarResposta(ex, 200, pets);

        // GET /api/pets/{id}
        } else if ("GET".equals(metodo) && seg.length == 1) {
            long id = Long.parseLong(seg[0]);
            enviarResposta(ex, 200, petService.buscarPorId(id, usuario));

        // POST /api/pets
        } else if ("POST".equals(metodo) && seg.length == 0) {
            Map<String, Object> body = lerJson(ex);
            enviarResposta(ex, 201, petService.cadastrar(body, usuario));

        // PUT /api/pets/{id}
        } else if ("PUT".equals(metodo) && seg.length == 1) {
            long id = Long.parseLong(seg[0]);
            Map<String, Object> body = lerJson(ex);
            enviarResposta(ex, 200, petService.atualizar(id, body, usuario));

        // DELETE /api/pets/{id}
        } else if ("DELETE".equals(metodo) && seg.length == 1) {
            long id = Long.parseLong(seg[0]);
            petService.remover(id, usuario);
            enviarResposta(ex, 204, "");

        } else {
            enviarErro(ex, 404, "Endpoint não encontrado");
        }
    }
}
