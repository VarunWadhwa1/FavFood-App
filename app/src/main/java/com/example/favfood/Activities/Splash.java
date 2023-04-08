package com.example.favfood.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.favfood.MainActivity;
import com.example.favfood.R;
import com.google.firebase.auth.FirebaseAuth;

public class Splash extends AppCompatActivity {

    Animation topAnimation,bottomAnimation;
    ImageView imageView;
    TextView logo, tagline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        topAnimation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.top_animation);
        bottomAnimation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.bottom_animation);

        imageView = findViewById(R.id.imageView2);
        logo = findViewById(R.id.textView);
        tagline = findViewById(R.id.textView2);

        imageView.setAnimation(topAnimation);
        logo.setAnimation(bottomAnimation);
        tagline.setAnimation(bottomAnimation);

        FirebaseAuth auth = FirebaseAuth.getInstance();

        int SPLASH_SCREEN = 1500;
        new Handler().postDelayed(() -> {
            if (auth.getCurrentUser() != null) {
                Intent i = new Intent(Splash.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }else {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
            finish();
        }, SPLASH_SCREEN);
    }
}