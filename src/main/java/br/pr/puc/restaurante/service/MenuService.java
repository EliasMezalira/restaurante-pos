package br.pr.puc.restaurante.service;

import br.pr.puc.restaurante.model.entity.Menu;
import br.pr.puc.restaurante.model.request.ItemCardapioCreateRequest;
import br.pr.puc.restaurante.model.request.ItemCardapioUpdateRequest;
import br.pr.puc.restaurante.repository.MenuRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class MenuService {

    @Inject
    MenuRepository repository;

    public Menu findById(Long id) {
        return repository.findById(id);
    }

    public List<Menu> listAll(String categoria, String sortBy, String orderBy, int pageSize, int page) {
        Sort.Direction direction = orderBy.equalsIgnoreCase("desc") ? Sort.Direction.Descending : Sort.Direction.Ascending;
        Sort sort = Sort.by(sortBy).direction(direction);

        var query = (categoria != null && !categoria.isBlank())
                ? repository.find("categoria", sort, categoria)
                : repository.findAll(sort);

        return query.page(page, pageSize).list();
    }

    public long countAll(String categoria) {
        if (categoria != null && !categoria.isBlank()) {
            return repository.count("categoria", categoria);
        }
        return repository.count();
    }

    @Transactional
    public Menu create(ItemCardapioCreateRequest request) {
        if (request.nome() == null || request.nome().isBlank()) throw new IllegalArgumentException("Nome é obrigatório");

        Menu menu = new Menu();
        menu.setNome(request.nome());
        menu.setIngredientes(request.ingredientes());
        menu.setCategoria(request.categoria());
        menu.setValor(request.valor());

        repository.persist(menu);
        return menu;
    }

    @Transactional
    public Menu update(Long id, ItemCardapioUpdateRequest request) {
        Menu menu = repository.findById(id);
        if (menu == null) return null;

        menu.setNome(request.nome());
        menu.setIngredientes(request.ingredientes());
        menu.setCategoria(request.categoria());
        menu.setValor(request.valor());

        return menu;
    }

    @Transactional
    public boolean delete(Long id) {
        return repository.deleteById(id);
    }
}