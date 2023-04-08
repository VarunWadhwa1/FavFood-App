package com.example.favfood.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.favfood.Fragments.FragmentChangePassword;
import com.example.favfood.MainActivity;
import com.example.favfood.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;
import java.util.Random;

public class Login extends AppCompatActivity {

    EditText email, password;
    MaterialButton loginBtn, signupBtn;
    public static final String TAG = "LOGIN";
    private FirebaseAuth auth;
    TextView forgot;
    String emailId, pass;
    FirebaseUser mUser;
    ImageView phone,google,anonymous;
    FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email1);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.phLoginBtn);
        signupBtn = findViewById(R.id.signBtn);
        forgot = findViewById(R.id.forgotPass);
        phone = findViewById(R.id.phone);
        google = findViewById(R.id.google);
        anonymous = findViewById(R.id.anon);

        auth = FirebaseAuth.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        authStateListener = firebaseAuth -> {
            if (mUser != null) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } else {
                Log.d(TAG, "AuthStateChanged:Logout");
            }
        };
        forgot.setOnClickListener(v -> new FragmentChangePassword().Dialog(v));
        signupBtn.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), Signup.class));
            finish();
        });
        loginBtn.setOnClickListener(view -> userSign());
        phone.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(),PhoneLogin.class)));
        google.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(),GoogleLogIn.class)));
        anonymous.setOnClickListener(v -> {
            anonymous.setClickable(false);
            if (mUser == null){
                auth.signInAnonymously().addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        Anonymous();
                }).addOnFailureListener(e -> Log.e("TAG",e.getMessage()));
            }
            else
            {
                Toast.makeText(Login.this, "Already Logged in anonymously", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void userSign() {
        emailId = email.getText().toString();
        pass = password.getText().toString().trim();
        if (emailId.isEmpty()) {
            email.setError("Email is empty");
            email.requestFocus();
            return;
        }
        if (pass.isEmpty()) {
            password.setError("Password is empty");
            password.requestFocus();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
        builder.setMessage("Logging user Please wait...");
        builder.setTitle("Signing in");
        builder.setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        auth.signInWithEmailAndPassword(emailId, pass).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                alertDialog.dismiss();
                Toast.makeText(Login.this, "Unsuccessful", Toast.LENGTH_SHORT).show();
            } else {
                alertDialog.dismiss();
                checkIfEmailVerified();
            }
        });
    }
    private void checkIfEmailVerified() {
        FirebaseUser users = FirebaseAuth.getInstance().getCurrentUser();
        boolean emailIdVerified = Objects.requireNonNull(users).isEmailVerified();
        if (!emailIdVerified) {
            Toast.makeText(this, "Verify the Email Id", Toast.LENGTH_SHORT).show();
            auth.signOut();
        } else {
            email.getText().clear();
            password.getText().clear();

            String name = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName();
            Toast.makeText(getApplicationContext(), "Welcome " + name, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
        finish();
    }
    public  void  Anonymous(){
        String ALLOWED_CHARACTERS ="0123456789";
        final Random random = new Random();
        final StringBuilder sb = new StringBuilder(4);
        for(int i=0;i<4;++i) {
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        }
        SharedPreferences preferences =getSharedPreferences("Features",MODE_PRIVATE);
        preferences.edit().putString("Guest","Guest User" + sb).apply();

        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
        Toast.makeText(Login.this, "Hello Guest User"+sb, Toast.LENGTH_LONG).show();
    }
}
