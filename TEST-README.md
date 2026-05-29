# 🧪 Testes - Restaurante POS

## Estrutura de Testes

Este projeto contém testes em três níveis:

### 1️⃣ **Testes Unitários** - `UserServiceTest.java`
- **Localização**: `src/test/java/br/pr/puc/restaurante/service/`
- **Tipo**: Testes unitários com Mockito
- **Escopo**: Testa a lógica de negócio isolada
- **Cobertura**:
  - ✅ Login (validações e sucesso)
  - ✅ Criar usuário (validações e persistência)
  - ✅ Obter por ID (validações e busca)
  - ✅ Listar todos (sucesso e lista vazia)
  - ✅ Atualizar (validações e atualização)
  - ✅ Deletar (validações e exclusão)

**Ferramentas**: JUnit 5, Mockito, Quarkus @QuarkusTest

---

### 2️⃣ **Testes de Integração do Repository** - `UsuarioRepositoryTest.java`
- **Localização**: `src/test/java/br/pr/puc/restaurante/repository/`
- **Tipo**: Testes de integração com banco de dados
- **Escopo**: Testa operações CRUD com Panache/JPA
- **Cobertura**:
  - ✅ Persistir usuários
  - ✅ Buscar por ID
  - ✅ Buscar por login
  - ✅ Buscar com credenciais
  - ✅ Listar todos
  - ✅ Atualizar
  - ✅ Deletar
  - ✅ Contar registros

**Ferramentas**: JUnit 5, Quarkus @QuarkusTest, @Transactional

---

### 3️⃣ **Testes de Integração da API** - `UserControllerTest.java`
- **Localização**: `src/test/java/br/pr/puc/restaurante/controller/`
- **Tipo**: Testes de integração end-to-end
- **Escopo**: Testa endpoints HTTP REST
- **Cobertura**:
  - ✅ POST /usuario (criar)
  - ✅ POST /usuario/login (autenticar)
  - ✅ GET /usuario (listar)
  - ✅ GET /usuario/{id} (obter por ID)
  - ✅ PUT /usuario/{id} (atualizar)
  - ✅ DELETE /usuario/{id} (deletar)
  - ✅ Validações HTTP (400, 404, 500)

**Ferramentas**: JUnit 5, REST Assured, Quarkus @QuarkusTest

---

## 🚀 Como Executar os Testes

### Executar TODOS os testes
```bash
./mvnw test
```

### Executar testes específicos
```bash
# Apenas testes de Service
./mvnw test -Dtest=UserServiceTest

# Apenas testes de Repository
./mvnw test -Dtest=UsuarioRepositoryTest

# Apenas testes de Controller (API)
./mvnw test -Dtest=UserControllerTest
```

### Executar um teste específico
```bash
./mvnw test -Dtest=UserServiceTest#testCriarUsuarioComDadosValidos
```

### Executar com debug
```bash
./mvnw test -X
```

### Executar e gerar relatório
```bash
./mvnw test jacoco:report
```

---

## 📊 Cobertura de Testes

### UserServiceTest (30 testes)
- **Login**: 5 testes (sucesso + validações)
- **Criar**: 6 testes (sucesso + validações)
- **Obter por ID**: 5 testes (sucesso + validações)
- **Listar**: 2 testes (sucesso + lista vazia)
- **Atualizar**: 5 testes (sucesso + validações)
- **Deletar**: 5 testes (sucesso + validações)

### UsuarioRepositoryTest (17 testes)
- **Persist**: 2 testes
- **Find by ID**: 2 testes
- **Find by Login**: 2 testes
- **Busca de Usuário**: 3 testes
- **List All**: 2 testes
- **Update**: 1 teste
- **Delete**: 2 testes
- **Count**: 2 testes

### UserControllerTest (19 testes)
- **Criar**: 5 testes (validações HTTP)
- **Login**: 4 testes (validações HTTP)
- **Listar**: 1 teste
- **Obter por ID**: 3 testes (validações HTTP)
- **Atualizar**: 3 testes (validações HTTP)
- **Deletar**: 3 testes (validações HTTP)

**Total**: 66 testes

---

## ✅ Validações Testadas

### Campos obrigatórios
- ❌ Login vazio
- ❌ Senha vazia
- ❌ Nome vazio
- ❌ Usuario vazio (login)
- ❌ Password vazio

### Validações de ID
- ❌ ID nulo
- ❌ ID zero
- ❌ ID negativo
- ❌ ID inexistente

### Regras de negócio
- ❌ Login duplicado ao criar
- ❌ Senha incorreta ao fazer login
- ❌ Usuário não encontrado

### Status HTTP
- ✅ 201 Created (novo usuário)
- ✅ 200 OK (sucesso)
- ✅ 204 No Content (deletado)
- ✅ 400 Bad Request (validação)
- ✅ 404 Not Found (recurso não existe)
- ✅ 500 Internal Server Error (erro)

---

## 🔧 Pré-requisitos

### Dependências no pom.xml
```xml
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-junit</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>rest-assured</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>
```

---

## 📝 Anotações Utilizadas

| Anotação | Uso |
|----------|-----|
| `@QuarkusTest` | Marca classe como teste Quarkus |
| `@InjectMock` | Injeta mock do Mockito |
| `@Inject` | Injeta bean real |
| `@Transactional` | Executa em transação (Repository) |
| `@BeforeEach` | Executado antes de cada teste |
| `@Test` | Marca método como teste |
| `@DisplayName` | Nome descritivo do teste |
| `@Order` | Ordena execução de testes |

---

## 🎯 Padrões de Teste Utilizados

### Padrão Arrange-Act-Assert (AAA)
```java
@Test
void testExample() {
    // Arrange - Preparar dados
    CreateUsuarioRequest request = new CreateUsuarioRequest(...);
    when(usuarioRepository.findByLogin(...)).thenReturn(null);
    
    // Act - Executar ação
    UsuarioResponse response = userService.criar(request);
    
    // Assert - Verificar resultado
    assertNotNull(response);
    assertEquals("valor esperado", response.nome());
}
```

### Padrão Given-When-Then (REST Assured)
```java
@Test
void testEndpoint() {
    given()
        .contentType(ContentType.JSON)
        .body(request)
    .when()
        .post("/usuario")
    .then()
        .statusCode(201)
        .body("nome", equalTo("João"));
}
```

---

## 🐛 Troubleshooting

### Erro: "Teste não encontrado"
```bash
# Verificar se o arquivo está em src/test/java
ls src/test/java/br/pr/puc/restaurante/service/
```

### Erro: "Dependência não encontrada"
```bash
# Instalar dependências
./mvnw dependency:resolve
```

### Erro: "Conexão com banco de dados recusada"
- Certifique-se que o PostgreSQL está rodando
- Ou use o docker-compose: `docker compose up -d db`

### Erro: "@InjectMock não funciona"
- Verifique que Mockito está no pom.xml
- Use `mockito-junit-jupiter` para suporte a JUnit 5

---

## 💡 Boas Práticas

✅ Cada teste testa UM comportamento
✅ Use nomes descritivos com `@DisplayName`
✅ Organize testes com `@Order`
✅ Use `@BeforeEach` para setup comum
✅ Limpe dados após testes (`@Transactional` cuida disso)
✅ Mock dependências externas
✅ Não teste a biblioteca (Panache, REST Assured)
✅ Teste regras de negócio da aplicação

---

## 📚 Referências

- [Quarkus Testing Guide](https://quarkus.io/guides/getting-started-testing)
- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [REST Assured Documentation](https://github.com/rest-assured/rest-assured/wiki/Usage)
- [Hibernate Panache Testing](https://quarkus.io/guides/hibernate-orm-panache)
