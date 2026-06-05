package br.pr.puc.restaurante.service;

import br.pr.puc.restaurante.model.entity.Garcom;
import br.pr.puc.restaurante.model.request.GarcomCreateRequest;
import br.pr.puc.restaurante.model.request.GarcomUpdateRequest;
import br.pr.puc.restaurante.repository.GarcomRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class GarcomService {

    @Inject
    GarcomRepository repository;

    public Garcom findById(Long id) {
        return repository.findById(id);
    }

    public List<Garcom> listAll(String sortBy, String orderBy, int pageSize, int page) {
        Sort.Direction direction = orderBy.equalsIgnoreCase("desc") ? Sort.Direction.Descending : Sort.Direction.Ascending;
        return repository.findAll(Sort.by(sortBy).direction(direction))
                .page(page, pageSize)
                .list();
    }

    public long countAll() {
        return repository.count();
    }

    @Transactional
    public Garcom create(GarcomCreateRequest request) {
        if (request.nome() == null || request.nome().isBlank()) throw new IllegalArgumentException("Nome é obrigatório");

        Garcom garcom = new Garcom();
        garcom.setNome(request.nome());
        garcom.setPercentualGorjeta(request.percentualGorjeta());
        garcom.setIdade(request.idade());

        repository.persist(garcom);
        return garcom;
    }

    @Transactional
    public Garcom update(Long id, GarcomUpdateRequest request) {
        Garcom garcom = repository.findById(id);
        if (garcom == null) return null;

        garcom.setNome(request.nome());
        garcom.setPercentualGorjeta(request.percentualGorjeta());
        garcom.setIdade(request.idade());

        return garcom;
    }

    @Transactional
    public boolean delete(Long id) {
        return repository.deleteById(id);
    }
}