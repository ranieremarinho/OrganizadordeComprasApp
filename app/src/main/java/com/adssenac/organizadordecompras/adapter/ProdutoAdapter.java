package com.adssenac.organizadordecompras.adapter;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.adssenac.organizadordecompras.AlterarProdutoActivity;
import com.adssenac.organizadordecompras.R;
import com.adssenac.organizadordecompras.data.ProdutoDao;
import com.adssenac.organizadordecompras.model.Produto;

import java.util.List;

public class ProdutoAdapter extends RecyclerView.Adapter<ProdutoAdapter.ViewHolder> {

    List<Produto> lista;
    ProdutoDao produtoDao;

    public ProdutoAdapter(List<Produto> lista, ProdutoDao produtoDao) {
        this.lista = lista;
        this.produtoDao = produtoDao;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkComprado;
        TextView nome;
        TextView quantidade;

        public ViewHolder(View itemView) {
            super(itemView);
            checkComprado = itemView.findViewById(R.id.checkComprado);
            nome = itemView.findViewById(R.id.textNomeProduto);
            quantidade = itemView.findViewById(R.id.textQuantidade);
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

        holder.itemView.setOnLongClickListener(v -> {

            String[] opcoes = {"Alterar", "Excluir"};

            new androidx.appcompat.app.AlertDialog.Builder(v.getContext())
                    .setTitle("Escolha uma ação")
                    .setItems(opcoes, (dialog, which) -> {

                        if(which == 0){

                            // ALTERAR
                            Intent intent = new Intent(
                                    v.getContext(),
                                    AlterarProdutoActivity.class
                            );

                            intent.putExtra("produtoId", produto.id);
                            v.getContext().startActivity(intent);

                        }else if(which == 1){

                            // EXCLUIR
                            produtoDao.deletar(produto);
                            lista.remove(position);
                            notifyItemRemoved(position);

                        }

                    })
                    .show();

            return true;
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }
}