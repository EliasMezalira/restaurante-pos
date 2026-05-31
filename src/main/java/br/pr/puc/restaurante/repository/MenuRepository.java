package br.pr.puc.restaurante.repository;

import br.pr.puc.restaurante.model.entity.Menu;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MenuRepository implements PanacheRepository<Menu> {
}