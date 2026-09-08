package com.adssenac.organizadordecompras.adapter;

import android.graphics.Color;
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
import com.adssenac.organizadordecompras.model.Cotacao;
import com.adssenac.organizadordecompras.model.CotacaoDetalhe;

import java.util.List;
import java.util.Locale;

public class CotacaoAdapter extends RecyclerView.Adapter<CotacaoAdapter.ViewHolder> {

    private List<CotacaoDetalhe> lista;
    private CotacaoDao cotacaoDao;
    private Runnable onAlterado;

    public CotacaoAdapter(List<CotacaoDetalhe> lista, CotacaoDao cotacaoDao, Runnable onAlterado) {
        this.lista = lista;
        this.cotacaoDao = cotacaoDao;
        this.onAlterado = onAlterado;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textFornecedorNome;
        TextView textFornecedorCodigo;
        TextView textPreco;
        TextView textMelhorPreco;

        public ViewHolder(View itemView) {
            super(itemView);
            textFornecedorNome = itemView.findViewById(R.id.textFornecedorNomeCotacao);
            textFornecedorCodigo = itemView.findViewById(R.id.textFornecedorCodigoCotacao);
            textPreco = itemView.findViewById(R.id.textPrecoCotacao);
            textMelhorPreco = itemView.findViewById(R.id.textMelhorPrecoTag);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cotacao, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        CotacaoDetalhe cotacao = lista.get(position);

        holder.textFornecedorNome.setText(cotacao.fornecedorNome);
        holder.textFornecedorCodigo.setText("Código: " + cotacao.fornecedorCodigo);
        holder.textPreco.setText(formatarPreco(cotacao.preco));

        // a lista já vem ordenada por preço ASC, então a posição 0 é a mais barata
        if (position == 0) {
            holder.textMelhorPreco.setVisibility(View.VISIBLE);
            holder.textPreco.setTextColor(Color.parseColor("#2E7D32"));
        } else {
            holder.textMelhorPreco.setVisibility(View.GONE);
            holder.textPreco.setTextColor(Color.BLACK);
        }

        holder.itemView.setOnLongClickListener(v -> {

            String[] opcoes = {"Alterar preço", "Excluir cotação"};

            new AlertDialog.Builder(v.getContext())
                    .setTitle(cotacao.fornecedorNome)
                    .setItems(opcoes, (dialog, which) -> {

                        int pos = holder.getAdapterPosition();
                        if (pos == RecyclerView.NO_POSITION) return;

                        CotacaoDetalhe cd = lista.get(pos);

                        if (which == 0) {
                            alterarPreco(v, cd);
                        }

                        if (which == 1) {
                            excluirCotacao(cd);
                        }
                    })
                    .show();

            return true;
        });
    }

    private void alterarPreco(View v, CotacaoDetalhe cd) {

        EditText input = new EditText(v.getContext());
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER
                | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setText(String.valueOf(cd.preco));

        new AlertDialog.Builder(v.getContext())
                .setTitle("Alterar preço - " + cd.fornecedorNome)
                .setView(input)
                .setPositiveButton("Salvar", (d, w) -> {

                    String texto = input.getText().toString().trim().replace(",", ".");

                    double novoPreco;

                    try {
                        novoPreco = Double.parseDouble(texto);
                    } catch (NumberFormatException e) {
                        Toast.makeText(v.getContext(), "Preço inválido", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (novoPreco < 0) {
                        Toast.makeText(v.getContext(), "Preço inválido", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Cotacao cotacao = cotacaoDao.buscarPorId(cd.id);

                    if (cotacao != null) {
                        cotacao.preco = novoPreco;
                        cotacaoDao.atualizar(cotacao);
                    }

                    if (onAlterado != null) onAlterado.run();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void excluirCotacao(CotacaoDetalhe cd) {

        Cotacao cotacao = cotacaoDao.buscarPorId(cd.id);

        if (cotacao != null) {
            cotacaoDao.deletar(cotacao);
        }

        if (onAlterado != null) onAlterado.run();
    }

    private String formatarPreco(double preco) {
        return String.format(Locale.getDefault(), "R$ %.2f", preco);
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }
}
