package br.pr.puc.restaurante.model.response;

import java.math.BigDecimal;

public record LoginResponse (boolean loginSuccess, BigDecimal userId, String userName){
}
