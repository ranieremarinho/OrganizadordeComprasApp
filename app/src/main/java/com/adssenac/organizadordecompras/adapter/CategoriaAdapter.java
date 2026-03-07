package com.adssenac.organizadordecompras.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.adssenac.organizadordecompras.R;
import com.adssenac.organizadordecompras.data.ProdutoDao;
import com.adssenac.organizadordecompras.model.Categoria;
import com.adssenac.organizadordecompras.data.CategoriaDao;

import java.util.List;

public class CategoriaAdapter extends RecyclerView.Adapter<CategoriaAdapter.ViewHolder> {

    private List<Categoria> listaCategorias;
    private OnCategoriaClickListener listener;
    private CategoriaDao categoriaDao;
    private ProdutoDao produtoDao;

    public interface OnCategoriaClickListener {
        void onCategoriaClick(Categoria categoria);
    }

    public CategoriaAdapter(List<Categoria> listaCategorias,
                            CategoriaDao categoriaDao,
                            ProdutoDao produtoDao,
                            OnCategoriaClickListener listener) {

        this.listaCategorias = listaCategorias;
        this.categoriaDao = categoriaDao;
        this.produtoDao = produtoDao;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textNomeCategoria;

        public ViewHolder(View itemView) {
            super(itemView);
            textNomeCategoria = itemView.findViewById(R.id.textNomeCategoria);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_categoria, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Categoria categoria = listaCategorias.get(position);
        holder.textNomeCategoria.setText(categoria.nome);

        holder.itemView.setOnLongClickListener(v -> {

            String[] opcoes = {"Alterar", "Excluir"};

            new androidx.appcompat.app.AlertDialog.Builder(v.getContext())
                    .setTitle("Categoria")
                    .setItems(opcoes, (dialog, which) -> {

                        int pos = holder.getAdapterPosition();
                        if (pos == RecyclerView.NO_POSITION) return;

                        Categoria cat = listaCategorias.get(pos);

                        if (which == 0) {

                            // DIGITAR NOVO NOME

                            android.widget.EditText input = new android.widget.EditText(v.getContext());
                            input.setText(cat.nome);

                            new androidx.appcompat.app.AlertDialog.Builder(v.getContext())
                                    .setTitle("Alterar categoria")
                                    .setView(input)
                                    .setPositiveButton("Continuar", (d, w) -> {

                                        String novoNome = input.getText().toString().trim();

                                        if (novoNome.isEmpty()) {
                                            android.widget.Toast.makeText(
                                                    v.getContext(),
                                                    "Nome não pode ser vazio",
                                                    android.widget.Toast.LENGTH_SHORT
                                            ).show();
                                            return;
                                        }

                                        Categoria existente = categoriaDao.buscarPorNome(novoNome);

                                        if (existente != null) {
                                            android.widget.Toast.makeText(
                                                    v.getContext(),
                                                    "Categoria já existe",
                                                    android.widget.Toast.LENGTH_SHORT
                                            ).show();
                                            return;
                                        }

                                        // CONFIRMAÇÃO FINAL

                                        new androidx.appcompat.app.AlertDialog.Builder(v.getContext())
                                                .setTitle("Confirmar alteração")
                                                .setMessage("Deseja alterar o nome da categoria para:\n\n" + novoNome + "?")
                                                .setPositiveButton("Sim", (d2, w2) -> {

                                                    cat.nome = novoNome;

                                                    categoriaDao.atualizar(cat);

                                                    notifyItemChanged(pos);

                                                })
                                                .setNegativeButton("Cancelar", null)
                                                .show();

                                    })
                                    .setNegativeButton("Cancelar", null)
                                    .show();
                        }

                        if (which == 1) {

                            new androidx.appcompat.app.AlertDialog.Builder(v.getContext())
                                    .setTitle("Excluir categoria")
                                    .setMessage("Deseja realmente excluir a categoria \"" + cat.nome + "\"?")
                                    .setPositiveButton("Sim", (d, w) -> {

                                        // buscar categoria Diversos
                                        Categoria diversos = categoriaDao.buscarPorNome("Diversos");

                                        if(diversos != null){

                                            // mover produtos para Diversos
                                            produtoDao.moverProdutosParaOutraCategoria(cat.id, diversos.id);

                                        }

                                        categoriaDao.deletar(cat);

                                        listaCategorias.remove(pos);

                                        notifyItemRemoved(pos);

                                    })
                                    .setNegativeButton("Cancelar", null)
                                    .show();
                        }

                    })
                    .show();

            return true;
        });
    }

    @Override
    public int getItemCount() {
        return listaCategorias.size();
    }
}