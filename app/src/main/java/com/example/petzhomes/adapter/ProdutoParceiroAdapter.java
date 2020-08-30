package com.example.petzhomes.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petzhomes.R;
import com.example.petzhomes.modal.Produto;

import java.util.List;


public class ProdutoParceiroAdapter extends RecyclerView.Adapter<ProdutoParceiroAdapter.MyViewHolder> {

    private List<Produto> listaProduto;
    private Context context;

    public ProdutoParceiroAdapter(List<Produto> listaProduto, Context context) {
        this.listaProduto = listaProduto;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_pesquisa_produto, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Produto produto = listaProduto.get(position);
        holder.txtDescricaoProduto.setText(produto.getDescricao());
        holder.txtValorProduto.setText("R$ " + produto.getValor());
        holder.txtMarcaProduto.setText(produto.getMarca());

    }

    @Override
    public int getItemCount() {
        return listaProduto.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView txtDescricaoProduto, txtValorProduto, txtMarcaProduto;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDescricaoProduto = itemView.findViewById(R.id.txtDescricaoProduto);
            txtValorProduto = itemView.findViewById(R.id.txtValorProduto);
            txtMarcaProduto = itemView.findViewById(R.id.txtMarcaProduto);
        }
    }

}
