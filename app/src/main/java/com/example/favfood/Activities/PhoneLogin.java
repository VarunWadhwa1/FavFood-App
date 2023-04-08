package com.example.favfood.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.favfood.MainActivity;
import com.example.favfood.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class PhoneLogin extends AppCompatActivity {

    EditText phoneNo,otp;
    MaterialButton phoneLogin,generateOtp;
    FirebaseAuth auth;
    private String verificationId;
    CountryCodePicker ccp;
    String selected_country_code;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);
        auth = FirebaseAuth.getInstance();
        phoneNo = findViewById(R.id.phone1);
        otp = findViewById(R.id.otp);
        phoneLogin = findViewById(R.id.phLoginBtn);
        generateOtp = findViewById(R.id.generateOtp);
        ccp = findViewById(R.id.ccp);
        ccp.setOnCountryChangeListener(() -> selected_country_code = ccp.getSelectedCountryCodeWithPlus());
        generateOtp.setOnClickListener(v -> {
            if (TextUtils.isEmpty(phoneNo.getText().toString())) {
                Toast.makeText(PhoneLogin.this, "Please enter a valid phone number.", Toast.LENGTH_SHORT).show();
            } else {

                if(selected_country_code == null){
                    selected_country_code = "+91";
                }
                String phone = selected_country_code + phoneNo.getText().toString();
                sendVerificationCode(phone);
                Toast.makeText(PhoneLogin.this, "Getting OTP...", Toast.LENGTH_SHORT).show();
            }
        });
        phoneLogin.setOnClickListener(v -> {
            if (TextUtils.isEmpty(otp.getText().toString())) {
                Toast.makeText(PhoneLogin.this, "Please enter OTP", Toast.LENGTH_SHORT).show();
            } else {
                verifyCode(otp.getText().toString());
            }
        });
    }
    private void signInWithCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(PhoneLogin.this, "Signing you in...", Toast.LENGTH_SHORT).show();
                String name = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getPhoneNumber();
                Toast.makeText(getApplicationContext(), "Welcome " + name, Toast.LENGTH_SHORT).show();
                Intent i = new Intent(PhoneLogin.this, MainActivity.class);
                startActivity(i);
                finish();
            } else {
                Toast.makeText(PhoneLogin.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    private void sendVerificationCode(String number) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth).setPhoneNumber(number).setTimeout(
                60L, TimeUnit.SECONDS).setActivity(this).setCallbacks(mCallBack).build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
        }
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            final String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                otp.setText(code);
                verifyCode(code);
            }
        }
        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(PhoneLogin.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };
    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }
}