package com.adssenac.organizadordecompras.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.adssenac.organizadordecompras.R;
import com.adssenac.organizadordecompras.data.CotacaoDao;
import com.adssenac.organizadordecompras.model.Cotacao;
import com.adssenac.organizadordecompras.model.Produto;

import java.util.List;
import java.util.Locale;

public class FazerCotacaoAdapter extends RecyclerView.Adapter<FazerCotacaoAdapter.ViewHolder> {

    public interface OnProdutoLongClickListener {
        void onProdutoLongClick(Produto produto);
    }

    private List<Produto> listaProdutos;
    private CotacaoDao cotacaoDao;
    private OnProdutoLongClickListener listener;
    private int fornecedorSelecionadoId = -1;

    public FazerCotacaoAdapter(List<Produto> listaProdutos,
                                CotacaoDao cotacaoDao,
                                OnProdutoLongClickListener listener) {

        this.listaProdutos = listaProdutos;
        this.cotacaoDao = cotacaoDao;
        this.listener = listener;
    }

    public void setFornecedorSelecionadoId(int fornecedorId) {
        this.fornecedorSelecionadoId = fornecedorId;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textNomeProdutoCotacao;
        TextView textPrecoAtualCotacao;

        public ViewHolder(View itemView) {
            super(itemView);
            textNomeProdutoCotacao = itemView.findViewById(R.id.textNomeProdutoCotacao);
            textPrecoAtualCotacao = itemView.findViewById(R.id.textPrecoAtualCotacao);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_produto_fazer_cotacao, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Produto produto = listaProdutos.get(position);

        holder.textNomeProdutoCotacao.setText(produto.nome);

        if (fornecedorSelecionadoId != -1) {

            Cotacao existente = cotacaoDao.buscarPorProdutoEFornecedor(produto.id, fornecedorSelecionadoId);

            if (existente != null) {
                holder.textPrecoAtualCotacao.setVisibility(View.VISIBLE);
                holder.textPrecoAtualCotacao.setText(String.format(Locale.getDefault(),
                        "Já cotado: R$ %.2f (toque e segure para alterar)", existente.preco));
            } else {
                holder.textPrecoAtualCotacao.setVisibility(View.VISIBLE);
                holder.textPrecoAtualCotacao.setText("Sem cotação com este fornecedor — toque e segure para lançar");
            }

        } else {
            holder.textPrecoAtualCotacao.setVisibility(View.GONE);
        }

        holder.itemView.setOnLongClickListener(v -> {

            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return true;

            if (listener != null) {
                listener.onProdutoLongClick(listaProdutos.get(pos));
            }

            return true;
        });
    }

    @Override
    public int getItemCount() {
        return listaProdutos.size();
    }
}
