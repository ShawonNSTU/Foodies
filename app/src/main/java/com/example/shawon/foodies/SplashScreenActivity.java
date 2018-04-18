package com.example.shawon.foodies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class SplashScreenActivity extends AppCompatActivity {

    private ImageView mSplashImage,mSplashText;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorStatusBar));

        mSplashImage = (ImageView) findViewById(R.id.splashImage);
        mSplashText = (ImageView) findViewById(R.id.splashText);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);

        Animation animation = AnimationUtils.loadAnimation(this,R.anim.splash_screen_animation);

        mSplashText.startAnimation(animation);
        mSplashImage.startAnimation(animation);
        progressBar.startAnimation(animation);

        Thread timer = new Thread(){
            public void run() {
                try {
                    sleep(4500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
                    startActivity(new Intent(SplashScreenActivity.this,WelcomeActivity.class));
                    finish();
                }
            }
        };

        timer.start();

    }
}
