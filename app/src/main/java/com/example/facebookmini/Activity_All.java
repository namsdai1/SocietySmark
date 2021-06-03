package com.example.facebookmini;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.QuickContactBadge;
import android.widget.Toast;

import com.example.facebookmini.R;
import com.example.facebookmini.fragment.HomeFragment;
import com.example.facebookmini.fragment.NotificationFragment;
import com.example.facebookmini.fragment.ProfileFragment;
import com.example.facebookmini.fragment.SeachFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Activity_All extends AppCompatActivity {
BottomNavigationView bottomNavigationView;
Fragment selectedfragmen=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__all);
        bottomNavigationView=findViewById(R.id.bottom_navigation);


//
        bottomNavigationView.setOnNavigationItemSelectedListener((BottomNavigationView.OnNavigationItemSelectedListener) mOnNavigationItemSelectedListener);
        Bundle inten = getIntent().getExtras();
        if(inten!=null){
            String publisher=inten.getString("publisherid");
            SharedPreferences.Editor editor=getSharedPreferences("PREPS",MODE_PRIVATE).edit();
            editor.putString("publisherid",publisher);
            editor.apply();
            selectedfragmen=new ProfileFragment();
            loadFragment(selectedfragmen);
        }else {
            selectedfragmen=new HomeFragment();
            loadFragment(selectedfragmen);
        }
    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    selectedfragmen=new HomeFragment();
                    loadFragment(selectedfragmen);
                    return true;
                case R.id.nav_search:
                    selectedfragmen=new SeachFragment();
                    loadFragment(selectedfragmen);
                    return true;
                case R.id.nav_add:
                    selectedfragmen = null;
                    startActivity(new Intent(Activity_All.this,PostActivity.class));
                    return true;
                case R.id.nav_heart:
                    selectedfragmen=new NotificationFragment();
                    loadFragment(selectedfragmen);
                    return true;
                case R.id.nav_profile:
                    SharedPreferences.Editor editor=getSharedPreferences("PREPS", Context.MODE_PRIVATE).edit();
                    editor.putString("profileid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                    editor.apply();
                    selectedfragmen=new ProfileFragment();
                    loadFragment(selectedfragmen);
                    return true;
            }
            return false;
        }
    };
    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(
                        android.R.anim.slide_in_left,  // enter
                        R.anim.fadeout,  // exit
                        R.anim.fadein,   // popEnter
                        android.R.anim.slide_out_right); // popExit
        transaction.replace(R.id.fr_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}