package com.example.aplicacionmoviles;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.aplicacionmoviles.models.Usuario;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InfoUsuarioFragment extends Fragment implements View.OnClickListener {

    private Usuario user;
    private EditText editText;
    private Button btnUpdate;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.info_usuario, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    
        user = (Usuario) getActivity().getIntent().getSerializableExtra("userLogin");

        editText = getActivity().findViewById(R.id.editNombre);
        editText.setText( user.getNombre() );

        editText = getActivity().findViewById(R.id.editUsuario);
        editText.setText( user.getUsuario() );

        editText = getActivity().findViewById(R.id.editCorreo);
        editText.setText( user.getCorreo() );

        editText = getActivity().findViewById(R.id.editDireccion);
        editText.setText( user.getDireccion() );

        editText = getActivity().findViewById(R.id.editTelefono);
        editText.setText( user.getTelefono() );

        btnUpdate = getActivity().findViewById( R.id.btnUpdate );
        btnUpdate.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        try {
            editText = getActivity().findViewById(R.id.editNombre);
            myRef.child("Usuarios").child( user.getId() ).child( "nombre" ).setValue( editText.getText().toString() );
            editText = getActivity().findViewById(R.id.editUsuario);
            myRef.child("Usuarios").child( user.getId() ).child( "usuario" ).setValue( editText.getText().toString() );
            editText = getActivity().findViewById(R.id.editCorreo);
            myRef.child("Usuarios").child( user.getId() ).child( "correo" ).setValue( editText.getText().toString() );
            editText = getActivity().findViewById(R.id.editDireccion);
            myRef.child("Usuarios").child( user.getId() ).child( "direccion" ).setValue( editText.getText().toString() );
            editText = getActivity().findViewById(R.id.editTelefono);
            myRef.child("Usuarios").child( user.getId() ).child( "telefono" ).setValue( editText.getText().toString() );

            myRef.child("Usuarios").child( user.getId() ).child( "login" ).setValue( 1 );
        }catch( Exception e ){
            e.printStackTrace();
        }

    }
}
