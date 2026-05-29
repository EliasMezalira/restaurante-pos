# 📚 Documentação CRUD - Restaurante POS

Bem-vindo! Esta pasta contém a documentação completa para implementar operações CRUD no projeto Restaurante POS.

---

## 📖 Arquivos de Documentação

### 1. **QUICK-REFERENCE.md** ⚡
**Para**: Visão geral rápida e checklist visual

- 6 arquivos obrigatórios para cada entidade
- Validação final com checklist
- Erros comuns a evitar
- Template SQL
- Estrutura do projeto
- **Melhor para**: Referência rápida durante desenvolvimento

---

### 2. **CRUD-IMPLEMENTATION-GUIDE.md** 📖
**Para**: Guia completo e detalhado

- Contexto do projeto (Framework, Stack)
- Arquitetura de camadas
- Padrão CRUD completo com código
- Exemplos para cada layer (Entity, Repository, Service, Controller)
- Padrão Liquibase para migrations
- Cuidados importantes
- **Melhor para**: Aprendizado profundo e implementação detalhada

---

### 3. **AGENT-CRUD-INSTRUCTIONS.md** 🤖
**Para**: Instruções específicas para automação

- Protocolo de implementação em 4 fases
- Ordem exata de criação de arquivos
- Convenções obrigatórias
- Padrões de código
- Exemplo prático: Garcom CRUD
- **Melhor para**: Agent seguir um padrão consistente

---

## 🚀 Como Começar

### Cenário 1: Você é novo no projeto
1. Leia **QUICK-REFERENCE.md** (5 min)
2. Explore **CRUD-IMPLEMENTATION-GUIDE.md** (20 min)
3. Veja um exemplo em `UserController`, `UserService`, `UsuarioRepository`
4. Implemente seu CRUD seguindo o padrão

### Cenário 2: Você precisa implementar um novo CRUD agora
1. Abra **QUICK-REFERENCE.md**
2. Veja o checklist de 6 arquivos
3. Copie um exemplo existente (UserController, etc.)
4. Adapte para sua entidade
5. Valide com o checklist final

### Cenário 3: Você é um Agent/Automação
1. Consulte **AGENT-CRUD-INSTRUCTIONS.md**
2. Siga a ordem de criação de arquivos
3. Valide as 4 fases
4. Confirme todos os 6 arquivos

---

## 🏗️ Arquitetura (Resumo)

```
REST API (Client)
    ↓
Controller (@Path, @POST, @GET, @PUT, @DELETE)
    ↓
Service (@ApplicationScoped, validações, mapping)
    ↓
Repository (extends PanacheRepository<T>)
    ↓
Entity (JPA @Entity, @Table, @Column)
    ↓
PostgreSQL Database
```

---

## 📋 Os 6 Arquivos Obrigatórios

Para cada entidade (ex: **Usuario**), você deve criar:

| # | Tipo | Local | Responsabilidade |
|---|------|-------|------------------|
| 1 | Entity | `model/entity/Usuario.java` | Mapear tabela do BD |
| 2 | Request DTO | `model/request/CreateUsuarioRequest.java` | Receber dados do cliente |
| 3 | Response DTO | `model/response/UsuarioResponse.java` | Enviar dados ao cliente |
| 4 | Repository | `repository/UsuarioRepository.java` | Queries ao BD |
| 5 | Service | `service/UserService.java` | Lógica de negócio |
| 6 | Controller | `controller/UserController.java` | Endpoints REST |

---

## ✅ Checklist Rápido

Antes de considerar seu CRUD completo:

- [ ] Entity com @Entity, @Table, @Id, @GeneratedValue, @Column
- [ ] Repository que estende PanacheRepository
- [ ] Request/Response DTOs como records
- [ ] Service com 5 métodos: criar, obterPorId, listar, atualizar, deletar
- [ ] Service com validações
- [ ] Service mapeando Entity → Response
- [ ] Controller com 5 endpoints REST (POST, GET, GET/{id}, PUT/{id}, DELETE/{id})
- [ ] Controller com try/catch e Response com status apropriado
- [ ] OpenAPI annotations no Controller
- [ ] Sem dados sensíveis em Response
- [ ] Sem Entity sendo retornada diretamente

---

## 🔗 Estrutura do Projeto

```
restaurante-pos/
├── src/main/java/br/pr/puc/restaurante/
│   ├── controller/
│   │   ├── UserController.java         ✅ Exemplo existente
│   │   └── {Novo}Controller.java
│   ├── service/
│   │   ├── UserService.java            ✅ Exemplo existente
│   │   └── {Novo}Service.java
│   ├── repository/
│   │   ├── UsuarioRepository.java      ✅ Exemplo existente
│   │   └── {Novo}Repository.java
│   └── model/
│       ├── entity/
│       │   ├── Usuario.java            ✅ Exemplo existente
│       │   └── {Novo}.java
│       ├── request/
│       │   ├── LoginRequest.java       ✅ Exemplo existente
│       │   ├── Create{Novo}Request.java
│       │   └── Update{Novo}Request.java
│       └── response/
│           ├── LoginResponse.java      ✅ Exemplo existente
│           └── {Novo}Response.java
└── src/main/resources/db/
    ├── db.changelog-master.sql
    └── migrations/
        ├── 001-create-usuario.sql      ✅ Exemplo existente
        └── {seq}-create-{novo}.sql
```

---

## 🧪 Testando seu CRUD

### 1. Rodar a aplicação
```bash
./mvnw quarkus:dev
```

### 2. Acessar Swagger UI
```
http://localhost:8080/q/swagger-ui
```

### 3. Testar com curl

**Criar:**
```bash
curl -X POST http://localhost:8080/usuario \
  -H "Content-Type: application/json" \
  -d '{"login":"joao","senha":"123","nome":"João"}'
```

**Listar:**
```bash
curl http://localhost:8080/usuario
```

**Obter por ID:**
```bash
curl http://localhost:8080/usuario/1
```

**Atualizar:**
```bash
curl -X PUT http://localhost:8080/usuario/1 \
  -H "Content-Type: application/json" \
  -d '{"nome":"João Silva"}'
```

**Deletar:**
```bash
curl -X DELETE http://localhost:8080/usuario/1
```

---

## 🎯 Exemplo Prático: Criar CRUD de Garcom

### Passo 1: Verifique a tabela
A tabela `garcom` já existe em `db/migrations/001-create-usuario.sql`

### Passo 2: Criar Entity
`model/entity/Garcom.java`

### Passo 3: Criar DTOs
- `model/request/CreateGarcomRequest.java`
- `model/request/UpdateGarcomRequest.java`
- `model/response/GarcomResponse.java`

### Passo 4: Criar Repository
`repository/GarcomRepository.java`

### Passo 5: Criar Service
`service/GarcomService.java` com 5 métodos CRUD

### Passo 6: Criar Controller
`controller/GarcomController.java` com 5 endpoints

### Passo 7: Testar
- Rodar: `./mvnw quarkus:dev`
- Acessar: `http://localhost:8080/q/swagger-ui`
- Testar endpoints

---

## ⚠️ Erros Comuns

❌ **Retornar Entity diretamente**
```java
// ERRADO
return Response.ok(usuario).build();

// CORRETO
return Response.ok(mapToResponse(usuario)).build();
```

❌ **Validação no Controller**
```java
// ERRADO - Validação no Controller
if (nome == null) return Response.badRequest().build();

// CORRETO - Validação na Service
service.criar(request); // Service valida internamente
```

❌ **Esquecer @Inject**
```java
// ERRADO
UsuarioRepository repo = new UsuarioRepository();

// CORRETO
@Inject
UsuarioRepository repo;
```

❌ **Retornar senhas**
```java
// ERRADO
record UsuarioResponse(String senha) {}

// CORRETO
record UsuarioResponse(BigDecimal id, String nome) {}
```

---

## 📞 Dúvidas?

Consulte:
- **Estrutura**: QUICK-REFERENCE.md
- **Detalhes**: CRUD-IMPLEMENTATION-GUIDE.md
- **Automação**: AGENT-CRUD-INSTRUCTIONS.md
- **Exemplos vivos**: `UserController.java`, `UserService.java`, `UsuarioRepository.java`

---

## 📝 Notas Importantes

1. **Sempre validar na Service**, não no Controller
2. **Sempre mapear Entity → Response DTO**, nunca retornar Entity diretamente
3. **IDs em Response como BigDecimal** (evita overflow)
4. **Nunca expor dados sensíveis** (senhas, tokens)
5. **Sempre usar record para DTOs** (conciso e imutável)
6. **Sempre documentar endpoints** com @Operation e @APIResponse
7. **Sempre tratarcexceções** no Controller com Response e status apropriado

---

**Última atualização**: Maio 2026
**Framework**: Quarkus 3.35.4
**Banco de Dados**: PostgreSQL
**Pattern**: Layered Architecture
