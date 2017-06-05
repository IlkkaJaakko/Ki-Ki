package com.bricenangue.insyconn.ki_ki;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.firebase.client.Firebase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

/**
 * Created by bricenangue on 31.05.17.
 */

public class KiKiApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();


        FacebookSdk.sdkInitialize(this);
        AppEventsLogger.activateApp(this);
        Firebase.setAndroidContext(this);
        //register for app notification topic to receive updates about app
       // FirebaseMessaging.getInstance().subscribeToTopic("com.bricenangue.nextgeneration.ebuycamer");

       // FirebaseInstanceId.getInstance().getToken();

    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }
}
