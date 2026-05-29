package br.pr.puc.restaurante.model.response;

import java.math.BigDecimal;

public record UsuarioResponse(
    BigDecimal id,
    String login,
    String nome
) {}
