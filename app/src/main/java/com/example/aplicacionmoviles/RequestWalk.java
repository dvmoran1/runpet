package com.example.aplicacionmoviles;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.aplicacionmoviles.models.Mascota;
import com.example.aplicacionmoviles.models.Paseo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class RequestWalk extends AppCompatActivity {

    private FirebaseDatabase db;
    private DatabaseReference root, myRef;
    private ArrayList<String> mascotas;
    private String nameUser, idUser;
    private Spinner pets;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_walks_add);

        idUser = getIntent().getStringExtra("idUser");
        nameUser = getIntent().getStringExtra("nameUser");

        db = FirebaseDatabase.getInstance();
        root = db.getReference();

        init();

        Button btnRequest = findViewById(R.id.btnRequest);
        btnRequest.setOnClickListener(ocl -> {

            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Location currentL = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            DatabaseReference walks = root.child("Paseos");
            String id = walks.push().getKey();

            Spinner namePet = findViewById(R.id.listPets);
            Date currentDate = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            Paseo newWalk = new Paseo();
            newWalk.setId(id);
            newWalk.setIdDuenio( idUser );
            newWalk.setNomMascota( namePet.getSelectedItem().toString() );
            newWalk.setStatus( 0 );
            newWalk.setDateWalk( sdf.format( currentDate ) );
            newWalk.setLongitude( currentL.getLongitude() );
            newWalk.setLatitude( currentL.getLatitude() );
            newWalk.setNomDuenio( nameUser );
            newWalk.setNomPaseador( "Solicitud pendiente" );

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put(id, newWalk);

            walks.updateChildren(hashMap)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getApplicationContext(), "Se ha mandado la solicitud a los paseadores", Toast.LENGTH_LONG).show();
                        finish();
                    });


        });

    }

    public void init(){
        myRef = db.getReference().child("Mascotas");
        ValueEventListener event = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mascotas = new ArrayList<>();
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    Mascota pet = ds.getValue( Mascota.class );
                    if( pet.getIdDuenio().equals( idUser ) ){
                        mascotas.add(pet.getNombre());
                    }
                }
                Spinner spinnerPet = findViewById(R.id.listPets);
                ArrayAdapter<String> petsAdapter = new ArrayAdapter<>( RequestWalk.this, android.R.layout.simple_spinner_dropdown_item, mascotas);
                petsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerPet.setAdapter(petsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        myRef.addValueEventListener(event);

    }

}
