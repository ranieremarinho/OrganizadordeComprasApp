package com.adssenac.organizadordecompras.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Categoria {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String nome;

    public Categoria(String nome) {
        this.nome = nome;
    }
}