package com.example.petzhomes.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.security.keystore.UserPresenceUnavailableException;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.petzhomes.R;
import com.example.petzhomes.config.ConfiguracaoFirebase;
import com.example.petzhomes.config.UsuarioFirebase;
import com.example.petzhomes.modal.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout textEmail, textSenha;
    private TextInputEditText editEmail, editSenha;
    private FirebaseAuth autenticacao;
    private ProgressBar progressBar;
    private TextView txtNaoTemConta;
    private Button btnEntrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editEmail = findViewById(R.id.editEmail);
        editSenha = findViewById(R.id.editSenha);
        progressBar = findViewById(R.id.progressBar);
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        textEmail = findViewById(R.id.textEmail);
        textSenha = findViewById(R.id.textSenha);
        txtNaoTemConta = findViewById(R.id.txtNaoTemConta);
        btnEntrar = findViewById(R.id.btnEntar);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser usuarioAtual = autenticacao.getCurrentUser();
        progressBar.setVisibility(View.GONE);
        if(usuarioAtual != null){
            textEmail.setVisibility(View.GONE);
            textSenha.setVisibility(View.GONE);
            btnEntrar.setVisibility(View.GONE);
            txtNaoTemConta.setVisibility(View.GONE);
            abrirMainActivity();
        }else{
            textEmail.setVisibility(View.VISIBLE);
            textSenha.setVisibility(View.VISIBLE);
            btnEntrar.setVisibility(View.VISIBLE);
            txtNaoTemConta.setVisibility(View.VISIBLE);
        }
    }

    //Validar autenticacao
    public void validarAutenticacao(View view){
        String email = editEmail.getText().toString();
        String senha = editSenha.getText().toString();
        if(!email.isEmpty() || !senha.isEmpty()){
            Usuario usuario = new Usuario();
            usuario.setEmail(email);
            usuario.setSenha(senha);
            logar(usuario);
        }else{
            Toast.makeText(
                    getApplicationContext(),
                    "Preencha Todos os Campos",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    public void logar(Usuario usuario){
        autenticacao.signInWithEmailAndPassword(usuario.getEmail(), usuario.getSenha())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            abrirMainActivity();
                        }else{
                            String excecao = "";
                            try {
                                throw task.getException();
                            }catch ( FirebaseAuthInvalidUserException e ) {
                                excecao = "Usuário não está cadastrado.";
                            }catch ( FirebaseAuthInvalidCredentialsException e ){
                                excecao = "E-mail e senha não correspondem a um usuário cadastrado";
                            }catch (Exception e){
                                excecao = "Erro ao cadastrar usuário: "  + e.getMessage();
                                e.printStackTrace();
                            }
                            Toast.makeText(
                                    LoginActivity.this,
                                    excecao,
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
                });
    }
    public void cadastrarSe(View view){
        startActivity(new Intent(LoginActivity.this, CadastroActivity.class));
    }
    private void abrirMainActivity(){
        progressBar.setVisibility(View.VISIBLE);
        UsuarioFirebase.redirecionarUsuarioLogado(LoginActivity.this);
    }
}