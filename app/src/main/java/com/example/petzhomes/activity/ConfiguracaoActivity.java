package com.example.petzhomes.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
<<<<<<< HEAD
import androidx.viewpager.widget.ViewPager;
=======
>>>>>>> bdf656a6a0651a2a9793adbc6c0006a2b43663e2

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.petzhomes.R;
import com.example.petzhomes.config.ConfiguracaoFirebase;
import com.example.petzhomes.config.UsuarioFirebase;
<<<<<<< HEAD
import com.example.petzhomes.fragment.EnderecoFragment;
import com.example.petzhomes.fragment.PerfilFragment;
=======
>>>>>>> bdf656a6a0651a2a9793adbc6c0006a2b43663e2
import com.example.petzhomes.helper.Base64Custom;
import com.example.petzhomes.helper.Permissao;
import com.example.petzhomes.modal.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
<<<<<<< HEAD
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
=======
>>>>>>> bdf656a6a0651a2a9793adbc6c0006a2b43663e2

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConfiguracaoActivity extends AppCompatActivity {

<<<<<<< HEAD
    private SmartTabLayout smartTabLayout;
    private ViewPager viewPager;
    private PerfilFragment perfilFragment = new PerfilFragment();
=======
    private TextInputEditText txtNome, txtEmail;
    private CircleImageView imgEditarPerfil;
    private Usuario usuarioLogado;
    private static final int SELECAO_GALERIA = 200;
    private StorageReference storageReference;
    private String idUsuario;
    private FirebaseUser usuarioPerfil;

    private String[] permissoesNecessarias = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };
>>>>>>> bdf656a6a0651a2a9793adbc6c0006a2b43663e2

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracao);

<<<<<<< HEAD
=======
        //Validar Permissoes
        Permissao.validarPermissoes(permissoesNecessarias, this, 1);

>>>>>>> bdf656a6a0651a2a9793adbc6c0006a2b43663e2
        //Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Editar Perfil");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

<<<<<<< HEAD
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("Perfil", PerfilFragment.class)
                .add("Endereço", EnderecoFragment.class).create()
        );

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);

        smartTabLayout = (SmartTabLayout) findViewById(R.id.viewpagertab);
        smartTabLayout.setViewPager(viewPager);
=======
        //Configuracoes Iniciais
        txtNome = findViewById(R.id.editNome);
        txtEmail = findViewById(R.id.editEmail);
        imgEditarPerfil = findViewById(R.id.imgFotoPerfil);

        //Configuacao Firebase
        usuarioLogado = UsuarioFirebase.getUsuarioLogado();
        usuarioPerfil = UsuarioFirebase.getUsuarioAtual();
        storageReference = ConfiguracaoFirebase.getStorageReference();
        idUsuario = UsuarioFirebase.getIdentificadorUsuario();
        usuarioLogado.setId(idUsuario);

        //Carregar dados do usuario
        txtNome.setText(usuarioPerfil.getDisplayName());
        txtEmail.setText(usuarioPerfil.getEmail());
        Uri url = usuarioPerfil.getPhotoUrl();
        if(url != null){
            Glide.with(ConfiguracaoActivity.this).load(url).into(imgEditarPerfil);
        }else{
            imgEditarPerfil.setImageResource(R.drawable.padrao);
        }
>>>>>>> bdf656a6a0651a2a9793adbc6c0006a2b43663e2

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

<<<<<<< HEAD
=======
    public void atualizarNome(View view){
        String nome = txtNome.getText().toString();
        if(!nome.isEmpty()){
            //Atualizar o nome no perfil
            UsuarioFirebase.atualizarNomeUsuario(nome);

            //Atualizar o nome no banco de dados
            usuarioLogado.setNome(nome);
            usuarioLogado.atualizar();

            Toast.makeText(
                    getApplicationContext(),
                    "Nome de usuário atualizado com sucesso!",
                    Toast.LENGTH_SHORT
            ).show();
        }else{
            Toast.makeText(
                    getApplicationContext(),
                    "Preencha os Campos",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    public void alterarFoto(View view){
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if( i.resolveActivity(getPackageManager()) != null){
            startActivityForResult(i, SELECAO_GALERIA);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            Bitmap imagem = null;
            try{
                //Selecao apenas da galeria
                switch (requestCode){
                    case SELECAO_GALERIA:
                        Uri localImagemSelecionada = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSelecionada);
                        break;
                }

                //Caso tenha sido escolhido uma imagem
                if(imagem != null){
                    //Configura imagem na tela
                    imgEditarPerfil.setImageBitmap(imagem);

                    //Recuperar dados da imagem para o Firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    //Salvar imagem no firebase
                    StorageReference imagemRef = storageReference
                            .child("imagens")
                            .child("perfil")
                            .child(idUsuario + ".jpeg");
                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Erro ao fazer upload da imagem",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Sucesso ao fazer upload da imagem",
                                    Toast.LENGTH_SHORT
                            ).show();
                            Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                            while(!uri.isComplete());
                            Uri url = uri.getResult();
                            atualizaFotoUsuario(url);
                        }
                    });

                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void atualizaFotoUsuario(Uri url){
        boolean retorno = UsuarioFirebase.atualizarFotoUsuario(url);
        if(retorno){
            usuarioLogado.setFoto(url.toString());
            usuarioLogado.atualizar();
            Toast.makeText(
                    getApplicationContext(),
                    "Sua foto foi atualizada",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    public void mudarSenha(View view){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String emailAddress = usuarioLogado.getEmail();

        auth.sendPasswordResetEmail(emailAddress).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(
                            getApplicationContext(),
                            "Email com redefinição de senha enviado",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
        });
    }

    public void excluirConta(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Excluir Conta");
        builder.setMessage("Tem certeza que deseja excluir sua conta? Após isso você só poderá acessar o app se criar outra conta");
        builder.setCancelable(true);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Conta Excluída", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                DatabaseReference usuariosRef = ConfiguracaoFirebase.getFirebaseDatabase()
                        .child("usuarios");
                usuariosRef.child(Base64Custom.codificarBase64(usuarioLogado.getEmail())).removeValue();
                FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
                autenticacao.signOut();
                startActivity(new Intent(ConfiguracaoActivity.this, LoginActivity.class));
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

>>>>>>> bdf656a6a0651a2a9793adbc6c0006a2b43663e2
}
