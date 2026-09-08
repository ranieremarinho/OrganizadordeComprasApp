package com.adssenac.organizadordecompras.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;
import androidx.room.ColumnInfo;

// OBS: onDelete NÃO é CASCADE de propósito. Uma Categoria não pode mais
// ser excluída enquanto existir Produto vinculado a ela (essa checagem é
// feita na aplicação, em CategoriaAdapter, antes de chamar o delete).
@Entity(
        foreignKeys = @ForeignKey(
                entity = Categoria.class,
                parentColumns = "id",
                childColumns = "categoriaId",
                onDelete = ForeignKey.NO_ACTION
        )
)
public class Produto {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String nome;

    public String quantidade;

    public boolean comprado;

    @ColumnInfo(index = true)
    public int categoriaId;

    public Produto(String nome, String quantidade, int categoriaId) {
        this.nome = nome;
        this.quantidade = quantidade;
        this.categoriaId = categoriaId;
        this.comprado = false;
    }
}
