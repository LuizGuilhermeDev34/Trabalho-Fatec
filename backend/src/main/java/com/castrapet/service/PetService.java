package com.castrapet.service;

import com.castrapet.exception.BusinessException;
import com.castrapet.exception.NotFoundException;
import com.castrapet.model.Especie;
import com.castrapet.model.Pet;
import com.castrapet.model.Usuario;
import com.castrapet.repository.PetRepository;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PetService {

    private final PetRepository petRepo = new PetRepository();

    public List<Map<String, Object>> listarMeusPets(Usuario tutor) {
        return petRepo.findByTutorId(tutor.getId()).stream()
                .map(this::petParaMap)
                .collect(Collectors.toList());
    }

    public Map<String, Object> buscarPorId(long id, Usuario tutor) {
        Pet pet = petRepo.findByIdAndTutorId(id, tutor.getId())
                .orElseThrow(() -> new NotFoundException("Pet não encontrado"));
        return petParaMap(pet);
    }

    public Map<String, Object> cadastrar(Map<String, Object> dados, Usuario tutor) {
        validarDadosPet(dados);
        Pet pet = new Pet();
        preencherPet(pet, dados);
        pet.setTutor(tutor);
        petRepo.save(pet);
        return petParaMap(pet);
    }

    public Map<String, Object> atualizar(long id, Map<String, Object> dados, Usuario tutor) {
        Pet pet = petRepo.findByIdAndTutorId(id, tutor.getId())
                .orElseThrow(() -> new NotFoundException("Pet não encontrado"));
        validarDadosPet(dados);
        preencherPet(pet, dados);
        petRepo.save(pet);
        return petParaMap(pet);
    }

    public void remover(long id, Usuario tutor) {
        petRepo.findByIdAndTutorId(id, tutor.getId())
                .orElseThrow(() -> new NotFoundException("Pet não encontrado"));
        petRepo.delete(id);
    }

    private void validarDadosPet(Map<String, Object> d) {
        if (d.get("nome") == null || d.get("nome").toString().isBlank())
            throw new BusinessException("Nome do pet é obrigatório");
        if (d.get("especie") == null)
            throw new BusinessException("Espécie é obrigatória (CACHORRO ou GATO)");
        if (d.get("sexo") == null || d.get("sexo").toString().isBlank())
            throw new BusinessException("Sexo é obrigatório");
    }

    private void preencherPet(Pet pet, Map<String, Object> d) {
        pet.setNome(d.get("nome").toString());
        pet.setEspecie(Especie.valueOf(d.get("especie").toString().toUpperCase()));
        pet.setRaca(d.get("raca") != null ? d.get("raca").toString() : null);
        pet.setSexo(d.get("sexo").toString().toUpperCase());
        if (d.get("dataNascimento") != null)
            pet.setDataNascimento(LocalDate.parse(d.get("dataNascimento").toString()));
        if (d.get("peso") != null && d.get("peso") instanceof Number n)
            pet.setPeso(n.doubleValue());
        if (d.get("castrado") instanceof Boolean b) pet.setCastrado(b);
        pet.setObservacoes(d.get("observacoes") != null ? d.get("observacoes").toString() : null);
    }

    public Map<String, Object> petParaMap(Pet pet) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", pet.getId());
        m.put("nome", pet.getNome());
        m.put("especie", pet.getEspecie().name());
        m.put("raca", pet.getRaca());
        m.put("sexo", pet.getSexo());
        m.put("dataNascimento", pet.getDataNascimento() != null ? pet.getDataNascimento().toString() : null);
        m.put("idadeEmMeses", pet.getIdadeEmMeses());
        m.put("peso", pet.getPeso());
        m.put("castrado", pet.isCastrado());
        m.put("observacoes", pet.getObservacoes());
        if (pet.getTutor() != null) {
            m.put("tutorId", pet.getTutor().getId());
            m.put("tutorNome", pet.getTutor().getNome());
        }
        return m;
    }
}
