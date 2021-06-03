package com.example.facebookmini.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.facebookmini.Adapter.Adapter_Post;
import com.example.facebookmini.Adapter.Adapter_Story;
import com.example.facebookmini.R;
import com.example.facebookmini.model.Post;
import com.example.facebookmini.model.Story;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
RecyclerView rcv;
Adapter_Post adapter_post;
List<Post> list;
List<String> followlist;
FirebaseUser firebaseUser;
//
RecyclerView recyclerView_story;
Adapter_Story adapter_story;
List<Story> list_story;
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
        View view=inflater.inflate(R.layout.fragment_home, container, false);
        rcv=view.findViewById(R.id.recycler_view);
        recyclerView_story=view.findViewById(R.id.recycler_view_story);

        rcv.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        rcv.setLayoutManager(linearLayoutManager);
        list=new ArrayList<>();
        adapter_post=new Adapter_Post(getContext(),list);
        rcv.setAdapter(adapter_post);
        //story
        recyclerView_story.setHasFixedSize(true);
        Context context;
        list_story=new ArrayList<>();
        LinearLayoutManager linearLayoutManager1=new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        recyclerView_story.setLayoutManager(linearLayoutManager1);
        adapter_story=new Adapter_Story(getContext(),list_story);
        recyclerView_story.setAdapter(adapter_story);


        checkFollowing();

        return view;
    }
    private void checkFollowing(){
        followlist=new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followlist.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    followlist.add(snapshot1.getKey());
                }
                readPost();
                readStory();
                readPostCurren();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public void readPost(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshots:snapshot.getChildren()){
                    Post post=snapshots.getValue(Post.class);
                    Log.d("AAAA", "onDataChange: "+post.getPublisher());
                    for(String id:followlist){
                        if(post.getPublisher().equals(id)){
                            list.add(post);
                        }
                    }
                }
                adapter_post.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void readPostCurren(){
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshots:snapshot.getChildren()){
                    Post post=snapshots.getValue(Post.class);
                    Log.d("AAAA", "onDataChange: "+post.getPublisher());
                        if(post.getPublisher().equals(firebaseUser.getUid())){
                            list.add(post);
                        }
                }
                adapter_post.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void readStory(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long timecurren=System.currentTimeMillis();
                list_story.clear();
                list_story.add(new Story("",0,0,"",FirebaseAuth.getInstance().getCurrentUser().getUid()));
                for(String id:followlist){
                    int countStory=0;
                    Story story=null;
                    for(DataSnapshot snapshot1:snapshot.child(id).getChildren()){
                        story=snapshot1.getValue(Story.class);
                        if(timecurren>story.getTimeStart() && timecurren<story.getTimeEnd()){
                            countStory++;

                        }
                    }
                    if(countStory>0){
                        list_story.add(story);
                    }
                }
                adapter_story.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}