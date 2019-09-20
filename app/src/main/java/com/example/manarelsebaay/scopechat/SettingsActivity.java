package com.example.manarelsebaay.scopechat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
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
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {

    private EditText username,status ;
    private Button   update;
    private CircleImageView profileImage ;
    private FirebaseAuth mAuth;
    private String CurrentuserUID;
    private DatabaseReference RootRef;
    private static final  int Gallerypick=1;
    private StorageReference UserProfileImageRef;
    private ProgressDialog LoadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth=FirebaseAuth.getInstance();
        CurrentuserUID=mAuth.getCurrentUser().getUid();
        RootRef= FirebaseDatabase.getInstance().getReference();
        UserProfileImageRef= FirebaseStorage.getInstance().getReference().child("Profile Images");
        LoadingBar= new ProgressDialog(this);




        IntializaFields();
        username.setVisibility(View.INVISIBLE);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateSettings();
            }
        });


        RetrieveSettings();

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent= new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/'");
                startActivityForResult(galleryIntent,Gallerypick);




            }
        });


    }

    private void RetrieveSettings()
    {
        RootRef.child("Users").child(CurrentuserUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if ((dataSnapshot.exists()) && dataSnapshot.hasChild("name") && dataSnapshot.hasChild("status") )
                {
                    String Username=dataSnapshot.child("name").getValue().toString();
                    String Userstatus=dataSnapshot.child("status").getValue().toString();
                    String retrieveProfileimage=dataSnapshot.child("image").getValue().toString();


                    username.setText(Username);
                    status.setText(Userstatus);

                    Picasso.get().load(retrieveProfileimage).into(profileImage);

                }
                else if ((dataSnapshot.exists()) && dataSnapshot.hasChild("name"))
                {
                    String Username=dataSnapshot.child("name").getValue().toString();
                    String Userstatus=dataSnapshot.child("status").getValue().toString();
                    username.setText(Username);
                    status.setText(Userstatus);
                }
                else
                {
                    username.setVisibility(View.VISIBLE);
                    Toast.makeText(SettingsActivity.this, "update your settings", Toast.LENGTH_SHORT).show();

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    private void IntializaFields()
    {

        username=findViewById(R.id.User_name);
        status=findViewById(R.id.Status);
        update=findViewById(R.id.Update);
        profileImage=findViewById(R.id.profile_image);


    }


 @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    if(requestCode==Gallerypick && resultCode==RESULT_OK && data!=null)
        {
            Uri Imageuri=data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode==RESULT_OK)
            {
                LoadingBar.setTitle("set Profile Image ");
                LoadingBar.setMessage("please wait");
                LoadingBar.setCanceledOnTouchOutside(false);
                LoadingBar.show();



                Uri resultUri = result.getUri();

                StorageReference filepath= UserProfileImageRef.child(CurrentuserUID + ".jpg");





                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener <UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task <UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful())
                        {
                            Toast.makeText(SettingsActivity.this, "Image is Uploaded", Toast.LENGTH_SHORT).show();



                             final String DownloadedURl=task.getResult().getStorage().getDownloadUrl().toString();

                             RootRef.child("Users").child(CurrentuserUID).child("image").setValue(DownloadedURl)
                                     .addOnCompleteListener(new OnCompleteListener <Void>() {
                                         @Override
                                         public void onComplete(@NonNull Task <Void> task) {



                                             if (task.isSuccessful())
                                             {

                                                 Toast.makeText(SettingsActivity.this, " is Stored ", Toast.LENGTH_SHORT).show();

                                                 LoadingBar.dismiss();




                                             }
                                             else
                                             {
                                                 String message= task.getException().toString();


                                                 Toast.makeText(SettingsActivity.this, "Errorr" + message , Toast.LENGTH_SHORT).show();
                                                 LoadingBar.dismiss();

                                             }

                                         }
                                     });



                        }
                        else
                        {
                            String message= task.getException().toString();


                            Toast.makeText(SettingsActivity.this, "Errorr" + message , Toast.LENGTH_SHORT).show();
                            LoadingBar.dismiss();

                        }



                    }
                });



            }

        }




    }



    private void UpdateSettings() {

        String settname=username.getText().toString();
        String settstatus=status.getText().toString();

        if (TextUtils.isEmpty(settname))
        {
            Toast.makeText(this, "please Enter your name ...", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(settstatus))
        {
            Toast.makeText(this, "please Enter your status ...", Toast.LENGTH_SHORT).show();
        }
        else
        {

            HashMap<String,String> ProfileMap=new HashMap <>();
            ProfileMap.put("uid",(CurrentuserUID));
            ProfileMap.put("name",(settname));
            ProfileMap.put("status",(settstatus));

            RootRef.child("Users").child(CurrentuserUID).setValue(ProfileMap).addOnCompleteListener(new OnCompleteListener <Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful())
                    {

                        sendUserToMainActivity();

                        Toast.makeText(SettingsActivity.this," Settings Updated ",Toast.LENGTH_SHORT).show();


                    }else
                    {
                        String message =task.getException().toString();

                        Toast.makeText(SettingsActivity.this, "ERROR" + message, Toast.LENGTH_SHORT).show();


                    }



                }
            });





        }




        }




    private void sendUserToMainActivity() {

        Intent ToMainActivity =new Intent(SettingsActivity.this,MainActivity.class);
        ToMainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(ToMainActivity);
        finish();


    }






    }














