package com.example.spotifyadblocker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.example.spotifyadblocker.Models.Users;
import com.example.spotifyadblocker.databinding.ActivityVerificationBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

public class VerificationActivity extends AppCompatActivity {
    ActivityVerificationBinding b;
    String verificationId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b=ActivityVerificationBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());


        verificationId=getIntent().getStringExtra("verificationId");


        b.btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(b.inputcode1.getText().toString().trim().isEmpty()
                ||b.inputcode2.getText().toString().trim().isEmpty()
                ||b.inputcode3.getText().toString().trim().isEmpty()
                ||b.inputcode4.getText().toString().trim().isEmpty()
                ||b.inputcode5.getText().toString().trim().isEmpty()
                ||b.inputcode6.getText().toString().trim().isEmpty()){
                    Toast.makeText(VerificationActivity.this, "Please enter verification code", Toast.LENGTH_SHORT).show();
                    return;
                }

                    b.progressBar.setVisibility(View.VISIBLE);
                    b.btnVerify.setVisibility(View.INVISIBLE);


                String code=b.inputcode1.getText().toString()+
                        b.inputcode2.getText().toString()+
                        b.inputcode3.getText().toString()+
                        b.inputcode4.getText().toString()+
                        b.inputcode5.getText().toString()+
                        b.inputcode6.getText().toString();



                PhoneAuthCredential phoneAuthCredential= PhoneAuthProvider.getCredential(verificationId,code);

                FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        Users user=new Users(getIntent().getStringExtra("userName"),"+91"+getIntent().getStringExtra("phoneNo"));

                         FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getUid())
                                 .set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                             @Override
                             public void onSuccess(Void aVoid) {
                                 Toast.makeText(VerificationActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                                 Intent i=new Intent(VerificationActivity.this,MainActivity.class);
                                 startActivity(i);
                                 finish();
                             }
                         }).addOnFailureListener(new OnFailureListener() {
                             @Override
                             public void onFailure(@NonNull Exception e) {
                                 b.progressBar.setVisibility(View.INVISIBLE);
                                 b.btnVerify.setVisibility(View.VISIBLE);
                                 Toast.makeText(VerificationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                             }
                         });


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        b.progressBar.setVisibility(View.INVISIBLE);
                        b.btnVerify.setVisibility(View.VISIBLE);
                        Toast.makeText(VerificationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });

        b.resendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneAuthOptions options= PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                        .setPhoneNumber("+91"+getIntent().getStringExtra("phoneNo"))
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(VerificationActivity.this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {

                                Toast.makeText(VerificationActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {

                                VerificationActivity.this.verificationId=verificationId;
                                Toast.makeText(VerificationActivity.this, "Code Sent", Toast.LENGTH_SHORT).show();

                            }
                        })
                        .build();

                PhoneAuthProvider.verifyPhoneNumber(options);
            }
        });
        setupOtp();


    }

    public void setupOtp(){
        b.inputcode1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               if(!s.toString().isEmpty()){
                   b.inputcode2.requestFocus();
               }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        b.inputcode2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().isEmpty()){
                    b.inputcode3.requestFocus();
                }
                else{
                    b.inputcode1.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        b.inputcode3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().isEmpty()){
                    b.inputcode4.requestFocus();
                }
                else{
                    b.inputcode2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        b.inputcode4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().isEmpty()){
                    b.inputcode5.requestFocus();
                }
                else {
                    b.inputcode3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        b.inputcode5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().isEmpty()){
                    b.inputcode6.requestFocus();
                }
                else{
                    b.inputcode4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        b.inputcode6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                 if(s.toString().isEmpty()){
                     b.inputcode5.requestFocus();
                 }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}