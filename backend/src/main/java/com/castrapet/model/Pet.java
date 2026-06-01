package com.castrapet.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

public class Pet {

    private Long id;
    private String nome;
    private Especie especie;
    private String raca;
    private String sexo;
    private LocalDate dataNascimento;
    private Double peso;
    private boolean castrado;
    private String observacoes;
    private Usuario tutor;
    private LocalDateTime criadoEm;

    public Pet() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Especie getEspecie() { return especie; }
    public void setEspecie(Especie especie) { this.especie = especie; }

    public String getRaca() { return raca; }
    public void setRaca(String raca) { this.raca = raca; }

    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }

    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }

    public Double getPeso() { return peso; }
    public void setPeso(Double peso) { this.peso = peso; }

    public boolean isCastrado() { return castrado; }
    public void setCastrado(boolean castrado) { this.castrado = castrado; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public Usuario getTutor() { return tutor; }
    public void setTutor(Usuario tutor) { this.tutor = tutor; }

    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }

    public Integer getIdadeEmMeses() {
        if (dataNascimento == null) return null;
        return (int) Period.between(dataNascimento, LocalDate.now()).toTotalMonths();
    }
}
