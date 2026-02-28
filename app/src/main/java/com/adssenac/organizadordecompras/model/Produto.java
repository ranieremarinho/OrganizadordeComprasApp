package com.adssenac.organizadordecompras.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;
import androidx.room.ColumnInfo;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
        foreignKeys = @ForeignKey(
                entity = Categoria.class,
                parentColumns = "id",
                childColumns = "categoriaId",
                onDelete = CASCADE
        )
)
public class Produto {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String nome;

    public int quantidade;

    public boolean comprado;

    @ColumnInfo(index = true)
    public int categoriaId;

    public Produto(String nome, int quantidade, int categoriaId) {
        this.nome = nome;
        this.quantidade = quantidade;
        this.categoriaId = categoriaId;
        this.comprado = false;
    }
}