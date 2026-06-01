package com.castrapet.repository;

import com.castrapet.model.Clinica;
import com.castrapet.model.Vaga;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class VagaRepository {

    private static final Map<Long, Vaga> banco   = new ConcurrentHashMap<Long, Vaga>();
    private static final AtomicLong      contador = new AtomicLong(1);

    private static final String[] HORARIOS = {
        "07:00", "08:00", "09:00", "10:00", "11:00",
        "13:00", "14:00", "15:00", "16:00", "17:00"
    };

    static {
        ClinicaRepository clinicaRepo = new ClinicaRepository();
        long[] clinicaIds = {1L, 2L};

        for (long cid : clinicaIds) {
            Clinica cl = clinicaRepo.findById(cid).orElse(null);
            for (int dia = 1; dia <= 90; dia++) {
                LocalDate data = LocalDate.now().plusDays(dia);
                if (data.getDayOfWeek().getValue() == 7) continue; // pula domingo
                for (String h : HORARIOS) {
                    Vaga v = new Vaga();
                    v.setId(contador.getAndIncrement());
                    v.setClinica(cl);
                    v.setData(data);
                    v.setHorario(LocalTime.parse(h));
                    v.setCapacidade(3);
                    v.setVagasOcupadas(0);
                    banco.put(v.getId(), v);
                }
            }
        }
    }

    public List<Vaga> findDisponiveisByClinicaAndData(long clinicaId, LocalDate data) {
        List<Vaga> lista = new ArrayList<Vaga>();
        for (Vaga v : banco.values()) {
            if (v.getClinica() != null
                    && v.getClinica().getId() == clinicaId
                    && v.getData().equals(data)
                    && v.isDisponivel()) {
                lista.add(v);
            }
        }
        lista.sort((a, b) -> a.getHorario().compareTo(b.getHorario()));
        return lista;
    }

    public Optional<Vaga> findById(long id) {
        return Optional.ofNullable(banco.get(id));
    }

    public Vaga save(Vaga v) {
        if (v.getId() == null) v.setId(contador.getAndIncrement());
        banco.put(v.getId(), v);
        return v;
    }
}
