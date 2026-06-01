package com.castrapet.repository;

import com.castrapet.model.Agendamento;
import com.castrapet.model.StatusAgendamento;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class AgendamentoRepository {

    private static final Map<Long, Agendamento> banco = new ConcurrentHashMap<Long, Agendamento>();
    private static final AtomicLong contador = new AtomicLong(1);

    public List<Agendamento> findByTutorId(long tutorId) {
        List<Agendamento> lista = new ArrayList<Agendamento>();
        for (Agendamento a : banco.values()) {
            if (a.getPet() != null && a.getPet().getTutor() != null
                    && a.getPet().getTutor().getId() == tutorId) {
                lista.add(a);
            }
        }
        lista.sort((x, y) -> y.getDataHora().compareTo(x.getDataHora()));
        return lista;
    }

    public Optional<Agendamento> findById(long id) {
        return Optional.ofNullable(banco.get(id));
    }

    public List<Agendamento> findAll(int page, int size) {
        List<Agendamento> todos = new ArrayList<Agendamento>(banco.values());
        todos.sort((x, y) -> y.getDataHora().compareTo(x.getDataHora()));
        int inicio = page * size;
        int fim = Math.min(inicio + size, todos.size());
        if (inicio >= todos.size()) return new ArrayList<Agendamento>();
        return todos.subList(inicio, fim);
    }

    public long countByStatus(StatusAgendamento status) {
        return banco.values().stream().filter(a -> a.getStatus() == status).count();
    }

    public long count() {
        return banco.size();
    }

    public long countAgendamentosAtivosByPet(long petId) {
        return banco.values().stream()
                .filter(a -> a.getPet() != null
                        && a.getPet().getId() == petId
                        && a.getStatus() != StatusAgendamento.CANCELADO)
                .count();
    }

    public Agendamento save(Agendamento a) {
        if (a.getId() == null) a.setId(contador.getAndIncrement());
        banco.put(a.getId(), a);
        return a;
    }
}
