package com.adssenac.organizadordecompras;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

import com.adssenac.organizadordecompras.data.AppDatabase;
import com.adssenac.organizadordecompras.model.Categoria;

public class MainActivity extends AppCompatActivity {
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        db = Room.databaseBuilder(
                getApplicationContext(),
                AppDatabase.class,
                "organizador-db"
        ).allowMainThreadQueries().build();

        Categoria categoria = new Categoria("Supermercado");
        db.categoriaDao().inserir(categoria);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}