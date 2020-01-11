package com.example.aplicacionmoviles.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.aplicacionmoviles.R;
import com.example.aplicacionmoviles.utils.LatLngInterpolator;
import com.example.aplicacionmoviles.utils.MarkerAnimation;
import com.example.aplicacionmoviles.utils.StepDetector;
import com.example.aplicacionmoviles.utils.StepListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MapaFragment extends Fragment implements OnMapReadyCallback, SensorEventListener, StepListener {
    private GoogleMap map;
    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    Marker mCurrLocationMarker;
    MarkerOptions mp;
    private boolean firstTimeFlag = true;

    private StepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accel;
    private static final String TEXT_NUM_STEPS = "Number of Steps: ";
    private int numSteps;

    private static final int REQUEST_CODE = 101;

    public MapaFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_home, container, false);
        setHasOptionsMenu(true);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLocation();
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isGooglePlayServicesAvailable()) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
            startCurrentLocationUpdates();
        }
    }

    private void startCurrentLocationUpdates() {

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        fetchLocation();
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());
    }

    private final LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationResult.getLastLocation() == null)
                return;
            currentLocation = locationResult.getLastLocation();
            if (firstTimeFlag && map != null) {
                animateCamera(currentLocation);
                firstTimeFlag = false;
            }
            showMarker(currentLocation);
        }
    };

    private void showMarker(@NonNull Location currentLocation) {
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        Bundle posicion = getArguments();
        if (posicion != null && map != null) {
            mp = new MarkerOptions();
            mp.position(new LatLng(posicion.getDouble("latitude"), posicion.getDouble("longitude")));
            mp.title(posicion.getString("name"));
            map.addMarker(mp);
        }
        if (mCurrLocationMarker == null) {
            mCurrLocationMarker = map.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).position(latLng));
        } else {
            MarkerAnimation.animateMarkerToGB(mCurrLocationMarker, latLng, new LatLngInterpolator.Spherical());
        }
    }

    private void animateCamera(@NonNull Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(getContext());
        if (ConnectionResult.SUCCESS == status)
            return true;
        else {
            if (googleApiAvailability.isUserResolvableError(status))
                Toast.makeText(getContext(), "Google Play Services isn't installed", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (fusedLocationProviderClient != null) {
            fusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.new_options_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btnWalk_acept:
                numSteps = 0;
                sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_FASTEST);
                acept_walk();
                return true;
            case R.id.item_finish:
                sensorManager.unregisterListener(this);
                complete_walk();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void complete_walk() {
        Bundle data = getArguments();
        if( data != null ){
            float distance = numSteps * 0.0008f;
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference();
            try {
                myRef.child("Paseos").child( data.getString("idWalk") ).child( "distance" ).setValue( distance );
                myRef.child("Paseos").child( data.getString("idWalk") ).child( "status" ).setValue( 2 );
            }catch( Exception e ){
                e.printStackTrace();
            }
        }

    }

    private void acept_walk() {
        Bundle data = getArguments();
        if (data != null) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference();
            LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Location location = null;
            if (lm != null) {
                location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
            try {
                myRef.child("Paseos").child( data.getString("idWalk") ).child( "nomPaseador" ).setValue( data.getString("nameWalker") );
                myRef.child("Paseos").child( data.getString("idWalk") ).child( "status" ).setValue( 1 );
                if (location != null) {
                    myRef.child("Paseos").child( data.getString("idWalk") ).child("longitude").setValue( location.getLongitude() );
                    myRef.child("Paseos").child( data.getString("idWalk") ).child("latitude").setValue( location.getLatitude() );
                }
            }catch( Exception e ){
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //Toast.makeText(getContext(), "SI se activa el sensor", Toast.LENGTH_LONG).show();
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void step(long timeNs) {
        numSteps++;
        //Toast.makeText( getContext(), "Pasos dados: "+numSteps, Toast.LENGTH_LONG ).show();
    }
}
