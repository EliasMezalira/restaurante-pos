package br.pr.puc.restaurante.controller;

import br.pr.puc.restaurante.model.request.ItemCardapioCreateRequest;
import br.pr.puc.restaurante.model.request.ItemCardapioUpdateRequest;
import br.pr.puc.restaurante.service.MenuService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/cardapio")
@Tag(name = "Cardápio (Menu)", description = "Operações de gerenciamento do cardápio")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MenuController {

    private static final String HEADER_TOTAL_COUNT = "X-Paging-Total-Count";
    private static final String HEADER_CURRENT_PAGE = "X-Paging-Current-Page";

    @Inject
    public MenuService menuService;

    @GET
    @Path("/{id}")
    @Operation(summary = "Busca item do cardápio por ID")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Item encontrado"),
            @APIResponse(responseCode = "404", description = "Item não encontrado")
    })
    public Response searchById(@PathParam("id") Long id) {
        try {
            var item = menuService.findById(id);
            if (item == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(item).build();
        } catch (RuntimeException e) {
            return Response.serverError().build();
        }
    }

    @GET
    @Operation(summary = "Lista itens do cardápio", description = "Permite filtrar por categoria e paginação.")
    public Response search(@QueryParam("categoria") String categoria,
                           @QueryParam("sort") @DefaultValue("nome") String sortBy,
                           @QueryParam("order") @DefaultValue("asc") String orderBy,
                           @QueryParam("pageSize") @DefaultValue("10") int pageSize,
                           @QueryParam("page") @DefaultValue("0") int page) {
        try {
            var items = menuService.listAll(categoria, sortBy, orderBy, pageSize, page);
            long total = menuService.countAll(categoria);
            return Response.ok(items)
                    .header(HEADER_TOTAL_COUNT, total)
                    .header(HEADER_CURRENT_PAGE, page)
                    .build();
        } catch (RuntimeException e) {
            return Response.serverError().build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Exclui um item do cardápio")
    public Response delete(@PathParam("id") Long id) {
        try {
            boolean deleted = menuService.delete(id);
            if (!deleted) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.noContent().build();
        } catch (RuntimeException e) {
            return Response.serverError().build();
        }
    }

    @POST
    @Operation(summary = "Cria um novo item no cardápio")
    @APIResponses({
            @APIResponse(responseCode = "201", description = "Item criado com sucesso"),
            @APIResponse(responseCode = "400", description = "Dados inválidos")
    })
    public Response create(@RequestBody(required = true) ItemCardapioCreateRequest request) {
        try {
            var created = menuService.create(request);
            return Response.status(Response.Status.CREATED).entity(created).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (RuntimeException e) {
            return Response.serverError().build();
        }
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Atualiza um item do cardápio")
    public Response update(@PathParam("id") Long id, @RequestBody(required = true) ItemCardapioUpdateRequest request) {
        try {
            var updated = menuService.update(id, request);
            if (updated == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(updated).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (RuntimeException e) {
            return Response.serverError().build();
        }
    }
}