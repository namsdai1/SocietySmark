package com.example.facebookmini.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.facebookmini.R;
import com.example.facebookmini.fragment.PostDetailFragment;
import com.example.facebookmini.model.Post;
import com.squareup.picasso.Picasso;

import java.util.List;

public class Adapter_Foto extends RecyclerView.Adapter<Adapter_Foto.Foto_Holder> {
    Context mcontext;
    List<Post> list;

    public Adapter_Foto(Context mcontext, List<Post> list) {
        this.mcontext = mcontext;
        this.list = list;
    }

    @NonNull
    @Override
    public Foto_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.fotos_item, parent, false);
        return new Adapter_Foto.Foto_Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Foto_Holder holder, int position) {
        final Post post=list.get(position);
        Picasso.get().load(post.getPostimage()).into(holder.post_img);
        holder.post_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor=mcontext.getSharedPreferences("PREPS",Context.MODE_PRIVATE).edit();
                Log.d("AAAA", "onClick: "+post.getPostid());
                editor.putString("postid",post.getPostid());
                editor.apply();
                ((FragmentActivity)mcontext).getSupportFragmentManager().beginTransaction().replace(R.id.fr_container,new PostDetailFragment()).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Foto_Holder extends RecyclerView.ViewHolder{
        public ImageView post_img;
        public Foto_Holder(@NonNull View itemView) {
            super(itemView);
            post_img=itemView.findViewById(R.id.img_post);
        }
    }
}
