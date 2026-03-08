package com.adssenac.organizadordecompras;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adssenac.organizadordecompras.adapter.ProdutoAdapter;
import com.adssenac.organizadordecompras.adapter.ProdutoCategoriaAdapter;
import com.adssenac.organizadordecompras.data.AppDatabase;
import com.adssenac.organizadordecompras.model.Categoria;
import com.adssenac.organizadordecompras.model.Produto;
import com.adssenac.organizadordecompras.model.ProdutoCategoria;

import java.util.List;

public class TodosProdutosActivity extends AppCompatActivity {

    RecyclerView recycler;
    RecyclerView.Adapter adapter;
    List<Produto> lista;
    Button botaoCadastrarProduto;
    Button btnSalvarCategoria;
    Button botaoZerarQuantidades;
    Button botaoLimparComprados;
    AppDatabase db;

    int modoOrdenacao = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todos_produtos);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recycler = findViewById(R.id.recyclerTodosProdutos);
        botaoCadastrarProduto = findViewById(R.id.botaoCadastrarProduto);
        btnSalvarCategoria = findViewById(R.id.btnSalvarCategoria);
        botaoZerarQuantidades = findViewById(R.id.botaoZerarQuantidades);
        botaoLimparComprados = findViewById(R.id.botaoLimparComprados);

        db = AppDatabase.getInstance(this);

        verificarCategoriaPadrao();

        recycler.setLayoutManager(new LinearLayoutManager(this));

        lista = db.produtoDao().listarTodos();

        adapter = new ProdutoAdapter(lista, db.produtoDao());

        recycler.setAdapter(adapter);
        botaoCadastrarProduto.setOnClickListener(v -> {

            Intent intent = new Intent(
                    TodosProdutosActivity.this,
                    CadastroProdutoActivity.class
            );

            startActivity(intent);

        });
        btnSalvarCategoria.setOnClickListener(v -> {

            Intent intent = new Intent(
                    TodosProdutosActivity.this,
                    CadastroCategoriaActivity.class
            );

            startActivity(intent);

        });
        botaoZerarQuantidades.setOnClickListener(v -> {

            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Confirmar")
                    .setMessage("Deseja zerar a quantidade dos produtos comprados?")
                    .setPositiveButton("Sim", (dialog, which) -> {

                        db.produtoDao().zerarQuantidadesComprados();

                        atualizarLista();

                    })
                    .setNegativeButton("Cancelar", null)
                    .show();

        });
        botaoLimparComprados.setOnClickListener(v -> {

            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Excluir produtos")
                    .setMessage("Deseja excluir todos os produtos comprados?")
                    .setPositiveButton("Sim", (dialog, which) -> {

                        db.produtoDao().deletarComprados();

                        atualizarLista();

                    })
                    .setNegativeButton("Cancelar", null)
                    .show();

        });
    }
    private void atualizarLista() {

        lista.clear();

        lista.addAll(db.produtoDao().listarTodos());

        adapter.notifyDataSetChanged();

    }
   // @Override
    //protected void onResume() {
    //    super.onResume();

     //   atualizarLista();
   // }

    @Override
    protected void onResume() {
        super.onResume();

        if(modoOrdenacao == 0){
            carregarOrdemAlfabetica();
        }else{
            carregarOrdemCategoria();
        }
    }
    private void verificarCategoriaPadrao(){

        Categoria categoria = db.categoriaDao().buscarPorNome("Diversos");

        if(categoria == null){

            Categoria nova = new Categoria("Diversos");

            db.categoriaDao().inserir(nova);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {

        getMenuInflater().inflate(R.menu.menu_ordem_produtos, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {

        if(item.getItemId() == R.id.menu_ordem_alfabetica){

            modoOrdenacao = 0;
            carregarOrdemAlfabetica();
            return true;

        } else if(item.getItemId() == R.id.menu_ordem_categoria){

            modoOrdenacao = 1;
            carregarOrdemCategoria();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void carregarOrdemAlfabetica(){

        lista = db.produtoDao().listarTodos();

        adapter = new ProdutoAdapter(lista, db.produtoDao());

        recycler.setAdapter(adapter);
    }
    private void carregarOrdemCategoria(){

        List<ProdutoCategoria> lista = db.produtoDao().listarOrdenadoPorCategoria();

        adapter = new ProdutoCategoriaAdapter(lista, db.produtoDao());

        recycler.setAdapter(adapter);
    }

}