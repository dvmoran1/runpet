package com.example.aplicacionmoviles;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.aplicacionmoviles.models.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignUp extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private static final String TAG = "EmailPassword";
    private EditText usuario, contra, nombre, correo;
    private CheckBox checkBox;
    private FirebaseDatabase database;
    private DatabaseReference root;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        root = database.getReference();

        usuario = findViewById(R.id.userRegister);
        contra = findViewById(R.id.passRegister);
        nombre = findViewById(R.id.nomUsu);
        checkBox = findViewById(R.id.checkTipo);
        correo = findViewById(R.id.mailUsu);
        findViewById(R.id.btnRegistra).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            default:
                registraUsuario( correo.getText().toString(), contra.getText().toString());
        }
    }

    private void registraUsuario(String user, String pass) {
        mAuth.createUserWithEmailAndPassword(user,pass)
                .addOnCompleteListener(this, task -> {
                    if(task.isSuccessful()){
                        saveToken();
                    }else{
                        Toast.makeText(getApplicationContext(), "createUserWithEmail:failure", Toast.LENGTH_SHORT);
                    }
                });
    }

    private void saveToken() {

        DatabaseReference users = root.child("Usuarios");
        String id = users.push().getKey();

        Usuario newUser = new Usuario();
        newUser.setId(id);

        if( checkBox.isChecked() ){
            newUser.setTipo(1);
        }else{
            newUser.setTipo(0);
        }
        newUser.setNombre(nombre.getText().toString());
        newUser.setHabilitado(1);
        newUser.setCorreo( correo.getText().toString() );
        newUser.setUsuario( usuario.getText().toString() );
        newUser.setLogin(0);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(id, newUser);

        users.updateChildren(hashMap)
                .addOnSuccessListener( v -> {
                    finish();
                });
    }

}
