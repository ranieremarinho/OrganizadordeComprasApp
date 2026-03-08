package com.adssenac.organizadordecompras.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.CheckBox;

import androidx.recyclerview.widget.RecyclerView;

import com.adssenac.organizadordecompras.R;
import com.adssenac.organizadordecompras.model.Categoria;

import java.util.List;

public class CategoriaSelecaoAdapter extends RecyclerView.Adapter<CategoriaSelecaoAdapter.ViewHolder> {

    private List<Categoria> listaCategorias;
    private OnCategoriaClickListener listener;
    private int categoriaSelecionadaId = -1;

    public Categoria getCategoriaSelecionada() {

        for(Categoria c : listaCategorias){
            if(c.id == categoriaSelecionadaId){
                return c;
            }
        }

        return null;
    }

    public interface OnCategoriaClickListener {
        void onCategoriaClick(Categoria categoria);
    }

    public CategoriaSelecaoAdapter(List<Categoria> listaCategorias, OnCategoriaClickListener listener) {
        this.listaCategorias = listaCategorias;
        this.listener = listener;
    }

    public void setCategoriaSelecionadaId(int id) {
        categoriaSelecionadaId = id;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_categoria_selecao, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Categoria categoria = listaCategorias.get(position);

        holder.textNomeCategoria.setText(categoria.nome);

        holder.checkCategoria.setChecked(categoria.id == categoriaSelecionadaId);

        holder.itemView.setOnClickListener(v -> {

            int pos = holder.getAdapterPosition();

            if (pos == RecyclerView.NO_POSITION) return;

            categoriaSelecionadaId = categoria.id;
            notifyDataSetChanged();

            Categoria categoriaClicada = listaCategorias.get(pos);

            if(listener != null){
                listener.onCategoriaClick(categoriaClicada);
            }

        });

    }

    @Override
    public int getItemCount() {
        return listaCategorias.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        CheckBox checkCategoria;
        TextView textNomeCategoria;

        public ViewHolder(View itemView) {
            super(itemView);

            checkCategoria = itemView.findViewById(R.id.checkCategoria);
            textNomeCategoria = itemView.findViewById(R.id.textNomeCategoria);
        }
    }

}