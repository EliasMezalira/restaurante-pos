package br.pr.puc.restaurante.model.request;

public record GarcomCreateRequest(String nome, String documento, String fotoBase64, String[] folgas) {}