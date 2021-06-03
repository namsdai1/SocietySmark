package com.example.facebookmini;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Activity_Login extends AppCompatActivity {
    Button btnDangNhap,btnDangKy;
    EditText edtUser,edtPass;
    FirebaseAuth auth;
    DatabaseReference reference;
    ProgressDialog pd;
    FirebaseUser firebaseUser;
    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null){
            Log.d("AAAA", "onBindViewHolder: "+firebaseUser.getUid());
            startActivity(new Intent(Activity_Login.this,Activity_All.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences1=getSharedPreferences("PREPS", Context.MODE_PRIVATE);
        Boolean booleanvalue=preferences1.getBoolean("nightmode",true);
        if(booleanvalue){
            setTheme(R.style.darkthmeo);
        }else {
            setTheme(R.style.AppTheme);

        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__login);
        btnDangNhap=findViewById( R.id.btnDangNhap );
        btnDangKy=findViewById( R.id.btnDangKy );
        edtUser=findViewById( R.id.edtUser );
        edtPass=findViewById( R.id.edtPass );
        auth=FirebaseAuth.getInstance();
        btnDangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog pd=new ProgressDialog(Activity_Login.this);
                pd.setMessage("Please wait...");
                pd.show();
                String user=edtUser.getText().toString();
                String pass=edtPass.getText().toString();
                if(TextUtils.isEmpty(user) || TextUtils.isEmpty(pass)){
                    edtUser.setError("Bạn đã nhập sai thông tin User Name hoặc Password");
                    edtPass.setError("Bạn đã nhập sai thông tin User Name hoặc Password");
                }else {
                    auth.signInWithEmailAndPassword(user,pass).addOnCompleteListener(Activity_Login.this,new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("User").child(auth.getCurrentUser().getUid());
                                reference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        pd.dismiss();
                                        Intent i=new Intent(Activity_Login.this, Activity_All.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(i);
                                        finish();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        pd.dismiss();
                                    }
                                });


                            }else {
                                Toast.makeText(Activity_Login.this, "Authentication failed!", Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }
                        }
                    });
                }
            }
        });

        btnDangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(Activity_Login.this,Activity_Register.class);
                startActivity(i);
            }
        });

    }
}