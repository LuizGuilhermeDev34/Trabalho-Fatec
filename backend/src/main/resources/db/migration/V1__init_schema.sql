-- V1__init_schema.sql
-- CastraPet - Schema inicial

CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'TUTOR',
    telefone VARCHAR(20),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMP NOT NULL DEFAULT NOW(),
    atualizado_em TIMESTAMP
);

CREATE TABLE pets (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    especie VARCHAR(10) NOT NULL,        -- CACHORRO, GATO
    raca VARCHAR(100),
    sexo VARCHAR(10) NOT NULL,           -- MACHO, FEMEA
    data_nascimento DATE,
    peso DECIMAL(5,2),
    castrado BOOLEAN NOT NULL DEFAULT FALSE,
    observacoes TEXT,
    tutor_id BIGINT NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    criado_em TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE clinicas (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(200) NOT NULL,
    endereco VARCHAR(300) NOT NULL,
    cidade VARCHAR(100),
    estado VARCHAR(2),
    cep VARCHAR(10),
    telefone VARCHAR(20),
    email VARCHAR(255),
    ativa BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE vagas (
    id BIGSERIAL PRIMARY KEY,
    clinica_id BIGINT NOT NULL REFERENCES clinicas(id) ON DELETE CASCADE,
    data DATE NOT NULL,
    horario TIME NOT NULL,
    capacidade INTEGER NOT NULL DEFAULT 1,
    vagas_ocupadas INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT vagas_ocupadas_check CHECK (vagas_ocupadas <= capacidade),
    UNIQUE (clinica_id, data, horario)
);

CREATE TABLE agendamentos (
    id BIGSERIAL PRIMARY KEY,
    pet_id BIGINT NOT NULL REFERENCES pets(id),
    clinica_id BIGINT NOT NULL REFERENCES clinicas(id),
    vaga_id BIGINT REFERENCES vagas(id),
    data_hora TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDENTE',
    observacoes TEXT,
    criado_em TIMESTAMP NOT NULL DEFAULT NOW(),
    atualizado_em TIMESTAMP
);

-- Índices para performance
CREATE INDEX idx_pets_tutor ON pets(tutor_id);
CREATE INDEX idx_agendamentos_pet ON agendamentos(pet_id);
CREATE INDEX idx_agendamentos_clinica ON agendamentos(clinica_id);
CREATE INDEX idx_agendamentos_status ON agendamentos(status);
CREATE INDEX idx_vagas_clinica_data ON vagas(clinica_id, data);

-- Admin padrão (senha: Admin@123)
INSERT INTO usuarios (nome, email, senha, role)
VALUES ('Administrador', 'admin@castrapet.com',
        '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKVA7nSeduzKRqvEqAeKJnmRIq2i', 'ADMIN');

-- Clínica de exemplo
INSERT INTO clinicas (nome, endereco, cidade, estado, cep, telefone, email)
VALUES ('Clínica Pet Saúde', 'Rua das Flores, 123', 'São Paulo', 'SP', '01310-100', '(11) 3000-0000', 'contato@petsaude.com');

-- Vagas de exemplo para os próximos 7 dias
INSERT INTO vagas (clinica_id, data, horario, capacidade)
SELECT 1, CURRENT_DATE + s.day, t.horario::time, 3
FROM generate_series(1, 7) AS s(day),
     (VALUES ('08:00'), ('09:00'), ('10:00'), ('14:00'), ('15:00'), ('16:00')) AS t(horario);
