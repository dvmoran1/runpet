package com.example.aplicacionmoviles;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.aplicacionmoviles.models.Mascota;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class AddPets extends AppCompatActivity {

    private Spinner spinnerRaza, spinnerTam;
    private ArrayAdapter adapter;
    private Button addPet;
    private String idUser;
    private String nameUser;
    private EditText namePet;
    private DatabaseReference root;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_pets_add);

        idUser = getIntent().getStringExtra("idUser");
        nameUser = getIntent().getStringExtra("nameUser");

        database = FirebaseDatabase.getInstance();
        root = database.getReference();

        spinnerRaza = findViewById(R.id.listRaza);
        adapter = ArrayAdapter.createFromResource( this , R.array.planets_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRaza.setAdapter(adapter);
        spinnerTam = findViewById(R.id.listTam);
        adapter = ArrayAdapter.createFromResource(this, R.array.tam_pets, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTam.setAdapter(adapter);

        namePet = findViewById(R.id.petName);

        addPet = findViewById(R.id.btnAgregaPet);
        addPet.setOnClickListener(v -> {
            Mascota pet = new Mascota();
            pet.setIdDuenio( idUser );
            pet.setNombre( namePet.getText().toString() );
            pet.setRaza( spinnerRaza.getSelectedItem().toString() );
            pet.setTamanio( spinnerTam.getSelectedItem().toString() );
            pet.setNomDuenio( nameUser );

            DatabaseReference pets = root.child("Mascotas");
            String id = pets.push().getKey();

            pet.setId(id);

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put(id, pet);

            pets.updateChildren(hashMap)
                    .addOnSuccessListener(aVoid -> {
                        finish();
                    });

        });

    }
}
