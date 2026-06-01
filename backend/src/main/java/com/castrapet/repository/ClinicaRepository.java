package com.castrapet.repository;

import com.castrapet.model.Clinica;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class ClinicaRepository {

    private static final Map<Long, Clinica> banco = new ConcurrentHashMap<Long, Clinica>();
    private static final AtomicLong contador = new AtomicLong(3);

    static {
        Clinica c1 = new Clinica();
        c1.setId(1L); c1.setNome("Clínica Pet Saúde");
        c1.setEndereco("Rua das Flores, 123"); c1.setCidade("São Paulo");
        c1.setEstado("SP"); c1.setCep("01310-100");
        c1.setTelefone("(11) 3000-0000"); c1.setEmail("contato@petsaude.com");
        c1.setAtiva(true);
        banco.put(1L, c1);

        Clinica c2 = new Clinica();
        c2.setId(2L); c2.setNome("VetCare Animal");
        c2.setEndereco("Av. Paulista, 900"); c2.setCidade("São Paulo");
        c2.setEstado("SP"); c2.setTelefone("(11) 4000-1111");
        c2.setEmail("atendimento@vetcare.com"); c2.setAtiva(true);
        banco.put(2L, c2);
    }

    public List<Clinica> findByAtivaTrue() {
        List<Clinica> lista = new ArrayList<Clinica>();
        for (Clinica c : banco.values()) {
            if (c.isAtiva()) lista.add(c);
        }
        return lista;
    }

    public Optional<Clinica> findById(long id) {
        return Optional.ofNullable(banco.get(id));
    }

    public Clinica save(Clinica cl) {
        if (cl.getId() == null) cl.setId(contador.getAndIncrement());
        banco.put(cl.getId(), cl);
        return cl;
    }
}
