package com.example.desi_marketplace;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;


public class WelcomeActivity extends AppCompatActivity {


    //variables
     Animation animation;
     ImageView imageView_icon;
     private static int SPLASH_TIME_OUT = 3000;
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome);


         animation = AnimationUtils.loadAnimation(WelcomeActivity.this, R.anim.animation);
         imageView_icon=findViewById(R.id.imageView_icon);

         imageView_icon.setAnimation(animation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent signin = new Intent(WelcomeActivity.this,MainActivity.class);
                startActivity(signin);
                finish();
            }
        },SPLASH_TIME_OUT);
    }
}