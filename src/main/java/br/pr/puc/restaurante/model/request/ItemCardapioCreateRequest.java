package br.pr.puc.restaurante.model.request;
import java.math.BigDecimal;

public record ItemCardapioCreateRequest(String nome, String ingredientes, String categoria, BigDecimal valor) {}