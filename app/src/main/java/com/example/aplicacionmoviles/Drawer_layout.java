package com.example.aplicacionmoviles;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.aplicacionmoviles.fragments.HomeFragment;
import com.example.aplicacionmoviles.fragments.MapaFragment;
import com.example.aplicacionmoviles.fragments.PaseoFragment;
import com.example.aplicacionmoviles.fragments.PetsFragment;
import com.example.aplicacionmoviles.models.Usuario;
import com.google.android.material.navigation.NavigationView;

public class Drawer_layout extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Usuario userLogin;
    private TextView textView;
    private SensorManager mSensorManager;
    private Sensor mAccelometer;
    private ShakeDetector mShakeDetector;
    private Bundle data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_layout);

        userLogin = (Usuario) getIntent().getSerializableExtra("userLogin");
        int times = getIntent().getIntExtra("times", 0);
        if( times == 1 ){
            Toast.makeText(this, "updated correctly", Toast.LENGTH_LONG).show();
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        textView = headerView.findViewById(R.id.userMain);
        textView.setText( userLogin.getNombre() );

        textView = headerView.findViewById(R.id.mailMain);
        textView.setText( userLogin.getCorreo() );

        navigationView.setNavigationItemSelectedListener(this);

        if( userLogin.getLogin() == 0 ){
            getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.mainLayout, new InfoUsuarioFragment()).commit();
        }else{
            getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.mainLayout, new HomeFragment()).commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer_layout, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        FragmentManager manager = getSupportFragmentManager();
        // Handle navigation view item clicks here.
        switch ( item.getItemId() ){
            case R.id.nav_home:
                Fragment homeFragment = new HomeFragment();
                data = new Bundle();
                data.putString("idUser", userLogin.getId());
                data.putString( "nameUser", userLogin.getNombre() );
                homeFragment.setArguments( data );
                manager.beginTransaction().addToBackStack(null).replace(R.id.mainLayout, homeFragment).commit();
                break;
            case R.id.nav_pets:
                Fragment petFragment = new PetsFragment();
                data = new Bundle();
                data.putString("idUser", userLogin.getId());
                data.putString( "nameUser", userLogin.getNombre() );
                petFragment.setArguments( data );
                manager.beginTransaction().addToBackStack(null).replace(R.id.mainLayout, petFragment).commit();
                break;
            case R.id.nav_paseo:
                Fragment walkFragment = new PaseoFragment();
                data = new Bundle();
                data.putString("idUser", userLogin.getId());
                data.putString( "nameUser", userLogin.getNombre() );
                walkFragment.setArguments( data );
                manager.beginTransaction().addToBackStack(null).replace(R.id.mainLayout, walkFragment).commit();
                break;
            case R.id.nav_maps:
                manager.beginTransaction().addToBackStack(null).replace(R.id.mainLayout, new MapaFragment()).commit();
                break;
            case R.id.nav_cuenta:
                manager.beginTransaction().addToBackStack(null).replace(R.id.mainLayout, new InfoUsuarioFragment()).commit();
                break;
            case R.id.nav_cierra:
                MainActivity.session.LogOutUser();
                break;
        }


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
