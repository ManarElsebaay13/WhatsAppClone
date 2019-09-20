package com.example.manarelsebaay.scopechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class RegisterActivity extends AppCompatActivity {



    private Button CreateAccountButt;
    private TextView AlreadyHave;
    private EditText Remail,Rpass;
    private FirebaseAuth mAuth;
    public ProgressDialog LoadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


          mAuth=FirebaseAuth.getInstance();



        IntializeFields();

        AlreadyHave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToLoginActivity();

            }
        });


        CreateAccountButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNewAccount();
            }
        });


    }


    private void CreateNewAccount()

    {
        String email= Remail.getText().toString();
        String password= Rpass.getText().toString();


        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(this,"Please Enter your Email",Toast.LENGTH_SHORT).show();
        }


        if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this,"Please Enter your password",Toast.LENGTH_SHORT).show();
        }

        else
            {
                LoadingBar.setTitle("Creating New Account...");
                LoadingBar.setMessage("Please Wait while creating New Account ...");
                LoadingBar.setCanceledOnTouchOutside(true);
                LoadingBar.show();

                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener( new OnCompleteListener <AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if(task.isSuccessful())
                        {
                            sendUserToLoginActivity();
                            Toast.makeText(RegisterActivity.this, "Account is Created", Toast.LENGTH_SHORT).show();
                            LoadingBar.dismiss();
                        }
                        else
                            {
                                String message =task.getException().toString();
                                Toast.makeText(RegisterActivity.this, "ERROR" + message, Toast.LENGTH_SHORT).show();
                                LoadingBar.dismiss();
                            }



                    }
                });





            }

    }


    private void IntializeFields()

    {
        CreateAccountButt=findViewById(R.id.Create_Account);
        AlreadyHave=findViewById(R.id.Already_have_Account);
        Remail=findViewById(R.id.Email);
        Rpass=findViewById(R.id.Password);
        LoadingBar =new ProgressDialog(this);

    }

    private void sendUserToLoginActivity() {

        Intent ToLoginActivity =new Intent(RegisterActivity.this,LoginActivity.class);

        startActivity(ToLoginActivity);


    }





}
