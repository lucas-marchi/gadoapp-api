# GadoApp API (Backend)

API RESTful para gestão de rebanhos bovinos, construída com Java e Spring Boot.
Suporta arquitetura Multi-tenant lógica e sincronização Offline-First.

## 🚀 Tecnologias

- **Java 21**
- **Spring Boot 3.4**
- **Spring Security + JWT** (Autenticação Stateless)
- **PostgreSQL** (Banco de Dados)
- **Lombok** (Produtividade)
- **Swagger/OpenAPI** (Documentação)

## ⚙️ Como Rodar

1. **Banco de Dados:**
   Certifique-se de ter o PostgreSQL rodando e crie um banco chamado `gadoapp_db`.
   Configure as credenciais em `src/main/resources/application.properties`.

2. **Executar:**
   ```bash
   ./mvnw spring-boot:run
   ```

3. **Documentação (Swagger):**
   Acesse: `http://localhost:8080/swagger-ui/index.html`

## 🔒 Segurança

- O sistema utiliza Tokens **JWT (Bearer)**.
- Para acessar os endpoints protegidos (`/api/herds`, `/api/bovines`), você deve:
  1. Criar conta em `/api/auth/register`.
  2. Pegar o token retornado.
  3. Enviar no Header: `Authorization: Bearer <SEU_TOKEN>`.

## 📂 Arquitetura

- **Multi-tenant:** Cada usuário vê apenas seus próprios dados (filtragem via `user_id`).
- **Offline-Ready:**
  - Entidades possuem `updatedAt` e `active` (Soft Delete).
  - Endpoints de Sync (`/api/sync/*`) suportam Delta Sync (enviam apenas o que mudou).
