-- CastraPet - Schema Java Puro (JDBC)

CREATE TABLE IF NOT EXISTS usuarios (
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

CREATE TABLE IF NOT EXISTS pets (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    especie VARCHAR(10) NOT NULL,
    raca VARCHAR(100),
    sexo VARCHAR(10) NOT NULL,
    data_nascimento DATE,
    peso DECIMAL(5,2),
    castrado BOOLEAN NOT NULL DEFAULT FALSE,
    observacoes TEXT,
    tutor_id BIGINT NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    criado_em TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS clinicas (
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

CREATE TABLE IF NOT EXISTS vagas (
    id BIGSERIAL PRIMARY KEY,
    clinica_id BIGINT NOT NULL REFERENCES clinicas(id) ON DELETE CASCADE,
    data DATE NOT NULL,
    horario TIME NOT NULL,
    capacidade INTEGER NOT NULL DEFAULT 1,
    vagas_ocupadas INTEGER NOT NULL DEFAULT 0,
    UNIQUE (clinica_id, data, horario)
);

CREATE TABLE IF NOT EXISTS agendamentos (
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

CREATE INDEX IF NOT EXISTS idx_pets_tutor ON pets(tutor_id);
CREATE INDEX IF NOT EXISTS idx_agendamentos_pet ON agendamentos(pet_id);
CREATE INDEX IF NOT EXISTS idx_agendamentos_clinica ON agendamentos(clinica_id);
CREATE INDEX IF NOT EXISTS idx_agendamentos_status ON agendamentos(status);
CREATE INDEX IF NOT EXISTS idx_vagas_clinica_data ON vagas(clinica_id, data);

-- Admin padrão (senha: admin123 — hash SHA-256 com salt)
-- Formato: sha256(salt + senha):salt
-- Gerado com salt fixo "castraadmin" para reprodutibilidade:
-- sha256("castraadmin" + "admin123") = valor calculado pela aplicação
-- A aplicação insere via INSERT IF NOT EXISTS (ON CONFLICT DO NOTHING)
INSERT INTO usuarios (nome, email, senha, role)
VALUES ('Administrador', 'admin@castrapet.com',
        '0000000000000000000000000000000000000000000000000000000000000000:castraadmin',
        'ADMIN')
ON CONFLICT (email) DO NOTHING;

-- Clínica de exemplo
INSERT INTO clinicas (nome, endereco, cidade, estado, cep, telefone, email)
VALUES ('Clínica Pet Saúde', 'Rua das Flores, 123', 'São Paulo', 'SP', '01310-100',
        '(11) 3000-0000', 'contato@petsaude.com')
ON CONFLICT DO NOTHING;

-- Vagas de exemplo para os próximos 7 dias
INSERT INTO vagas (clinica_id, data, horario, capacidade)
SELECT 1, CURRENT_DATE + s.day, t.horario::time, 3
FROM generate_series(1, 7) AS s(day),
     (VALUES ('08:00'), ('09:00'), ('10:00'), ('14:00'), ('15:00'), ('16:00')) AS t(horario)
ON CONFLICT (clinica_id, data, horario) DO NOTHING;
