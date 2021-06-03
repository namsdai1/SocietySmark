package com.example.facebookmini.Adapter;

import android.content.Context;
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

import com.example.facebookmini.R;
import com.example.facebookmini.fragment.PostDetailFragment;
import com.example.facebookmini.fragment.ProfileFragment;
import com.example.facebookmini.model.Notification;
import com.example.facebookmini.model.Post;
import com.example.facebookmini.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class Adapter_Notification extends RecyclerView.Adapter<Adapter_Notification.Notification_Holder> {
    Context mcontext;
    List<Notification> list;

    public Adapter_Notification(Context mcontext, List<Notification> list) {
        this.mcontext = mcontext;
        this.list = list;
    }

    @NonNull
    @Override
    public Notification_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false);
        return new Adapter_Notification.Notification_Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Notification_Holder holder, int position) {
        Log.d("AAAA", "onBindViewHolder: "+list.size());
        final Notification notification=list.get(position);
        holder.text.setText(notification.getText());
        getUserInfo(holder.img_profile,holder.username,notification.getUserid());
        if(notification.isIspost()){
            holder.post_img.setVisibility(View.VISIBLE);
            getPostImage(holder.post_img,notification.getPostid());
        }else {
            holder.post_img.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(notification.isIspost()){
                    SharedPreferences.Editor editor=mcontext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                    editor.putString("postid",notification.getPostid());
                    ((FragmentActivity)mcontext).getSupportFragmentManager() .beginTransaction().replace(R.id.fr_container,new PostDetailFragment()).commit();
                }else {
                    SharedPreferences.Editor editor=mcontext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                    editor.putString("profileid",notification.getUserid());
                    ((FragmentActivity)mcontext).getSupportFragmentManager() .beginTransaction().replace(R.id.fr_container,new ProfileFragment()).commit();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Notification_Holder extends RecyclerView.ViewHolder{
        ImageView img_profile,post_img;
        TextView username,text;
        public Notification_Holder(@NonNull View itemView) {
            super(itemView);
            img_profile=itemView.findViewById(R.id.img_profile);
            post_img=itemView.findViewById(R.id.post_img);
            username=itemView.findViewById(R.id.username);
            text=itemView.findViewById(R.id.comment);
        }
    }
    private void getUserInfo(final ImageView imageView, final TextView username, String publisherid){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("User")
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
    private void getPostImage(final ImageView imageView, final String postid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts")
                .child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Post post=snapshot.getValue(Post.class);
                Picasso.get().load(post.getPostimage()).into(imageView);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
