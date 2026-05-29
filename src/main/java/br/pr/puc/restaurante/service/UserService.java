package br.pr.puc.restaurante.service;

import br.pr.puc.restaurante.model.entity.Usuario;
import br.pr.puc.restaurante.model.request.CreateUsuarioRequest;
import br.pr.puc.restaurante.model.request.LoginRequest;
import br.pr.puc.restaurante.model.request.UpdateUsuarioRequest;
import br.pr.puc.restaurante.model.response.LoginResponse;
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
    public UsuarioRepository usuarioRepository;

    // LOGIN
    public LoginResponse login(LoginRequest loginRequest) {
        validaLoginRequest(loginRequest);

        var usuario = usuarioRepository.buscaUsuario(loginRequest.user(), loginRequest.password());
        return mapToLoginResponse(usuario);
    }

    // CREATE
    public UsuarioResponse criar(CreateUsuarioRequest request) {
        validaCreateRequest(request);
        
        Usuario usuario = new Usuario();
        usuario.setLogin(request.login());
        usuario.setSenha(request.senha());
        usuario.setNome(request.nome());
        
        usuarioRepository.persist(usuario);
        return mapToResponse(usuario);
    }

    // READ
    public UsuarioResponse obterPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID invalido");
        }
        
        Usuario usuario = usuarioRepository.findById(id);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario nao encontrado");
        }
        return mapToResponse(usuario);
    }

    // LIST ALL
    public List<UsuarioResponse> listarTodos() {
        return usuarioRepository.listAll()
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    // UPDATE
    public UsuarioResponse atualizar(Long id, UpdateUsuarioRequest request) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID invalido");
        }
        
        if (request == null || isBlank(request.nome())) {
            throw new IllegalArgumentException("Nome deve ser informado");
        }
        
        Usuario usuario = usuarioRepository.findById(id);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario nao encontrado");
        }
        
        usuario.setNome(request.nome());
        usuarioRepository.persist(usuario);
        
        return mapToResponse(usuario);
    }

    // DELETE
    public void deletar(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID invalido");
        }
        
        Usuario usuario = usuarioRepository.findById(id);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario nao encontrado");
        }
        usuarioRepository.delete(usuario);
    }

    // Validacoes
    private void validaLoginRequest(LoginRequest loginRequest) {
        if (loginRequest == null) {
            throw new IllegalArgumentException("Dados de login nao informados.");
        }

        if (isBlank(loginRequest.user()) || isBlank(loginRequest.password())) {
            throw new IllegalArgumentException("Usuario e senha devem ser informados.");
        }
    }

    private void validaCreateRequest(CreateUsuarioRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Dados nao informados");
        }
        
        if (isBlank(request.login())) {
            throw new IllegalArgumentException("Login deve ser informado");
        }
        
        if (isBlank(request.senha())) {
            throw new IllegalArgumentException("Senha deve ser informada");
        }
        
        if (isBlank(request.nome())) {
            throw new IllegalArgumentException("Nome deve ser informado");
        }

        // Verificar se login ja existe
        Usuario usuarioExistente = usuarioRepository.findByLogin(request.login());
        if (usuarioExistente != null) {
            throw new IllegalArgumentException("Login ja existe");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    // Mapeamentos
    private LoginResponse mapToLoginResponse(Usuario usuario) {
        if (usuario == null) {
            return new LoginResponse(false, null, null);
        }

        return new LoginResponse(
                true,
                BigDecimal.valueOf(usuario.getId()),
                usuario.getNome()
        );
    }

    private UsuarioResponse mapToResponse(Usuario usuario) {
        return new UsuarioResponse(
            BigDecimal.valueOf(usuario.getId()),
            usuario.getLogin(),
            usuario.getNome()
        );
    }
}
