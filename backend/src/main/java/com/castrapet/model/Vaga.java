package com.castrapet.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Vaga {

    private Long id;
    private Clinica clinica;
    private LocalDate data;
    private LocalTime horario;
    private int capacidade;
    private int vagasOcupadas;

    public Vaga() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Clinica getClinica() { return clinica; }
    public void setClinica(Clinica clinica) { this.clinica = clinica; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public LocalTime getHorario() { return horario; }
    public void setHorario(LocalTime horario) { this.horario = horario; }

    public int getCapacidade() { return capacidade; }
    public void setCapacidade(int capacidade) { this.capacidade = capacidade; }

    public int getVagasOcupadas() { return vagasOcupadas; }
    public void setVagasOcupadas(int vagasOcupadas) { this.vagasOcupadas = vagasOcupadas; }

    public boolean isDisponivel() { return vagasOcupadas < capacidade; }

    public int getVagasRestantes() { return capacidade - vagasOcupadas; }
}
