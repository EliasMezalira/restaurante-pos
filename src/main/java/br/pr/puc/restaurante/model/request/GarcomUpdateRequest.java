package br.pr.puc.restaurante.model.request;

public record GarcomUpdateRequest(String nome, String documento, String fotoBase64, String[] folgas) {}