package com.adssenac.organizadordecompras.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.adssenac.organizadordecompras.model.Cotacao;
import com.adssenac.organizadordecompras.model.CotacaoDetalhe;
import com.adssenac.organizadordecompras.model.MelhorPreco;

import java.util.List;

@Dao
public interface CotacaoDao {

    @Insert
    void inserir(Cotacao cotacao);

    @Update
    void atualizar(Cotacao cotacao);

    @Delete
    void deletar(Cotacao cotacao);

    @Query("SELECT * FROM Cotacao WHERE id = :id")
    Cotacao buscarPorId(int id);

    @Query("SELECT * FROM Cotacao WHERE produtoId = :produtoId AND fornecedorId = :fornecedorId LIMIT 1")
    Cotacao buscarPorProdutoEFornecedor(int produtoId, int fornecedorId);

    // Lista as cotações de um produto, já com o nome/código do fornecedor,
    // ordenadas da mais barata para a mais cara (para facilitar a comparação).
    @Query("SELECT Cotacao.id AS id, Cotacao.produtoId AS produtoId, Cotacao.fornecedorId AS fornecedorId, " +
            "Cotacao.preco AS preco, Fornecedor.nome AS fornecedorNome, Fornecedor.codigo AS fornecedorCodigo " +
            "FROM Cotacao " +
            "INNER JOIN Fornecedor ON Cotacao.fornecedorId = Fornecedor.id " +
            "WHERE Cotacao.produtoId = :produtoId " +
            "ORDER BY Cotacao.preco ASC")
    List<CotacaoDetalhe> listarPorProduto(int produtoId);

    // Retorna a cotação de menor preço de um produto (a "melhor" cotação).
    @Query("SELECT Cotacao.preco AS preco, Fornecedor.nome AS fornecedorNome " +
            "FROM Cotacao " +
            "INNER JOIN Fornecedor ON Cotacao.fornecedorId = Fornecedor.id " +
            "WHERE Cotacao.produtoId = :produtoId " +
            "ORDER BY Cotacao.preco ASC LIMIT 1")
    MelhorPreco buscarMelhorPreco(int produtoId);

    // Usado para bloquear a exclusão de um Fornecedor que ainda esteja
    // sendo usado (cotado) em algum Produto.
    @Query("SELECT COUNT(*) FROM Cotacao WHERE fornecedorId = :fornecedorId")
    int contarPorFornecedor(int fornecedorId);

    @Query("DELETE FROM Cotacao WHERE produtoId = :produtoId")
    void deletarPorProduto(int produtoId);
}
