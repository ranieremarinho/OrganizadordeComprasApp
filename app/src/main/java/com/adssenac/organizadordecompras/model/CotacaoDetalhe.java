package com.adssenac.organizadordecompras.model;

/**
 * DTO (não é uma tabela) usado para exibir, em uma única linha,
 * os dados de uma Cotacao já combinados com o nome/código do Fornecedor.
 * Preenchido a partir de um JOIN entre Cotacao e Fornecedor (ver CotacaoDao).
 */
public class CotacaoDetalhe {

    public int id;
    public int produtoId;
    public int fornecedorId;
    public double preco;

    public String fornecedorNome;
    public String fornecedorCodigo;
}
