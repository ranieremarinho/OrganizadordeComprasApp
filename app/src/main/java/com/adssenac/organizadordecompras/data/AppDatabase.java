package com.adssenac.organizadordecompras.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.adssenac.organizadordecompras.model.Categoria;
import com.adssenac.organizadordecompras.model.Produto;

@Database(entities = {Categoria.class, Produto.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract CategoriaDao categoriaDao();
    public abstract ProdutoDao produtoDao();

    public static synchronized AppDatabase getInstance(Context context) {

        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "organizador-db"
                    ).fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }

        return instance;
    }
}