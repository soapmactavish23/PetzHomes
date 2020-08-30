package com.example.petzhomes.fragment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.security.keystore.UserPresenceUnavailableException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.petzhomes.R;
import com.example.petzhomes.activity.LoginActivity;
import com.example.petzhomes.config.ConfiguracaoFirebase;
import com.example.petzhomes.config.UsuarioFirebase;
import com.example.petzhomes.helper.Base64Custom;
import com.example.petzhomes.helper.Permissao;
import com.example.petzhomes.modal.Usuario;
import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class PerfilFragment extends Fragment {

    private TextInputEditText txtNome, txtEmail, txtCpf, txtContato;
    private CircleImageView imgEditarPerfil;
    private Usuario usuarioLogado;
    private Usuario usuarioAtual;
    private StorageReference storageReference;
    private String idUsuario;
    private FirebaseUser usuarioPerfil;
    private Button btnAlterar, btnMudarSenha, btnExcluirConta, btnAlterarFoto;

    private String[] permissoesNecessarias = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private static final int SELECAO_GALERIA = 200;

    public PerfilFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        //Validar Permissoes
        Permissao.validarPermissoes(permissoesNecessarias, getActivity(), 1);

        //Configuracoes Iniciais
        txtNome = view.findViewById(R.id.editNome);
        txtEmail = view.findViewById(R.id.editEmail);
        txtCpf = view.findViewById(R.id.editCpf);
        txtContato = view.findViewById(R.id.editContato);
        imgEditarPerfil = view.findViewById(R.id.imgFotoPerfil);
        btnAlterar = view.findViewById(R.id.btnAlterar);
        btnMudarSenha = view.findViewById(R.id.btnMudarSenha);
        btnExcluirConta = view.findViewById(R.id.btnExcluirConta);
        btnAlterarFoto = view.findViewById(R.id.btnAlterarFoto);

        //Criando Formato da Mascara
        SimpleMaskFormatter simpleMaskFormatterCPF = new SimpleMaskFormatter("NNN.NNN.NNN-NN");
        SimpleMaskFormatter simpleMaskFormatterContato = new SimpleMaskFormatter("(NN) NNNNN-NNNN");

        //Ajustando para os textos
        MaskTextWatcher maskTextWatcherCPF = new MaskTextWatcher(txtCpf, simpleMaskFormatterCPF);
        MaskTextWatcher maskTextWatcherContato = new MaskTextWatcher(txtContato, simpleMaskFormatterContato);

        //Aplicando Mascaras
        txtCpf.addTextChangedListener(maskTextWatcherCPF);
        txtContato.addTextChangedListener(maskTextWatcherContato);

        //Configuacao Firebase
        usuarioLogado = UsuarioFirebase.getUsuarioLogado();
        usuarioPerfil = UsuarioFirebase.getUsuarioAtual();
        storageReference = ConfiguracaoFirebase.getStorageReference();
        idUsuario = UsuarioFirebase.getIdentificadorUsuario();
        usuarioLogado.setId(idUsuario);

        //Dados usuario
        Bundle bundle = getActivity().getIntent().getExtras();
        usuarioAtual = (Usuario) bundle.getSerializable("DadosUsuario");

        //Carregar dados do usuario
        txtNome.setText(usuarioAtual.getNome());
        txtEmail.setText(usuarioPerfil.getEmail());
        txtContato.setText(usuarioAtual.getContato());
        txtCpf.setText(usuarioAtual.getCpf());

        //Foto de usuario
        Uri url = usuarioPerfil.getPhotoUrl();
        if(url != null){
            Glide.with(getActivity()).load(url).into(imgEditarPerfil);
        }else{
            imgEditarPerfil.setImageResource(R.drawable.padrao);
        }

        //Alterar Foto
        btnAlterarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alterarFoto();
            }
        });

        //Atualizar Nome
        btnAlterar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                atualizarNome();
            }
        });

        //Mudar Senha
        btnMudarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mudarSenha();
            }
        });

        //Excluir Conta
        btnExcluirConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                excluirConta();
            }
        });

        return view;
    }

    public void atualizarNome(){
        String nome = txtNome.getText().toString();
        String cpf = txtCpf.getText().toString();
        String contato = txtContato.getText().toString();
        if(!nome.isEmpty() && !cpf.isEmpty() && !contato.isEmpty()){
            //Atualizar o nome no perfil
            UsuarioFirebase.atualizarNomeUsuario(nome);

            //Atualizar o nome no banco de dados
            usuarioLogado.setNome(nome);
            usuarioLogado.setCpf(cpf);
            usuarioLogado.setContato(contato);
            usuarioLogado.atualizar();

            Toast.makeText(
                    getActivity(),
                    "Nome de usuário atualizado com sucesso!",
                    Toast.LENGTH_SHORT
            ).show();
        }else{
            Toast.makeText(
                    getActivity(),
                    "Preencha os Campos",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void atualizaFotoUsuario(Uri url){
        boolean retorno = UsuarioFirebase.atualizarFotoUsuario(url);
        if(retorno){
            usuarioLogado.setFoto(url.toString());
            usuarioLogado.setCpf(usuarioAtual.getCpf());
            usuarioLogado.setContato(usuarioAtual.getContato());
            usuarioLogado.atualizar();
            Toast.makeText(
                    getActivity(),
                    "Sua foto foi atualizada",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    public void mudarSenha(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String emailAddress = usuarioLogado.getEmail();

        auth.sendPasswordResetEmail(emailAddress).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(
                            getActivity(),
                            "Email com redefinição de senha enviado",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
        });
    }

    public void excluirConta(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
                            Toast.makeText(getActivity(), "Conta Excluída", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                DatabaseReference usuariosRef = ConfiguracaoFirebase.getFirebaseDatabase()
                        .child("usuarios");
                usuariosRef.child(Base64Custom.codificarBase64(usuarioLogado.getEmail())).removeValue();
                FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
                autenticacao.signOut();
                startActivity(new Intent(getActivity(), LoginActivity.class));
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

    public void alterarFoto(){
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if( i.resolveActivity(getActivity().getPackageManager()) != null){
            startActivityForResult(i, SELECAO_GALERIA);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            Bitmap imagem = null;
            try{
                //Selecao apenas da galeria
                switch (requestCode){
                    case SELECAO_GALERIA:
                        Uri localImagemSelecionada = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), localImagemSelecionada);
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
                                    getActivity(),
                                    "Erro ao fazer upload da imagem",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(
                                    getActivity(),
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

}
