package br.pr.puc.restaurante.model.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "garcom")
public class Garcom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(name = "percentual_gorjeta", nullable = false)
    private BigDecimal percentualGorjeta;

    @Column(nullable = false)
    private Integer idade;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public BigDecimal getPercentualGorjeta() { return percentualGorjeta; }
    public void setPercentualGorjeta(BigDecimal percentualGorjeta) { this.percentualGorjeta = percentualGorjeta; }
    public Integer getIdade() { return idade; }
    public void setIdade(Integer idade) { this.idade = idade; }
}