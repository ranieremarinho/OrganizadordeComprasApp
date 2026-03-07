package com.adssenac.organizadordecompras;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adssenac.organizadordecompras.adapter.CategoriaSelecaoAdapter;
import com.adssenac.organizadordecompras.data.AppDatabase;
import com.adssenac.organizadordecompras.model.Categoria;
import com.adssenac.organizadordecompras.model.Produto;

import java.util.List;

public class AlterarProdutoActivity extends AppCompatActivity {

    EditText editNomeProduto;
    EditText editQuantidade;
    RecyclerView recyclerCategorias;
    Button botaoSalvar;

    AppDatabase db;

    int categoriaSelecionada = -1;

    Produto produto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alterar_produto);

        editNomeProduto = findViewById(R.id.editNomeProduto);
        editQuantidade = findViewById(R.id.editQuantidade);
        recyclerCategorias = findViewById(R.id.recyclerCategorias);
        botaoSalvar = findViewById(R.id.botaoSalvar);

        db = AppDatabase.getInstance(this);

        // Recebe o ID do produto
        int produtoId = getIntent().getIntExtra("produtoId", -1);

        // Busca o produto no banco
        produto = db.produtoDao().buscarPorId(produtoId);

        // Segurança caso algo dê errado
        if (produto == null) {
            finish();
            return;
        }

        // Preenche os campos
        editNomeProduto.setText(produto.nome);
        editQuantidade.setText(produto.quantidade);

        categoriaSelecionada = produto.categoriaId;

        // Configura RecyclerView
        recyclerCategorias.setLayoutManager(new LinearLayoutManager(this));

        List<Categoria> listaCategorias = db.categoriaDao().listarTodas();

        CategoriaSelecaoAdapter adapter = new CategoriaSelecaoAdapter(
                listaCategorias,
                categoria -> {
                    categoriaSelecionada = categoria.id;
                });

        // Marca a categoria atual do produto
        adapter.setCategoriaSelecionadaId(produto.categoriaId);

        recyclerCategorias.setAdapter(adapter);

        // Botão salvar
        botaoSalvar.setOnClickListener(v -> {

            produto.nome = editNomeProduto.getText().toString();
            produto.quantidade = editQuantidade.getText().toString();
            produto.categoriaId = categoriaSelecionada;

            db.produtoDao().atualizar(produto);

            finish();
        });
    }
}