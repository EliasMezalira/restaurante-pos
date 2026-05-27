package br.pr.puc.restaurante.service;

import br.pr.puc.restaurante.model.entity.Usuario;
import br.pr.puc.restaurante.model.request.LoginRequest;
import br.pr.puc.restaurante.model.response.LoginResponse;
import br.pr.puc.restaurante.repository.UsuarioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.math.BigDecimal;

@ApplicationScoped
public class UserService {
    @Inject
    public UsuarioRepository usuarioRepository;

    public LoginResponse login(LoginRequest loginRequest) {
        validaLoginRequest(loginRequest);

        var usuario = usuarioRepository.buscaUsuario(loginRequest.user(), loginRequest.password());
        return mapToResponse(usuario);
    }

    private void validaLoginRequest(LoginRequest loginRequest) {
        if (loginRequest == null) {
            throw new IllegalArgumentException("Dados de login nao informados.");
        }

        if (isBlank(loginRequest.user()) || isBlank(loginRequest.password())) {
            throw new IllegalArgumentException("Usuario e senha devem ser informados.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private LoginResponse mapToResponse(Usuario usuario) {
        if (usuario == null) {
            return new LoginResponse(false, null, null);
        }

        return new LoginResponse(
                true,
                BigDecimal.valueOf(usuario.getId()),
                usuario.getNome()
        );
    }
}
