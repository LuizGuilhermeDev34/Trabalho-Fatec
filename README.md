# 🐾 CastraPet

> Sistema web de agendamento de castração de cães e gatos — conectando tutores a clínicas parceiras de forma simples e organizada.

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2-6DB33F?style=flat-square&logo=spring-boot&logoColor=white)
![React](https://img.shields.io/badge/React-18-20232A?style=flat-square&logo=react&logoColor=61DAFB)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=flat-square&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=docker&logoColor=white)

---

## O problema

O acesso à castração de animais de estimação ainda é uma barreira para muitos tutores — seja pela falta de informação sobre clínicas disponíveis, dificuldade de agendamento ou ausência de acompanhamento do processo.

O CastraPet centraliza esse processo: o tutor cadastra seus pets, consulta clínicas parceiras, verifica vagas disponíveis por data e realiza o agendamento em poucos cliques.

---

## O que você pode fazer aqui

| Funcionalidade | Descrição |
|---|---|
| 👤 **Cadastro de tutor** | Criação de conta com autenticação JWT segura |
| 🐶 **Gestão de pets** | Cadastro, edição e remoção de cães e gatos vinculados ao tutor |
| 🏥 **Clínicas parceiras** | Listagem de clínicas ativas com vagas disponíveis por data |
| 📅 **Agendamentos** | Criação, consulta e cancelamento de agendamentos |
| 🛡️ **Painel admin** | Visão geral de todos os agendamentos, atualização de status e estatísticas |

---

## Tecnologias

**Backend**
- Java 17 + Spring Boot 3.2
- Spring Security + JWT (jjwt 0.12) + BCrypt — autenticação e autorização por roles
- Spring Data JPA + Hibernate + Flyway — ORM e migrações de banco
- PostgreSQL
- Swagger / OpenAPI 3 — documentação interativa da API
- Lombok

**Frontend**
- React 18 + Vite 5
- React Router 6 — navegação client-side
- Axios — requisições HTTP com interceptors para JWT

**Infraestrutura**
- Docker + Docker Compose — orquestração completa em um único comando
- Nginx — proxy reverso do frontend para o backend

---

## Como rodar localmente

### Com Docker (recomendado)

```bash
# Clone o repositório
git clone https://github.com/LuizGuilhermeDev34/Trabalho-Fatec.git
cd Trabalho-Fatec

# Sobe todos os serviços (banco, backend e frontend)
docker compose up --build
```

| Serviço     | URL                                   |
|-------------|---------------------------------------|
| Frontend    | http://localhost:3000                 |
| API Backend | http://localhost:8080                 |
| Swagger UI  | http://localhost:8080/swagger-ui.html |

```bash
# Parar os serviços
docker compose down

# Remover também o volume do banco
docker compose down -v
```

### Sem Docker

```bash
# Backend — requer PostgreSQL local na porta 5432
cd backend
mvn spring-boot:run

# Frontend — em outro terminal
cd frontend
npm install
npm run dev
```

---

## Credenciais padrão

| Usuário | E-mail              | Senha     | Perfil |
|---------|---------------------|-----------|--------|
| Admin   | admin@castrapet.com | Admin@123 | ADMIN  |

---

## Principais endpoints da API

```
POST   /api/auth/register               Cadastrar tutor
POST   /api/auth/login                  Login → retorna JWT

GET    /api/pets                        Listar meus pets
POST   /api/pets                        Cadastrar pet
PUT    /api/pets/{id}                   Atualizar pet
DELETE /api/pets/{id}                   Remover pet

GET    /api/clinicas                    Listar clínicas ativas
GET    /api/clinicas/{id}/vagas         Vagas disponíveis por data

POST   /api/agendamentos                Criar agendamento
GET    /api/agendamentos                Meus agendamentos
PUT    /api/agendamentos/{id}/cancelar  Cancelar agendamento
PUT    /api/agendamentos/{id}/status    Atualizar status (ADMIN/VET)

GET    /api/agendamentos/admin/todos         Todos os agendamentos (ADMIN)
GET    /api/agendamentos/admin/estatisticas  Estatísticas gerais (ADMIN)
```

---

## Estrutura do projeto

```
castrapet/
├── docker-compose.yml
│
├── backend/                          Java 17 + Spring Boot
│   └── src/main/java/com/castrapet/
│       ├── controller/               REST endpoints
│       ├── service/                  Regras de negócio
│       ├── repository/               Spring Data JPA
│       ├── model/                    Entidades JPA
│       ├── dto/                      Request / Response
│       ├── security/                 JWT + Spring Security
│       ├── exception/                Handler global de erros
│       └── config/                   SecurityConfig, CORS
│
└── frontend/                         React + Vite
    └── src/
        ├── services/api.js           Axios + interceptors JWT
        ├── pages/                    Login, Dashboard, Pets, etc.
        ├── components/               Componentes reutilizáveis
        └── hooks/                    useAuth, usePets, etc.
```

---

## Autor

**Luiz Guilherme da Silva**
[LinkedIn](https://www.linkedin.com/in/luizguilhermedev/) · [GitHub](https://github.com/LuizGuilhermeDev34)

---

*Projeto acadêmico — FATEC, curso de Análise e Desenvolvimento de Sistemas.*
