package br.pr.puc.restaurante.controller;

import br.pr.puc.restaurante.model.request.CreateUsuarioRequest;
import br.pr.puc.restaurante.model.request.LoginRequest;
import br.pr.puc.restaurante.model.request.UpdateUsuarioRequest;
import br.pr.puc.restaurante.model.response.LoginResponse;
import br.pr.puc.restaurante.model.response.UsuarioResponse;
import br.pr.puc.restaurante.service.UserService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import java.util.List;

@Path("/usuario")
@Tag(name = "Usuario", description = "Operacoes de usuario")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserController {
    @Inject
    public UserService userService;

    // LOGIN
    @POST
    @Path("/login")
    @Operation(summary = "Realiza login", description = "Valida usuario e senha e retorna o resultado da autenticacao.")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Login processado", 
                content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = LoginResponse.class))),
            @APIResponse(responseCode = "400", description = "Dados de login invalidos"),
            @APIResponse(responseCode = "500", description = "Erro interno")
    })
    public Response login(
            @RequestBody(description = "Credenciais do usuario", required = true,
                content = @Content(schema = @Schema(implementation = LoginRequest.class)))
            LoginRequest loginRequest) {
        try {
            return Response.ok(userService.login(loginRequest)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (RuntimeException e) {
            return Response.serverError().build();
        }
    }

    // CREATE
    @POST
    @Operation(summary = "Criar novo usuario", description = "Cria um novo usuario no sistema")
    @APIResponses({
            @APIResponse(responseCode = "201", description = "Usuario criado com sucesso",
                content = @Content(schema = @Schema(implementation = UsuarioResponse.class))),
            @APIResponse(responseCode = "400", description = "Dados invalidos"),
            @APIResponse(responseCode = "500", description = "Erro interno")
    })
    public Response criar(
            @RequestBody(description = "Dados do novo usuario", required = true,
                content = @Content(schema = @Schema(implementation = CreateUsuarioRequest.class)))
            CreateUsuarioRequest request) {
        try {
            return Response.status(Response.Status.CREATED).entity(userService.criar(request)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (RuntimeException e) {
            return Response.serverError().build();
        }
    }

    // READ BY ID
    @GET
    @Path("/{id}")
    @Operation(summary = "Obter usuario por ID", description = "Retorna um usuario especifico pelo ID")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Usuario encontrado",
                content = @Content(schema = @Schema(implementation = UsuarioResponse.class))),
            @APIResponse(responseCode = "400", description = "ID invalido"),
            @APIResponse(responseCode = "404", description = "Usuario nao encontrado"),
            @APIResponse(responseCode = "500", description = "Erro interno")
    })
    public Response obterPorId(@PathParam("id") Long id) {
        try {
            return Response.ok(userService.obterPorId(id)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (RuntimeException e) {
            return Response.serverError().build();
        }
    }

    // LIST ALL
    @GET
    @Operation(summary = "Listar todos os usuarios", description = "Retorna a lista de todos os usuarios")
    @APIResponse(responseCode = "200", description = "Lista de usuarios",
        content = @Content(schema = @Schema(implementation = UsuarioResponse.class)))
    public Response listar() {
        try {
            List<UsuarioResponse> usuarios = userService.listarTodos();
            return Response.ok(usuarios).build();
        } catch (RuntimeException e) {
            return Response.serverError().build();
        }
    }

    // UPDATE
    @PUT
    @Path("/{id}")
    @Operation(summary = "Atualizar usuario", description = "Atualiza os dados de um usuario")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Usuario atualizado",
                content = @Content(schema = @Schema(implementation = UsuarioResponse.class))),
            @APIResponse(responseCode = "400", description = "Dados invalidos ou ID invalido"),
            @APIResponse(responseCode = "404", description = "Usuario nao encontrado"),
            @APIResponse(responseCode = "500", description = "Erro interno")
    })
    public Response atualizar(
            @PathParam("id") Long id,
            @RequestBody(description = "Dados a atualizar", required = true,
                content = @Content(schema = @Schema(implementation = UpdateUsuarioRequest.class)))
            UpdateUsuarioRequest request) {
        try {
            return Response.ok(userService.atualizar(id, request)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (RuntimeException e) {
            return Response.serverError().build();
        }
    }

    // DELETE
    @DELETE
    @Path("/{id}")
    @Operation(summary = "Deletar usuario", description = "Remove um usuario do sistema")
    @APIResponses({
            @APIResponse(responseCode = "204", description = "Usuario deletado com sucesso"),
            @APIResponse(responseCode = "400", description = "ID invalido"),
            @APIResponse(responseCode = "404", description = "Usuario nao encontrado"),
            @APIResponse(responseCode = "500", description = "Erro interno")
    })
    public Response deletar(@PathParam("id") Long id) {
        try {
            userService.deletar(id);
            return Response.noContent().build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (RuntimeException e) {
            return Response.serverError().build();
        }
    }
}
