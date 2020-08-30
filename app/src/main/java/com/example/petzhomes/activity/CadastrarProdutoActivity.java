package com.example.petzhomes.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.petzhomes.R;
import com.example.petzhomes.modal.Produto;
import com.google.android.material.textfield.TextInputEditText;

public class CadastrarProdutoActivity extends AppCompatActivity {

    private TextInputEditText editDescricao, editMarca,editValor;
    private Produto produtoSelecionado;
    private Produto produto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_produto);

        //Configurando a Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Cadastrar Serviços");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        //Configuracoes Iniciais
        editMarca = findViewById(R.id.editMarca);
        editDescricao = findViewById(R.id.editDescricao);
        editValor = findViewById(R.id.editValor);

        //Dados usuario
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            produtoSelecionado = (Produto) bundle.getSerializable("produtoSelecionado");
            produto = produtoSelecionado;
            editMarca.setText(produto.getMarca());
            editDescricao.setText(produto.getDescricao());
            editValor.setText(produto.getValor());

        }else{
            produto = new Produto();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

    public void salvarProduto(View view){
        String descricao = editDescricao.getText().toString();
        String valor = editValor.getText().toString();
        String marca = editMarca.getText().toString();

        if(descricao.isEmpty() || valor.isEmpty()){
            Toast.makeText(
                    getApplicationContext(),
                    "Preencha todos os campos",
                    Toast.LENGTH_SHORT
            ).show();
        }else{
            produto.setMarca(marca);
            produto.setDescricao(descricao);
            produto.setValor(valor);

            if(produto.salvar()){
                Toast.makeText(getApplicationContext(),"Sucesso ao salvar o serviço", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

    }

}
