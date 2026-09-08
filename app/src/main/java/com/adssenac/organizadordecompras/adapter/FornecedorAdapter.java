package com.adssenac.organizadordecompras.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.adssenac.organizadordecompras.R;
import com.adssenac.organizadordecompras.data.CotacaoDao;
import com.adssenac.organizadordecompras.data.FornecedorDao;
import com.adssenac.organizadordecompras.model.Fornecedor;

import java.util.List;

public class FornecedorAdapter extends RecyclerView.Adapter<FornecedorAdapter.ViewHolder> {

    private List<Fornecedor> listaFornecedores;
    private FornecedorDao fornecedorDao;
    private CotacaoDao cotacaoDao;

    public FornecedorAdapter(List<Fornecedor> listaFornecedores,
                              FornecedorDao fornecedorDao,
                              CotacaoDao cotacaoDao) {

        this.listaFornecedores = listaFornecedores;
        this.fornecedorDao = fornecedorDao;
        this.cotacaoDao = cotacaoDao;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textNomeFornecedor;
        TextView textCodigoFornecedor;

        public ViewHolder(View itemView) {
            super(itemView);
            textNomeFornecedor = itemView.findViewById(R.id.textNomeFornecedor);
            textCodigoFornecedor = itemView.findViewById(R.id.textCodigoFornecedor);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_fornecedor, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Fornecedor fornecedor = listaFornecedores.get(position);

        holder.textNomeFornecedor.setText(fornecedor.nome);
        holder.textCodigoFornecedor.setText("Código: " + fornecedor.codigo);

        holder.itemView.setOnLongClickListener(v -> {

            String[] opcoes = {"Alterar", "Excluir"};

            new AlertDialog.Builder(v.getContext())
                    .setTitle("Fornecedor")
                    .setItems(opcoes, (dialog, which) -> {

                        int pos = holder.getAdapterPosition();
                        if (pos == RecyclerView.NO_POSITION) return;

                        Fornecedor forn = listaFornecedores.get(pos);

                        if (which == 0) {
                            alterarFornecedor(v, forn, pos);
                        }

                        if (which == 1) {
                            excluirFornecedor(v, forn, pos);
                        }

                    })
                    .show();

            return true;
        });
    }

    private void alterarFornecedor(View v, Fornecedor forn, int pos) {

        View form = LayoutInflater.from(v.getContext())
                .inflate(R.layout.dialog_editar_fornecedor, null);

        EditText inputNome = form.findViewById(R.id.editNomeFornecedorDialog);
        EditText inputCodigo = form.findViewById(R.id.editCodigoFornecedorDialog);

        inputNome.setText(forn.nome);
        inputCodigo.setText(forn.codigo);

        new AlertDialog.Builder(v.getContext())
                .setTitle("Alterar fornecedor")
                .setView(form)
                .setPositiveButton("Salvar", (d, w) -> {

                    String novoNome = inputNome.getText().toString().trim();
                    String novoCodigo = inputCodigo.getText().toString().trim();

                    if (novoNome.isEmpty() || novoCodigo.isEmpty()) {
                        Toast.makeText(v.getContext(),
                                "Preencha nome e código",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    forn.nome = novoNome;
                    forn.codigo = novoCodigo;

                    fornecedorDao.atualizar(forn);

                    notifyItemChanged(pos);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void excluirFornecedor(View v, Fornecedor forn, int pos) {

        // Regra: não é possível excluir o fornecedor se algum produto
        // ainda tiver cotação (preço) cadastrada com ele.
        int qtdVinculos = cotacaoDao.contarPorFornecedor(forn.id);

        if (qtdVinculos > 0) {

            Toast.makeText(v.getContext(),
                    "Não é possível excluir: existem " + qtdVinculos +
                            " produto(s) com cotação deste fornecedor",
                    Toast.LENGTH_LONG).show();

            return;
        }

        new AlertDialog.Builder(v.getContext())
                .setTitle("Excluir fornecedor")
                .setMessage("Deseja realmente excluir o fornecedor \"" + forn.nome + "\"?")
                .setPositiveButton("Sim", (d, w) -> {

                    fornecedorDao.deletar(forn);

                    listaFornecedores.remove(pos);

                    notifyItemRemoved(pos);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public int getItemCount() {
        return listaFornecedores.size();
    }
}
