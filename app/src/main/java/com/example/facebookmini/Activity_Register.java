package com.example.facebookmini;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.facebookmini.Dao.Dao_User;
import com.example.facebookmini.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class Activity_Register extends AppCompatActivity {
    private final int PICK_IMAGE_REQUEST = 71;
    EditText etusername,etfullname,etemail,etpassword;
    Button register;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    User muser;
    DatabaseReference reference;
    ProgressDialog progressDialog;
    CircleImageView imageUser;
    DatabaseReference mdata;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        SharedPreferences preferences1=getSharedPreferences("PREPS", Context.MODE_PRIVATE);
        Boolean booleanvalue=preferences1.getBoolean("nightmode",true);
        if(booleanvalue){
            setTheme(R.style.darkthmeo);
        }else {
            setTheme(R.style.AppTheme);

        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        etusername=findViewById(R.id.edtUser);
        etfullname=findViewById(R.id.edtFullName);
        etemail=findViewById(R.id.edtemail);
        etpassword=findViewById(R.id.edtPass);
        imageUser= findViewById( R.id.imgUser );
        register=findViewById(R.id.btnDangKy);
        imageUser.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runTimePermission();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context;
                progressDialog=new ProgressDialog(Activity_Register.this);
                progressDialog.setMessage("Please wait..");
                progressDialog.show();
                final int[] count = {0};
                final String user=etusername.getText().toString();
                final String pass=etpassword.getText().toString();
                final String email=etemail.getText().toString();
                final String fullname=etfullname.getText().toString();
                if (!user.equals( "" ) && !pass.equals( "" ) && !email.equals( "" ) && !fullname.equals( "" )  && pass.length()>6 && imageUser.getDrawable()!=null){
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference( "User" );
                    mDatabase.addListenerForSingleValueEvent( new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                if(etusername.getText().toString().equalsIgnoreCase( childSnapshot.child( "userName" ).getValue().toString())){
                                    count[0]++;
                                }
                            }
                            if(count[0] ==0) {

                                mdata= FirebaseDatabase.getInstance().getReference();
                                FirebaseStorage storage=FirebaseStorage.getInstance();
                                final StorageReference storageReference = storage.getReferenceFromUrl("gs://facebookmini-8bd33.appspot.com");
                                Calendar calendar = Calendar.getInstance();
                                StorageReference mountainsRef=storageReference.child("image"+calendar.getTimeInMillis()+".png");
                                imageUser.setDrawingCacheEnabled(true);
                                imageUser.buildDrawingCache();
                                byte[] data =imageViewToByte(imageUser);

                                UploadTask uploadTask = mountainsRef.putBytes(data);
                                uploadTask.addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        Toast.makeText(Activity_Register.this, "Loi", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                                        // ...
                                        Task<Uri> dowloadURl=taskSnapshot.getMetadata().getReference().getDownloadUrl();
                                        dowloadURl.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                String imageUrl = uri.toString();
                                                registeradd(imageUrl,user,fullname,email,pass);
                                                //createNewPost(imageUrl);
                                                overridePendingTransition( android.R.anim.slide_out_right,android.R.anim.fade_out);
                                            }

                                        });

                                    }

                                });


                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    } );
                }else if(user.equals( "" ) || pass.equals( "" ) || fullname.equals( "" ) || email.equals( "" ) ){
                    if(user.equals( "" )){
                        etusername.setError( "Bạn chưa nhập User Name" );
                    }
                    if (pass.equals( "" )){
                        etpassword.setError( "Bạn chưa nhập Password" );
                    }
                    if (fullname.equals( "" )){
                        etfullname.setError( "Bạn chưa nhập Full Name" );
                    }
                    if (email.equals( "" )){
                        etemail.setError( "Bạn chưa nhập Email" );
                    }


                }
            }
        });

    }
    public void registeradd(final String imageUrl, final String username, final String fullname, final String email , final String password){

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("AAAA", "createUserWithEmail:success");
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            String userid=firebaseUser.getUid();
                            muser = new User();
                            muser.setIDUser(userid);
                            muser.setUserName( username );
                            muser.setPassword( password );
                            muser.setFullName( fullname );
                            muser.setEmail(email);
                            muser.setImages( imageUrl);
                            Dao_User.insert_Firebase(Activity_Register.this, muser);
                            Intent intentManHinhLogin = new Intent( Activity_Register.this, Activity_Login.class );
                            startActivity( intentManHinhLogin );

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("AAAA", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Activity_Register.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }
    public void runTimePermission(){
        Dexter.withContext(Activity_Register.this).withPermission( Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                galleryIntent();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(com.karumi.dexter.listener.PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }
    private void galleryIntent() {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i,PICK_IMAGE_REQUEST);
    }
    public static byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap= getResizedBitmap( bitmap,1024 );
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }
    public static Bitmap getResizedBitmap(Bitmap bitmap, int maxSize) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float bitmapRatio = (float) width / height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == PICK_IMAGE_REQUEST){
                Uri imageUri = data.getData();
                imageUser.setImageURI(imageUri);
            }
        }
    }
}
