package com.adssenac.organizadordecompras;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adssenac.organizadordecompras.adapter.CategoriaAdapter;
import com.adssenac.organizadordecompras.adapter.CategoriaSelecaoAdapter;
import com.adssenac.organizadordecompras.data.AppDatabase;
import com.adssenac.organizadordecompras.model.Categoria;
import com.adssenac.organizadordecompras.model.Produto;

import java.util.List;

public class CadastroProdutoActivity extends AppCompatActivity {

    EditText editNomeProduto;
    EditText editQuantidade;

    RecyclerView recyclerCategorias;

    Button botaoSalvarProduto;

    AppDatabase db;

    List<Categoria> listaCategorias;

    int categoriaSelecionada = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_produto);

        editNomeProduto = findViewById(R.id.editNomeProduto);
        editQuantidade = findViewById(R.id.editQuantidade);
        recyclerCategorias = findViewById(R.id.recyclerCategorias);
        botaoSalvarProduto = findViewById(R.id.botaoSalvarProduto);

        db = AppDatabase.getInstance(this);

        recyclerCategorias.setLayoutManager(new LinearLayoutManager(this));

        listaCategorias = db.categoriaDao().listarTodas();

        CategoriaSelecaoAdapter adapter = new CategoriaSelecaoAdapter(
                listaCategorias,
                categoria -> {
                    categoriaSelecionada = categoria.id;
                });

        recyclerCategorias.setAdapter(adapter);

        botaoSalvarProduto.setOnClickListener(v -> salvarProduto());
    }
    private void salvarProduto(){

        String nome = editNomeProduto.getText().toString().trim();
        String quantidade = editQuantidade.getText().toString().trim();

        Produto existente = db.produtoDao().buscarPorNomeIgnoreCase(nome);

        if(existente != null){

            Toast.makeText(this,
                    "Produto já cadastrado",
                    Toast.LENGTH_SHORT).show();

            return;
        }

        if(nome.isEmpty() || quantidade.isEmpty()){

            Toast.makeText(this,
                    "Preencha todos os campos",
                    Toast.LENGTH_SHORT).show();

            return;
        }

        if(categoriaSelecionada == -1){

            Toast.makeText(this,
                    "Selecione uma categoria",
                    Toast.LENGTH_SHORT).show();

            return;
        }

        Produto produto = new Produto(nome, quantidade, categoriaSelecionada);

        db.produtoDao().inserir(produto);

        Toast.makeText(this,
                "Produto cadastrado",
                Toast.LENGTH_SHORT).show();

        editNomeProduto.setText("");
        editQuantidade.setText("");

    }
}