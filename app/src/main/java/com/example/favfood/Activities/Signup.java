package com.example.favfood.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.favfood.Models.DatabaseUser;
import com.example.favfood.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class Signup extends AppCompatActivity {

    EditText email, password, rePassword, firstname,lastName;
    MaterialButton signup, login;
    String fName,lName,emailId,pass;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;
    AlertDialog.Builder builder;
    AlertDialog alertDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        firstname = findViewById(R.id.firstname);
        lastName = findViewById(R.id.lastName);
        email = findViewById(R.id.email2);
        password = findViewById(R.id.password1);
        rePassword = findViewById(R.id.conPassword);
        signup = findViewById(R.id.signBtn1);
        login = findViewById(R.id.loginBtn1);
        mAuth = FirebaseAuth.getInstance();

        builder = new AlertDialog.Builder(Signup.this);
        builder.setMessage("Creating user \nPlease wait...");
        builder.setTitle("Signing in");
        builder.setCancelable(false);
        alertDialog = builder.create();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");

        login.setOnClickListener(view -> {
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
        });

        signup.setOnClickListener(view -> {
            emailId = email.getText().toString();
            fName = firstname.getText().toString();
            lName = lastName.getText().toString();
            pass = password.getText().toString();
            String repass = rePassword.getText().toString();
            if(emailId.isEmpty())
            {
                email.setError("Email is empty");
                email.requestFocus();
                return;
            }
            if(!Patterns.EMAIL_ADDRESS.matcher(emailId).matches())
            {
                email.setError("Enter the valid email");
                email.requestFocus();
                return;
            }
            if(pass.isEmpty())
            {
                password.setError("Password is empty");
                password.requestFocus();
                return;
            }
            if(pass.length()<6)
            {
                password.setError("Entered Length of password is less than 6");
                password.requestFocus();
                return;
            }
            if(repass.isEmpty())
            {
                rePassword.setError("Field is empty");
                rePassword.requestFocus();
                return;
            }
            if(!repass.equals(pass))
            {
                rePassword.setError("Password doesn't match");
                rePassword.requestFocus();
                return;
            }
            alertDialog.show();
            mAuth.createUserWithEmailAndPassword(emailId, pass).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    sendEmailVerification();
                    alertDialog.dismiss();
                    OnAuth(Objects.requireNonNull(task.getResult().getUser()));
                }
                else{
                    Toast.makeText(Signup.this, "User Already exist", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                }
                mAuth.signOut();
            });
        });
    }

    private void sendEmailVerification() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            user.sendEmailVerification().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    Toast.makeText(Signup.this, "Check your email for verification", Toast.LENGTH_SHORT).show();
                    FirebaseAuth.getInstance().signOut();
                }
            });
        }
    }
    private void OnAuth(FirebaseUser user)
    {
        DatabaseUser user1 = new DatabaseUser(fName+lName,emailId,"","","","");
        databaseReference.child(user.getUid()).setValue(user1);
        firstname.getText().clear();
        lastName.getText().clear();
        email.getText().clear();
        password.getText().clear();
        rePassword.getText().clear();
    }
}