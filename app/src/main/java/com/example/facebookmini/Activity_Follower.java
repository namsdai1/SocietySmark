package com.example.facebookmini;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


import com.example.facebookmini.Adapter.Adapter_Search;
import com.example.facebookmini.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Activity_Follower extends AppCompatActivity {
    String id,title;
    List<String> idlist;
    RecyclerView recyclerView;
    Adapter_Search adapter_search;
    ArrayList<User> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences1=getSharedPreferences("PREPS", Context.MODE_PRIVATE);
        Boolean booleanvalue=preferences1.getBoolean("nightmode",true);
        if(booleanvalue){
            setTheme(R.style.darkthmeo);
        }else {
            setTheme(R.style.AppTheme);

        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__follower);
        recyclerView=findViewById(R.id.recycler_view);
        Intent i=getIntent();
        id=i.getStringExtra("id");
        title=i.getStringExtra("title");
        Toolbar toolbar=findViewById(R.id.toobar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list=new ArrayList<>();
        adapter_search=new Adapter_Search(Activity_Follower.this,list,false);
        recyclerView.setAdapter(adapter_search);
        idlist=new ArrayList<>();
        switch (title){
            case "likes":
                getLike();
                break;
            case "following":
                getFollwing();
                break;
            case "followers":
                getFollowers();
                break;
            case "views":
                getVies();
                break;
        }
    }

    private void getFollwing() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Follow")
                .child(id).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idlist.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    idlist.add(snapshot1.getKey());
                }
                showUser();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowers() {
        Log.d("AAAA", "onDataChange: "+id+",,,"+title);
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Follow")
                .child(id).child("followers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idlist.clear();
                Log.d("AAAAA", "onDataChange: "+snapshot.getChildrenCount());
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    idlist.add(snapshot1.getKey());
                }
                showUser();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getLike() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Likes")
                .child(id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idlist.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    idlist.add(snapshot1.getKey());
                }
                showUser();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getVies(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Story")
                .child(id).child(getIntent().getStringExtra("storyid")).child("views");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idlist.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    idlist.add(snapshot1.getKey());
                }
                showUser();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void showUser(){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("User");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    User user=snapshot1.getValue(User.class);
                    for(String id:idlist){
                        if(user.getIDUser().equals(id)){
                            list.add(user);
                        }
                    }
                }
                Log.d("AAAA", "onDataChange: "+idlist.size());
                Log.d("AAAA", "onDataChange: "+list.size());
                adapter_search.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}