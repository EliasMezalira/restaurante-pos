package br.pr.puc.restaurante.service;

import br.pr.puc.restaurante.model.entity.Cliente;
import br.pr.puc.restaurante.model.request.ClienteCreateRequest;
import br.pr.puc.restaurante.model.request.ClienteUpdateRequest;
import br.pr.puc.restaurante.repository.ClienteRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class ClienteService {

    @Inject
    ClienteRepository repository;

    public Cliente findById(Long id) {
        return repository.findById(id);
    }

    public List<Cliente> listAll(String sortBy, String orderBy, int pageSize, int page) {
        Sort.Direction direction = orderBy.equalsIgnoreCase("desc") ? Sort.Direction.Descending : Sort.Direction.Ascending;
        return repository.findAll(Sort.by(sortBy).direction(direction))
                .page(page, pageSize)
                .list();
    }

    @Transactional
    public Cliente create(ClienteCreateRequest request) {
        if (request.nome() == null || request.nome().isBlank()) throw new IllegalArgumentException("Nome é obrigatório");

        Cliente cliente = new Cliente();
        cliente.setNome(request.nome());
        cliente.setTelefone(request.telefone());
        cliente.setRua(request.rua());
        cliente.setNumero(request.numero());
        cliente.setBairro(request.bairro());

        repository.persist(cliente);
        return cliente;
    }

    @Transactional
    public Cliente update(Long id, ClienteUpdateRequest request) {
        Cliente cliente = repository.findById(id);
        if (cliente == null) return null;

        cliente.setNome(request.nome());
        cliente.setTelefone(request.telefone());
        cliente.setRua(request.rua());
        cliente.setNumero(request.numero());
        cliente.setBairro(request.bairro());

        return cliente;
    }

    @Transactional
    public boolean delete(Long id) {
        return repository.deleteById(id);
    }
}