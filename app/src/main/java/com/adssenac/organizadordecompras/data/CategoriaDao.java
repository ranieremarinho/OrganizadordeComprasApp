package com.adssenac.organizadordecompras.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.adssenac.organizadordecompras.model.Categoria;

import java.util.List;

@Dao
public interface CategoriaDao {

    @Insert
    void inserir(Categoria categoria);

    @Update
    void atualizar(Categoria categoria);

    @Delete
    void deletar(Categoria categoria);

    @Query("SELECT * FROM Categoria ORDER BY nome ASC")
    List<Categoria> listarTodas();
    @Query("SELECT * FROM Categoria WHERE nome = :nome LIMIT 1")
    Categoria buscarPorNome(String nome);
}