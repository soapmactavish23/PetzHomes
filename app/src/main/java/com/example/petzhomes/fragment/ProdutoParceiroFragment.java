package com.example.petzhomes.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.petzhomes.R;
import com.example.petzhomes.activity.CadastrarProdutoActivity;
import com.example.petzhomes.activity.CadastrarServicoActivity;
import com.example.petzhomes.adapter.PesquisaServicoAdapter;
import com.example.petzhomes.adapter.ProdutoParceiroAdapter;
import com.example.petzhomes.config.ConfiguracaoFirebase;
import com.example.petzhomes.config.UsuarioFirebase;
import com.example.petzhomes.helper.RecyclerItemClickListener;
import com.example.petzhomes.modal.Produto;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProdutoParceiroFragment extends Fragment {

    private FloatingActionButton btnAddProduto;
    private SearchView searchViewPesquisa;
    private RecyclerView recyclerViewPesquisa;
    private List<Produto> listaProdutos;
    private DatabaseReference produtosRef;
    private String idUsuario;
    private ProdutoParceiroAdapter adapter;
    private ValueEventListener valueEventListenerProdutos;
    private Produto produtoSelecionado;

    public ProdutoParceiroFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_produto_parceiro, container, false);

        btnAddProduto = view.findViewById(R.id.btnAddProduto);
        searchViewPesquisa = view.findViewById(R.id.searchViewPesquisa);
        recyclerViewPesquisa = view.findViewById(R.id.recyclerProduto);
        listaProdutos = new ArrayList<>();

        idUsuario = UsuarioFirebase.getIdentificadorUsuario();
        produtosRef = ConfiguracaoFirebase.getFirebaseDatabase().child("produtos").child(idUsuario);

        //Configurar o swipe
        searchViewPesquisa.setQueryHint("Buscar Produtos");
        searchViewPesquisa.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                String textoDigitado = s.toUpperCase();
                pesquisarProdutos(textoDigitado);
                int total = listaProdutos.size();
                return true;
            }
        });

        searchViewPesquisa.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                recuperarProdutos();
                return true;
            }
        });

        //Configurar o recyclerView
        recyclerViewPesquisa.setHasFixedSize(true);
        recyclerViewPesquisa.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Configurar o adatper
        adapter = new ProdutoParceiroAdapter(listaProdutos, getActivity());
        recyclerViewPesquisa.setAdapter(adapter);

        //Configurar o toque
        recyclerViewPesquisa.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getActivity(),
                        recyclerViewPesquisa,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                produtoSelecionado = listaProdutos.get(position);
                                Intent intent = new Intent(getActivity(), CadastrarProdutoActivity.class);
                                intent.putExtra("produtoSelecionado", produtoSelecionado);
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

        btnAddProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), CadastrarProdutoActivity.class));
            }
        });

        swipe();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarProdutos();
    }

    @Override
    public void onStop() {
        super.onStop();
        produtosRef.removeEventListener(valueEventListenerProdutos);
        listaProdutos.clear();
    }

    public void swipe(){
        ItemTouchHelper.Callback itemTouch = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int dragFlags = ItemTouchHelper.ACTION_STATE_IDLE;
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                excluirProduto(viewHolder);
            }
        };

        new ItemTouchHelper(itemTouch).attachToRecyclerView(recyclerViewPesquisa);

    }

    public void recuperarProdutos(){
        listaProdutos.clear();
        valueEventListenerProdutos = produtosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaProdutos.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    Produto produto = ds.getValue(Produto.class);
                    System.out.println(produto.getDescricao());
                    listaProdutos.add(produto);
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void pesquisarProdutos(String texto){
        listaProdutos.clear();
        if(texto.length() >= 2){
            Query query = produtosRef.orderByChild("descricao").startAt(texto);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //Limpar lista
                    for(DataSnapshot ds: dataSnapshot.getChildren()){
                        Produto produto = ds.getValue(Produto.class);
                        listaProdutos.add(produto);
                    }
                    adapter.notifyDataSetChanged();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public void excluirProduto(final RecyclerView.ViewHolder viewHolder){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("Excluir");
        alertDialog.setMessage("Tem certeza que deseja excluir esse produto?");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int position = viewHolder.getAdapterPosition();
                produtoSelecionado = listaProdutos.get(position);
                produtoSelecionado.deletar();
                adapter.notifyItemRemoved(position);
                listaProdutos.clear();
                adapter.notifyDataSetChanged();
            }
        });
        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getActivity(), "Cancelado", Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged();
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.show();
    }

}
