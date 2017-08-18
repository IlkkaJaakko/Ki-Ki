package com.bricenangue.insyconn.ki_ki.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.bricenangue.insyconn.ki_ki.R;
import com.google.firebase.auth.FirebaseAuth;

public class StartingSplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIME_OUT = 3000;

    private FirebaseAuth auth;



    public boolean haveNetworkConnection() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting_splash);
        auth= FirebaseAuth.getInstance();
    }


    @Override
    protected void onStart() {
        start();
        super.onStart();
    }

    private void start() {

        if (isUserLogin()){
            startActivity(new Intent(StartingSplashActivity.this,HomePageActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        }else {
            if (haveNetworkConnection()){
                new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

                    @Override
                    public void run() {
                        // This method will be executed once the timer is over
                        // Start your app logging activity
                        startActivity(new Intent(StartingSplashActivity.this, LaunchActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));

                    }
                }, SPLASH_TIME_OUT);


            }

        }


    }

    private boolean isUserLogin(){
        if(auth.getCurrentUser() != null){
            return true;
        }
        return false;
    }
    
   
    
}
