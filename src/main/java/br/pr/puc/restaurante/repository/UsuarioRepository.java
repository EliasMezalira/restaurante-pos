package br.pr.puc.restaurante.repository;

import br.pr.puc.restaurante.model.entity.Usuario;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UsuarioRepository implements PanacheRepository<Usuario> {
    public Usuario buscaUsuario(String username, String password){
        return find("login = ?1 and senha = ?2", username, password).firstResult();
    }

    public Usuario findByLogin(String login) {
        return find("login", login).firstResult();
    }
}
