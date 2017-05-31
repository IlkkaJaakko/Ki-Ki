package com.bricenangue.insyconn.ki_ki;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Pair;
import android.view.Surface;
import android.view.WindowManager;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by bricenangue on 19/11/2016.
 */

public class ConfigApp {

    public static final String FIREBASE_APP_URL_USERS_POSTS = "Posts";
    public static final String FIREBASE_APP_URL_POSTS_EXIST = "Posts-Exits";
    public static final String FIREBASE_APP_URL_USERS_POSTS_USER = "User-Posts";

    public static final String FIREBASE_APP_URL_POSTS_ALL_POST_PICTURES = "All-Pictures";



    public static final String FIREBASE_APP_URL_USERS_DEAL = "Deals";
    public static final String FIREBASE_APP_URL_USERS_DEAL_USER = "User-Deals";
    public static final String FIREBASE_APP_URL_DEAL_EXIST = "Deals-Exits";

    public static final String FIREBASE_APP_URL_USERS_OFFERS_USER = "User-Offers";

    public static final String FIREBASE_APP_URL_USERS_FAVORITES_USER = "User-Favorites";
    public static final String FIREBASE_APP_URL_USERS_FAVORITES = "Favorites";

    static final String FIREBASE_APP_URL_REGIONS = "Cities";
    // path All-Cities/CITY_112 ==> CITY_countryNumber+TownNumber
    static final String FIREBASE_APP_URL_PATH_ALL_CITIES = "CITY_";


    static final String FIREBASE_APP_URL_USERS = "Users";

    static final String FIREBASE_APP_URL_CHATS = "Chats";
    static final String FIREBASE_APP_URL_MESSAGES = "Messages";
    static final String FIREBASE_APP_URL_MY_CHAT = "My-Chat";
    static final String FIREBASE_APP_URL_CHAT_EXISTS = "Chat-Exists";
    static final String FIREBASE_APP_URL_CHAT_USER = "User-Chat";
    static final String FIREBASE_APP_URL_USER_FOLLOWERS = "Followers";

    static final String FIREBASE_APP_SENDER_ID = "714204703996";
    //also for privacy policy
    static final String OOOWEBHOST_SERVER_URL   =  "http://time-tracker.comlu.com/";
    public static final String FIREBASE_APP_URL_USERS_POSTS_ALL_CITY = "All-Cities";
    public static final String FIREBASE_APP_FOLLOWERS_MESSAGE_NOTIFICATION = "New Stuff";


    private Context context;
    static final String FIREBASE_APP_URL_STORAGE_USER_PROFILE_PICTURE = "/Photo/profilePicture";

    static  final String FIREBASE_APP_URL_STORAGE_USER_PROFILES= "USERS";

    public ConfigApp(Context context){
        this.context=context;
    }


    public boolean haveNetworkConnection() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }


    public static String getData(ArrayList<Pair<String, String>> values) throws UnsupportedEncodingException {
        StringBuilder result=new StringBuilder();
        for(Pair<String,String> pair : values){

            if(result.length()!=0)

                result.append("&");
            result.append(URLEncoder.encode(pair.first, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.second, "UTF-8"));

        }
        return result.toString();
    }

    public static void lockScreenOrientation(Activity activity)
    {
        WindowManager windowManager =  (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        Configuration configuration = activity.getResources().getConfiguration();
        int rotation = windowManager.getDefaultDisplay().getRotation();

        // Search for the natural position of the device
        if(configuration.orientation == Configuration.ORIENTATION_LANDSCAPE &&
                (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) ||
                configuration.orientation == Configuration.ORIENTATION_PORTRAIT &&
                        (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270))
        {
            // Natural position is Landscape
            switch (rotation)
            {
                case Surface.ROTATION_0:
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    break;
                case Surface.ROTATION_90:
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                    break;
                case Surface.ROTATION_180:
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                    break;
                case Surface.ROTATION_270:
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    break;
            }
        }
        else
        {
            // Natural position is Portrait
            switch (rotation)
            {
                case Surface.ROTATION_0:
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    break;
                case Surface.ROTATION_90:
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    break;
                case Surface.ROTATION_180:
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                    break;
                case Surface.ROTATION_270:
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                    break;
            }
        }
    }

    public static void unlockScreenOrientation(Activity activity)
    {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

}
