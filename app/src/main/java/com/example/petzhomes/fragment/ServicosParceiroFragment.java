package com.example.petzhomes.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SearchView;

import com.example.petzhomes.R;
import com.example.petzhomes.activity.CadastrarServicoActivity;
import com.example.petzhomes.activity.CadastroActivity;
import com.example.petzhomes.adapter.PesquisaServicoAdapter;
import com.example.petzhomes.config.ConfiguracaoFirebase;
import com.example.petzhomes.config.UsuarioFirebase;
import com.example.petzhomes.helper.RecyclerItemClickListener;
import com.example.petzhomes.modal.Servico;
import com.example.petzhomes.modal.Usuario;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 */
public class ServicosParceiroFragment extends Fragment {

    private FloatingActionButton btnAddServico;
    private SearchView searchViewPesquisa;
    private RecyclerView recyclerViewPesquisa;
    private List<Servico> listaServicos;
    private DatabaseReference servicosRef;
    private String idUsuarioLogado;
    private PesquisaServicoAdapter adapter;
    private ValueEventListener valueEventListenerServicos;

    public ServicosParceiroFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_servicos_parceiro, container, false);

        //Configuracoes Iniciais
        btnAddServico = view.findViewById(R.id.btnAddServico);
        searchViewPesquisa = view.findViewById(R.id.searchViewPesquisa);
        recyclerViewPesquisa = view.findViewById(R.id.recyclerPesquisa);
        listaServicos = new ArrayList<>();
        idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();
        servicosRef = ConfiguracaoFirebase.getFirebaseDatabase().child("servicos").child(idUsuarioLogado);

        //Configurar o searchView
        searchViewPesquisa.setQueryHint("Buscar Serviços");
        searchViewPesquisa.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                String textoDigitado = s.toUpperCase();
                pesquisarServicos(textoDigitado);
                int total = listaServicos.size();
                return true;
            }
        });

        searchViewPesquisa.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                recuperarServicos();
                return true;
            }
        });

        //Configurar recyclerView
        recyclerViewPesquisa.setHasFixedSize(true);
        recyclerViewPesquisa.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Configurar o adapter
        adapter = new PesquisaServicoAdapter(listaServicos, getActivity());
        recyclerViewPesquisa.setAdapter(adapter);

        //Configurando o click
        recyclerViewPesquisa.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getActivity(),
                        recyclerViewPesquisa,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Servico servicoSelecionado = listaServicos.get(position);
                                Intent intent = new Intent(getActivity(), CadastrarServicoActivity.class);
                                intent.putExtra("servicoSelecionado", servicoSelecionado);
                                startActivity(intent);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            }
                        }
                )
        );

        //Abrir tela de cadastro de Servicos
        btnAddServico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), CadastrarServicoActivity.class));
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarServicos();
    }

    @Override
    public void onStop() {
        super.onStop();
        servicosRef.removeEventListener(valueEventListenerServicos);
        listaServicos.clear();
    }

    private void recuperarServicos(){
        listaServicos.clear();
        valueEventListenerServicos = servicosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    Servico servico = ds.getValue(Servico.class);
                    listaServicos.add(servico);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void pesquisarServicos(String texto){
        listaServicos.clear();
        //Pesquisar seus servicos
        if(texto.length() >= 2){
            Query query = servicosRef.orderByChild("tipo").startAt(texto);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //Limpar lista
                    for(DataSnapshot ds: dataSnapshot.getChildren()){
                        Servico servico = ds.getValue(Servico.class);
                        listaServicos.add(servico);
                    }
                    adapter.notifyDataSetChanged();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

}
