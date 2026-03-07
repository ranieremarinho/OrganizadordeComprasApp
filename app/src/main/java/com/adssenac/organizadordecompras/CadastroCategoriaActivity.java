package com.adssenac.organizadordecompras;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adssenac.organizadordecompras.adapter.CategoriaAdapter;
import com.adssenac.organizadordecompras.data.AppDatabase;
import com.adssenac.organizadordecompras.data.CategoriaDao;
import com.adssenac.organizadordecompras.data.ProdutoDao;
import com.adssenac.organizadordecompras.model.Categoria;

import java.util.List;

public class CadastroCategoriaActivity extends AppCompatActivity {

    EditText editNomeCategoria;
    Button botaoSalvarCategoria;

    RecyclerView recyclerCategorias;

    CategoriaAdapter adapter;
    List<Categoria> listaCategorias;

    AppDatabase db;
    CategoriaDao categoriaDao;
    ProdutoDao produtoDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_categoria);

        editNomeCategoria = findViewById(R.id.editNomeCategoria);
        botaoSalvarCategoria = findViewById(R.id.botaoSalvarCategoria);
        recyclerCategorias = findViewById(R.id.recyclerCategorias);

        db = AppDatabase.getInstance(this);

        categoriaDao = db.categoriaDao();
        produtoDao = db.produtoDao();

        recyclerCategorias.setLayoutManager(new LinearLayoutManager(this));

        listaCategorias = categoriaDao.listarTodas();

        adapter = new CategoriaAdapter(
                listaCategorias,
                categoriaDao,
                produtoDao,
                categoria -> {
                }
        );

        recyclerCategorias.setAdapter(adapter);

        botaoSalvarCategoria.setOnClickListener(v -> salvarCategoria());
    }

    private void salvarCategoria(){

        String nome = editNomeCategoria.getText().toString().trim();

        if(nome.isEmpty()){
            Toast.makeText(this,
                    "Digite o nome da categoria",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Categoria existente = categoriaDao.buscarPorNome(nome);

        if(existente != null){
            Toast.makeText(this,
                    "Categoria já existe",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Categoria categoria = new Categoria(nome);

        categoriaDao.inserir(categoria);

        editNomeCategoria.setText("");

        atualizarLista();

        Toast.makeText(this,
                "Categoria cadastrada",
                Toast.LENGTH_SHORT).show();
    }

    private void atualizarLista(){

        listaCategorias.clear();

        listaCategorias.addAll(categoriaDao.listarTodas());

        adapter.notifyDataSetChanged();

    }
}