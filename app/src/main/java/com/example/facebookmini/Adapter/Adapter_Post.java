package com.example.facebookmini.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.facebookmini.Activity_Comments;
import com.example.facebookmini.Activity_Follower;
import com.example.facebookmini.R;
import com.example.facebookmini.fragment.PostDetailFragment;
import com.example.facebookmini.fragment.ProfileFragment;
import com.example.facebookmini.model.Notification;
import com.example.facebookmini.model.Post;
import com.example.facebookmini.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class Adapter_Post extends RecyclerView.Adapter<Adapter_Post.Post_Holder> {

    Context mcontet;
    List<Post> list;
    FirebaseUser firebaseUser;
    public Adapter_Post(Context mcontet, List<Post> list) {
        this.mcontet = mcontet;
        this.list = list;
    }

    @NonNull
    @Override
    public Post_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view =LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        return new Adapter_Post.Post_Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Post_Holder holder, int position) {
       firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
       final Post post=list.get(position);
        Picasso.get().load(post.getPostimage()).into(holder.img_post);
        Log.d("AAAA", "onBindViewHolder: "+post.getPostimage());
        if(post.getDescription().equals("")){
            holder.description.setVisibility(View.GONE);
        }else {
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(post.getDescription());
        }
        publisherInfo(holder.img_prolife,holder.username,holder.publisher,post.getPublisher());
        isLike(post.getPostid(),holder.like);
        nrLikes(holder.likes,post.getPostid());
        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.like.getTag().equals("like")){
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid())
                            .child(firebaseUser.getUid()).setValue(true);
                    addNotification(post.getPublisher(),post.getPostid());
                }else {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid())
                            .child(firebaseUser.getUid()).removeValue();
                }
            }
        });
        holder.img_prolife.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                        SharedPreferences.Editor editor=mcontet.getSharedPreferences("PREPS",Context.MODE_PRIVATE).edit();
                        editor.putString("profileid",post.getPublisher());
                        editor.apply();
                        ((FragmentActivity)mcontet).getSupportFragmentManager().beginTransaction().replace(R.id.fr_container,new ProfileFragment()).commit();
            }
        });
        holder.publisher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor=mcontet.getSharedPreferences("PREPS",Context.MODE_PRIVATE).edit();
                editor.putString("profileid",post.getPublisher());
                editor.apply();
                ((FragmentActivity)mcontet).getSupportFragmentManager().beginTransaction().replace(R.id.fr_container,new ProfileFragment()).commit();
            }
        });
        holder.img_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor=mcontet.getSharedPreferences("PREPS",Context.MODE_PRIVATE).edit();
                editor.putString("postid",post.getPostid());
                editor.apply();
                ((FragmentActivity)mcontet).getSupportFragmentManager().beginTransaction().replace(R.id.fr_container,new PostDetailFragment()).commit();
            }
        });

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(mcontet, Activity_Comments.class);
                i.putExtra("postid",post.getPostid());
                i.putExtra("publisherid",post.getPublisher());
                mcontet.startActivity(i);
            }
        });
        idSaved(post.getPostid(),holder.save);
        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.save.getTag().equals("save")){
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid()).child(post.getPostid()).setValue(true);
                }else {
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid()).child(post.getPostid()).removeValue();
                }
            }
        });

        getComments(post.getPostid(),holder.comments);
        if(post.getPublisher().equals(firebaseUser.getUid())){
            holder.save.setVisibility(View.GONE);
        }
        holder.likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(mcontet, Activity_Follower.class);
                i.putExtra("id",post.getPostid());
                i.putExtra("title","likes");
                mcontet.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Post_Holder extends RecyclerView.ViewHolder{
         ImageView img_prolife,img_post,like,comment,save;
         TextView username,likes,publisher,description,comments;
        public Post_Holder(@NonNull View itemView) {
            super(itemView);
            img_prolife=itemView.findViewById(R.id.img_profile);
            img_post=itemView.findViewById(R.id.post_img);
            comments=itemView.findViewById(R.id.comments);
            like=itemView.findViewById(R.id.like);
            likes=itemView.findViewById(R.id.likes);
            comment=itemView.findViewById(R.id.comment);
            save=itemView.findViewById(R.id.save);
            username=itemView.findViewById(R.id.username);
            publisher=itemView.findViewById(R.id.publisher);
            description=itemView.findViewById(R.id.description);
        }
    }
    private void getComments(String postid, final TextView comments){
           DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Comments").child(postid);
           reference.addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot snapshot) {
                   comments.setText("View All "+snapshot.getChildrenCount()+" Comments");
               }

               @Override
               public void onCancelled(@NonNull DatabaseError error) {

               }
           });
    }
    private void isLike(String postid, final ImageView imageView){
        final FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference()
                .child("Likes")
                .child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(firebaseUser.getUid()).exists()){
                    imageView.setImageResource(R.drawable.heart2);
                    imageView.setTag("liked");
                }else {
                    imageView.setImageResource(R.drawable.heart1);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void nrLikes(final TextView likes, String posid){
        final FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference()
                .child("Likes")
                .child(posid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                likes.setText(snapshot.getChildrenCount()+" likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void publisherInfo(final ImageView img, final TextView username, final TextView publisher, String userid){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("User").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                Picasso.get().load(user.getImages()).into(img);
                username.setText(user.getUserName());
                publisher.setText(user.getUserName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void idSaved(final String postid, final ImageView imageView){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Saves").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(postid).exists()){
                    imageView.setImageResource(R.drawable.save_back);
                    imageView.setTag("saved");
                }else {
                    imageView.setImageResource(R.drawable.savee_back);
                    imageView.setTag("save");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void addNotification(String userid,String postid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);
        reference.push().setValue(new Notification(firebaseUser.getUid(),"liked your post",postid,true));
    }

}
