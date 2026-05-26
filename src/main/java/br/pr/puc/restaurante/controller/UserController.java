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

@Path("/usuario")
public class UserController {
    @Inject
    public UserService userService;

    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(LoginRequest loginRequest){
        try {
            return Response.ok(userService.login(loginRequest)).build();
        } catch (RuntimeException e) {
            return Response.serverError().build();
        }

    }
}
