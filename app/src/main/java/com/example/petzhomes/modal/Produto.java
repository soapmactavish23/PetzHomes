package com.example.petzhomes.modal;

import com.example.petzhomes.config.ConfiguracaoFirebase;
import com.example.petzhomes.config.UsuarioFirebase;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

public class Produto implements Serializable {

    private String id;
    private String idParceiro;
    private String descricao;
    private String valor;
    private String marca;

    public Produto() {

        //Gerar o id
        DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference produtoRef = databaseReference.child("produtos");
        String idProduto = produtoRef.push().getKey();
        setId(idProduto);

        //Receber o id do parceiro
        String idParceiro = UsuarioFirebase.getIdentificadorUsuario();;
        setIdParceiro(idParceiro);

    }

    public Boolean salvar(){
        DatabaseReference produtoRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("produtos")
                .child(getIdParceiro())
                .child(getId());
        produtoRef.setValue(this);
        return true;
    }

    public Boolean deletar(){
        DatabaseReference produtoRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("produtos")
                .child(getIdParceiro())
                .child(getId());
        produtoRef.removeValue();
        return true;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdParceiro() {
        return idParceiro;
    }

    public void setIdParceiro(String idParceiro) {
        this.idParceiro = idParceiro;
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

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }
}
