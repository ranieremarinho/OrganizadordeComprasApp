package com.adssenac.organizadordecompras.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.adssenac.organizadordecompras.model.Produto;

import java.util.List;

@Dao
public interface ProdutoDao {

    @Insert
    void inserir(Produto produto);

    @Update
    void atualizar(Produto produto);

    @Delete
    void deletar(Produto produto);

    @Query("SELECT * FROM Produto WHERE categoriaId = :categoriaId")
    List<Produto> listarPorCategoria(int categoriaId);

    @Query("DELETE FROM Produto WHERE comprado = 1")
    void deletarComprados();
}