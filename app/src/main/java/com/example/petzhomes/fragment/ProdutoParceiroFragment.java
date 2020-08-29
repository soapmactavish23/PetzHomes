package com.example.petzhomes.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.petzhomes.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProdutoParceiroFragment extends Fragment {

    public ProdutoParceiroFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_produto_parceiro, container, false);
    }
}
