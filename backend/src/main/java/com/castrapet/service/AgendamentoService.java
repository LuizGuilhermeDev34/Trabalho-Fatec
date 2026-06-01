package com.castrapet.service;

import com.castrapet.exception.BusinessException;
import com.castrapet.exception.NotFoundException;
import com.castrapet.model.*;
import com.castrapet.repository.*;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepo = new AgendamentoRepository();
    private final PetRepository petRepo = new PetRepository();
    private final ClinicaRepository clinicaRepo = new ClinicaRepository();
    private final VagaRepository vagaRepo = new VagaRepository();

    public List<Map<String, Object>> listarMeusAgendamentos(Usuario tutor) {
        return agendamentoRepo.findByTutorId(tutor.getId()).stream()
                .map(this::agendamentoParaMap)
                .collect(Collectors.toList());
    }

    public Map<String, Object> buscarPorId(long id, Usuario usuario) {
        Agendamento a = agendamentoRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Agendamento não encontrado"));
        if (usuario.getRole() == Role.TUTOR &&
                !a.getPet().getTutor().getId().equals(usuario.getId())) {
            throw new BusinessException("Acesso negado a este agendamento");
        }
        return agendamentoParaMap(a);
    }

    public Map<String, Object> agendar(Map<String, Object> dados, Usuario tutor) {
        Long petId = toLong(dados.get("petId"));
        Long clinicaId = toLong(dados.get("clinicaId"));
        Long vagaId = toLong(dados.get("vagaId"));
        String dataHoraStr = str(dados.get("dataHora"));

        if (petId == null) throw new BusinessException("petId é obrigatório");
        if (clinicaId == null) throw new BusinessException("clinicaId é obrigatório");
        if (vagaId == null) throw new BusinessException("vagaId é obrigatório");
        if (dataHoraStr == null) throw new BusinessException("dataHora é obrigatória");

        Pet pet = petRepo.findByIdAndTutorId(petId, tutor.getId())
                .orElseThrow(() -> new NotFoundException("Pet não encontrado ou não pertence ao tutor"));

        if (pet.isCastrado())
            throw new BusinessException("Pet '" + pet.getNome() + "' já é castrado");

        if (agendamentoRepo.countAgendamentosAtivosByPet(pet.getId()) > 0)
            throw new BusinessException("Pet '" + pet.getNome() + "' já possui um agendamento ativo");

        Clinica clinica = clinicaRepo.findById(clinicaId)
                .orElseThrow(() -> new NotFoundException("Clínica não encontrada"));

        if (!clinica.isAtiva()) throw new BusinessException("Clínica não está ativa");

        Vaga vaga = vagaRepo.findById(vagaId)
                .orElseThrow(() -> new NotFoundException("Vaga não encontrada"));

        if (!vaga.isDisponivel())
            throw new BusinessException("Vaga sem disponibilidade. Escolha outro horário.");

        vaga.setVagasOcupadas(vaga.getVagasOcupadas() + 1);
        vagaRepo.save(vaga);

        Agendamento ag = new Agendamento();
        ag.setPet(pet);
        ag.setClinica(clinica);
        ag.setVaga(vaga);
        ag.setDataHora(LocalDateTime.parse(dataHoraStr));
        ag.setStatus(StatusAgendamento.PENDENTE);
        ag.setObservacoes(str(dados.get("observacoes")));
        agendamentoRepo.save(ag);
        return agendamentoParaMap(ag);
    }

    public Map<String, Object> cancelar(long id, Usuario usuario) {
        Agendamento ag = agendamentoRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Agendamento não encontrado"));

        if (usuario.getRole() == Role.TUTOR &&
                !ag.getPet().getTutor().getId().equals(usuario.getId()))
            throw new BusinessException("Acesso negado");

        if (ag.getStatus() == StatusAgendamento.CANCELADO)
            throw new BusinessException("Agendamento já está cancelado");
        if (ag.getStatus() == StatusAgendamento.REALIZADO)
            throw new BusinessException("Não é possível cancelar um agendamento já realizado");

        if (ag.getVaga() != null) {
            Vaga vaga = ag.getVaga();
            vaga.setVagasOcupadas(Math.max(0, vaga.getVagasOcupadas() - 1));
            vagaRepo.save(vaga);
        }

        ag.setStatus(StatusAgendamento.CANCELADO);
        agendamentoRepo.save(ag);
        return agendamentoParaMap(ag);
    }

    public Map<String, Object> atualizarStatus(long id, Map<String, Object> dados) {
        Agendamento ag = agendamentoRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Agendamento não encontrado"));

        if (dados.get("status") == null) throw new BusinessException("Status é obrigatório");

        StatusAgendamento novoStatus = StatusAgendamento.valueOf(dados.get("status").toString().toUpperCase());
        ag.setStatus(novoStatus);
        if (dados.get("observacoes") != null) ag.setObservacoes(str(dados.get("observacoes")));

        if (novoStatus == StatusAgendamento.REALIZADO) {
            Pet pet = ag.getPet();
            pet.setCastrado(true);
            petRepo.save(pet);
        }

        agendamentoRepo.save(ag);
        return agendamentoParaMap(ag);
    }

    public List<Map<String, Object>> listarTodos(int page, int size) {
        return agendamentoRepo.findAll(page, size).stream()
                .map(this::agendamentoParaMap)
                .collect(Collectors.toList());
    }

    public Map<String, Object> estatisticas() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("pendentes", agendamentoRepo.countByStatus(StatusAgendamento.PENDENTE));
        m.put("confirmados", agendamentoRepo.countByStatus(StatusAgendamento.CONFIRMADO));
        m.put("realizados", agendamentoRepo.countByStatus(StatusAgendamento.REALIZADO));
        m.put("cancelados", agendamentoRepo.countByStatus(StatusAgendamento.CANCELADO));
        m.put("total", agendamentoRepo.count());
        return m;
    }

    public Map<String, Object> agendamentoParaMap(Agendamento a) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", a.getId());
        if (a.getPet() != null) {
            m.put("petId", a.getPet().getId());
            m.put("petNome", a.getPet().getNome());
            m.put("petEspecie", a.getPet().getEspecie().name());
            if (a.getPet().getTutor() != null) {
                m.put("tutorId", a.getPet().getTutor().getId());
                m.put("tutorNome", a.getPet().getTutor().getNome());
                m.put("tutorTelefone", a.getPet().getTutor().getTelefone());
            }
        }
        if (a.getClinica() != null) {
            m.put("clinicaId", a.getClinica().getId());
            m.put("clinicaNome", a.getClinica().getNome());
        }
        m.put("dataHora", a.getDataHora() != null ? a.getDataHora().toString() : null);
        m.put("status", a.getStatus() != null ? a.getStatus().name() : null);
        m.put("observacoes", a.getObservacoes());
        m.put("criadoEm", a.getCriadoEm() != null ? a.getCriadoEm().toString() : null);
        return m;
    }

    private Long toLong(Object v) {
        if (v == null) return null;
        if (v instanceof Number n) return n.longValue();
        try { return Long.parseLong(v.toString()); } catch (Exception e) { return null; }
    }

    private String str(Object v) { return v == null ? null : v.toString(); }
}
