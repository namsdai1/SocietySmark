package com.example.facebookmini;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.facebookmini.Adapter.Adapter_Comments;
import com.example.facebookmini.model.Comments;
import com.example.facebookmini.model.Notification;
import com.example.facebookmini.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class Activity_Comments extends AppCompatActivity {
    RecyclerView recyclerView;
    Adapter_Comments adapter_comments;
    List<Comments> list;
EditText addcommments;
ImageView img_profile;
TextView post;
String postid;
String publisherid;
FirebaseUser firebaseUser;
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
        setContentView(R.layout.activity__comments);
        Toolbar toolbar=findViewById(R.id.tb_comment);
        recyclerView=findViewById(R.id.recycler_comments);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        list=new ArrayList<>();
        adapter_comments=new Adapter_Comments(this,list);
        recyclerView.setAdapter(adapter_comments);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        addcommments=findViewById(R.id.addcomments);
        img_profile=findViewById(R.id.img_comments);
        post=findViewById(R.id.tv_post1);
        Intent i=getIntent();
        postid=i.getStringExtra("postid");
        publisherid=i.getStringExtra("publisherid");
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(addcommments.getText().toString().equals("")) {
                    Toast.makeText(Activity_Comments.this, "You can't send empty comments", Toast.LENGTH_SHORT).show();
                }else {
                    addComments();
                }
            }
        });
        getimg();
        readComments();
    }

    private void addComments() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Comments").child(postid);
        reference.push().setValue(new Comments(addcommments.getText().toString(),firebaseUser.getUid()));
        addNotifications();
        addcommments.setText("");
    }
    private void getimg(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("User").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                Picasso.get().load(user.getImages()).into(img_profile);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void readComments(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    Comments comments=snapshot1.getValue(Comments.class);
                    list.add(comments);
                }
                adapter_comments.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void addNotifications(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(publisherid);
        reference.push().setValue(new Notification(firebaseUser.getUid(),"commented:"+addcommments.getText().toString(),postid,true));
    }
}