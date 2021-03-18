package com.example.spotifyadblocker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.spotifyadblocker.databinding.ActivityLoginpageBinding;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LoginPageActivity extends AppCompatActivity {

    ActivityLoginpageBinding b;

    @SuppressLint("StaticFieldLeak")
    public  static Activity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b=ActivityLoginpageBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        activity=this;
        b.btnSendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(b.userName.getText().toString().length()<4||b.userName.getText().toString().matches("[0-9]+")){
                    b.userName.setError("Invalid");
                    return;
                }
                if(b.phoneNo.getText().toString().length()!=10){
                    b.phoneNo.setError("Invalid");
                    return;
                }
                b.btnSendOtp.setVisibility(View.INVISIBLE);
                b.progressBar2.setVisibility(View.VISIBLE);
                PhoneAuthOptions options= PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                        .setPhoneNumber("+91"+b.phoneNo.getText().toString())
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(LoginPageActivity.this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                b.btnSendOtp.setVisibility(View.VISIBLE);
                                b.progressBar2.setVisibility(View.INVISIBLE);
                                Toast.makeText(LoginPageActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {

                                Toast.makeText(LoginPageActivity.this, "Code Sent", Toast.LENGTH_SHORT).show();

                            Intent i=new Intent(LoginPageActivity.this,VerificationActivity.class);
                            i.putExtra("phoneNo",b.phoneNo.getText().toString());
                            i.putExtra("verificationId",verificationId);
                            i.putExtra("userName",b.userName.getText().toString().trim());
                                b.btnSendOtp.setVisibility(View.VISIBLE);
                                b.progressBar2.setVisibility(View.INVISIBLE);
                            startActivity(i);

                            }
                        })
                        .build();

                PhoneAuthProvider.verifyPhoneNumber(options);
            }
        });
//        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
//            startActivity(new Intent(LoginPageActivity.this,MainActivity.class));
//            finish();
//        }

    }


}