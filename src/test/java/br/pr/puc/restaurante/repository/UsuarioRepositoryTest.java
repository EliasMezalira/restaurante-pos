package br.pr.puc.restaurante.repository;

import br.pr.puc.restaurante.model.entity.Usuario;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@DisplayName("Testes de Integração do Repository de Usuário")
class UsuarioRepositoryTest {

    @Inject
    UsuarioRepository usuarioRepository;

    private Usuario usuarioTeste;

    @BeforeEach
    @Transactional
    void setUp() {
        // Limpar dados antes de cada teste
        usuarioRepository.deleteAll();

        usuarioTeste = new Usuario();
        usuarioTeste.setNome("João Silva");
        usuarioTeste.setLogin("joao.silva");
        usuarioTeste.setSenha("senha123");
    }

    // ==================== TESTES PERSIST ====================
    @Test
    @Transactional
    @DisplayName("Deve persistir um novo usuário no banco de dados")
    void testPersistirNovoUsuario() {
        usuarioRepository.persist(usuarioTeste);

        assertNotNull(usuarioTeste.getId());
        assertTrue(usuarioTeste.getId() > 0);
    }

    @Test
    @Transactional
    @DisplayName("Deve persistir múltiplos usuários")
    void testPersistirMultiplosUsuarios() {
        Usuario usuario2 = new Usuario();
        usuario2.setNome("Maria Silva");
        usuario2.setLogin("maria.silva");
        usuario2.setSenha("senha456");

        usuarioRepository.persist(usuarioTeste);
        usuarioRepository.persist(usuario2);

        List<Usuario> usuarios = usuarioRepository.listAll();
        assertEquals(2, usuarios.size());
    }

    // ==================== TESTES FIND BY ID ====================
    @Test
    @Transactional
    @DisplayName("Deve encontrar usuário por ID")
    void testFindById() {
        usuarioRepository.persist(usuarioTeste);
        Long id = usuarioTeste.getId();

        Usuario usuario = usuarioRepository.findById(id);

        assertNotNull(usuario);
        assertEquals("João Silva", usuario.getNome());
        assertEquals("joao.silva", usuario.getLogin());
    }

    @Test
    @Transactional
    @DisplayName("Deve retornar null ao buscar usuário com ID inexistente")
    void testFindByIdInexistente() {
        Usuario usuario = usuarioRepository.findById(99999L);
        assertNull(usuario);
    }

    // ==================== TESTES FIND BY LOGIN ====================
    @Test
    @Transactional
    @DisplayName("Deve encontrar usuário por login")
    void testFindByLogin() {
        usuarioRepository.persist(usuarioTeste);

        Usuario usuario = usuarioRepository.findByLogin("joao.silva");

        assertNotNull(usuario);
        assertEquals("João Silva", usuario.getNome());
    }

    @Test
    @Transactional
    @DisplayName("Deve retornar null ao buscar usuário com login inexistente")
    void testFindByLoginInexistente() {
        Usuario usuario = usuarioRepository.findByLogin("login.inexistente");
        assertNull(usuario);
    }

    // ==================== TESTES BUSCA USUARIO ====================
    @Test
    @Transactional
    @DisplayName("Deve buscar usuário com credenciais válidas")
    void testBuscaUsuarioComCredenciaisValidas() {
        usuarioRepository.persist(usuarioTeste);

        Usuario usuario = usuarioRepository.buscaUsuario("joao.silva", "senha123");

        assertNotNull(usuario);
        assertEquals("João Silva", usuario.getNome());
    }

    @Test
    @Transactional
    @DisplayName("Deve retornar null ao buscar usuário com senha incorreta")
    void testBuscaUsuarioComSenhaIncorreta() {
        usuarioRepository.persist(usuarioTeste);

        Usuario usuario = usuarioRepository.buscaUsuario("joao.silva", "senhaErrada");

        assertNull(usuario);
    }

    @Test
    @Transactional
    @DisplayName("Deve retornar null ao buscar usuário com login inexistente")
    void testBuscaUsuarioComLoginInexistente() {
        Usuario usuario = usuarioRepository.buscaUsuario("login.inexistente", "senha123");

        assertNull(usuario);
    }

    // ==================== TESTES LIST ALL ====================
    @Test
    @Transactional
    @DisplayName("Deve listar todos os usuários")
    void testListAll() {
        Usuario usuario2 = new Usuario();
        usuario2.setNome("Maria Silva");
        usuario2.setLogin("maria.silva");
        usuario2.setSenha("senha456");

        usuarioRepository.persist(usuarioTeste);
        usuarioRepository.persist(usuario2);

        List<Usuario> usuarios = usuarioRepository.listAll();

        assertNotNull(usuarios);
        assertEquals(2, usuarios.size());
    }

    @Test
    @Transactional
    @DisplayName("Deve retornar lista vazia quando não há usuários")
    void testListAllVazio() {
        List<Usuario> usuarios = usuarioRepository.listAll();

        assertNotNull(usuarios);
        assertTrue(usuarios.isEmpty());
    }

    // ==================== TESTES UPDATE ====================
    @Test
    @Transactional
    @DisplayName("Deve atualizar usuário existente")
    void testAtualizarUsuario() {
        usuarioRepository.persist(usuarioTeste);
        Long id = usuarioTeste.getId();

        Usuario usuario = usuarioRepository.findById(id);
        usuario.setNome("João Silva Atualizado");
        usuarioRepository.persist(usuario);

        Usuario usuarioAtualizado = usuarioRepository.findById(id);
        assertEquals("João Silva Atualizado", usuarioAtualizado.getNome());
    }

    // ==================== TESTES DELETE ====================
    @Test
    @Transactional
    @DisplayName("Deve deletar usuário existente")
    void testDeletarUsuario() {
        usuarioRepository.persist(usuarioTeste);
        Long id = usuarioTeste.getId();

        usuarioRepository.delete(usuarioTeste);

        Usuario usuario = usuarioRepository.findById(id);
        assertNull(usuario);
    }

    @Test
    @Transactional
    @DisplayName("Deve manter outros usuários ao deletar um")
    void testDeletarUsuarioSemAfetar outros() {
        Usuario usuario2 = new Usuario();
        usuario2.setNome("Maria Silva");
        usuario2.setLogin("maria.silva");
        usuario2.setSenha("senha456");

        usuarioRepository.persist(usuarioTeste);
        usuarioRepository.persist(usuario2);

        usuarioRepository.delete(usuarioTeste);

        List<Usuario> usuarios = usuarioRepository.listAll();
        assertEquals(1, usuarios.size());
        assertEquals("Maria Silva", usuarios.get(0).getNome());
    }

    @Test
    @Transactional
    @DisplayName("Deve deletar todos os usuários")
    void testDeleteAll() {
        Usuario usuario2 = new Usuario();
        usuario2.setNome("Maria Silva");
        usuario2.setLogin("maria.silva");
        usuario2.setSenha("senha456");

        usuarioRepository.persist(usuarioTeste);
        usuarioRepository.persist(usuario2);

        usuarioRepository.deleteAll();

        List<Usuario> usuarios = usuarioRepository.listAll();
        assertTrue(usuarios.isEmpty());
    }

    // ==================== TESTES COUNT ====================
    @Test
    @Transactional
    @DisplayName("Deve contar usuários")
    void testCount() {
        Usuario usuario2 = new Usuario();
        usuario2.setNome("Maria Silva");
        usuario2.setLogin("maria.silva");
        usuario2.setSenha("senha456");

        usuarioRepository.persist(usuarioTeste);
        usuarioRepository.persist(usuario2);

        long count = usuarioRepository.count();
        assertEquals(2, count);
    }

    @Test
    @Transactional
    @DisplayName("Deve retornar 0 quando não há usuários")
    void testCountVazio() {
        long count = usuarioRepository.count();
        assertEquals(0, count);
    }
}
