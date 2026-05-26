package br.pr.puc.restaurante.service;

import br.pr.puc.restaurante.model.entity.Usuario;
import br.pr.puc.restaurante.model.request.LoginRequest;
import br.pr.puc.restaurante.model.response.LoginResponse;
import br.pr.puc.restaurante.repository.UsuarioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class UserService {
    @Inject
    public UsuarioRepository usuarioRepository;

    public LoginResponse login(LoginRequest loginRequest) {
        var usuario = usuarioRepository.buscaUsuario(loginRequest.user(), loginRequest.password());
        return mapToResponse(usuario);
    }

    private LoginResponse mapToResponse(Usuario usuario) {
        return null;
    }
}
