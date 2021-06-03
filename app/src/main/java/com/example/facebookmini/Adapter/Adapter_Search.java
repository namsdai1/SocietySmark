package com.example.facebookmini.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.facebookmini.Activity_All;
import com.example.facebookmini.R;
import com.example.facebookmini.fragment.ProfileFragment;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class Adapter_Search extends RecyclerView.Adapter<Adapter_Search.Search_holder> {
    private Context context;
    private ArrayList<User> list;
    FirebaseUser firebaseUser;
    private boolean isframent;

    public Adapter_Search(Context context, ArrayList<User> list,boolean isframent) {
        this.context = context;
        this.list = list;
        this.isframent = isframent;
    }

    @NonNull
    @Override
    public Search_holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item, parent, false);;
        return (new Adapter_Search.Search_holder(view));
    }
    @Override
    public void onBindViewHolder(@NonNull final Search_holder holder, final int position) {
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        Log.d("AAAA", "onBindViewHolder: "+list.get(position).getIDUser());
        holder.txtfullname.setText(list.get(position).getFullName());
        holder.txtusername.setText(list.get(position).getUserName());
        holder.btnfollow.setVisibility(View.VISIBLE);
        Picasso.get().load(list.get(position).getImages()).into(holder.imguser);
        isFollowing(list.get(position).getIDUser(),holder.btnfollow);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isframent){
                SharedPreferences.Editor editor=context.getSharedPreferences("PREPS",Context.MODE_PRIVATE).edit();
                editor.putString("profileid",list.get(position).getIDUser());
                editor.apply();
                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fr_container,new ProfileFragment()).commit();
                }else {
                    Intent i=new Intent(context, Activity_All.class);
                    i.putExtra("publisherid",list.get(position).getIDUser());
                    context.startActivity(i);
                }
            }
        });
        holder.btnfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.btnfollow.getText().toString().equalsIgnoreCase("follow")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following")
                            .child(list.get(position).getIDUser()).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(list.get(position).getIDUser()).child("followers")
                            .child(firebaseUser.getUid()).setValue(true);
                    addNotifications(list.get(position).getIDUser());
                }else {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following")
                            .child(list.get(position).getIDUser()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(list.get(position).getIDUser()).child("followers")
                            .child(firebaseUser.getUid()).removeValue();
                }
            }
        });

       if(list.get(position).getIDUser().equals(firebaseUser.getUid())){
           holder.btnfollow.setVisibility(View.GONE);
       }



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class Search_holder extends RecyclerView.ViewHolder{
        TextView txtusername,txtfullname;
        CircleImageView imguser;
        Button btnfollow;
        public Search_holder(@NonNull View itemView) {
            super(itemView);
            txtfullname=(TextView) itemView.findViewById(R.id.txt_searchfullname);
            txtusername=(TextView) itemView.findViewById(R.id.txt_searchuser);
            btnfollow= itemView.findViewById(R.id.btn_search);
            imguser= itemView.findViewById(R.id.img_search);

        }
    }
    private void isFollowing(final String userid, final Button button){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(userid).exists()){
                    button.setText("following");
                }else {
                    button.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void addNotifications(String userid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);
        reference.push().setValue(new Notification(firebaseUser.getUid(),"Start following you","",true));
    }
}
