package com.example.facebookmini.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.facebookmini.Activity_All;
import com.example.facebookmini.R;
import com.example.facebookmini.model.Comments;
import com.example.facebookmini.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class Adapter_Comments extends RecyclerView.Adapter<Adapter_Comments.CommentsHolder> {
    Context mcontext;
    List<Comments> list;
    FirebaseUser firebaseUser;
    public Adapter_Comments(Context mcontext, List<Comments> list) {
        this.mcontext = mcontext;
        this.list = list;
    }

    @NonNull
    @Override
    public CommentsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.comments_item, parent, false);
        return new Adapter_Comments.CommentsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsHolder holder, int position) {
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        final Comments comments=list.get(position);
        holder.comments.setText(comments.getComments());
        getUserInfo(holder.img_profile,holder.username,comments.getPublisher());
        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(mcontext, Activity_All.class);
                i.putExtra("publisherid",comments.getPublisher());
                mcontext.startActivity(i);
            }
        });
        holder.img_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(mcontext, Activity_All.class);
                i.putExtra("publisherid",comments.getPublisher());
                mcontext.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class CommentsHolder extends RecyclerView.ViewHolder{
        ImageView img_profile;
        TextView username,comments;
        public CommentsHolder(@NonNull View itemView) {
            super(itemView);
            img_profile=itemView.findViewById(R.id.img_profile);
            username=itemView.findViewById(R.id.username);
            comments=itemView.findViewById(R.id.comment);
        }
    }
    private void getUserInfo(final ImageView imageView, final TextView username, String publisherid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User")
                .child(publisherid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                Picasso.get().load(user.getImages()).into(imageView);
                username.setText(user.getUserName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
