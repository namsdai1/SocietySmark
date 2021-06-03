package com.example.facebookmini;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.facebookmini.model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

public class Activity_EditProfile extends AppCompatActivity {
ImageView close,img_profile;
TextView save,tv_chance;
MaterialEditText fullname,username;
FirebaseUser firebaseUser;
Uri mImageUri;
StorageTask uploadTask;
StorageReference storageReference;
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
        setContentView(R.layout.activity__edit_profile);
        close=findViewById(R.id.close);
        img_profile=findViewById(R.id.img_profile);
        save=findViewById(R.id.save);
        fullname=findViewById(R.id.fullname);
        username=findViewById(R.id.Username);
        tv_chance=findViewById(R.id.tv_chance);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        storageReference=FirebaseStorage.getInstance().getReference("uploads");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User")
                .child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                fullname.setText(user.getFullName());
                username.setText(user.getUserName());
                Picasso.get().load(user.getImages()).into(img_profile);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        tv_chance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity().setAspectRatio(1,1)
                        .start(Activity_EditProfile.this);
            }
        });
        img_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity().setAspectRatio(1,1)
                        .start(Activity_EditProfile.this);
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile(fullname.getText().toString(),username.getText().toString());
            }
        });

    }

    private void updateProfile(String fullname, String username) {
        Log.d("check", "updateProfile: check1");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User").child(firebaseUser.getUid());
        HashMap<String, Object> hashMap=new HashMap<>();
        hashMap.put("fullName",fullname);
        hashMap.put("userName",username);
        reference.updateChildren(hashMap);
    }
    private String getFileExtension(Uri uri){
        Log.d("check", "updateProfile: check2");
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private void uploadImage(){
        Log.d("check", "updateProfile: check3");
        Context context;
        final ProgressDialog pd=new ProgressDialog(this);
        pd.setMessage("Uploading...");
        pd.show();
        if(mImageUri!=null){
            final StorageReference filereference=storageReference.child(System.currentTimeMillis()
            +"."+getFileExtension(mImageUri));
            uploadTask=filereference.putFile(mImageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return filereference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri dowload = task.getResult();
                        String myUri=dowload.toString();
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User").child(firebaseUser.getUid());
                        HashMap<String, Object> hashMap=new HashMap<>();
                        hashMap.put("images",""+myUri);
                        reference.updateChildren(hashMap);
                        pd.dismiss();
                    }else {
                        Toast.makeText(Activity_EditProfile.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Activity_EditProfile.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }else {
            Toast.makeText(Activity_EditProfile.this, "No img selectd", Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d("check", "updateProfile: check");
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            mImageUri=result.getUri();
//            img_profile.setImageURI(mImageUri);
            uploadImage();
        }else {
            Toast.makeText(this, "Something gone wrong!", Toast.LENGTH_SHORT).show();
        }
    }

}