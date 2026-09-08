package com.adssenac.organizadordecompras.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Representa a cotação (preço) de um Produto em um determinado Fornecedor.
 * É a tabela associativa que liga Produto <-> Fornecedor (N para N),
 * permitindo que um produto tenha 0 ou muitas cotações (fornecedores)
 * e que um fornecedor forneça 0 ou muitos produtos.
 *
 * Ao excluir um Produto, suas cotações são removidas junto (CASCADE),
 * pois elas não fazem sentido sem o produto.
 *
 * Ao excluir um Fornecedor, a cotação NÃO é removida automaticamente
 * (sem CASCADE): a exclusão do fornecedor é bloqueada pela aplicação
 * enquanto existirem cotações (produtos) vinculadas a ele.
 */
@Entity(
        foreignKeys = {
                @ForeignKey(
                        entity = Produto.class,
                        parentColumns = "id",
                        childColumns = "produtoId",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Fornecedor.class,
                        parentColumns = "id",
                        childColumns = "fornecedorId",
                        onDelete = ForeignKey.NO_ACTION
                )
        },
        indices = {
                @Index("produtoId"),
                @Index("fornecedorId")
        }
)
public class Cotacao {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public int produtoId;

    public int fornecedorId;

    public double preco;

    public Cotacao(int produtoId, int fornecedorId, double preco) {
        this.produtoId = produtoId;
        this.fornecedorId = fornecedorId;
        this.preco = preco;
    }
}
