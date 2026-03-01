package com.adssenac.organizadordecompras.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.adssenac.organizadordecompras.model.Categoria;
import com.adssenac.organizadordecompras.model.Produto;

@Database(entities = {Categoria.class, Produto.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract CategoriaDao categoriaDao();
    public abstract ProdutoDao produtoDao();
}
