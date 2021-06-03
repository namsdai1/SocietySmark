package com.example.facebookmini.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.usb.UsbRequest;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.facebookmini.Activity_Login;
import com.example.facebookmini.Adapter.Adapter_Search;
import com.example.facebookmini.R;
import com.example.facebookmini.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class SeachFragment extends Fragment {
    RecyclerView recyclerView;
    Adapter_Search adapter_search;
    ArrayList<User> list;
    EditText edt_seach;
    @Override
    public View onCreateView(LayoutInflater inflater,@NonNull ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        SharedPreferences preferences1=getContext().getSharedPreferences("PREPS", Context.MODE_PRIVATE);
        Boolean booleanvalue=preferences1.getBoolean("nightmode",true);
        if(booleanvalue){
            ((Activity)getContext()).setTheme(R.style.darkthmeo);
        }else {
            ((Activity)getContext()).setTheme(R.style.AppTheme);

        }
        View view=inflater.inflate(R.layout.fragment_seach, container, false);
        recyclerView=view.findViewById(R.id.rcv_search);
        edt_seach=view.findViewById(R.id.et_search);
        recyclerView.setHasFixedSize(true);


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        list=new ArrayList<>();
        adapter_search=new Adapter_Search(getContext(),list,true);
        recyclerView.setAdapter(adapter_search);
        readUser();
        edt_seach.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                 searchUser(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        return view;
    }
    private void searchUser(String s){
        Query query= FirebaseDatabase.getInstance().getReference("User").orderByChild("userName")
                .startAt(s)
                .endAt(s+"\uf88f");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot mdata:snapshot.getChildren()){
                    User user=mdata.getValue(User.class);
                    list.add(user);
                }
                adapter_search.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void readUser(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("User");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(edt_seach.getText().toString().equals("")){
                    Toast.makeText(getContext(), "tru", Toast.LENGTH_SHORT).show();
                    list.clear();
                    for(DataSnapshot mdata:snapshot.getChildren()){
                        User user=mdata.getValue(User.class);
                        list.add((user));
                    }
                    Log.d("AAAA", "onDataChange: "+list.get(0).getIDUser());
                    Log.d("AAAA", "onDataChange: "+list.get(1).getIDUser());
                    adapter_search.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}