# 🎯 Quick Reference - CRUD Implementation Checklist

## Para cada nova entidade, crie 6 arquivos:

### 1️⃣ Entity (`model/entity`)
```
✅ @Entity + @Table
✅ @Id + @GeneratedValue(IDENTITY)
✅ @Column(constraints)
✅ Getters/Setters para cada campo
```

### 2️⃣ Request DTO (`model/request`)
```
✅ record Create{Name}Request(...)
✅ Apenas campos necessários
✅ Separado: Update{Name}Request se diferente
```

### 3️⃣ Response DTO (`model/response`)
```
✅ record {Name}Response(...)
✅ SEM dados sensíveis
✅ IDs como BigDecimal
```

### 4️⃣ Repository (`repository`)
```
✅ @ApplicationScoped
✅ implements PanacheRepository<{Name}>
✅ Métodos customizados (findByXxx)
```

### 5️⃣ Service (`service`)
```
✅ @ApplicationScoped + @Inject Repository
✅ Método: criar(request) → Response
✅ Método: obterPorId(id) → Response
✅ Método: listar() → List<Response>
✅ Método: atualizar(id, request) → Response
✅ Método: deletar(id) → void
✅ Validações via isValid() ou validar()
✅ Mapeamento Entity → Response
```

### 6️⃣ Controller (`controller`)
```
✅ @Path("/path")
✅ Endpoint: @POST → criar()
✅ Endpoint: @GET → listar()
✅ Endpoint: @GET /{id} → obterPorId()
✅ Endpoint: @PUT /{id} → atualizar()
✅ Endpoint: @DELETE /{id} → deletar()
✅ Try/Catch com Response(status)
✅ OpenAPI @Operation + @APIResponse
```

---

## 📝 Validação Final

- [ ] 6 arquivos criados
- [ ] Entity tem @Entity, @Table, @Id, @Column
- [ ] Repository estende PanacheRepository
- [ ] Service tem 5 métodos CRUD
- [ ] Service valida inputs
- [ ] Service mapeia Entity → Response
- [ ] Controller tem 5 endpoints
- [ ] Controller trata exceções
- [ ] Não há Entity sendo retornada diretamente
- [ ] Não há dados sensíveis em Response
- [ ] Todas as anotações @Inject e @ApplicationScoped presentes
- [ ] OpenAPI annotations no Controller

---

## 🔴 Erros a Evitar

- ❌ Retornar Entity (usar Response)
- ❌ Validação no Controller (usar Service)
- ❌ getId() em Response (usar BigDecimal.valueOf)
- ❌ Sem @Inject em Controller/Service
- ❌ Sem @ApplicationScoped em Service/Repository
- ❌ Sem try/catch no Controller
- ❌ Retornar senhas/dados sensíveis
- ❌ Métodos com nomes genéricos (criar, não create)
- ❌ GET/POST misturados (use métodos REST corretos)

---

## 📊 Template SQL (Liquibase)

```sql
--liquibase formatted sql

--changeset author:seq-create-tablename
CREATE TABLE IF NOT EXISTS tablename (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    column_name VARCHAR(100) NOT NULL,
    constraint_col DECIMAL(10,2) NOT NULL
);

-- Constraints
DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'uk_name') THEN
        ALTER TABLE tablename ADD CONSTRAINT uk_name UNIQUE (column_name);
    END IF;
END $$;

-- Indexes
CREATE INDEX IF NOT EXISTS idx_tablename_column ON tablename(column_name);
```

---

## 🧪 Teste Rápido

```bash
# Criar
curl -X POST http://localhost:8080/entidade \
  -H "Content-Type: application/json" \
  -d '{"campo":"valor"}'

# Listar
curl http://localhost:8080/entidade

# Obter
curl http://localhost:8080/entidade/1

# Atualizar
curl -X PUT http://localhost:8080/entidade/1 \
  -H "Content-Type: application/json" \
  -d '{"campo":"novo_valor"}'

# Deletar
curl -X DELETE http://localhost:8080/entidade/1
```

---

## 📚 Estrutura do Projeto

```
restaurante-pos/
├── src/main/
│   ├── java/br/pr/puc/restaurante/
│   │   ├── controller/       ← Endpoints REST
│   │   ├── service/          ← Lógica de negócio
│   │   ├── repository/       ← Acesso ao BD
│   │   └── model/
│   │       ├── entity/       ← JPA Entities
│   │       ├── request/      ← DTOs de entrada
│   │       └── response/     ← DTOs de saída
│   └── resources/
│       └── db/
│           ├── migrations/   ← Scripts SQL
│           └── changelog.sql
├── pom.xml                   ← Dependências
├── docker-compose.yml        ← PostgreSQL
└── .docs/                    ← Documentação
```

---

## 🔗 Referências Rápidas

| Arquivo | Path | Responsabilidade |
|---------|------|------------------|
| Entity | `model/entity/*.java` | Mapear tabela BD para classe |
| Request | `model/request/*.java` | Receber dados de entrada |
| Response | `model/response/*.java` | Enviar dados de saída |
| Repository | `repository/*.java` | Queries ao BD |
| Service | `service/*.java` | Lógica, validação, mapping |
| Controller | `controller/*.java` | HTTP endpoints |

---

## ⚡ Comandos Maven

```bash
./mvnw quarkus:dev              # Modo desenvolvimento
./mvnw test                     # Rodar testes
./mvnw package                  # Build para produção
./mvnw clean                    # Limpar
```

---

## 📝 Exemplo de Nomes

| Entidade | Entity | Controller | Service | Repository |
|----------|--------|-----------|---------|------------|
| Usuário | `Usuario` | `UserController` | `UserService` | `UsuarioRepository` |
| Garçom | `Garcom` | `GarcomController` | `GarcomService` | `GarcomRepository` |
| Menu | `Menu` | `MenuController` | `MenuService` | `MenuRepository` |
| Comanda | `Comanda` | `ComandaController` | `ComandaService` | `ComandaRepository` |

---

## 🎯 Workflow Recomendado

1. **Criar tabela** no `db/migrations/` (Liquibase)
2. **Criar Entity** em `model/entity/`
3. **Criar Repository** em `repository/`
4. **Criar Request/Response** em `model/request` e `model/response`
5. **Criar Service** em `service/` (com 5 métodos CRUD)
6. **Criar Controller** em `controller/` (com 5 endpoints)
7. **Rodar** `./mvnw quarkus:dev`
8. **Testar** no Swagger: `http://localhost:8080/q/swagger-ui`
