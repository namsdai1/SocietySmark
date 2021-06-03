package com.example.facebookmini.Adapter;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.facebookmini.Activity_ADDStory;
import com.example.facebookmini.Activity_Story;
import com.example.facebookmini.R;
import com.example.facebookmini.model.Story;
import com.example.facebookmini.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class Adapter_Story extends RecyclerView.Adapter<Adapter_Story.StoryHolder> {
    Context mcontext;
    List<Story> list;



    public Adapter_Story(Context mcontext, List<Story> list) {
        this.mcontext = mcontext;
        this.list = list;
    }

    @NonNull
    @Override
    public StoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==0){
            View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.add_story_item, parent, false);
            return new Adapter_Story.StoryHolder(view);
        }else {
            View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.story_item, parent, false);
            return new Adapter_Story.StoryHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final StoryHolder holder, int position) {
        final Story story=list.get(position);
        userInfo(holder,story.getUserid(),position);
        if(holder.getAdapterPosition()!=0){
            seenStory(holder,story.getUserid());
        }
        if(holder.getAdapterPosition()==0){
            myStory(holder.addstory_text,holder.story_plus,false);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.getAdapterPosition()==0){
                    myStory(holder.addstory_text,holder.story_plus,true);
                }else {
                    Intent i=new Intent(mcontext, Activity_Story.class);
                    i.putExtra("userid",story.getUserid());
                    mcontext.startActivity(i);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class StoryHolder extends RecyclerView.ViewHolder{
        public ImageView story_photo,story_plus,story_photo_seen;
        public TextView story_username,addstory_text;
        public StoryHolder(@NonNull View itemView) {
            super(itemView);
            story_photo=itemView.findViewById(R.id.story_photo);
            story_plus=itemView.findViewById(R.id.story_plus);
            story_photo_seen=itemView.findViewById(R.id.story_photo_seen);
            story_username=itemView.findViewById(R.id.story_username);
            addstory_text=itemView.findViewById(R.id.addstory_text);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position==0){
            return 0;
        }
        return 1;
    }
    private void userInfo(final StoryHolder viewHolder, final String userid, final int pos){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("User").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                Picasso.get().load(user.getImages()).into(viewHolder.story_photo);
                if(pos!=0){
                    Picasso.get().load(user.getImages()).into(viewHolder.story_photo_seen);
                    viewHolder.story_username.setText(user.getUserName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void myStory(final TextView textView, final ImageView imageView, final boolean click){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Story")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count=0;
                long timeCurrent=System.currentTimeMillis();
                for (DataSnapshot snapshot1:snapshot.getChildren()){
                    Story story=snapshot1.getValue(Story.class);
                    if(timeCurrent > story.getTimeStart() && timeCurrent< story.getTimeEnd()){
                        count++;
                    }
                }
                if(click){
                    if(count>0){
                        AlertDialog alertDialog=new AlertDialog.Builder(mcontext).create();
                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "View Story",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent in=new Intent(mcontext, Activity_Story.class);
                                        in.putExtra("userid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        mcontext.startActivity(in);
                                        dialogInterface.dismiss();
                                    }
                                });
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Add Story",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent=new Intent(mcontext, Activity_ADDStory.class);
                                        mcontext.startActivity(intent);
                                        dialogInterface.dismiss();
                                    }
                                });
                        alertDialog.show();
                    }else {
                        Intent intent=new Intent(mcontext, Activity_ADDStory.class);
                        mcontext.startActivity(intent);
                    }
                }else {
                    if(count>0){
                        textView.setText("My story");
                        imageView.setVisibility(View.GONE);
                    }else {
                        textView.setText("Add Story");
                        imageView.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void seenStory(final StoryHolder viewHolder, String userid){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Story")
                .child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i=0;
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    if(!snapshot1.child("views").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).exists()
                    && System.currentTimeMillis() <snapshot1.getValue(Story.class).getTimeEnd()){
                        i++;
                    }
                }
                if(i>0){
                    viewHolder.story_photo.setVisibility(View.VISIBLE);
                    viewHolder.story_photo_seen.setVisibility(View.GONE);
                }else {
                    viewHolder.story_photo.setVisibility(View.GONE);
                    viewHolder.story_photo_seen.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
