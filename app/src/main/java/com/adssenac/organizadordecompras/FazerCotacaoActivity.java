package com.adssenac.organizadordecompras;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adssenac.organizadordecompras.adapter.FazerCotacaoAdapter;
import com.adssenac.organizadordecompras.data.AppDatabase;
import com.adssenac.organizadordecompras.data.CotacaoDao;
import com.adssenac.organizadordecompras.data.FornecedorDao;
import com.adssenac.organizadordecompras.data.ProdutoDao;
import com.adssenac.organizadordecompras.model.Cotacao;
import com.adssenac.organizadordecompras.model.Fornecedor;
import com.adssenac.organizadordecompras.model.Produto;

import java.util.ArrayList;
import java.util.List;

/**
 * Tela "Fazer Cotação": o usuário escolhe um fornecedor no Spinner e, em
 * seguida, lança o preço de cada produto para esse fornecedor tocando e
 * segurando o item na lista (ordem alfabética). Cada preço é salvo
 * imediatamente ao confirmar no AlertDialog.
 */
public class FazerCotacaoActivity extends AppCompatActivity {

    Spinner spinnerFornecedor;
    RecyclerView recyclerProdutosCotacao;

    AppDatabase db;
    FornecedorDao fornecedorDao;
    ProdutoDao produtoDao;
    CotacaoDao cotacaoDao;

    List<Fornecedor> listaFornecedores = new ArrayList<>();
    List<Produto> listaProdutos = new ArrayList<>();

    FazerCotacaoAdapter adapter;

    Fornecedor fornecedorSelecionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fazer_cotacao);

        spinnerFornecedor = findViewById(R.id.spinnerFornecedor);
        recyclerProdutosCotacao = findViewById(R.id.recyclerProdutosCotacao);

        db = AppDatabase.getInstance(this);
        fornecedorDao = db.fornecedorDao();
        produtoDao = db.produtoDao();
        cotacaoDao = db.cotacaoDao();

        listaProdutos = produtoDao.listarTodosOrdemAlfabetica();

        recyclerProdutosCotacao.setLayoutManager(new LinearLayoutManager(this));

        adapter = new FazerCotacaoAdapter(listaProdutos, cotacaoDao, this::abrirDialogPreco);

        recyclerProdutosCotacao.setAdapter(adapter);

        carregarSpinnerFornecedores();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // caso o usuário tenha cadastrado um novo fornecedor e voltado para cá
        carregarSpinnerFornecedores();
    }

    private void carregarSpinnerFornecedores() {

        listaFornecedores.clear();
        listaFornecedores.addAll(fornecedorDao.listarTodos());

        if (listaFornecedores.isEmpty()) {

            Toast.makeText(this,
                    "Cadastre ao menos um fornecedor para fazer cotações",
                    Toast.LENGTH_LONG).show();
        }

        List<String> nomes = new ArrayList<>();

        for (Fornecedor f : listaFornecedores) {
            nomes.add(f.nome + " (" + f.codigo + ")");
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                nomes
        );

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerFornecedor.setAdapter(spinnerAdapter);

        spinnerFornecedor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                fornecedorSelecionado = listaFornecedores.get(position);
                adapter.setFornecedorSelecionadoId(fornecedorSelecionado.id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                fornecedorSelecionado = null;
                adapter.setFornecedorSelecionadoId(-1);
            }
        });

        if (!listaFornecedores.isEmpty()) {
            spinnerFornecedor.setSelection(0);
            fornecedorSelecionado = listaFornecedores.get(0);
            adapter.setFornecedorSelecionadoId(fornecedorSelecionado.id);
        } else {
            fornecedorSelecionado = null;
            adapter.setFornecedorSelecionadoId(-1);
        }
    }

    private void abrirDialogPreco(Produto produto) {

        if (fornecedorSelecionado == null) {
            Toast.makeText(this,
                    "Selecione um fornecedor antes de lançar a cotação",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Cotacao existente = cotacaoDao.buscarPorProdutoEFornecedor(produto.id, fornecedorSelecionado.id);

        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        if (existente != null) {
            input.setText(String.valueOf(existente.preco));
        }

        new AlertDialog.Builder(this)
                .setTitle(produto.nome)
                .setMessage("Fornecedor: " + fornecedorSelecionado.nome)
                .setView(input)
                .setPositiveButton("Salvar", (dialog, which) -> salvarPreco(produto, input))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void salvarPreco(Produto produto, EditText input) {

        String texto = input.getText().toString().trim().replace(",", ".");

        if (texto.isEmpty()) {
            Toast.makeText(this, "Informe o preço", Toast.LENGTH_SHORT).show();
            return;
        }

        double preco;

        try {
            preco = Double.parseDouble(texto);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Preço inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (preco < 0) {
            Toast.makeText(this, "Preço inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        Cotacao existente = cotacaoDao.buscarPorProdutoEFornecedor(produto.id, fornecedorSelecionado.id);

        if (existente != null) {
            existente.preco = preco;
            cotacaoDao.atualizar(existente);
        } else {
            Cotacao cotacao = new Cotacao(produto.id, fornecedorSelecionado.id, preco);
            cotacaoDao.inserir(cotacao);
        }

        adapter.notifyDataSetChanged();

        Toast.makeText(this, "Cotação salva", Toast.LENGTH_SHORT).show();
    }
}
