package com.example.manarelsebaay.scopechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {

    private Button sendverificationbutt,verifybutt;
    private EditText InputPhone,Inputverification;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private ProgressDialog loadingBar;

    private  String mVerificationId;
    private  PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);


        sendverificationbutt=findViewById(R.id.Send_verification_Code);
        verifybutt=findViewById(R.id.Verify);
        InputPhone=findViewById(R.id.Phone_number_input);
        Inputverification=findViewById(R.id.verification_number_input);
        loadingBar=new ProgressDialog(this);


        sendverificationbutt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phoneNumber= InputPhone.getText().toString();

                if(TextUtils.isEmpty(phoneNumber))
                {
                    Toast.makeText(PhoneLoginActivity.this, "Please Enter your Phone number...", Toast.LENGTH_SHORT).show();
                }

                else
                {

                    loadingBar.setTitle("phone verification");
                    loadingBar.setMessage("please wait while Authinticating your phone");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            PhoneLoginActivity.this,               // Activity (for callback binding)
                            callbacks);        // OnVerificationStateChangedCallbacksPhoneAuthActivity.java
                }


            }
        });




        verifybutt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendverificationbutt.setVisibility(View.INVISIBLE);
                InputPhone.setVisibility(View.INVISIBLE);

                String verificationCode=Inputverification.getText().toString();



                if(TextUtils.isEmpty(verificationCode))
                {
                    Toast.makeText(PhoneLoginActivity.this, "Please Enter your verificationCode...", Toast.LENGTH_SHORT).show();
                }

                else
                {


                    loadingBar.setTitle(" verification code");
                    loadingBar.setMessage("please wait while verifing");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                    signInWithPhoneAuthCredential(credential);



                }



            }
        });



          callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
              @Override
              public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                  signInWithPhoneAuthCredential(phoneAuthCredential);

              }

              @Override
              public void onVerificationFailed(FirebaseException e) {

                  String meg= e.toString();

                  loadingBar.dismiss();
                  Toast.makeText(PhoneLoginActivity.this, "Invalid phone number" + meg, Toast.LENGTH_SHORT).show();


                  sendverificationbutt.setVisibility(View.VISIBLE);
                  verifybutt.setVisibility(View.INVISIBLE);
                  InputPhone.setVisibility(View.VISIBLE);
                  Inputverification.setVisibility(View.INVISIBLE);
                  mAuth=FirebaseAuth.getInstance();


              }



              @Override
              public void onCodeSent( String verificationId,
                                      PhoneAuthProvider.ForceResendingToken token) {

                  mVerificationId = verificationId;
                  mResendToken = token;

                  loadingBar.dismiss();

                  Toast.makeText(PhoneLoginActivity.this, "code has been sent", Toast.LENGTH_SHORT).show();

                  sendverificationbutt.setVisibility(View.INVISIBLE);
                  verifybutt.setVisibility(View.VISIBLE);
                  InputPhone.setVisibility(View.INVISIBLE);
                  Inputverification.setVisibility(View.VISIBLE);
                  mAuth=FirebaseAuth.getInstance();



              }


          };


    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task <AuthResult> task) {
                        if (task.isSuccessful())

                        {
                            loadingBar.dismiss();
                            Toast.makeText(PhoneLoginActivity.this, " you are login", Toast.LENGTH_SHORT).show();

                            sendUserToMainactivity();

                        }

                        else
                            {
                                String message= task.getException().toString();

                                Toast.makeText(PhoneLoginActivity.this, " Error"+ message, Toast.LENGTH_SHORT).show();


                            }
                        }
                    });

    }


    private void sendUserToMainactivity() {


        Intent ToMainActivity =new Intent(PhoneLoginActivity.this,MainActivity.class);
        startActivity(ToMainActivity);
        finish();
    }


}
