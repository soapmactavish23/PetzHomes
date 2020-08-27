package com.example.petzhomes.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.petzhomes.R;
import com.example.petzhomes.config.ConfiguracaoFirebase;
import com.example.petzhomes.config.UsuarioFirebase;
import com.example.petzhomes.helper.Base64Custom;
import com.example.petzhomes.modal.Usuario;
import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class CadastroActivity extends AppCompatActivity {

    private TextInputEditText editNome, editEmail, editSenha, editConfirmarSenha, editCpf, editContato;
    private SeekBar seekBar;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        editNome = findViewById(R.id.editNome);
        editEmail = findViewById(R.id.editEmailCadastro);
        editSenha = findViewById(R.id.editSenha);
        editConfirmarSenha = findViewById(R.id.editSenhaConfirmar);
        editCpf = findViewById(R.id.editCPF);
        editContato = findViewById(R.id.editContato);
        seekBar = findViewById(R.id.seekBar);

        //Criando Formato da Mascara
        SimpleMaskFormatter simpleMaskFormatterCPF = new SimpleMaskFormatter("NNN.NNN.NNN-NN");
        SimpleMaskFormatter simpleMaskFormatterContato = new SimpleMaskFormatter("(NN) NNNNN-NNNN");

        //Ajustando para os textos
        MaskTextWatcher maskTextWatcherCPF = new MaskTextWatcher(editCpf, simpleMaskFormatterCPF);
        MaskTextWatcher maskTextWatcherContato = new MaskTextWatcher(editContato, simpleMaskFormatterContato);

        //Aplicando Mascaras
        editCpf.addTextChangedListener(maskTextWatcherCPF);
        editContato.addTextChangedListener(maskTextWatcherContato);

    }

    public void voltar(View view){
        startActivity(new Intent(CadastroActivity.this, LoginActivity.class));
    }

    public void validarCadastro(View view){
        String nome = editNome.getText().toString();
        String email = editEmail.getText().toString();
        String senha = editSenha.getText().toString();
        String confirmarSenha = editConfirmarSenha.getText().toString();
        String cpf = editCpf.getText().toString();
        String contato = editContato.getText().toString();

        String msg = "";

        if(nome.isEmpty() || nome.equals("")){
            msg = "Preencha o campo Nome";
        }else if(email.isEmpty() || email.equals("")){
            msg = "Preencha o campo E-mail";
        }else if (senha.isEmpty() || senha.equals("")){
            msg = "Preencha o campo Senha";
        }else if(confirmarSenha.isEmpty() || confirmarSenha.equals("")){
            msg = "Preencha o campo Confirmar Senha";
        }else if(cpf.isEmpty() || cpf.equals("")){
            msg = "Preencha o campo CPF";
        }else if(contato.isEmpty() || contato.equals("")){
            msg = "Preencha o campo Contato";
        }else{
            msg = "Processando...";
            final Usuario usuario = new Usuario();
            usuario.setNome(nome);
            usuario.setEmail(email);
            usuario.setSenha(senha);
            usuario.setContato(contato);
            usuario.setCpf(cpf);

            //Tipo de usuario
            switch (seekBar.getProgress()){
                case 0:
                    usuario.setTipo_usuario("CLIENTE");
                    break;
                case 1:
                    usuario.setTipo_usuario("ENTREGADOR");
                    break;
                case 2:
                    usuario.setTipo_usuario("PARCEIRO");
                    break;
                default:
                    usuario.setTipo_usuario("CLIENTE");
                    break;
            }
            cadastrar(usuario);
        }
        Toast.makeText(
                getApplicationContext(),
                msg,
                Toast.LENGTH_SHORT
        ).show();
    }

    private void cadastrar(final Usuario usuario){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    try{
                        String id = Base64Custom.codificarBase64(usuario.getEmail());
                        usuario.setId(id);
                        usuario.salvar();

                        //Salvar dados no profile do firebase
                        UsuarioFirebase.atualizarNomeUsuario(usuario.getNome());

                        Toast.makeText(
                                CadastroActivity.this,
                                "Sucesso ao cadastrar usuário",
                                Toast.LENGTH_SHORT
                        ).show();

                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else{
                    String excecao = "";
                    try {
                        throw task.getException();
                    }catch ( FirebaseAuthWeakPasswordException e){
                        excecao = "Digite uma senha mais forte!";
                    }catch ( FirebaseAuthInvalidCredentialsException e){
                        excecao= "Por favor, digite um e-mail válido";
                    }catch ( FirebaseAuthUserCollisionException e){
                        excecao = "Este conta já foi cadastrada";
                    }catch (Exception e){
                        excecao = "Erro ao cadastrar usuário: "  + e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(
                            CadastroActivity.this,
                            excecao,
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
        });
    }

}
