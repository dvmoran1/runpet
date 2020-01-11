package com.example.aplicacionmoviles.fragments;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.aplicacionmoviles.R;
import com.example.aplicacionmoviles.ShakeDetector;
import com.example.aplicacionmoviles.ShakeService;
import com.example.aplicacionmoviles.models.Paseo;
import com.example.aplicacionmoviles.models.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    public HomeFragment() {}

    private ArrayList<Paseo> listaPaseos;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private Usuario userLogin;
    private AdaptadorPaseo adapter;
    private ValueEventListener event;
    private SensorManager mSensorManager;
    private Sensor mAccelometer;
    private ShakeDetector mShakeDetector;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.home_page, container, false);
        Intent intent = new Intent( getContext(), ShakeService.class);
        getActivity().startService(intent);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if( userLogin.getTipo() == 1 ){
            mSensorManager.registerListener( mShakeDetector, mAccelometer, SensorManager.SENSOR_DELAY_UI );
        }else{
            getInfoPaseo();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        listaPaseos = new ArrayList<>();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("Paseos");
        userLogin = (Usuario) getActivity().getIntent().getSerializableExtra("userLogin");

        mSensorManager = (SensorManager) getActivity().getSystemService( Context.SENSOR_SERVICE );
        mAccelometer = mSensorManager
                .getDefaultSensor( Sensor.TYPE_ACCELEROMETER );
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(count -> {

            getInfoTable();

        });

    }

    private void getInfoPaseo() {
        if( event != null ){
            myRef.removeEventListener(event);
        }
        event = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaPaseos = new ArrayList<>();
                for (DataSnapshot pet: dataSnapshot.getChildren()) {
                    Paseo walk = pet.getValue( Paseo.class );
                    if( walk.getStatus() == 0 || walk.getStatus() == 1){
                        listaPaseos.add( walk );
                    }
                }

                if( getView() != null ){

                    ListView list = getView().findViewById( R.id.listView3 );
                    adapter = new AdaptadorPaseo( getContext() );
                    list.invalidateViews();
                    list.setAdapter(adapter);
                    list.setOnItemClickListener((parent, view, position, id) -> {

                        Paseo walking = listaPaseos.get( position );

                        double latitude = walking.getLatitude();
                        double longitude = walking.getLongitude();
                        String name = walking.getNomMascota();
                        String idWalk = walking.getId();

                        Fragment map = new MapaFragment();
                        Bundle data = new Bundle();
                        data.putDouble( "longitude", longitude );
                        data.putDouble( "latitude", latitude );
                        data.putString( "name", name);
                        data.putString( "idWalk", idWalk);
                        data.putString( "nameWalker", userLogin.getNombre() );
                        map.setArguments( data );

                        getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.mainLayout, map).commit();
                        Toast.makeText(getContext(), "Buscando", Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        myRef.addValueEventListener(event);
    }

    private void getInfoTable(){
        if( event != null ){
            myRef.removeEventListener(event);
        }
        event = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaPaseos = new ArrayList<>();
                for (DataSnapshot pet: dataSnapshot.getChildren()) {
                    Paseo walk = pet.getValue( Paseo.class );
                    if( walk.getStatus() == 0 || walk.getStatus() == 1){
                        listaPaseos.add( walk );
                    }
                }

                if( getView() != null ){

                    ListView list = getView().findViewById( R.id.listView3 );
                    adapter = new AdaptadorPaseo( getContext());
                    list.invalidateViews();
                    list.setAdapter(adapter);
                    list.setOnItemClickListener((parent, view, position, id) -> {

                        Paseo walking = listaPaseos.get( position );

                        double latitude = walking.getLatitude();
                        double longitude = walking.getLongitude();
                        String name = walking.getNomPaseador();
                        String idWalk = walking.getId();

                        Fragment map = new MapaFragment();
                        Bundle data = new Bundle();
                        data.putDouble( "longitude", longitude );
                        data.putDouble( "latitude", latitude );
                        data.putString( "name", name);
                        data.putString( "idWalk", idWalk);
                        data.putString( "nameWalker", userLogin.getNombre() );
                        map.setArguments( data );

                        getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.mainLayout, map).commit();
                        Toast.makeText(getContext(), "Buscando", Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        myRef.addValueEventListener(event);
    }

    class AdaptadorPaseo extends ArrayAdapter {

        Context appCompatActivity;

        AdaptadorPaseo ( Context context ){
            super( context, R.layout.requestpaseos, listaPaseos);
            appCompatActivity = context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) appCompatActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
            View item = inflater.inflate( R.layout.requestpaseos, parent, false );
            if( userLogin.getTipo() == 1 ){
                TextView textView1 = item.findViewById( R.id.duenio );
                textView1.setText(listaPaseos.get(position).getNomDuenio() );
                textView1 = item.findViewById( R.id.petNameW );
                textView1.setText(listaPaseos.get(position).getNomMascota() );
                return item;
            }else{
                TextView textView1 = item.findViewById( R.id.petNameW );
                if( listaPaseos.get( position ).getStatus() == 0 ){
                    textView1.setText("Buscando paseador");
                }
                if( listaPaseos.get( position ).getStatus() == 1 ){
                    textView1.setText(listaPaseos.get(position).getNomPaseador()+" acepto tu solicitud" );
                }
                textView1 = item.findViewById( R.id.duenio);
                textView1.setText("Mascota: "+listaPaseos.get(position).getNomMascota() );
                return item;
            }
        }
    }


}