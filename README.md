# 🐾 CastraPet — Sistema de Agendamento de Castração

Sistema completo para agendamento de castração de cães e gatos.  
**Backend**: Java 17 + Spring Boot 3 | **Frontend**: React + Vite | **Banco**: PostgreSQL

---

## 🚀 Subir tudo com Docker (recomendado)

### Pré-requisitos
- [Docker](https://docs.docker.com/get-docker/) instalado
- [Docker Compose](https://docs.docker.com/compose/install/) v2+

### 1. Clonar / baixar o projeto
```bash
git clone https://github.com/seu-usuario/castrapet.git
cd castrapet
```

### 2. Subir todos os serviços
```bash
docker compose up --build
```

### 3. Acessar
| Serviço      | URL                          |
|--------------|------------------------------|
| Frontend     | http://localhost:3000        |
| API Backend  | http://localhost:8080        |
| Swagger UI   | http://localhost:8080/swagger-ui.html |
| PgAdmin      | http://localhost:5050 *(opcional)* |

### 4. Parar os serviços
```bash
docker compose down

# Para remover também o volume do banco:
docker compose down -v
```

### 5. Subir com PgAdmin (ferramenta visual do banco)
```bash
docker compose --profile tools up --build
```
> PgAdmin: http://localhost:5050  
> Login: `admin@castrapet.com` / `admin123`  
> Servidor: host=`postgres`, port=`5432`, user=`postgres`, pass=`castrapet@2024`

---

## 🛠️ Desenvolvimento local (sem Docker)

### Backend
```bash
cd backend

# Requer PostgreSQL local rodando na porta 5432
# Crie o banco: createdb castrapet

mvn spring-boot:run
# API disponível em http://localhost:8080
```

### Frontend
```bash
cd frontend
npm install
npm run dev
# App disponível em http://localhost:3000
```

---

## 🔐 Credenciais padrão

| Usuário      | E-mail                  | Senha       | Role  |
|--------------|-------------------------|-------------|-------|
| Admin        | admin@castrapet.com     | Admin@123   | ADMIN |

---

## 📁 Estrutura do projeto

```
castrapet/
├── docker-compose.yml          ← Orquestração completa
│
├── backend/                    ← Spring Boot (Java 17)
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/java/com/castrapet/
│       ├── controller/         ← REST endpoints
│       ├── service/            ← Regras de negócio
│       ├── repository/         ← Spring Data JPA
│       ├── model/              ← Entidades JPA (@Entity)
│       ├── dto/                ← Request/Response DTOs
│       ├── security/           ← JWT + Spring Security
│       ├── exception/          ← Handler global de erros
│       └── config/             ← SecurityConfig, CORS
│
└── frontend/                   ← React + Vite
    ├── Dockerfile
    ├── nginx.conf              ← Proxy reverso → backend
    ├── vite.config.js
    └── src/
        ├── services/api.js     ← Axios + interceptors JWT
        ├── pages/              ← Login, Dashboard, Pets, etc.
        ├── components/         ← Componentes reutilizáveis
        └── hooks/              ← useAuth, usePets, etc.
```

---

## 🔌 Principais endpoints da API

```
POST   /api/auth/register          Cadastrar tutor
POST   /api/auth/login             Login → retorna JWT

GET    /api/pets                   Listar meus pets
POST   /api/pets                   Cadastrar pet
PUT    /api/pets/{id}              Atualizar pet
DELETE /api/pets/{id}              Remover pet

GET    /api/clinicas               Listar clínicas ativas
GET    /api/clinicas/{id}/vagas    Vagas disponíveis por data

POST   /api/agendamentos           Criar agendamento
GET    /api/agendamentos           Meus agendamentos
PUT    /api/agendamentos/{id}/cancelar   Cancelar
PUT    /api/agendamentos/{id}/status     Atualizar status (ADMIN/VET)

GET    /api/agendamentos/admin/todos          Todos (ADMIN)
GET    /api/agendamentos/admin/estatisticas   Stats (ADMIN)
```

---

## ⚙️ Variáveis de ambiente (backend)

| Variável                    | Padrão (Docker)                        |
|-----------------------------|----------------------------------------|
| `SPRING_DATASOURCE_URL`     | `jdbc:postgresql://postgres:5432/castrapet` |
| `SPRING_DATASOURCE_USERNAME`| `postgres`                             |
| `SPRING_DATASOURCE_PASSWORD`| `castrapet@2024`                       |
| `APP_JWT_SECRET`            | (chave base64 gerada)                  |
| `APP_JWT_EXPIRATION`        | `86400000` (24h em ms)                 |
| `APP_CORS_ALLOWED_ORIGINS`  | `http://localhost:3000`                |

---

## 🧪 Tecnologias

**Backend**
- Java 17, Spring Boot 3.2, Spring Security, Spring Data JPA
- JWT (jjwt 0.12), BCrypt, Hibernate, Flyway
- PostgreSQL, Lombok, Swagger/OpenAPI 3

**Frontend**
- React 18, Vite 5, React Router 6, Axios

**Infraestrutura**
- Docker, Docker Compose, Nginx (proxy reverso)
