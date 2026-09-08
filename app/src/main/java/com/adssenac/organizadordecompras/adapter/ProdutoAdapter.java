package com.adssenac.organizadordecompras.adapter;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.adssenac.organizadordecompras.AlterarProdutoActivity;
import com.adssenac.organizadordecompras.CotacaoActivity;
import com.adssenac.organizadordecompras.R;
import com.adssenac.organizadordecompras.data.CotacaoDao;
import com.adssenac.organizadordecompras.data.ProdutoDao;
import com.adssenac.organizadordecompras.model.MelhorPreco;
import com.adssenac.organizadordecompras.model.Produto;

import java.util.List;
import java.util.Locale;

public class ProdutoAdapter extends RecyclerView.Adapter<ProdutoAdapter.ViewHolder> {

    List<Produto> lista;
    ProdutoDao produtoDao;
    CotacaoDao cotacaoDao;

    public ProdutoAdapter(List<Produto> lista, ProdutoDao produtoDao, CotacaoDao cotacaoDao) {
        this.lista = lista;
        this.produtoDao = produtoDao;
        this.cotacaoDao = cotacaoDao;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkComprado;
        TextView nome;
        TextView quantidade;
        TextView melhorPreco;

        public ViewHolder(View itemView) {
            super(itemView);
            checkComprado = itemView.findViewById(R.id.checkComprado);
            nome = itemView.findViewById(R.id.textNomeProduto);
            quantidade = itemView.findViewById(R.id.textQuantidade);
            melhorPreco = itemView.findViewById(R.id.textMelhorPrecoProduto);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_produto, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Produto produto = lista.get(position);

        holder.nome.setText(produto.nome);
        holder.quantidade.setText(produto.quantidade);

        if(produto.comprado){
            holder.nome.setPaintFlags(holder.nome.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.nome.setTextColor(Color.GRAY);
        }else{
            holder.nome.setPaintFlags(0);
            holder.nome.setTextColor(Color.BLACK);
        }

        exibirMelhorPreco(holder, produto.id);

        // evita bug de reciclagem do RecyclerView
        holder.checkComprado.setOnCheckedChangeListener(null);

        holder.checkComprado.setChecked(produto.comprado);

        holder.checkComprado.setOnCheckedChangeListener((buttonView, isChecked) -> {

            produto.comprado = isChecked;
            produtoDao.atualizar(produto);

            if(isChecked){
                holder.nome.setPaintFlags(holder.nome.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.nome.setTextColor(Color.GRAY);
            }else{
                holder.nome.setPaintFlags(0);
                holder.nome.setTextColor(Color.BLACK);
            }

        });

        holder.itemView.setOnClickListener(v -> abrirCotacoes(v, produto));

        holder.itemView.setOnLongClickListener(v -> {

            String[] opcoes = {"Alterar", "Cotações (fornecedores)", "Excluir"};

            new androidx.appcompat.app.AlertDialog.Builder(v.getContext())
                    .setTitle("Escolha uma ação")
                    .setItems(opcoes, (dialog, which) -> {

                        int pos = holder.getAdapterPosition();
                        if (pos == RecyclerView.NO_POSITION) return;

                        Produto p = lista.get(pos);

                        if(which == 0){

                            // ALTERAR
                            Intent intent = new Intent(
                                    v.getContext(),
                                    AlterarProdutoActivity.class
                            );

                            intent.putExtra("produtoId", p.id);
                            v.getContext().startActivity(intent);

                        } else if (which == 1) {

                            abrirCotacoes(v, p);

                        } else if(which == 2){

                            // EXCLUIR
                            produtoDao.deletar(p);
                            lista.remove(pos);
                            notifyItemRemoved(pos);
                        }

                    })
                    .show();

            return true;
        });
    }

    private void abrirCotacoes(View v, Produto produto) {

        Intent intent = new Intent(v.getContext(), CotacaoActivity.class);
        intent.putExtra("produtoId", produto.id);
        intent.putExtra("produtoNome", produto.nome);
        v.getContext().startActivity(intent);
    }

    private void exibirMelhorPreco(ViewHolder holder, int produtoId) {

        MelhorPreco melhor = cotacaoDao.buscarMelhorPreco(produtoId);

        if (melhor != null) {
            holder.melhorPreco.setVisibility(View.VISIBLE);
            holder.melhorPreco.setText(String.format(Locale.getDefault(),
                    "Melhor preço: R$ %.2f (%s)", melhor.preco, melhor.fornecedorNome));
        } else {
            holder.melhorPreco.setVisibility(View.VISIBLE);
            holder.melhorPreco.setText("Sem cotações — toque para cotar");
        }
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }
}
