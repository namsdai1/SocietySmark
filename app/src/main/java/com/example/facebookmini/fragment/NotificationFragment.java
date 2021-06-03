package com.example.facebookmini.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.facebookmini.Adapter.Adapter_Notification;
import com.example.facebookmini.R;
import com.example.facebookmini.model.Notification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class NotificationFragment extends Fragment {
RecyclerView recyclerView;
Adapter_Notification adapter_notification;
List<Notification> notificationList;
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
        View view=inflater.inflate(R.layout.fragment_notification, container, false);
        recyclerView=view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        notificationList=new ArrayList<>();

        adapter_notification=new Adapter_Notification(getContext(),notificationList);
        recyclerView.setAdapter(adapter_notification);
        readNotifications();

        return view;
    }

    private void readNotifications() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notificationList.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    Notification notification=snapshot1.getValue(Notification.class);
                    notificationList.add(notification);
                }
                Log.d("AAAA", "onDataChange: "+notificationList.size());
                Collections.reverse(notificationList);
                adapter_notification.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}