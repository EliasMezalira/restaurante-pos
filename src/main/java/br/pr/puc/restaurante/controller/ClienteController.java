package br.pr.puc.restaurante.controller;

import br.pr.puc.restaurante.model.request.ClienteCreateRequest;
import br.pr.puc.restaurante.model.request.ClienteUpdateRequest;
import br.pr.puc.restaurante.service.ClienteService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/clientes")
@Tag(name = "Cliente", description = "Operações de gerenciamento de clientes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClienteController {

    private static final String HEADER_TOTAL_COUNT = "X-Paging-Total-Count";
    private static final String HEADER_CURRENT_PAGE = "X-Paging-Current-Page";

    @Inject
    public ClienteService clienteService;

    @GET
    @Path("/{id}")
    @Operation(summary = "Busca cliente por ID", description = "Retorna os dados de um cliente específico.")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Cliente encontrado"),
            @APIResponse(responseCode = "404", description = "Cliente não encontrado"),
            @APIResponse(responseCode = "500", description = "Erro interno")
    })
    public Response searchById(@PathParam("id") Long id) {
        try {
            var cliente = clienteService.findById(id);
            if (cliente == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(cliente).build();
        } catch (RuntimeException e) {
            return Response.serverError().build();
        }
    }

    @GET
    @Operation(summary = "Lista clientes", description = "Retorna uma lista paginada de clientes.")
    @APIResponse(responseCode = "200", description = "Lista de clientes")
    public Response search(@QueryParam("sort") @DefaultValue("nome") String sortBy,
                           @QueryParam("order") @DefaultValue("asc") String orderBy,
                           @QueryParam("pageSize") @DefaultValue("10") int pageSize,
                           @QueryParam("page") @DefaultValue("0") int page) {
        try {
            var items = clienteService.listAll(sortBy, orderBy, pageSize, page);
            long total = clienteService.countAll();
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
    @Operation(summary = "Exclui um cliente", description = "Remove um cliente pelo seu ID.")
    @APIResponses({
            @APIResponse(responseCode = "204", description = "Cliente excluído com sucesso"),
            @APIResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    public Response delete(@PathParam("id") Long id) {
        try {
            boolean deleted = clienteService.delete(id);
            if (!deleted) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.noContent().build();
        } catch (RuntimeException e) {
            return Response.serverError().build();
        }
    }

    @POST
    @Operation(summary = "Cria um novo cliente", description = "Cadastra um novo cliente no sistema.")
    @APIResponses({
            @APIResponse(responseCode = "201", description = "Cliente criado com sucesso"),
            @APIResponse(responseCode = "400", description = "Dados inválidos")
    })
    public Response create(@RequestBody(required = true) ClienteCreateRequest request) {
        try {
            var created = clienteService.create(request);
            return Response.status(Response.Status.CREATED).entity(created).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (RuntimeException e) {
            return Response.serverError().build();
        }
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Atualiza um cliente", description = "Atualiza os dados de um cliente existente.")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Cliente atualizado com sucesso"),
            @APIResponse(responseCode = "400", description = "Dados inválidos"),
            @APIResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    public Response update(@PathParam("id") Long id, @RequestBody(required = true) ClienteUpdateRequest request) {
        try {
            var updated = clienteService.update(id, request);
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