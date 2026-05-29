# 📋 Guia de Implementação CRUD - Restaurante POS

## Contexto do Projeto
- **Framework**: Quarkus 3.35.4 com Java 25
- **ORM**: Hibernate Panache
- **Banco**: PostgreSQL
- **Padrão**: Arquitetura em Camadas (Controller → Service → Repository → Entity)
- **Migração BD**: Liquibase
- **API**: REST com Jakarta/JAX-RS

---

## 🏗️ Arquitetura de Camadas

```
┌─────────────────────────────────────┐
│  Controller (REST Endpoints)        │  POST, GET, PUT, DELETE, GET/{id}
└────────────────┬────────────────────┘
                 │ @Inject UserService
┌────────────────▼────────────────────┐
│  Service (Lógica de Negócio)        │  Validações, transformações
└────────────────┬────────────────────┘
                 │ @Inject UsuarioRepository
┌────────────────▼────────────────────┐
│  Repository (Acesso a Dados)        │  Queries customizadas
└────────────────┬────────────────────┘
                 │ extends PanacheRepository<T>
┌────────────────▼────────────────────┐
│  Entity (JPA Model)                 │  @Entity, @Column, @Id
└────────────────┬────────────────────┘
                 │
┌────────────────▼────────────────────┐
│  PostgreSQL Database                │
└─────────────────────────────────────┘
```

---

## 📂 Convenções de Estrutura de Diretórios

```
src/main/java/br/pr/puc/restaurante/
├── controller/
│   └── {EntityName}Controller.java
├── service/
│   └── {EntityName}Service.java
├── repository/
│   └── {EntityName}Repository.java
└── model/
    ├── entity/
    │   └── {EntityName}.java
    ├── request/
    │   ├── Create{EntityName}Request.java
    │   └── Update{EntityName}Request.java
    └── response/
        └── {EntityName}Response.java

src/main/resources/db/
└── migrations/
    └── {seq}-create-{tablename}.sql
```

**Exemplo para "Usuario":**
- Tabela: `usuario`
- Entity: `Usuario.java`
- Controller: `UserController.java`
- Service: `UserService.java`
- Repository: `UsuarioRepository.java`

---

## 🔧 Padrão CRUD Completo

### 1️⃣ ENTITY (Model)

**Arquivo**: `src/main/java/.../model/entity/{EntityName}.java`

```java
package br.pr.puc.restaurante.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tablename")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100, unique = true)
    private String login;

    @Column(nullable = false, length = 255)
    private String senha;

    @Column(nullable = false, length = 150)
    private String nome;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
}
```

**Regras**:
- Use `@Entity` e `@Table(name = "...")`
- `@Id` com `@GeneratedValue(strategy = GenerationType.IDENTITY)`
- `@Column` com constraints (nullable, length, unique)
- Sempre tenha getter/setter para cada field

---

### 2️⃣ REQUEST/RESPONSE DTOs

**Arquivo**: `src/main/java/.../model/request/Create{EntityName}Request.java`

```java
package br.pr.puc.restaurante.model.request;

public record CreateUsuarioRequest(
    String login,
    String senha,
    String nome
) {}
```

**Arquivo**: `src/main/java/.../model/response/{EntityName}Response.java`

```java
package br.pr.puc.restaurante.model.response;

import java.math.BigDecimal;

public record UsuarioResponse(
    BigDecimal id,
    String login,
    String nome
) {}
```

**Regras**:
- Use `record` para DTOs (conciso e imutável)
- Requests: separados por operação (CreateRequest, UpdateRequest)
- Responses: sempre sem dados sensíveis (nunca retorne senha)
- Nunca retorne entidades diretamente

---

### 3️⃣ REPOSITORY (Acesso a Dados)

**Arquivo**: `src/main/java/.../repository/{EntityName}Repository.java`

```java
package br.pr.puc.restaurante.repository;

import br.pr.puc.restaurante.model.entity.Usuario;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UsuarioRepository implements PanacheRepository<Usuario> {
    
    // Métodos customizados para queries específicas
    public Usuario buscaUsuario(String login, String password) {
        return find("login = ?1 and senha = ?2", login, password).firstResult();
    }
    
    public Usuario findByLogin(String login) {
        return find("login", login).firstResult();
    }
    
    // CRUD padrão é herdado de PanacheRepository:
    // - find("...") - queries customizadas
    // - persist(entity) - CREATE
    // - findById(id) - READ
    // - list() - READ ALL
    // - delete(entity) - DELETE
}
```

**Regras**:
- Sempre `@ApplicationScoped`
- Implemente `PanacheRepository<T>` (herda CRUD automático)
- Métodos customizados: usar `find("query", params)`
- Métodos devem ser descritivos: `findByXXX`, `buscaXXX`

---

### 4️⃣ SERVICE (Lógica de Negócio)

**Arquivo**: `src/main/java/.../service/{EntityName}Service.java`

```java
package br.pr.puc.restaurante.service;

import br.pr.puc.restaurante.model.entity.Usuario;
import br.pr.puc.restaurante.model.request.CreateUsuarioRequest;
import br.pr.puc.restaurante.model.response.UsuarioResponse;
import br.pr.puc.restaurante.repository.UsuarioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class UserService {
    
    @Inject
    UsuarioRepository usuarioRepository;

    // CREATE
    public UsuarioResponse criar(CreateUsuarioRequest request) {
        validarRequest(request);
        
        Usuario usuario = new Usuario();
        usuario.setLogin(request.login());
        usuario.setSenha(request.senha()); // TODO: usar bcrypt
        usuario.setNome(request.nome());
        
        usuarioRepository.persist(usuario);
        return mapToResponse(usuario);
    }

    // READ
    public UsuarioResponse obterPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não encontrado");
        }
        return mapToResponse(usuario);
    }

    // READ ALL
    public List<UsuarioResponse> listarTodos() {
        return usuarioRepository.listAll()
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    // UPDATE
    public UsuarioResponse atualizar(Long id, UpdateUsuarioRequest request) {
        validarRequest(request);
        
        Usuario usuario = usuarioRepository.findById(id);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não encontrado");
        }
        
        usuario.setNome(request.nome());
        usuarioRepository.persist(usuario);
        
        return mapToResponse(usuario);
    }

    // DELETE
    public void deletar(Long id) {
        Usuario usuario = usuarioRepository.findById(id);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não encontrado");
        }
        usuarioRepository.delete(usuario);
    }

    // Validações
    private void validarRequest(CreateUsuarioRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Dados não informados");
        }
        if (isBlank(request.login())) {
            throw new IllegalArgumentException("Login deve ser informado");
        }
        if (isBlank(request.senha())) {
            throw new IllegalArgumentException("Senha deve ser informada");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    // Mapeamento Entity → Response
    private UsuarioResponse mapToResponse(Usuario usuario) {
        return new UsuarioResponse(
            BigDecimal.valueOf(usuario.getId()),
            usuario.getLogin(),
            usuario.getNome()
        );
    }
}
```

**Regras**:
- `@ApplicationScoped`
- `@Inject` repositórios
- Separe métodos por operação: `criar()`, `obterPorId()`, `listar()`, `atualizar()`, `deletar()`
- Valide inputs ANTES de persistir
- Sempre mapeie Entity → Response
- Lance `IllegalArgumentException` para validações

---

### 5️⃣ CONTROLLER (REST Endpoints)

**Arquivo**: `src/main/java/.../controller/{EntityName}Controller.java`

```java
package br.pr.puc.restaurante.controller;

import br.pr.puc.restaurante.model.request.CreateUsuarioRequest;
import br.pr.puc.restaurante.model.response.UsuarioResponse;
import br.pr.puc.restaurante.service.UserService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import java.util.List;

@Path("/usuario")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserController {
    
    @Inject
    UserService userService;

    // CREATE
    @POST
    @Operation(summary = "Criar novo usuário")
    @APIResponse(responseCode = "201", description = "Usuário criado")
    public Response criar(
        @RequestBody(required = true)
        CreateUsuarioRequest request
    ) {
        try {
            return Response.status(Response.Status.CREATED)
                .entity(userService.criar(request))
                .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(e.getMessage())
                .build();
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    // READ
    @GET
    @Path("/{id}")
    @Operation(summary = "Obter usuário por ID")
    @APIResponse(responseCode = "200", description = "Usuário encontrado")
    @APIResponse(responseCode = "404", description = "Usuário não encontrado")
    public Response obterPorId(@PathParam("id") Long id) {
        try {
            return Response.ok(userService.obterPorId(id)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(e.getMessage())
                .build();
        }
    }

    // READ ALL
    @GET
    @Operation(summary = "Listar todos os usuários")
    public Response listar() {
        return Response.ok(userService.listarTodos()).build();
    }

    // UPDATE
    @PUT
    @Path("/{id}")
    @Operation(summary = "Atualizar usuário")
    @APIResponse(responseCode = "200", description = "Usuário atualizado")
    public Response atualizar(
        @PathParam("id") Long id,
        @RequestBody(required = true)
        UpdateUsuarioRequest request
    ) {
        try {
            return Response.ok(userService.atualizar(id, request)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(e.getMessage())
                .build();
        }
    }

    // DELETE
    @DELETE
    @Path("/{id}")
    @Operation(summary = "Deletar usuário")
    @APIResponse(responseCode = "204", description = "Usuário deletado")
    public Response deletar(@PathParam("id") Long id) {
        try {
            userService.deletar(id);
            return Response.noContent().build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(e.getMessage())
                .build();
        }
    }
}
```

**Regras**:
- `@Path("/entidade")` - caminho base
- Métodos: `POST` (create), `GET` (read), `GET/{id}` (read one), `PUT` (update), `DELETE` (delete)
- Sempre retorne `Response` com status apropriado
- Use `@PathParam` para IDs
- Use `@RequestBody` com OpenAPI annotations
- Trate `IllegalArgumentException` → 400/404
- Trate `Exception` geral → 500

---

## 🗄️ Padrão de Migração Liquibase

**Arquivo**: `src/main/resources/db/migrations/{seq}-{table}.sql`

```sql
--liquibase formatted sql

--changeset usuario:001-create-usuario
CREATE TABLE IF NOT EXISTS usuario (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    nome VARCHAR(150) NOT NULL,
    login VARCHAR(100) NOT NULL,
    senha VARCHAR(255) NOT NULL
);

-- Constraints
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'uk_usuario_login'
    ) THEN
        ALTER TABLE usuario
        ADD CONSTRAINT uk_usuario_login UNIQUE (login);
    END IF;
END $$;

-- Indexes
CREATE INDEX IF NOT EXISTS idx_usuario_login ON usuario(login);
```

**Regras**:
- Use `--liquibase formatted sql`
- Cada changeset: `--changeset author:seq-description`
- Use `IF NOT EXISTS` para segurança
- Crie índices para campos de busca frequente
- Documente constraints e validações

---

## ✅ Checklist para Implementar Novo CRUD

- [ ] Criar tabela no `db/migrations/{seq}-{table}.sql` com Liquibase
- [ ] Criar `Entity` em `model/entity/{Name}.java` com JPA annotations
- [ ] Criar `{Name}Repository.java` que implemente `PanacheRepository<T>`
- [ ] Criar `Create{Name}Request.java` record
- [ ] Criar `Update{Name}Request.java` record (opcional)
- [ ] Criar `{Name}Response.java` record
- [ ] Criar `{Name}Service.java` com métodos: criar, obterPorId, listar, atualizar, deletar
- [ ] Criar `{Name}Controller.java` com endpoints: POST, GET/{id}, GET, PUT/{id}, DELETE/{id}
- [ ] Adicionar OpenAPI annotations nos endpoints
- [ ] Testar endpoints via Swagger em `http://localhost:8080/q/swagger-ui`

---

## 🔗 Referências Rápidas

| Componente | Anotação Chave | Comportamento |
|-----------|----------------|---------------|
| Entity | `@Entity`, `@Table` | Mapeia para tabela BD |
| Repository | `implements PanacheRepository<T>` | CRUD automático + queries |
| Service | `@ApplicationScoped`, `@Inject` | Lógica centralizada |
| Controller | `@Path`, `@POST/@GET/@PUT/@DELETE` | Endpoints REST |
| Request/Response | `record` | DTOs imutáveis |

---

## 🚀 Comando para Testar

```bash
# Rodar aplicação
./mvnw quarkus:dev

# Acessar Swagger UI
http://localhost:8080/q/swagger-ui

# Criar usuário (POST)
curl -X POST http://localhost:8080/usuario \
  -H "Content-Type: application/json" \
  -d '{"login":"user123","senha":"pass123","nome":"João"}'

# Listar (GET)
curl http://localhost:8080/usuario

# Obter por ID (GET)
curl http://localhost:8080/usuario/1

# Atualizar (PUT)
curl -X PUT http://localhost:8080/usuario/1 \
  -H "Content-Type: application/json" \
  -d '{"nome":"João Silva"}'

# Deletar (DELETE)
curl -X DELETE http://localhost:8080/usuario/1
```

---

## ⚠️ Cuidados Importantes

1. **Senhas**: Usar hash (BCrypt) - NUNCA plain text
2. **IDs em Response**: Usar `BigDecimal` para evitar overflow
3. **Validação**: Fazer na Service ANTES de persistir
4. **Exceções**: Mapear para HTTP status apropriado
5. **DTOs**: NUNCA retornar Entity diretamente
6. **Índices**: Criar em colunas de busca frequente
7. **Constraints**: Usar BD-level + validação Service-level
