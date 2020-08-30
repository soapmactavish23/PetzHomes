package com.example.petzhomes.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.petzhomes.R;
import com.example.petzhomes.modal.Servico;
import com.example.petzhomes.modal.Usuario;
import com.google.android.material.textfield.TextInputEditText;

public class CadastrarServicoActivity extends AppCompatActivity {

    private SeekBar seekBarTipoServico;
    private TextInputEditText editDescricao, editValor;
    private Servico servicoSelecionado;
    private Servico servico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_servico);

        //Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Cadastrar Serviços");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        //Configuracoes Iniciais
        seekBarTipoServico = findViewById(R.id.seekBarTipoServico);
        editDescricao = findViewById(R.id.editDescricao);
        editValor = findViewById(R.id.editValor);

        //Dados usuario
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            servicoSelecionado = (Servico) bundle.getSerializable("servicoSelecionado");
            servico = servicoSelecionado;
            editDescricao.setText(servico.getDescricao());
            editValor.setText(servico.getValor());
            switch (servico.getTipo()){
                case "PetShop":
                    seekBarTipoServico.setProgress(0);
                    break;
                case "Veterinário":
                    seekBarTipoServico.setProgress(1);
                    break;
                case "Tosador":
                    seekBarTipoServico.setProgress(2);
                    break;
            }
        }else{
            servico = new Servico();
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

    public void salvarServico(View view){
        String descricao = editDescricao.getText().toString();
        String valor = editValor.getText().toString();

        if(descricao.isEmpty() || valor.isEmpty()){
            Toast.makeText(
                    getApplicationContext(),
                    "Preencha todos os campos",
                    Toast.LENGTH_SHORT
            ).show();
        }else{
            servico.setDescricao(descricao);
            servico.setValor(valor);
            switch (seekBarTipoServico.getProgress()){
                case 0:
                    servico.setTipo("PetShop");
                    break;
                case 1:
                    servico.setTipo("Veterinário");
                    break;
                case 2:
                    servico.setTipo("Tosador");
                    break;
            }
            if(servico.salvar()){
                Toast.makeText(getApplicationContext(),"Sucesso ao salvar o serviço", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

    }

}
