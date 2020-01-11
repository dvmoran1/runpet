package com.example.aplicacionmoviles.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.aplicacionmoviles.AddPets;
import com.example.aplicacionmoviles.R;
import com.example.aplicacionmoviles.models.Mascota;
import com.example.aplicacionmoviles.models.Usuario;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PetsFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private ArrayList<Mascota> listaMascotas;
    private FloatingActionButton fab;
    private Spinner spinner;
    private AdaptadorMascota adapter;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private Usuario userLogin;
    private ValueEventListener event;

    public PetsFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pets, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        getTableInfo();
        //Toast.makeText(getContext(), "Retorno", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        listaMascotas = new ArrayList<>();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("Mascotas");
        userLogin = (Usuario) getActivity().getIntent().getSerializableExtra("userLogin");

        fab = getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            if( v.getId() == R.id.fab ){
                String idUser = getArguments().getString("idUser");
                String nameUser = getArguments().getString("nameUser");
                Intent i = new Intent( getActivity(), AddPets.class );
                i.putExtra( "idUser", idUser );
                i.putExtra( "nameUser", nameUser );
                startActivity(i);
            }
        });

    }

    public void getTableInfo(){

        if( event != null ){
            myRef.removeEventListener(event);
        }
        event = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaMascotas = new ArrayList<>();
                for (DataSnapshot pet: dataSnapshot.getChildren()) {
                    Mascota CurrentPet = pet.getValue( Mascota.class );
                    if( CurrentPet.getIdDuenio().equals( userLogin.getId() ) ){
                        listaMascotas.add( CurrentPet );
                    }
                }

                ListView list = getView().findViewById( R.id.listView1 );
                adapter = new AdaptadorMascota( getContext());
                //list.setAdapter(null);
                list.invalidateViews();
                list.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        myRef.addValueEventListener(event);

    }

    class AdaptadorMascota extends ArrayAdapter{

        Context appCompatActivity;

        AdaptadorMascota( Context context ){
            super( context, R.layout.mascota, listaMascotas );
            appCompatActivity = context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) appCompatActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
            View item = inflater.inflate( R.layout.mascota, parent, false );
            ImageView image = item.findViewById(R.id.imagePet);
            TextView textView1 = item.findViewById( R.id.textNombrePet );
            textView1.setText(listaMascotas.get(position).getNombre() );
            textView1 = item.findViewById( R.id.textRaza );
            textView1.setText(listaMascotas.get(position).getRaza() );
            return item;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
