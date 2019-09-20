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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;




public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth ;
    private EditText email,password;
    private ImageView loginimage;
    private Button Login,phone;
    private TextView Forgetpass,createNewAccount,orloginphone;
    public ProgressDialog LoadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mAuth=FirebaseAuth.getInstance();

        IntializeFields();

        createNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToRegisterActivity();
            }
        });
        
        
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllowUserLogin();
            }
        });

        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(LoginActivity.this,PhoneLoginActivity.class);
                startActivity(I);
            }
        });
        
        

    }


    private void AllowUserLogin()
    {

        String Email= email.getText().toString();
        String Password= password.getText().toString();

        if(TextUtils.isEmpty(Email))
        {
            Toast.makeText(this,"Please Enter your Email",Toast.LENGTH_SHORT).show();
        }


        if(TextUtils.isEmpty(Password))
        {
            Toast.makeText(this,"Please Enter your password",Toast.LENGTH_SHORT).show();
        }

        else
        {

            LoadingBar.setTitle("Sign In");
            LoadingBar.setMessage("Please Wait.....");
            LoadingBar.setCanceledOnTouchOutside(true);
            LoadingBar.show();
            mAuth.signInWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener <AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful())
                    {
                        sendUserToMainActivity();
                        Toast.makeText(LoginActivity.this," Login Successfully ",Toast.LENGTH_SHORT).show();
                        LoadingBar.dismiss();


                    }else
                    {
                        String message =task.getException().toString();
                        Toast.makeText(LoginActivity.this, "ERROR" + message, Toast.LENGTH_SHORT).show();
                        LoadingBar.dismiss();

                    }
                }
            });


        }



    }


    private void IntializeFields()
    {

       email=findViewById(R.id.Email);
       password=findViewById(R.id.Password);
        Login=findViewById(R.id.login);
        phone=findViewById(R.id.Phone);
        Forgetpass=findViewById(R.id.forget_password);
        createNewAccount=findViewById(R.id.Create_new_Account);
        LoadingBar= new ProgressDialog(this);

    }






    private void sendUserToMainActivity() {

        Intent ToMainActivity =new Intent(LoginActivity.this,MainActivity.class);
         ToMainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(ToMainActivity);
         finish();


    }

    private void sendUserToRegisterActivity() {

        Intent ToRegisterActivity =new Intent(LoginActivity.this,RegisterActivity.class);

        startActivity(ToRegisterActivity);


    }


}
