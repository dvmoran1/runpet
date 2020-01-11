package com.example.aplicacionmoviles.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;

import com.example.aplicacionmoviles.Drawer_layout;
import com.example.aplicacionmoviles.MainActivity;
import com.example.aplicacionmoviles.models.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SessionManagement {

    SharedPreferences pref;
    Editor editor;
    Context _context;

    public SessionManagement(Context context) {
        this._context = context;
        pref = PreferenceManager.getDefaultSharedPreferences(this._context);
        editor = pref.edit();
    }

    public void createLoginSession(String email){
        editor.putString("email",email);
        editor.commit();
    }

    public void checkLogin(){
        if(pref.contains("email")){
            Intent i = new Intent(_context, Drawer_layout.class);
            DatabaseReference root = FirebaseDatabase.getInstance().getReference();
            DatabaseReference users = root.child("Usuarios");
            users.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for ( DataSnapshot u : dataSnapshot.getChildren() ) {
                        Usuario login = u.getValue(Usuario.class);
                        if( login.correo.equals( pref.getString("email", "") ) ){
                            i.putExtra("userLogin", login);
                        }
                    }
                    i.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                    _context.startActivity( i );
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public void LogOutUser(){
        editor.clear();
        editor.commit();
        Intent i = new Intent( _context, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        _context.startActivity(i);
    }

}
