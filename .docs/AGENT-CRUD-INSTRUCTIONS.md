---
title: "Agent Instructions - CRUD Implementation"
description: "Instruções para o Agent ao implementar CRUD no projeto Restaurante POS"
scope: "repository"
priority: "high"
---

# 🤖 Instruções para Agent - Implementação CRUD

## Quando Implementar Novo CRUD

Quando o usuário solicitar implementar uma operação CRUD para uma entidade, siga EXATAMENTE este protocolo:

### Fase 1: Análise (Leitura)
1. ✅ Leia `CRUD-IMPLEMENTATION-GUIDE.md`
2. ✅ Verifique se a tabela já existe em `create-database.sql`
3. ✅ Identifique a entidade (Entity, Service, Repository, Controller)
4. ✅ Consulte a estrutura atual em `src/main/java/br/pr/puc/restaurante/`

### Fase 2: Criação de Arquivos
Crie os 6 arquivos OBRIGATÓRIOS nesta ordem:

#### 1. **Entity** (modelo JPA)
- Localização: `src/main/java/br/pr/puc/restaurante/model/entity/`
- Padrão: `@Entity`, `@Table(name="...")`, `@Id`, `@GeneratedValue`
- Campos: sempre com `@Column` e constraints
- Sempre inclua getters/setters

#### 2. **Request DTO**
- Localização: `src/main/java/br/pr/puc/restaurante/model/request/`
- Padrão: `record Create{EntityName}Request(...) {}`
- Inclua APENAS campos necessários para criar
- Separado: `Update{EntityName}Request` se diferente

#### 3. **Response DTO**
- Localização: `src/main/java/br/pr/puc/restaurante/model/response/`
- Padrão: `record {EntityName}Response(...) {}`
- NUNCA inclua senhas ou dados sensíveis
- Use `BigDecimal` para IDs

#### 4. **Repository**
- Localização: `src/main/java/br/pr/puc/restaurante/repository/`
- Padrão: `public class {EntityName}Repository implements PanacheRepository<{EntityName}>`
- `@ApplicationScoped`
- Herde CRUD automático
- Adicione métodos customizados se necessário

#### 5. **Service**
- Localização: `src/main/java/br/pr/puc/restaurante/service/`
- Padrão: `@ApplicationScoped` + `@Inject RepositoryName`
- Implemente 5 métodos: `criar()`, `obterPorId()`, `listar()`, `atualizar()`, `deletar()`
- VALIDAÇÕES acontecem aqui, não no controller
- Lance `IllegalArgumentException` para erros de negócio
- Mapeie Entity → Response SEMPRE

#### 6. **Controller**
- Localização: `src/main/java/br/pr/puc/restaurante/controller/`
- Padrão: `@Path("/entidade")` + endpoints REST
- Implemente 5 endpoints: `POST`, `GET/{id}`, `GET`, `PUT/{id}`, `DELETE/{id}`
- Retorne `Response` com status apropriado (200, 201, 204, 400, 404, 500)
- Adicione OpenAPI annotations (`@Operation`, `@APIResponse`)
- Trate `IllegalArgumentException` → 400 ou 404
- Trate `Exception` genérica → 500

### Fase 3: Banco de Dados
Se a tabela NÃO existir:
- Crie arquivo: `src/main/resources/db/migrations/{seq}-create-{tablename}.sql`
- Use padrão Liquibase: `--changeset user:seq-description`
- Inclua constraints no SQL
- Crie índices para buscas frequentes

### Fase 4: Validação
- ✅ Verifique que há 6 arquivos criados
- ✅ Confirme que Entity tem todos os campos
- ✅ Verifique que Service tem 5 métodos CRUD
- ✅ Confirme que Controller tem 5 endpoints
- ✅ Teste endpoints (se permitido)

---

## Convenções Obrigatórias

| Aspecto | Regra |
|--------|-------|
| **Package names** | `br.pr.puc.restaurante.{controller\|service\|repository\|model.entity\|model.request\|model.response}` |
| **Class naming** | Entity: `{Name}`, Controller: `{Name}Controller` (CamelCase) |
| **Table naming** | snake_case: `usuario`, `garcom`, `menu` |
| **Method naming** | `criar()`, `obterPorId()`, `listar()`, `atualizar()`, `deletar()` |
| **REST endpoints** | POST (create), GET (list), GET/{id} (read one), PUT/{id} (update), DELETE/{id} (delete) |
| **Status codes** | 201 (created), 200 (ok), 204 (deleted), 400 (bad request), 404 (not found), 500 (error) |
| **DTOs** | Sempre use `record` para Request/Response |
| **Responses** | NUNCA retorne Entity, sempre map para Response |
| **IDs** | Use `BigDecimal` em Response, `Long` em Entity |

---

## Padrões de Código a Seguir

### Service - Validação
```java
private void validarRequest(CreateXxxRequest request) {
    if (request == null) throw new IllegalArgumentException("Dados não informados");
    if (isBlank(request.campo())) throw new IllegalArgumentException("Campo obrigatório");
}
```

### Service - Mapping
```java
private XxxResponse mapToResponse(Xxx entity) {
    return new XxxResponse(
        BigDecimal.valueOf(entity.getId()),
        entity.getNome()
    );
}
```

### Controller - Error Handling
```java
try {
    return Response.ok(userService.criar(request)).build();
} catch (IllegalArgumentException e) {
    return Response.status(Response.Status.BAD_REQUEST)
        .entity(e.getMessage()).build();
} catch (Exception e) {
    return Response.serverError().build();
}
```

---

## Erros Comuns a Evitar ❌

- ❌ Retornar Entity diretamente (use Response DTO)
- ❌ Colocar validação no Controller (responsabilidade da Service)
- ❌ Usar `getId()` diretamente em Response (usar `BigDecimal.valueOf()`)
- ❌ Esquecer `@ApplicationScoped` em Service/Repository
- ❌ Esquecer `@Inject` para dependências
- ❌ Não tratar exceções no Controller
- ❌ Misturar tabelas (sempre use o padrão Liquibase)
- ❌ Esquecer getters/setters na Entity
- ❌ Retornar senhas ou dados sensíveis em Response
- ❌ Não documentar endpoints com OpenAPI annotations

---

## Exemplo Prático: Implementar CRUD de "Garcom"

### Passo 1: Entity
```java
@Entity @Table(name = "garcom")
public class Garcom {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 150) private String nome;
    @Column(nullable = false) private BigDecimal percentualGorjeta;
    @Column(nullable = false) private Integer idade;
    // getters/setters...
}
```

### Passo 2: Request/Response
```java
record CreateGarcomRequest(String nome, BigDecimal percentualGorjeta, Integer idade) {}
record GarcomResponse(BigDecimal id, String nome, BigDecimal percentualGorjeta) {}
```

### Passo 3: Repository
```java
@ApplicationScoped
public class GarcomRepository implements PanacheRepository<Garcom> {}
```

### Passo 4: Service
```java
@ApplicationScoped public class GarcomService {
    @Inject GarcomRepository garcomRepository;
    public GarcomResponse criar(CreateGarcomRequest request) { ... }
    public GarcomResponse obterPorId(Long id) { ... }
    public List<GarcomResponse> listar() { ... }
    public GarcomResponse atualizar(Long id, UpdateGarcomRequest request) { ... }
    public void deletar(Long id) { ... }
}
```

### Passo 5: Controller
```java
@Path("/garcom")
public class GarcomController {
    @Inject GarcomService garcomService;
    @POST public Response criar(CreateGarcomRequest request) { ... }
    @GET @Path("/{id}") public Response obterPorId(@PathParam("id") Long id) { ... }
    @GET public Response listar() { ... }
    @PUT @Path("/{id}") public Response atualizar(Long id, UpdateGarcomRequest request) { ... }
    @DELETE @Path("/{id}") public Response deletar(@PathParam("id") Long id) { ... }
}
```

---

## Recursos do Projeto

- **Swagger UI**: http://localhost:8080/q/swagger-ui (teste endpoints)
- **Repositório de Docs**: `.docs/CRUD-IMPLEMENTATION-GUIDE.md` para detalhes
- **Padrão Existente**: Veja `UserController`, `UserService`, `UsuarioRepository`
- **Banco de Dados**: PostgreSQL em `localhost:5432/restaurante_pos`

---

## Após Implementação

✅ Confirme que todos os 6 arquivos estão criados
✅ Verifique que a estrutura segue o padrão de diretórios
✅ Confirme que validações estão na Service, não no Controller
✅ Verifique que Response DTOs NUNCA expõem dados sensíveis
✅ Se aplicável, rode testes ou verifique Swagger
