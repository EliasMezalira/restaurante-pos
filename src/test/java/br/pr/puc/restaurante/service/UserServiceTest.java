package br.pr.puc.restaurante.service;

import br.pr.puc.restaurante.model.entity.Usuario;
import br.pr.puc.restaurante.model.request.CreateUsuarioRequest;
import br.pr.puc.restaurante.model.request.LoginRequest;
import br.pr.puc.restaurante.model.request.UpdateUsuarioRequest;
import br.pr.puc.restaurante.model.response.LoginResponse;
import br.pr.puc.restaurante.model.response.UsuarioResponse;
import br.pr.puc.restaurante.repository.UsuarioRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@QuarkusTest
@DisplayName("Testes da Service de Usuário")
class UserServiceTest {

    @Inject
    UserService userService;

    @InjectMock
    UsuarioRepository usuarioRepository;

    private Usuario usuarioMock;

    @BeforeEach
    void setUp() {
        usuarioMock = new Usuario();
        usuarioMock.setId(1L);
        usuarioMock.setNome("João Silva");
        usuarioMock.setLogin("joao.silva");
        usuarioMock.setSenha("senha123");
    }

    // ==================== TESTES LOGIN ====================
    @Test
    @DisplayName("Deve fazer login com credenciais válidas")
    void testLoginComCredenciaisValidas() {
        LoginRequest loginRequest = new LoginRequest("joao.silva", "senha123");
        when(usuarioRepository.buscaUsuario("joao.silva", "senha123")).thenReturn(usuarioMock);

        LoginResponse response = userService.login(loginRequest);

        assertNotNull(response);
        assertTrue(response.sucesso());
        assertEquals(BigDecimal.valueOf(1L), response.id());
        assertEquals("João Silva", response.nome());
    }

    @Test
    @DisplayName("Deve falhar ao fazer login com usuário não encontrado")
    void testLoginUsuarioNaoEncontrado() {
        LoginRequest loginRequest = new LoginRequest("usuario.inexistente", "senha123");
        when(usuarioRepository.buscaUsuario("usuario.inexistente", "senha123")).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> userService.login(loginRequest));
    }

    @Test
    @DisplayName("Deve falhar ao fazer login com LoginRequest nulo")
    void testLoginComRequestNulo() {
        assertThrows(IllegalArgumentException.class, () -> userService.login(null));
    }

    @Test
    @DisplayName("Deve falhar ao fazer login com usuário em branco")
    void testLoginComUsuarioEmBranco() {
        LoginRequest loginRequest = new LoginRequest("", "senha123");
        assertThrows(IllegalArgumentException.class, () -> userService.login(loginRequest));
    }

    @Test
    @DisplayName("Deve falhar ao fazer login com senha em branco")
    void testLoginComSenhaEmBranco() {
        LoginRequest loginRequest = new LoginRequest("joao.silva", "");
        assertThrows(IllegalArgumentException.class, () -> userService.login(loginRequest));
    }

    // ==================== TESTES CRIAR ====================
    @Test
    @DisplayName("Deve criar usuário com dados válidos")
    void testCriarUsuarioComDadosValidos() {
        CreateUsuarioRequest request = new CreateUsuarioRequest("novo.user", "senha123", "Novo Usuário");
        when(usuarioRepository.findByLogin("novo.user")).thenReturn(null);

        UsuarioResponse response = userService.criar(request);

        assertNotNull(response);
        verify(usuarioRepository, times(1)).findByLogin("novo.user");
        verify(usuarioRepository, times(1)).persist(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve falhar ao criar usuário com CreateUsuarioRequest nulo")
    void testCriarComRequestNulo() {
        assertThrows(IllegalArgumentException.class, () -> userService.criar(null));
    }

    @Test
    @DisplayName("Deve falhar ao criar usuário com login em branco")
    void testCriarComLoginEmBranco() {
        CreateUsuarioRequest request = new CreateUsuarioRequest("", "senha123", "Novo Usuário");
        assertThrows(IllegalArgumentException.class, () -> userService.criar(request));
    }

    @Test
    @DisplayName("Deve falhar ao criar usuário com senha em branco")
    void testCriarComSenhaEmBranco() {
        CreateUsuarioRequest request = new CreateUsuarioRequest("novo.user", "", "Novo Usuário");
        assertThrows(IllegalArgumentException.class, () -> userService.criar(request));
    }

    @Test
    @DisplayName("Deve falhar ao criar usuário com nome em branco")
    void testCriarComNomeEmBranco() {
        CreateUsuarioRequest request = new CreateUsuarioRequest("novo.user", "senha123", "");
        assertThrows(IllegalArgumentException.class, () -> userService.criar(request));
    }

    @Test
    @DisplayName("Deve falhar ao criar usuário com login duplicado")
    void testCriarComLoginDuplicado() {
        CreateUsuarioRequest request = new CreateUsuarioRequest("joao.silva", "senha123", "João Silva");
        when(usuarioRepository.findByLogin("joao.silva")).thenReturn(usuarioMock);

        assertThrows(IllegalArgumentException.class, () -> userService.criar(request));
        verify(usuarioRepository, times(1)).findByLogin("joao.silva");
    }

    // ==================== TESTES OBTER POR ID ====================
    @Test
    @DisplayName("Deve obter usuário por ID válido")
    void testObterPorIdComIdValido() {
        when(usuarioRepository.findById(1L)).thenReturn(usuarioMock);

        UsuarioResponse response = userService.obterPorId(1L);

        assertNotNull(response);
        assertEquals(BigDecimal.valueOf(1L), response.id());
        assertEquals("João Silva", response.nome());
        verify(usuarioRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve falhar ao obter usuário com ID nulo")
    void testObterPorIdComIdNulo() {
        assertThrows(IllegalArgumentException.class, () -> userService.obterPorId(null));
    }

    @Test
    @DisplayName("Deve falhar ao obter usuário com ID inválido (zero)")
    void testObterPorIdComIdZero() {
        assertThrows(IllegalArgumentException.class, () -> userService.obterPorId(0L));
    }

    @Test
    @DisplayName("Deve falhar ao obter usuário com ID inválido (negativo)")
    void testObterPorIdComIdNegativo() {
        assertThrows(IllegalArgumentException.class, () -> userService.obterPorId(-1L));
    }

    @Test
    @DisplayName("Deve falhar ao obter usuário não encontrado")
    void testObterPorIdUsuarioNaoEncontrado() {
        when(usuarioRepository.findById(999L)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> userService.obterPorId(999L));
    }

    // ==================== TESTES LISTAR ====================
    @Test
    @DisplayName("Deve listar todos os usuários")
    void testListarTodosUsuarios() {
        Usuario usuario2 = new Usuario();
        usuario2.setId(2L);
        usuario2.setNome("Maria Silva");
        usuario2.setLogin("maria.silva");
        usuario2.setSenha("senha456");

        List<Usuario> usuarios = Arrays.asList(usuarioMock, usuario2);
        when(usuarioRepository.listAll()).thenReturn(usuarios);

        List<UsuarioResponse> response = userService.listarTodos();

        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals("João Silva", response.get(0).nome());
        assertEquals("Maria Silva", response.get(1).nome());
        verify(usuarioRepository, times(1)).listAll();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há usuários")
    void testListarComListaVazia() {
        when(usuarioRepository.listAll()).thenReturn(Arrays.asList());

        List<UsuarioResponse> response = userService.listarTodos();

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    // ==================== TESTES ATUALIZAR ====================
    @Test
    @DisplayName("Deve atualizar usuário com dados válidos")
    void testAtualizarComDadosValidos() {
        UpdateUsuarioRequest request = new UpdateUsuarioRequest("João Silva Atualizado");
        when(usuarioRepository.findById(1L)).thenReturn(usuarioMock);

        UsuarioResponse response = userService.atualizar(1L, request);

        assertNotNull(response);
        verify(usuarioRepository, times(1)).findById(1L);
        verify(usuarioRepository, times(1)).persist(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve falhar ao atualizar com ID nulo")
    void testAtualizarComIdNulo() {
        UpdateUsuarioRequest request = new UpdateUsuarioRequest("João Silva");
        assertThrows(IllegalArgumentException.class, () -> userService.atualizar(null, request));
    }

    @Test
    @DisplayName("Deve falhar ao atualizar com ID inválido (zero)")
    void testAtualizarComIdZero() {
        UpdateUsuarioRequest request = new UpdateUsuarioRequest("João Silva");
        assertThrows(IllegalArgumentException.class, () -> userService.atualizar(0L, request));
    }

    @Test
    @DisplayName("Deve falhar ao atualizar com UpdateUsuarioRequest nulo")
    void testAtualizarComRequestNulo() {
        assertThrows(IllegalArgumentException.class, () -> userService.atualizar(1L, null));
    }

    @Test
    @DisplayName("Deve falhar ao atualizar com nome em branco")
    void testAtualizarComNomeEmBranco() {
        UpdateUsuarioRequest request = new UpdateUsuarioRequest("");
        assertThrows(IllegalArgumentException.class, () -> userService.atualizar(1L, request));
    }

    @Test
    @DisplayName("Deve falhar ao atualizar usuário não encontrado")
    void testAtualizarUsuarioNaoEncontrado() {
        UpdateUsuarioRequest request = new UpdateUsuarioRequest("João Silva");
        when(usuarioRepository.findById(999L)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> userService.atualizar(999L, request));
    }

    // ==================== TESTES DELETAR ====================
    @Test
    @DisplayName("Deve deletar usuário com ID válido")
    void testDeletarComIdValido() {
        when(usuarioRepository.findById(1L)).thenReturn(usuarioMock);

        userService.deletar(1L);

        verify(usuarioRepository, times(1)).findById(1L);
        verify(usuarioRepository, times(1)).delete(usuarioMock);
    }

    @Test
    @DisplayName("Deve falhar ao deletar com ID nulo")
    void testDeletarComIdNulo() {
        assertThrows(IllegalArgumentException.class, () -> userService.deletar(null));
    }

    @Test
    @DisplayName("Deve falhar ao deletar com ID inválido (zero)")
    void testDeletarComIdZero() {
        assertThrows(IllegalArgumentException.class, () -> userService.deletar(0L));
    }

    @Test
    @DisplayName("Deve falhar ao deletar com ID inválido (negativo)")
    void testDeletarComIdNegativo() {
        assertThrows(IllegalArgumentException.class, () -> userService.deletar(-1L));
    }

    @Test
    @DisplayName("Deve falhar ao deletar usuário não encontrado")
    void testDeletarUsuarioNaoEncontrado() {
        when(usuarioRepository.findById(999L)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> userService.deletar(999L));
    }
}
