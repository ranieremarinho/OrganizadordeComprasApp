package com.adssenac.organizadordecompras.adapter;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.adssenac.organizadordecompras.AlterarProdutoActivity;
import com.adssenac.organizadordecompras.R;
import com.adssenac.organizadordecompras.data.ProdutoDao;
import com.adssenac.organizadordecompras.model.ProdutoCategoria;

import java.util.List;

public class ProdutoCategoriaAdapter extends RecyclerView.Adapter<ProdutoCategoriaAdapter.ViewHolder> {

    List<ProdutoCategoria> lista;
    ProdutoDao produtoDao;

    public ProdutoCategoriaAdapter(List<ProdutoCategoria> lista, ProdutoDao produtoDao){
        this.lista = lista;
        this.produtoDao = produtoDao;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView tituloCategoria;
        CheckBox checkComprado;
        TextView nome;
        TextView quantidade;

        public ViewHolder(View itemView){
            super(itemView);

            tituloCategoria = itemView.findViewById(R.id.textCategoriaTituloItem);
            checkComprado = itemView.findViewById(R.id.checkComprado);
            nome = itemView.findViewById(R.id.textNomeProduto);
            quantidade = itemView.findViewById(R.id.textQuantidade);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_produto_categoria, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){

        ProdutoCategoria produto = lista.get(position);

        holder.nome.setText(produto.nome);
        holder.quantidade.setText(produto.quantidade);

        holder.checkComprado.setOnCheckedChangeListener(null);
        holder.checkComprado.setChecked(produto.comprado);

        if(produto.comprado){
            holder.nome.setPaintFlags(holder.nome.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.nome.setTextColor(Color.GRAY);
        }else{
            holder.nome.setPaintFlags(0);
            holder.nome.setTextColor(Color.BLACK);
        }

        holder.checkComprado.setOnCheckedChangeListener((buttonView, isChecked) -> {

            produto.comprado = isChecked;

            com.adssenac.organizadordecompras.model.Produto p =
                    new com.adssenac.organizadordecompras.model.Produto(
                            produto.nome,
                            produto.quantidade,
                            produto.categoriaId
                    );

            p.id = produto.id;
            p.comprado = produto.comprado;

            produtoDao.atualizar(p);
        });

        String categoriaAtual = produto.categoriaNome;

        if(position == 0){

            holder.tituloCategoria.setVisibility(View.VISIBLE);

        }else{

            String categoriaAnterior = lista.get(position - 1).categoriaNome;

            if(!categoriaAtual.equals(categoriaAnterior)){
                holder.tituloCategoria.setVisibility(View.VISIBLE);
            }else{
                holder.tituloCategoria.setVisibility(View.GONE);
            }
        }

        holder.tituloCategoria.setText(categoriaAtual);

        holder.itemView.setOnLongClickListener(v -> {

            String[] opcoes = {"Alterar", "Excluir"};

            new androidx.appcompat.app.AlertDialog.Builder(v.getContext())
                    .setTitle("Escolha uma ação")
                    .setItems(opcoes, (dialog, which) -> {

                        int pos = holder.getAdapterPosition();
                        if(pos == RecyclerView.NO_POSITION) return;

                        ProdutoCategoria prod = lista.get(pos);

                        if(which == 0){

                            Intent intent = new Intent(
                                    v.getContext(),
                                    AlterarProdutoActivity.class
                            );

                            intent.putExtra("produtoId", prod.id);

                            v.getContext().startActivity(intent);
                        }

                        if(which == 1){

                            com.adssenac.organizadordecompras.model.Produto p =
                                    new com.adssenac.organizadordecompras.model.Produto(
                                            prod.nome,
                                            prod.quantidade,
                                            prod.categoriaId
                                    );

                            p.id = prod.id;
                            p.comprado = prod.comprado;

                            produtoDao.deletar(p);

                            lista.remove(pos);

                            notifyItemRemoved(pos);
                        }

                    })
                    .show();

            return true;
        });
    }

    @Override
    public int getItemCount(){
        return lista.size();
    }
}