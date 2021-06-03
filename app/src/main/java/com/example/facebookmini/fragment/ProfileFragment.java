package com.example.facebookmini.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.facebookmini.Activity_EditProfile;
import com.example.facebookmini.Activity_Follower;
import com.example.facebookmini.Activity_Login;
import com.example.facebookmini.Adapter.Adapter_Foto;
import com.example.facebookmini.MainActivity;
import com.example.facebookmini.R;
import com.example.facebookmini.model.Notification;
import com.example.facebookmini.model.Post;
import com.example.facebookmini.model.User;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class ProfileFragment extends Fragment {
ImageView img_profile,options;
TextView posts,followers,follwing,fullname,bio,username;
Button edt_profule;
FirebaseUser firebaseUser;
String profileid;
ImageButton my_fotos,saved_foto;
RecyclerView recyclerView;
List<Post> list;
Adapter_Foto adapter_foto;
List<String> mySave;
    RecyclerView recyclerView_save;
    List<Post> list_save;
    Adapter_Foto adapter_save;
    DrawerLayout drawerLayout;
    NavigationView nav_view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SharedPreferences preferences1=getContext().getSharedPreferences("PREPS", Context.MODE_PRIVATE);
        Boolean booleanvalue=preferences1.getBoolean("nightmode",true);
        if(booleanvalue){
                ((Activity)getContext()).setTheme(R.style.darkthmeo);
            }else {
                ((Activity)getContext()).setTheme(R.style.AppTheme);

            }
        View view=inflater.inflate(R.layout.fragment_profile, container, false);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences preferences=getContext().getSharedPreferences("PREPS", Context.MODE_PRIVATE);
        profileid=preferences.getString("profileid","none");
        Log.d("AAAA", "onCreateView: "+profileid);
        img_profile=view.findViewById(R.id.img_profile);
        options=view.findViewById(R.id.options);
        posts=view.findViewById(R.id.tv_post);
        followers=view.findViewById(R.id.followers);
        follwing=view.findViewById(R.id.following);
        fullname=view.findViewById(R.id.fullname);
        bio=view.findViewById(R.id.bfo);
        username=view.findViewById(R.id.username);
        my_fotos=view.findViewById(R.id.my_fotos);
        saved_foto=view.findViewById(R.id.save_fotos);
        edt_profule=view.findViewById(R.id.edit_protife);
        recyclerView=view.findViewById(R.id.recycler_view);
        drawerLayout=view.findViewById(R.id.drawerlo);
        nav_view=view.findViewById(R.id.nav_view);
        list=new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        adapter_foto=new Adapter_Foto(getContext(),list);
        recyclerView.setAdapter(adapter_foto);
        //save
        recyclerView_save=view.findViewById(R.id.recycler_view_save);
        list_save=new ArrayList<>();
        recyclerView_save.setHasFixedSize(true);
        recyclerView_save.setLayoutManager(new GridLayoutManager(getContext(),3));
        adapter_save=new Adapter_Foto(getContext(),list_save);
        recyclerView_save.setAdapter(adapter_save);
        my_fotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView_save.setVisibility(View.GONE);
            }
        });
        saved_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.setVisibility(View.GONE);
                recyclerView_save.setVisibility(View.VISIBLE);
            }
        });
        userInfo();
        getNrPost();
        getfollowers();
        getfollowing();
        myFoto();
        Mysave();
        if(profileid.equals(firebaseUser.getUid())){
            edt_profule.setText("Edit Profile");
        }else {
            checkfollow();
            saved_foto.setVisibility(View.GONE);
        }
        edt_profule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String btn=edt_profule.getText().toString();
                if(btn.equals("Edit Profile")){
                      startActivity(new Intent(getContext(), Activity_EditProfile.class));
                }else if(btn.equalsIgnoreCase("follow")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following")
                            .child(profileid).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid).child("followers")
                            .child(firebaseUser.getUid()).setValue(true);
                    addNotifications();

                }else if(btn.equalsIgnoreCase("following")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following")
                            .child(profileid).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid).child("followers")
                            .child(firebaseUser.getUid()).removeValue();
                }
            }
        });
        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getContext(), Activity_Follower.class);
                i.putExtra("id",profileid);
                i.putExtra("title","followers");
                startActivity(i);
            }
        });
        follwing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getContext(), Activity_Follower.class);
                i.putExtra("id",profileid);
                i.putExtra("title","following");
                startActivity(i);
            }
        });

        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 drawerLayout.openDrawer(Gravity.RIGHT);

            }
        });
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.nav_caidat:
                        Intent in=new Intent(getContext(), MainActivity.class);
                        startActivity(in);
                     break;
                    case R.id.nav_thoat:
                        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                        firebaseAuth.signOut();
                        Intent i=new Intent(getContext(), Activity_Login.class);
                        startActivity(i);
                     break;
                }
                return false;
            }
        });

        return view;
    }
    private void userInfo(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("User").child(profileid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(getContext()==null){
                    return;
                }
                User user=snapshot.getValue(User.class);
                Picasso.get().load(user.getImages()).into(img_profile);
                fullname.setText(user.getUserName());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void checkfollow(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Follow").child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(profileid).exists()){
                    edt_profule.setText("following");
                }else {
                    edt_profule.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getfollowers(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Follow").child(profileid).child("followers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followers.setText(""+snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getfollowing(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Follow").child(profileid).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                follwing.setText(""+snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getNrPost(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i=0;
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    Post post=snapshot1.getValue(Post.class);
                    if(post.getPublisher().equals(profileid)){
                        i++;
                    }
                }
                posts.setText(""+i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void myFoto(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    Post post=snapshot1.getValue(Post.class);
                    if(post.getPublisher().equals(profileid)){
                        list.add(post);
                    }
                }
                Collections.reverse(list);
                adapter_foto.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void Mysave(){
        mySave=new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Saves")
                .child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    mySave.add(snapshot1.getKey());
                }
                readSave();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readSave() {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list_save.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    Post post=snapshot1.getValue(Post.class);
                    for(String id:mySave){
                        if(id.equals(post.getPostid())){
                            list_save.add(post);
                        }
                    }
                }

                adapter_save.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void addNotifications(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(profileid);
        reference.push().setValue(new Notification(firebaseUser.getUid(),"Start following you","",false));
    }
}