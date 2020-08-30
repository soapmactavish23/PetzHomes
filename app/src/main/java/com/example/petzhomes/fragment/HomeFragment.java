package com.example.petzhomes.fragment;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.petzhomes.R;
import com.example.petzhomes.config.ConfiguracaoFirebase;
import com.example.petzhomes.config.UsuarioFirebase;
import com.example.petzhomes.modal.Usuario;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private CircleImageView imgFotoPerfil;
    private TextView txtNomeUsuario, txtQtdServicos, txtQtdProdutos;
    private DatabaseReference databaseReference;
    private DatabaseReference produtosRef;
    private DatabaseReference servicosRef;
    private String idUsuario;
    private FirebaseUser usuarioPerfil;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //Configuracoes Iniciais
        imgFotoPerfil = view.findViewById(R.id.imgFotoPerfil);
        txtNomeUsuario = view.findViewById(R.id.txtNomeUsuario);
        txtQtdServicos = view.findViewById(R.id.txtQtdServicos);
        txtQtdProdutos = view.findViewById(R.id.txtQtdProdutos);
        databaseReference = ConfiguracaoFirebase.getFirebaseDatabase();
        idUsuario = UsuarioFirebase.getIdentificadorUsuario();
        produtosRef = databaseReference.child("produtos").child(idUsuario);
        servicosRef = databaseReference.child("servicos").child(idUsuario);
        usuarioPerfil = UsuarioFirebase.getUsuarioAtual();

        //Foto Perfil
        Uri url = usuarioPerfil.getPhotoUrl();
        if(url != null){
            Glide.with(getActivity()).load(url).into(imgFotoPerfil);
        }else{
            imgFotoPerfil.setImageResource(R.drawable.padrao);
        }

        //Nome de Usuario
        txtNomeUsuario.setText(usuarioPerfil.getDisplayName());

        //Recuperando o total de Servicos
        servicosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int qtdServicos = (int) dataSnapshot.getChildrenCount();
                txtQtdServicos.setText(qtdServicos + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Recuperando o total de Produtos
        produtosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int qtdProdutos = (int) dataSnapshot.getChildrenCount();
                txtQtdProdutos.setText(qtdProdutos + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }
}
