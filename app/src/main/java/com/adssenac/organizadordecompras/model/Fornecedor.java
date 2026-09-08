package com.adssenac.organizadordecompras.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Fornecedor {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String nome;

    public String codigo;

    public Fornecedor(String nome, String codigo) {
        this.nome = nome;
        this.codigo = codigo;
    }
}
