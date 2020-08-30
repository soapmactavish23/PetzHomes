package com.example.petzhomes.fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.petzhomes.R;
import com.example.petzhomes.activity.ConfiguracaoActivity;
import com.example.petzhomes.activity.LoginActivity;
import com.example.petzhomes.config.ConfiguracaoFirebase;
import com.example.petzhomes.config.UsuarioFirebase;
import com.example.petzhomes.helper.Base64Custom;
import com.example.petzhomes.helper.Permissao;
import com.example.petzhomes.modal.Endereco;
import com.example.petzhomes.modal.Usuario;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.VIBRATOR_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class EnderecoFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private EditText editLocal;
    private Button btnSalvarEndereco;
    private LatLng meuLocal;

    public EnderecoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_endereco, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        editLocal = view.findViewById(R.id.editLocal);
        btnSalvarEndereco = view.findViewById(R.id.btnSalvarEndereco);

        btnSalvarEndereco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                salvarEndereco();
            }
        });

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Recuperar loc usuario
        recuperarLocalizacaoUsuario();

    }

    private void recuperarLocalizacaoUsuario() {
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                //Recuperar lat e long
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                meuLocal = new LatLng(latitude, longitude);

                mMap.addMarker(new MarkerOptions()
                        .position(meuLocal)
                        .title("Meu Local")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                );

                mMap.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(meuLocal, 15)
                );

            }
        };

        //Solicitar atualizacoes de localizacao
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    10000,
                    10,
                    locationListener
            );
            return;
        }
    }

    private void salvarEndereco(){
        String enderecoLocal = editLocal.getText().toString();

        Endereco endereco = new Endereco();

        if( !enderecoLocal.equals("") || enderecoLocal != null ){
            Address addressLocal = recuperarEndereco( enderecoLocal );
            if( addressLocal != null ){
                endereco.setCidade( addressLocal.getAdminArea() );
                endereco.setCep( addressLocal.getPostalCode() );
                endereco.setBairro( addressLocal.getSubLocality() );
                endereco.setRua( addressLocal.getThoroughfare() );
                endereco.setNumero( addressLocal.getFeatureName() );
                endereco.setLatitude( String.valueOf(addressLocal.getLatitude()) );
                endereco.setLongitude( String.valueOf(addressLocal.getLongitude()) );
            }
        }else {
            Address addressLocal = recuperarEnderecoAtual(meuLocal.latitude, meuLocal.longitude);
            if(addressLocal != null){
                endereco.setCidade( addressLocal.getAdminArea() );
                endereco.setCep( addressLocal.getPostalCode() );
                endereco.setBairro( addressLocal.getSubLocality() );
                endereco.setRua( addressLocal.getThoroughfare() );
                endereco.setNumero( addressLocal.getFeatureName() );
                endereco.setLatitude( String.valueOf(addressLocal.getLatitude()) );
                endereco.setLongitude( String.valueOf(addressLocal.getLongitude()) );
            }
        }

        StringBuilder mensagem = new StringBuilder();
        mensagem.append( "Cidade: " + endereco.getCidade() );
        mensagem.append( "\nRua: " + endereco.getRua() );
        mensagem.append( "\nBairro: " + endereco.getBairro() );
        mensagem.append( "\nNúmero: " + endereco.getNumero() );
        mensagem.append( "\nCep: " + endereco.getCep() );

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle("Confirme seu endereco!")
                .setMessage(mensagem)
                .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //salvar requisição

                    }
                }).setNegativeButton("cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private Address recuperarEndereco(String endereco){
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {
            List<Address> listaEnderecos = geocoder.getFromLocationName(endereco, 1);
            if( listaEnderecos != null && listaEnderecos.size() > 0 ){
                Address address = listaEnderecos.get(0);

                return address;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Address recuperarEnderecoAtual(Double latitude, Double longitude){
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try{
            List<Address> listaEnderecos = geocoder.getFromLocation(latitude, longitude, 1);
            if(listaEnderecos.size() > 0){
                Address address = listaEnderecos.get(0);
                System.out.println(address.getAdminArea());
                return address;
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

}
