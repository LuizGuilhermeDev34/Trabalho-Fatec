package com.castrapet.repository;

import com.castrapet.model.Pet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class PetRepository {

    private static final Map<Long, Pet> banco = new ConcurrentHashMap<Long, Pet>();
    private static final AtomicLong contador = new AtomicLong(1);

    public List<Pet> findByTutorId(long tutorId) {
        List<Pet> lista = new ArrayList<Pet>();
        for (Pet p : banco.values()) {
            if (p.getTutor() != null && p.getTutor().getId() == tutorId) lista.add(p);
        }
        return lista;
    }

    public Optional<Pet> findByIdAndTutorId(long id, long tutorId) {
        Pet p = banco.get(id);
        if (p != null && p.getTutor() != null && p.getTutor().getId() == tutorId)
            return Optional.of(p);
        return Optional.empty();
    }

    public Optional<Pet> findById(long id) {
        return Optional.ofNullable(banco.get(id));
    }

    public Pet save(Pet pet) {
        if (pet.getId() == null) pet.setId(contador.getAndIncrement());
        banco.put(pet.getId(), pet);
        return pet;
    }

    public void delete(long id) {
        banco.remove(id);
    }
}
