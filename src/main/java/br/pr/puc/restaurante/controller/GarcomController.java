package br.pr.puc.restaurante.controller;

import br.pr.puc.restaurante.model.request.GarcomCreateRequest;
import br.pr.puc.restaurante.model.request.GarcomUpdateRequest;
import br.pr.puc.restaurante.service.GarcomService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/garcom")
@Tag(name = "Garçom", description = "Operações de gerenciamento de garçons")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GarcomController {

    @Inject
    public GarcomService garcomService;

    @GET
    @Path("/{id}")
    @Operation(summary = "Busca garçom por ID")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Garçom encontrado"),
            @APIResponse(responseCode = "404", description = "Garçom não encontrado")
    })
    public Response searchById(@PathParam("id") Long id) {
        try {
            var garcom = garcomService.findById(id);
            if (garcom == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(garcom).build();
        } catch (RuntimeException e) {
            return Response.serverError().build();
        }
    }

    @GET
    @Operation(summary = "Lista garçons", description = "Retorna uma lista paginada.")
    public Response search(@QueryParam("sort") @DefaultValue("nome") String sortBy,
                           @QueryParam("order") @DefaultValue("asc") String orderBy,
                           @QueryParam("pageSize") @DefaultValue("10") int pageSize,
                           @QueryParam("page") @DefaultValue("0") int page) {
        try {
            return Response.ok(garcomService.listAll(sortBy, orderBy, pageSize, page)).build();
        } catch (RuntimeException e) {
            return Response.serverError().build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Exclui um garçom")
    public Response delete(@PathParam("id") Long id) {
        try {
            boolean deleted = garcomService.delete(id);
            if (!deleted) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.noContent().build();
        } catch (RuntimeException e) {
            return Response.serverError().build();
        }
    }

    @POST
    @Operation(summary = "Cria um novo garçom")
    @APIResponses({
            @APIResponse(responseCode = "201", description = "Criado com sucesso"),
            @APIResponse(responseCode = "400", description = "Dados inválidos")
    })
    public Response create(@RequestBody(required = true) GarcomCreateRequest request) {
        try {
            var created = garcomService.create(request);
            return Response.status(Response.Status.CREATED).entity(created).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (RuntimeException e) {
            return Response.serverError().build();
        }
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Atualiza um garçom")
    public Response update(@PathParam("id") Long id, @RequestBody(required = true) GarcomUpdateRequest request) {
        try {
            var updated = garcomService.update(id, request);
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