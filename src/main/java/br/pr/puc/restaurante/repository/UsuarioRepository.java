package br.pr.puc.restaurante.repository;

import br.pr.puc.restaurante.model.entity.Usuario;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

public class UsuarioRepository implements PanacheRepository<Usuario> {
    public Usuario buscaUsuario(String username, String password){
        return find("").firstResult();

    }

}
