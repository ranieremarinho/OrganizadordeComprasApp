package com.adssenac.organizadordecompras;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.app.AlertDialog;

import com.adssenac.organizadordecompras.data.AppDatabase;
import com.adssenac.organizadordecompras.model.Produto;
import com.adssenac.organizadordecompras.adapter.ProdutoAdapter;

import java.util.List;

public class ProdutoActivity extends AppCompatActivity {

    int categoriaId;
    String categoriaNome;
    Button botaoLimparComprados;
    Button botaoZerarQuantidades;
    RecyclerView recyclerProdutos;
    ProdutoAdapter adapter;
    List<Produto> listaProdutos;

    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produto);

        // componentes da tela
        botaoLimparComprados = findViewById(R.id.botaoLimparComprados);
        botaoZerarQuantidades = findViewById(R.id.botaoZerarQuantidades);
        recyclerProdutos = findViewById(R.id.recyclerProdutos);

        // banco
        db = AppDatabase.getInstance(this);

        // dados recebidos
        Intent intent = getIntent();
        categoriaId = intent.getIntExtra("categoriaId", -1);
        categoriaNome = intent.getStringExtra("categoriaNome");

        // título da tela
        TextView titulo = findViewById(R.id.textCategoriaTitulo);
        titulo.setText(categoriaNome);

        // configurar lista
        recyclerProdutos.setLayoutManager(new LinearLayoutManager(this));

        listaProdutos = db.produtoDao().listarPorCategoria(categoriaId);

        adapter = new ProdutoAdapter(listaProdutos, db.produtoDao());
        recyclerProdutos.setAdapter(adapter);

        botaoLimparComprados.setOnClickListener(v -> limparComprados());
        botaoZerarQuantidades.setOnClickListener(v -> zerarQuantidades());
    }

    private void limparComprados() {

        new AlertDialog.Builder(this)
                .setTitle("Limpar comprados")
                .setMessage("Deseja excluir todos os produtos já comprados?")
                .setPositiveButton("Limpar", (dialog, which) -> {

                    db.produtoDao().deletarComprados();

                    atualizarLista();

                    Toast.makeText(this, "Produtos comprados removidos", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void zerarQuantidades() {

        new AlertDialog.Builder(this)
                .setTitle("Zerar quantidades")
                .setMessage("Deseja zerar a quantidade de todos os itens comprados?")
                .setPositiveButton("Zerar", (dialog, which) -> {

                    db.produtoDao().zerarQuantidadesComprados();

                    atualizarLista();

                    Toast.makeText(this, "Quantidades zeradas", Toast.LENGTH_SHORT).show();

                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void atualizarLista() {

        listaProdutos.clear();
        listaProdutos.addAll(db.produtoDao().listarPorCategoria(categoriaId));
        adapter.notifyDataSetChanged();
    }
}