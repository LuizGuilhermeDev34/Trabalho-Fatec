package com.castrapet.service;

import com.castrapet.exception.BusinessException;
import com.castrapet.exception.NotFoundException;
import com.castrapet.model.Clinica;
import com.castrapet.model.Vaga;
import com.castrapet.repository.ClinicaRepository;
import com.castrapet.repository.VagaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ClinicaService {

    private final ClinicaRepository clinicaRepo = new ClinicaRepository();
    private final VagaRepository vagaRepo = new VagaRepository();

    public List<Map<String, Object>> listar() {
        return clinicaRepo.findByAtivaTrue().stream()
                .map(this::clinicaParaMap)
                .collect(Collectors.toList());
    }

    public Map<String, Object> buscarPorId(long id) {
        Clinica c = clinicaRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Clínica não encontrada"));
        return clinicaParaMap(c);
    }

    public List<Map<String, Object>> vagasDisponiveis(long clinicaId, LocalDate data) {
        return vagaRepo.findDisponiveisByClinicaAndData(clinicaId, data).stream()
                .map(this::vagaParaMap)
                .collect(Collectors.toList());
    }

    public Map<String, Object> cadastrarClinica(Map<String, Object> dados) {
        if (dados.get("nome") == null || dados.get("nome").toString().isBlank())
            throw new BusinessException("Nome da clínica é obrigatório");
        if (dados.get("endereco") == null || dados.get("endereco").toString().isBlank())
            throw new BusinessException("Endereço é obrigatório");

        Clinica cl = new Clinica();
        cl.setNome(dados.get("nome").toString());
        cl.setEndereco(dados.get("endereco").toString());
        cl.setCidade(dados.get("cidade") != null ? dados.get("cidade").toString() : null);
        cl.setEstado(dados.get("estado") != null ? dados.get("estado").toString() : null);
        cl.setCep(dados.get("cep") != null ? dados.get("cep").toString() : null);
        cl.setTelefone(dados.get("telefone") != null ? dados.get("telefone").toString() : null);
        cl.setEmail(dados.get("email") != null ? dados.get("email").toString() : null);
        cl.setAtiva(true);
        clinicaRepo.save(cl);
        return clinicaParaMap(cl);
    }

    public Map<String, Object> criarVaga(long clinicaId, Map<String, Object> dados) {
        Clinica clinica = clinicaRepo.findById(clinicaId)
                .orElseThrow(() -> new NotFoundException("Clínica não encontrada"));

        if (dados.get("data") == null) throw new BusinessException("Data é obrigatória");
        if (dados.get("horario") == null) throw new BusinessException("Horário é obrigatório");

        Vaga v = new Vaga();
        v.setClinica(clinica);
        v.setData(LocalDate.parse(dados.get("data").toString()));
        v.setHorario(LocalTime.parse(dados.get("horario").toString()));
        int cap = dados.get("capacidade") instanceof Number n ? n.intValue() : 1;
        v.setCapacidade(cap);
        v.setVagasOcupadas(0);
        vagaRepo.save(v);
        return vagaParaMap(v);
    }

    private Map<String, Object> clinicaParaMap(Clinica c) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", c.getId());
        m.put("nome", c.getNome());
        m.put("endereco", c.getEndereco());
        m.put("cidade", c.getCidade());
        m.put("estado", c.getEstado());
        m.put("cep", c.getCep());
        m.put("telefone", c.getTelefone());
        m.put("email", c.getEmail());
        m.put("ativa", c.isAtiva());
        return m;
    }

    private Map<String, Object> vagaParaMap(Vaga v) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", v.getId());
        m.put("data", v.getData().toString());
        m.put("horario", v.getHorario().toString());
        m.put("capacidade", v.getCapacidade());
        m.put("vagasRestantes", v.getVagasRestantes());
        if (v.getClinica() != null) m.put("clinicaId", v.getClinica().getId());
        return m;
    }
}
