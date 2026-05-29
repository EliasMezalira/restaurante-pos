package br.pr.puc.restaurante.model.request;

public record CreateUsuarioRequest(
    String login,
    String senha,
    String nome
) {}
