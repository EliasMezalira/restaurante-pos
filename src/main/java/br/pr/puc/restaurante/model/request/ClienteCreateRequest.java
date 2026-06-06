package br.pr.puc.restaurante.model.request;

public record ClienteCreateRequest(
	String nome,
	String telefone,
	String rua,
	String numero,
	String bairro,
	String complemento,
	String referencia,
	String email,
	String observacoes
) {}