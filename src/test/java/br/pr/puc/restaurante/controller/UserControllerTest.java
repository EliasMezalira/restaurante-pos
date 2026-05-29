package br.pr.puc.restaurante.controller;

import br.pr.puc.restaurante.model.request.CreateUsuarioRequest;
import br.pr.puc.restaurante.model.request.LoginRequest;
import br.pr.puc.restaurante.model.request.UpdateUsuarioRequest;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@DisplayName("Testes de Integração da API de Usuário")
class UserControllerTest {

    // ==================== TESTES CRIAR ====================
    @Test
    @Order(1)
    @DisplayName("Deve criar um novo usuário com sucesso")
    void testCriarUsuarioComSucesso() {
        CreateUsuarioRequest request = new CreateUsuarioRequest(
                "joao.silva",
                "senha123",
                "João Silva"
        );

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/usuario")
                .then()
                .statusCode(201)
                .body("nome", equalTo("João Silva"))
                .body("login", equalTo("joao.silva"))
                .body("id", notNullValue());
    }

    @Test
    @Order(2)
    @DisplayName("Deve retornar 400 ao criar usuário com login duplicado")
    void testCriarUsuarioComLoginDuplicado() {
        CreateUsuarioRequest request1 = new CreateUsuarioRequest(
                "usuario.teste",
                "senha123",
                "Usuário Teste"
        );

        // Criar primeiro usuário
        given()
                .contentType(ContentType.JSON)
                .body(request1)
                .when()
                .post("/usuario")
                .then()
                .statusCode(201);

        // Tentar criar segundo com mesmo login
        given()
                .contentType(ContentType.JSON)
                .body(request1)
                .when()
                .post("/usuario")
                .then()
                .statusCode(400)
                .body(containsString("Login ja existe"));
    }

    @Test
    @Order(3)
    @DisplayName("Deve retornar 400 ao criar usuário sem login")
    void testCriarUsuarioSemLogin() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"senha\": \"senha123\", \"nome\": \"Teste\"}")
                .when()
                .post("/usuario")
                .then()
                .statusCode(400);
    }

    @Test
    @Order(4)
    @DisplayName("Deve retornar 400 ao criar usuário sem senha")
    void testCriarUsuarioSemSenha() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"login\": \"teste\", \"nome\": \"Teste\"}")
                .when()
                .post("/usuario")
                .then()
                .statusCode(400);
    }

    @Test
    @Order(5)
    @DisplayName("Deve retornar 400 ao criar usuário sem nome")
    void testCriarUsuarioSemNome() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"login\": \"teste\", \"senha\": \"senha123\"}")
                .when()
                .post("/usuario")
                .then()
                .statusCode(400);
    }

    // ==================== TESTES LOGIN ====================
    @Test
    @Order(10)
    @DisplayName("Deve fazer login com credenciais válidas")
    void testLoginComCredenciaisValidas() {
        // Criar usuário primeiro
        CreateUsuarioRequest createRequest = new CreateUsuarioRequest(
                "login.teste",
                "senha123",
                "Usuário Login"
        );

        given()
                .contentType(ContentType.JSON)
                .body(createRequest)
                .when()
                .post("/usuario")
                .then()
                .statusCode(201);

        // Fazer login
        LoginRequest loginRequest = new LoginRequest("login.teste", "senha123");

        given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post("/usuario/login")
                .then()
                .statusCode(200)
                .body("sucesso", equalTo(true))
                .body("id", notNullValue())
                .body("nome", equalTo("Usuário Login"));
    }

    @Test
    @Order(11)
    @DisplayName("Deve retornar 400 ao fazer login com usuário inválido")
    void testLoginComUsuarioInvalido() {
        LoginRequest loginRequest = new LoginRequest("usuario.inexistente", "senha123");

        given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post("/usuario/login")
                .then()
                .statusCode(400);
    }

    @Test
    @Order(12)
    @DisplayName("Deve retornar 400 ao fazer login sem usuário")
    void testLoginSemUsuario() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"password\": \"senha123\"}")
                .when()
                .post("/usuario/login")
                .then()
                .statusCode(400);
    }

    @Test
    @Order(13)
    @DisplayName("Deve retornar 400 ao fazer login sem senha")
    void testLoginSemSenha() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"user\": \"teste\"}")
                .when()
                .post("/usuario/login")
                .then()
                .statusCode(400);
    }

    // ==================== TESTES LISTAR ====================
    @Test
    @Order(20)
    @DisplayName("Deve listar todos os usuários")
    void testListarTodosUsuarios() {
        given()
                .when()
                .get("/usuario")
                .then()
                .statusCode(200)
                .body("$", hasSize(greaterThanOrEqualTo(0)));
    }

    // ==================== TESTES OBTER POR ID ====================
    @Test
    @Order(30)
    @DisplayName("Deve obter usuário por ID válido")
    void testObterUsuarioPorIdValido() {
        // Criar usuário
        CreateUsuarioRequest request = new CreateUsuarioRequest(
                "usuario.obter",
                "senha123",
                "Usuário Obter"
        );

        String usuarioResponse = given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/usuario")
                .then()
                .statusCode(201)
                .extract()
                .asString();

        // Obter ID do usuário criado
        Long id = given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/usuario")
                .then()
                .extract()
                .path("id");

        // Obter usuário
        given()
                .when()
                .get("/usuario/" + id)
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("nome", equalTo("Usuário Obter"));
    }

    @Test
    @Order(31)
    @DisplayName("Deve retornar 404 ao obter usuário com ID inválido")
    void testObterUsuarioPorIdInvalido() {
        given()
                .when()
                .get("/usuario/99999")
                .then()
                .statusCode(404);
    }

    @Test
    @Order(32)
    @DisplayName("Deve retornar 400 ao obter usuário com ID zero")
    void testObterUsuarioPorIdZero() {
        given()
                .when()
                .get("/usuario/0")
                .then()
                .statusCode(400);
    }

    // ==================== TESTES ATUALIZAR ====================
    @Test
    @Order(40)
    @DisplayName("Deve atualizar usuário com dados válidos")
    void testAtualizarUsuarioComDadosValidos() {
        // Criar usuário
        CreateUsuarioRequest createRequest = new CreateUsuarioRequest(
                "usuario.atualizar",
                "senha123",
                "Usuário Atualizar"
        );

        Long id = given()
                .contentType(ContentType.JSON)
                .body(createRequest)
                .when()
                .post("/usuario")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        // Atualizar
        UpdateUsuarioRequest updateRequest = new UpdateUsuarioRequest("Usuário Atualizado");

        given()
                .contentType(ContentType.JSON)
                .body(updateRequest)
                .when()
                .put("/usuario/" + id)
                .then()
                .statusCode(200)
                .body("nome", equalTo("Usuário Atualizado"));
    }

    @Test
    @Order(41)
    @DisplayName("Deve retornar 400 ao atualizar usuário com nome vazio")
    void testAtualizarUsuarioComNomeVazio() {
        // Criar usuário
        CreateUsuarioRequest createRequest = new CreateUsuarioRequest(
                "usuario.atualizar2",
                "senha123",
                "Usuário Atualizar"
        );

        Long id = given()
                .contentType(ContentType.JSON)
                .body(createRequest)
                .when()
                .post("/usuario")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        // Tentar atualizar com nome vazio
        given()
                .contentType(ContentType.JSON)
                .body("{\"nome\": \"\"}")
                .when()
                .put("/usuario/" + id)
                .then()
                .statusCode(400);
    }

    @Test
    @Order(42)
    @DisplayName("Deve retornar 404 ao atualizar usuário inexistente")
    void testAtualizarUsuarioInexistente() {
        UpdateUsuarioRequest updateRequest = new UpdateUsuarioRequest("Novo Nome");

        given()
                .contentType(ContentType.JSON)
                .body(updateRequest)
                .when()
                .put("/usuario/99999")
                .then()
                .statusCode(404);
    }

    // ==================== TESTES DELETAR ====================
    @Test
    @Order(50)
    @DisplayName("Deve deletar usuário com sucesso")
    void testDeletarUsuarioComSucesso() {
        // Criar usuário
        CreateUsuarioRequest createRequest = new CreateUsuarioRequest(
                "usuario.deletar",
                "senha123",
                "Usuário Deletar"
        );

        Long id = given()
                .contentType(ContentType.JSON)
                .body(createRequest)
                .when()
                .post("/usuario")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        // Deletar
        given()
                .when()
                .delete("/usuario/" + id)
                .then()
                .statusCode(204);

        // Verificar que foi deletado
        given()
                .when()
                .get("/usuario/" + id)
                .then()
                .statusCode(404);
    }

    @Test
    @Order(51)
    @DisplayName("Deve retornar 404 ao deletar usuário inexistente")
    void testDeletarUsuarioInexistente() {
        given()
                .when()
                .delete("/usuario/99999")
                .then()
                .statusCode(404);
    }

    @Test
    @Order(52)
    @DisplayName("Deve retornar 400 ao deletar usuário com ID zero")
    void testDeletarUsuarioPorIdZero() {
        given()
                .when()
                .delete("/usuario/0")
                .then()
                .statusCode(400);
    }
}
