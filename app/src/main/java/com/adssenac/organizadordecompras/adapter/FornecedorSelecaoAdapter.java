package com.adssenac.organizadordecompras.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.adssenac.organizadordecompras.R;
import com.adssenac.organizadordecompras.model.Fornecedor;

import java.util.List;

public class FornecedorSelecaoAdapter extends RecyclerView.Adapter<FornecedorSelecaoAdapter.ViewHolder> {

    private List<Fornecedor> listaFornecedores;
    private OnFornecedorClickListener listener;
    private int fornecedorSelecionadoId = -1;

    public interface OnFornecedorClickListener {
        void onFornecedorClick(Fornecedor fornecedor);
    }

    public FornecedorSelecaoAdapter(List<Fornecedor> listaFornecedores, OnFornecedorClickListener listener) {
        this.listaFornecedores = listaFornecedores;
        this.listener = listener;
    }

    public void setFornecedorSelecionadoId(int id) {
        fornecedorSelecionadoId = id;
        notifyDataSetChanged();
    }

    public Fornecedor getFornecedorSelecionado() {

        for (Fornecedor f : listaFornecedores) {
            if (f.id == fornecedorSelecionadoId) {
                return f;
            }
        }

        return null;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        CheckBox checkFornecedor;
        TextView textNomeFornecedor;

        public ViewHolder(View itemView) {
            super(itemView);
            checkFornecedor = itemView.findViewById(R.id.checkFornecedor);
            textNomeFornecedor = itemView.findViewById(R.id.textNomeFornecedor);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_fornecedor_selecao, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Fornecedor fornecedor = listaFornecedores.get(position);

        holder.textNomeFornecedor.setText(fornecedor.nome + "  (" + fornecedor.codigo + ")");

        holder.checkFornecedor.setChecked(fornecedor.id == fornecedorSelecionadoId);

        holder.itemView.setOnClickListener(v -> {

            int pos = holder.getAdapterPosition();

            if (pos == RecyclerView.NO_POSITION) return;

            fornecedorSelecionadoId = fornecedor.id;
            notifyDataSetChanged();

            Fornecedor fornecedorClicado = listaFornecedores.get(pos);

            if (listener != null) {
                listener.onFornecedorClick(fornecedorClicado);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaFornecedores.size();
    }
}
