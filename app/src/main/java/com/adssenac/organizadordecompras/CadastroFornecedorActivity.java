package com.adssenac.organizadordecompras;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adssenac.organizadordecompras.adapter.FornecedorAdapter;
import com.adssenac.organizadordecompras.data.AppDatabase;
import com.adssenac.organizadordecompras.data.CotacaoDao;
import com.adssenac.organizadordecompras.data.FornecedorDao;
import com.adssenac.organizadordecompras.model.Fornecedor;

import java.util.List;

public class CadastroFornecedorActivity extends AppCompatActivity {

    EditText editNomeFornecedor;
    EditText editCodigoFornecedor;
    Button botaoSalvarFornecedor;

    RecyclerView recyclerFornecedores;

    FornecedorAdapter adapter;
    List<Fornecedor> listaFornecedores;

    AppDatabase db;
    FornecedorDao fornecedorDao;
    CotacaoDao cotacaoDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_fornecedor);

        editNomeFornecedor = findViewById(R.id.editNomeFornecedor);
        editCodigoFornecedor = findViewById(R.id.editCodigoFornecedor);
        botaoSalvarFornecedor = findViewById(R.id.botaoSalvarFornecedor);
        recyclerFornecedores = findViewById(R.id.recyclerFornecedores);

        db = AppDatabase.getInstance(this);

        fornecedorDao = db.fornecedorDao();
        cotacaoDao = db.cotacaoDao();

        recyclerFornecedores.setLayoutManager(new LinearLayoutManager(this));

        listaFornecedores = fornecedorDao.listarTodos();

        adapter = new FornecedorAdapter(listaFornecedores, fornecedorDao, cotacaoDao);

        recyclerFornecedores.setAdapter(adapter);

        botaoSalvarFornecedor.setOnClickListener(v -> salvarFornecedor());
    }

    private void salvarFornecedor() {

        String nome = editNomeFornecedor.getText().toString().trim();
        String codigo = editCodigoFornecedor.getText().toString().trim();

        if (nome.isEmpty() || codigo.isEmpty()) {
            Toast.makeText(this,
                    "Preencha o nome e o código do fornecedor",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Fornecedor existenteNome = fornecedorDao.buscarPorNome(nome);

        if (existenteNome != null) {
            Toast.makeText(this,
                    "Já existe um fornecedor com esse nome",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Fornecedor existenteCodigo = fornecedorDao.buscarPorCodigo(codigo);

        if (existenteCodigo != null) {
            Toast.makeText(this,
                    "Já existe um fornecedor com esse código",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Fornecedor fornecedor = new Fornecedor(nome, codigo);

        fornecedorDao.inserir(fornecedor);

        editNomeFornecedor.setText("");
        editCodigoFornecedor.setText("");

        atualizarLista();

        Toast.makeText(this,
                "Fornecedor cadastrado",
                Toast.LENGTH_SHORT).show();
    }

    private void atualizarLista() {

        listaFornecedores.clear();
        listaFornecedores.addAll(fornecedorDao.listarTodos());
        adapter.notifyDataSetChanged();
    }
}
