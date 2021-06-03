package com.example.facebookmini.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.facebookmini.Adapter.Adapter_Post;
import com.example.facebookmini.R;
import com.example.facebookmini.model.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PostDetailFragment extends Fragment {
RecyclerView recyclerView;
String postid;
Adapter_Post adapter_post;
List<Post> list;
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
        View view=inflater.inflate(R.layout.fragment_post_detail, container, false);
        recyclerView=view.findViewById(R.id.recycler_view);
        Toolbar toolbar=view.findViewById(R.id.toobar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        SharedPreferences preferences=getContext().getSharedPreferences("PREPS", Context.MODE_PRIVATE);
        postid=preferences.getString("postid","none");
        Log.d("AAAA", "onCreateView: "+postid);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        list=new ArrayList<>();
        adapter_post=new Adapter_Post(getContext(),list);
        recyclerView.setAdapter(adapter_post);
        readPost();
        return view;
    }

    private void readPost() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Posts").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                Post post=snapshot.getValue(Post.class);
                list.add(post);
                adapter_post.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}