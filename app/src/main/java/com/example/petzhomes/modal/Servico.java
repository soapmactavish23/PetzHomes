package com.example.petzhomes.modal;

import android.provider.ContactsContract;

import com.example.petzhomes.config.ConfiguracaoFirebase;
import com.example.petzhomes.config.UsuarioFirebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Servico implements Serializable {

    private String id;
    private String tipo;
    private String descricao;
    private String valor;
    private String idParceiro;

    public Servico() {
        //Gerar o id
        DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference servicoRef = databaseReference.child("servicos");
        String idServico = servicoRef.push().getKey();
        setId(idServico);

        //Receber o id do parceiro
        String idParceiro = UsuarioFirebase.getIdentificadorUsuario();;
        setIdParceiro(idParceiro);
    }

    public Boolean salvar(){
        DatabaseReference servicosRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("servicos")
                .child(getIdParceiro())
                .child(getId());
        servicosRef.setValue(this);
        return true;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getIdParceiro() {
        return idParceiro;
    }

    public void setIdParceiro(String idParceiro) {
        this.idParceiro = idParceiro;
    }
}
