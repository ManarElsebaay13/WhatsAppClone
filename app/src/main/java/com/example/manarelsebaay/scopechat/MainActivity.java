package com.example.manarelsebaay.scopechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {

    private Toolbar mtoolbar ;
    private ViewPager mviewpager ;
    private TabLayout mtablayout ;
    private TabsAccessorAdapter mtabsAccessorAdapter ;
    private FirebaseUser mcurrentuser ;
    private FirebaseAuth mAuth ;
    private DatabaseReference RootRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth=FirebaseAuth.getInstance();
        mcurrentuser=mAuth.getCurrentUser();


        RootRef= FirebaseDatabase.getInstance().getReference();



        mtoolbar=(findViewById(R.id.main_toolbar));

        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("ChatClone");

        mviewpager=findViewById(R.id.Main_tabs_pager);
        mtabsAccessorAdapter = new TabsAccessorAdapter(getSupportFragmentManager());
        mviewpager.setAdapter(mtabsAccessorAdapter);

        mtablayout=findViewById(R.id.Main_tabs);
        mtablayout.setupWithViewPager(mviewpager);


    }

    @Override
    protected void onStart ()
    {
        super.onStart();

        if (mcurrentuser==null)

        {
            sendUserToLoginActivity();
        } else

        {
           VerifyUserExistance();


        }





    }

    private void VerifyUserExistance() {


        String CurrentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();


        RootRef.child("Users").child(CurrentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if  ((dataSnapshot.child("name").exists()))
                {
                    Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                }
                else
                {

                     sendUserToSettingsActivity();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





    }

    private void sendUserToLoginActivity()
    {
        Intent TologinActivity =new Intent(MainActivity.this,LoginActivity.class);
        TologinActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(TologinActivity);
        finish();

    }

    private void sendUserToSettingsActivity()
    {
        Intent ToSettingsActivity =new Intent(MainActivity.this,SettingsActivity.class);
        ToSettingsActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);


        startActivity(ToSettingsActivity);
        finish();

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.options_menu,menu);

        return true;
    }

    @Override
   public boolean onOptionsItemSelected (MenuItem item)
    {

        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.Find_Friends)
        {

        }

        if (item.getItemId() == R.id.Create_Group)
        {
           RequestGroupChat();
        }
        if (item.getItemId() == R.id.Settings)
         {
                   sendUserToSettingsActivity();
          }
        if (item.getItemId() == R.id.Log_out)
        {
            mAuth.signOut();
            sendUserToLoginActivity();

        }

        return true;
    }

    private void RequestGroupChat()
    {

        AlertDialog.Builder builder =new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Enter Group Name");
        builder.setIcon(R.drawable.sign_up);
        final EditText GroupNameField =new EditText(MainActivity.this);
        GroupNameField.setHint("  Chat Group  ");
        builder.setView(GroupNameField);
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialog, int which) {

     String GroupName = GroupNameField.getText().toString();
     if(TextUtils.isEmpty(GroupName))
     {

         Toast.makeText(MainActivity.this, "Enter Your Group Name ", Toast.LENGTH_SHORT).show();
     } else

         {
             CreateGroup(GroupName);

         }

       }});

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {


                dialogInterface.cancel();

            }});

                 builder.show();




    }

    private void CreateGroup(String groupName)
    {

        RootRef.child("Groups").child(groupName).setValue("").
                addOnCompleteListener(new OnCompleteListener <Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(MainActivity.this, "Group is Created", Toast.LENGTH_SHORT).show();
                }
            }
        });





    }


}
