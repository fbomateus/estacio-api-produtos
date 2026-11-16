package br.edu.pos.apiprodutos.model;

import io.javalin.openapi.OpenApiRequired;

public class MovimentacaoEstoque {

    private Integer quantidade;

    @OpenApiRequired
    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }
}
