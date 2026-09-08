package com.adssenac.organizadordecompras.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.adssenac.organizadordecompras.model.Fornecedor;

import java.util.List;

@Dao
public interface FornecedorDao {

    @Insert
    void inserir(Fornecedor fornecedor);

    @Update
    void atualizar(Fornecedor fornecedor);

    @Delete
    void deletar(Fornecedor fornecedor);

    @Query("SELECT * FROM Fornecedor ORDER BY nome ASC")
    List<Fornecedor> listarTodos();

    @Query("SELECT * FROM Fornecedor WHERE id = :id")
    Fornecedor buscarPorId(int id);

    @Query("SELECT * FROM Fornecedor WHERE LOWER(nome) = LOWER(:nome) LIMIT 1")
    Fornecedor buscarPorNome(String nome);

    @Query("SELECT * FROM Fornecedor WHERE LOWER(codigo) = LOWER(:codigo) LIMIT 1")
    Fornecedor buscarPorCodigo(String codigo);
}
