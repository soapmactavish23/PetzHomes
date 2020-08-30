package com.example.petzhomes.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petzhomes.R;
import com.example.petzhomes.modal.Servico;
import com.google.android.gms.maps.model.CircleOptions;

import java.util.List;

public class PesquisaServicoAdapter extends RecyclerView.Adapter<PesquisaServicoAdapter.MyViewHolder> {

    private List<Servico> listaServicos;
    private Context context;

    public PesquisaServicoAdapter(List<Servico> listaServicos, Context context) {
        this.listaServicos = listaServicos;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_pesquisa_servico, parent,false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Servico servico = listaServicos.get(position);
        holder.txtDescricaoServico.setText(servico.getDescricao());
        holder.txtValorServico.setText("R$ " + servico.getValor());
        holder.txtTipoServico.setText(servico.getTipo());

    }

    @Override
    public int getItemCount() {
        return listaServicos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView txtDescricaoServico, txtTipoServico, txtValorServico;

        public MyViewHolder(View itemView){
            super(itemView);
            txtDescricaoServico = itemView.findViewById(R.id.txtDescricaoServico);
            txtTipoServico = itemView.findViewById(R.id.txtTipoServico);
            txtValorServico = itemView.findViewById(R.id.txtValorServico);
        }
    }

}
