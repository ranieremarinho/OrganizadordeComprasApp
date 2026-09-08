package com.adssenac.organizadordecompras;

import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adssenac.organizadordecompras.adapter.CotacaoAdapter;
import com.adssenac.organizadordecompras.adapter.FornecedorSelecaoAdapter;
import com.adssenac.organizadordecompras.data.AppDatabase;
import com.adssenac.organizadordecompras.data.CotacaoDao;
import com.adssenac.organizadordecompras.data.FornecedorDao;
import com.adssenac.organizadordecompras.model.Cotacao;
import com.adssenac.organizadordecompras.model.CotacaoDetalhe;
import com.adssenac.organizadordecompras.model.Fornecedor;

import java.util.List;

/**
 * Tela que lista e permite cadastrar as cotações (preços) de um produto
 * em diferentes fornecedores, permitindo comparar qual é o mais barato.
 */
public class CotacaoActivity extends AppCompatActivity {

    TextView textProdutoTitulo;
    RecyclerView recyclerCotacoes;
    RecyclerView recyclerFornecedores;
    EditText editPreco;
    Button botaoSalvarCotacao;

    AppDatabase db;
    CotacaoDao cotacaoDao;
    FornecedorDao fornecedorDao;

    int produtoId;
    String produtoNome;

    List<CotacaoDetalhe> listaCotacoes;
    CotacaoAdapter cotacaoAdapter;

    List<Fornecedor> listaFornecedores;
    FornecedorSelecaoAdapter fornecedorSelecaoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cotacao);

        textProdutoTitulo = findViewById(R.id.textProdutoTituloCotacao);
        recyclerCotacoes = findViewById(R.id.recyclerCotacoes);
        recyclerFornecedores = findViewById(R.id.recyclerFornecedoresCotacao);
        editPreco = findViewById(R.id.editPrecoCotacao);
        botaoSalvarCotacao = findViewById(R.id.botaoSalvarCotacao);

        db = AppDatabase.getInstance(this);
        cotacaoDao = db.cotacaoDao();
        fornecedorDao = db.fornecedorDao();

        produtoId = getIntent().getIntExtra("produtoId", -1);
        produtoNome = getIntent().getStringExtra("produtoNome");

        if (produtoId == -1) {
            finish();
            return;
        }

        textProdutoTitulo.setText("Cotações de: " + produtoNome);

        editPreco.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        // lista de cotações já cadastradas para este produto
        recyclerCotacoes.setLayoutManager(new LinearLayoutManager(this));

        listaCotacoes = cotacaoDao.listarPorProduto(produtoId);

        cotacaoAdapter = new CotacaoAdapter(listaCotacoes, cotacaoDao, this::atualizarListaCotacoes);

        recyclerCotacoes.setAdapter(cotacaoAdapter);

        // lista de fornecedores para escolher ao lançar uma nova cotação
        recyclerFornecedores.setLayoutManager(new LinearLayoutManager(this));

        listaFornecedores = fornecedorDao.listarTodos();

        fornecedorSelecaoAdapter = new FornecedorSelecaoAdapter(listaFornecedores, fornecedor -> {});

        recyclerFornecedores.setAdapter(fornecedorSelecaoAdapter);

        botaoSalvarCotacao.setOnClickListener(v -> salvarCotacao());
    }

    private void salvarCotacao() {

        if (listaFornecedores.isEmpty()) {
            Toast.makeText(this,
                    "Cadastre um fornecedor antes de lançar uma cotação",
                    Toast.LENGTH_LONG).show();
            return;
        }

        Fornecedor fornecedor = fornecedorSelecaoAdapter.getFornecedorSelecionado();

        if (fornecedor == null) {
            Toast.makeText(this, "Selecione um fornecedor", Toast.LENGTH_SHORT).show();
            return;
        }

        String textoPreco = editPreco.getText().toString().trim().replace(",", ".");

        if (textoPreco.isEmpty()) {
            Toast.makeText(this, "Informe o preço", Toast.LENGTH_SHORT).show();
            return;
        }

        double preco;

        try {
            preco = Double.parseDouble(textoPreco);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Preço inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (preco < 0) {
            Toast.makeText(this, "Preço inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        // se já existe uma cotação deste produto com este fornecedor, atualiza o preço
        Cotacao existente = cotacaoDao.buscarPorProdutoEFornecedor(produtoId, fornecedor.id);

        if (existente != null) {
            existente.preco = preco;
            cotacaoDao.atualizar(existente);
        } else {
            Cotacao cotacao = new Cotacao(produtoId, fornecedor.id, preco);
            cotacaoDao.inserir(cotacao);
        }

        editPreco.setText("");

        atualizarListaCotacoes();

        Toast.makeText(this, "Cotação salva", Toast.LENGTH_SHORT).show();
    }

    private void atualizarListaCotacoes() {

        listaCotacoes.clear();
        listaCotacoes.addAll(cotacaoDao.listarPorProduto(produtoId));
        cotacaoAdapter.notifyDataSetChanged();
    }
}
