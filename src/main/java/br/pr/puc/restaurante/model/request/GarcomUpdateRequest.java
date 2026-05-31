package br.pr.puc.restaurante.model.request;
import java.math.BigDecimal;

public record GarcomUpdateRequest(String nome, BigDecimal percentualGorjeta, Integer idade) {}