package br.pr.puc.restaurante.repository;

import br.pr.puc.restaurante.model.entity.Garcom;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GarcomRepository implements PanacheRepository<Garcom> {
}