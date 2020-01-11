package com.example.aplicacionmoviles;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.aplicacionmoviles.models.Usuario;
import com.example.aplicacionmoviles.utils.SessionManagement;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private static final String TAG = "EmailPassword";
    private EditText user, pass;
    private FirebaseDatabase database;
    private DatabaseReference root;
    public static SessionManagement session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = new SessionManagement(getApplicationContext());
        session.checkLogin();

        database = FirebaseDatabase.getInstance();
        root = database.getReference().child("Usuarios");

        mAuth = FirebaseAuth.getInstance();
        user = findViewById(R.id.userText);
        pass = findViewById(R.id.passText);

        findViewById(R.id.btnSingUp).setOnClickListener(this);
        findViewById(R.id.btnSession).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSession:
                login(user.getText().toString(), pass.getText().toString());
                break;
            case R.id.btnSingUp:
                Intent actividad = new Intent( this, SignUp.class );
                startActivity(actividad);
                break;
            default:
        }
    }

    private void login(String user, String pass) {
        if( user.equals("") || pass.equals("") ){
            Toast.makeText(getApplicationContext(), "Introducir la información necesaria", Toast.LENGTH_SHORT).show();
        }else {
            mAuth.signInWithEmailAndPassword(user, pass)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            ValueEventListener event = new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot usu: dataSnapshot.getChildren()) {
                                        Usuario usuario = usu.getValue( Usuario.class );
                                        if( usuario.getHabilitado() != 0 && usuario.getCorreo().equals(user)){
                                            Intent intent = new Intent(MainActivity.this, Drawer_layout.class);
                                            intent.putExtra("userLogin", usuario);
                                            intent.setFlags( Intent.FLAG_ACTIVITY_NO_HISTORY );
                                            session.createLoginSession(user);
                                            startActivity(intent);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            };
                            root.addValueEventListener(event);
                        } else {
                            Toast.makeText(getApplicationContext(), "Usuario o contraseña equivocada", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
