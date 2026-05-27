package br.pr.puc.restaurante.controller;

import br.pr.puc.restaurante.model.request.LoginRequest;
import br.pr.puc.restaurante.model.response.LoginResponse;
import br.pr.puc.restaurante.service.UserService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
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

@Path("/usuario")
@Tag(name = "Usuario", description = "Operacoes de usuario")
public class UserController {
    @Inject
    public UserService userService;

    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Realiza login", description = "Valida usuario e senha e retorna o resultado da autenticacao.")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Login processado",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = LoginResponse.class)
                    )
            ),
            @APIResponse(responseCode = "400", description = "Dados de login invalidos"),
            @APIResponse(responseCode = "500", description = "Erro interno")
    })
    public Response login(
            @RequestBody(
                    description = "Credenciais do usuario",
                    required = true,
                    content = @Content(schema = @Schema(implementation = LoginRequest.class))
            )
            LoginRequest loginRequest
    ){
        try {
            return Response.ok(userService.login(loginRequest)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        } catch (RuntimeException e) {
            return Response.serverError().build();
        }

    }
}
