package com.example.facebookmini.Dao;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.facebookmini.model.User;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Dao_User {
    public static boolean insert_Firebase(final Context context, User item){
        Log.d("AAAA", "insert_Firebase: "+item.getIDUser()+item.getEmail());
        DatabaseReference myRef= FirebaseDatabase.getInstance().getReference();
        myRef.child("User").child(item.getIDUser()).setValue(
                new User( item.getIDUser(),item.getUserName(), item.getPassword(), item.getFullName(), item.getEmail(),item.getImages()),
                new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {

                    }
                });
        return true;
    }
}
