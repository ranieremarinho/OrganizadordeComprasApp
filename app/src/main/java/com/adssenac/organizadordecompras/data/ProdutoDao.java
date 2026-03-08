package com.adssenac.organizadordecompras.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.adssenac.organizadordecompras.model.Produto;
import com.adssenac.organizadordecompras.model.ProdutoCategoria;

import java.util.List;

@Dao
public interface ProdutoDao {

    @Insert
    void inserir(Produto produto);

    @Update
    void atualizar(Produto produto);

    @Query("UPDATE Produto SET categoriaId = :novaCategoriaId WHERE categoriaId = :categoriaAntigaId")
    void moverProdutosParaOutraCategoria(int categoriaAntigaId, int novaCategoriaId);

    @Delete
    void deletar(Produto produto);

    @Query("DELETE FROM Produto WHERE comprado = 1")
    void deletarComprados();

    @Query("UPDATE Produto SET quantidade = '0' WHERE comprado = 1")
    void zerarQuantidadesComprados();

    @Query("SELECT * FROM Produto WHERE categoriaId = :categoriaId ORDER BY comprado ASC, nome ASC")
    List<Produto> listarPorCategoria(int categoriaId);

    @Query("SELECT * FROM Produto ORDER BY comprado ASC, nome ASC")
    List<Produto> listarTodos();

    @Query("SELECT * FROM Produto WHERE id = :id")
    Produto buscarPorId(int id);

    @Query("SELECT Produto.*, Categoria.nome AS categoriaNome " +
            "FROM Produto " +
            "INNER JOIN Categoria ON Produto.categoriaId = Categoria.id " +
            "ORDER BY Categoria.nome ASC, Produto.nome ASC")
    List<ProdutoCategoria> listarOrdenadoPorCategoria();

    @Query("SELECT * FROM Produto WHERE LOWER(nome) = LOWER(:nome) LIMIT 1")
    Produto buscarPorNomeIgnoreCase(String nome);
}