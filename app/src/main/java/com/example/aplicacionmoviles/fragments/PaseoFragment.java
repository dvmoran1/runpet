package com.example.aplicacionmoviles.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.aplicacionmoviles.R;
import com.example.aplicacionmoviles.RequestWalk;
import com.example.aplicacionmoviles.models.Paseo;
import com.example.aplicacionmoviles.models.Usuario;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PaseoFragment extends Fragment {

    private ArrayList<Paseo> listaMascotas;
    private FloatingActionButton fab;
    private Spinner spinner;
    private AdaptadorPaseos adapter;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private Usuario userLogin;
    private ValueEventListener event;

    public PaseoFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_walks, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        getTableInfo();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        listaMascotas = new ArrayList<>();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("Paseos");
        userLogin = (Usuario) getActivity().getIntent().getSerializableExtra("userLogin");

        fab = getActivity().findViewById(R.id.fab2);
        fab.setOnClickListener(v -> {
            if( v.getId() == R.id.fab2 ){
                String idUser = getArguments().getString("idUser");
                String nameUser = getArguments().getString("nameUser");
                Intent i = new Intent( getActivity(), RequestWalk.class );
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
                for (DataSnapshot walk: dataSnapshot.getChildren()) {
                    Paseo walking = walk.getValue( Paseo.class );
                    if( walking.getIdDuenio().equals( userLogin.getId() ) ){
                        listaMascotas.add( walking );
                    }
                }

                ListView list = getView().findViewById( R.id.listView2 );
                adapter = new AdaptadorPaseos( getContext());
                list.invalidateViews();
                list.setAdapter(adapter);
                list.setOnItemClickListener((parent, view, position, id) -> {

                    Paseo walking = listaMascotas.get( position );

                    double latitude = walking.getLatitude();
                    double longitude = walking.getLongitude();
                    String name = walking.getNomMascota();

                    Fragment map = new MapaFragment();
                    Bundle data = new Bundle();
                    data.putDouble( "longitude", longitude );
                    data.putDouble( "latitude", latitude );
                    data.putString( "name", name);

                    getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.mainLayout, map).commit();
                    Toast.makeText(getContext(), "Buscando", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        myRef.addValueEventListener(event);

    }

    class AdaptadorPaseos extends ArrayAdapter {

        Context appCompatActivity;

        AdaptadorPaseos( Context context ){
            super( context, R.layout.mascota, listaMascotas );
            appCompatActivity = context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) appCompatActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
            View item = inflater.inflate( R.layout.paseos, parent, false );
            TextView textView1 = item.findViewById( R.id.petWalk );
            textView1.setText(listaMascotas.get(position).getNomMascota() );
            textView1 = item.findViewById( R.id.walker );
            textView1.setText("Paseador: "+listaMascotas.get(position).getNomPaseador() );
            textView1 = item.findViewById( R.id.walkingDate );
            textView1.setText("Fecha del paseo:"+ listaMascotas.get(position).getDateWalk() );
            long distance = listaMascotas.get(position).getDistance();
            distance = round( distance, 3 );
            textView1 = item.findViewById(R.id.walkingDistance);
            textView1.setText( "Distancia: "+distance+" KM" );
            return item;
        }
    }

    private long round(long number, int places) {
        if (places < 0 ) throw  new IllegalArgumentException();
        long factor = (long) Math.pow( 10, places );
        number *= factor;
        long temp = Math.round( number );
        return temp/factor;
    }

}
