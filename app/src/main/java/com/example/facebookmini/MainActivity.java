package com.example.facebookmini;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
Switch darkSwitch;
Button btnlogin,btnlanguages;
SharedPreferences sharedPreferences=null;
static String langu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES){
            setTheme(R.style.darkthmeo);
        }else {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        loadLocale();
        //change title actionbar theo leanguage
        ActionBar actionBar=getSupportActionBar();
//        actionBar.setTitle(getResources().getString(R.string.app_name));
        setContentView(R.layout.activity_main);
        darkSwitch=findViewById(R.id.st_darkmode);
        btnlanguages=findViewById(R.id.btn_languages);
        btnlogin=findViewById(R.id.btn_login);
        sharedPreferences=getSharedPreferences("night",0);
        Boolean booleanvalue=sharedPreferences.getBoolean("nightmode",true);
        if(booleanvalue){
            Toast.makeText(this, ""+booleanvalue, Toast.LENGTH_SHORT).show();
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            darkSwitch.setChecked(true);
        }
        if(AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES) {
            darkSwitch.setChecked(true);
        }
        //thiet lap light/dark mode
        darkSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecker) {
                if(isChecker){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    save(true);
                    restarApp();
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    save(false);
                    restarApp();
                }
            }
        });
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MainActivity.this,MainActivity2.class);
                startActivity(i);
            }
        });
        //Thiet lap ngon ngu
        btnlanguages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChanceLanguageDiaLog();
            }
        });

    }

    private void showChanceLanguageDiaLog() {
        final String[] listItem={"English","한국어","Tiếng Việt"};
        Context context;
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        mBuilder.setTitle("Choose Language");
        mBuilder.setSingleChoiceItems(listItem, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i==0){
                    setLocale("en");
                    recreate();
                }
                if(i==1){
                    setLocale("ko");
                    recreate();
                }
                if(i==2){
                    setLocale("vi");
                    recreate();
                }
                dialogInterface.dismiss();
            }

        });
        AlertDialog alertDialog = mBuilder.create();
        alertDialog.show();

    }

    private void setLocale(String vi) {
        Locale locale = new Locale(vi);
        Locale.setDefault(locale);
        Configuration config=new Configuration();
        config.locale=locale;
        getBaseContext().getResources().updateConfiguration(config,getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor=getSharedPreferences("Setting",MODE_PRIVATE).edit();
        editor.putString("My_Lang",vi);
        editor.apply();
    }
    private void loadLocale(){
        SharedPreferences preferences=getSharedPreferences("Setting", Activity.MODE_PRIVATE);
        String  langues= preferences.getString("My_Lang","");
        langu=langues;
        setLocale(langues);
    }

    private void save(boolean nightmode) {
        SharedPreferences.Editor editor=getSharedPreferences("PREPS",Context.MODE_PRIVATE).edit();
        editor.putBoolean("nightmode",nightmode);
        editor.commit();
    }

    private void restarApp() {
        Intent i=new Intent(getApplicationContext(),MainActivity.class);
        startActivity(i);
        finish();
    }
}