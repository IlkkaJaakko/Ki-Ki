package com.bricenangue.insyconn.ki_ki;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by bricenangue on 19/11/2016.
 */

public class UserSharedPreference {
    Context context;
    public static final String SP_NAME="userDetails";
    private SharedPreferences userLocalDataBase;

    public UserSharedPreference(Context context){
        this.context=context;
        userLocalDataBase=context.getSharedPreferences(SP_NAME,0);
    }
    public void storeUserLocation(String location, int locationnumber, String countryname, int countryNumber){
        SharedPreferences.Editor spEditor=userLocalDataBase.edit();
        spEditor.putString("location",location);
        spEditor.putInt("locationnumber",locationnumber);
        spEditor.putString("country",countryname);
        spEditor.putInt("countrynumber",countryNumber);

        spEditor.apply();
    }

    public void getUserLocation(){
        String location=userLocalDataBase.getString("location", "");
        int number=userLocalDataBase.getInt("locationnumber",-1);
        String countryname=userLocalDataBase.getString("country", "");
        int countryNumber=userLocalDataBase.getInt("countrynumber",-1);

        return ;

    }

    public void clearUserData(){
        SharedPreferences.Editor spEditor=userLocalDataBase.edit();
        spEditor.clear();
        spEditor.apply();
    }

    public void setUserNumberofAds(long numberAds){
        SharedPreferences.Editor spEditor=userLocalDataBase.edit();
        if(numberAds<0){
            spEditor.putLong("numberAds",0);

        }else {
            spEditor.putLong("numberAds",numberAds);

        }

        spEditor.apply();
    }
    public Long getUserNumberofAds(){
        return userLocalDataBase.getLong("numberAds", 0);
    }

    public void reduceNumberofAds(){
        if(getUserNumberofAds()<=0){
            setUserNumberofAds(0);
        }else {
            setUserNumberofAds(getUserNumberofAds() - 1);
        }

    }

    public void addNumberofAds(){
        setUserNumberofAds(getUserNumberofAds() + 1);
    }

    public void setEmailVerified(boolean verified){
        SharedPreferences.Editor spEditor=userLocalDataBase.edit();
        spEditor.putBoolean("verified",verified);

        spEditor.apply();
    }



    public void storePersonalData(UserPersonalData userPersonalData){
        SharedPreferences.Editor spEditor=userLocalDataBase.edit();
        spEditor.putLong("size",Double.doubleToRawLongBits(userPersonalData.getSize()));
        spEditor.putInt("age",userPersonalData.getAge());
        spEditor.putInt("activity_level",userPersonalData.getActivity_level());
        spEditor.putLong("weight",Double.doubleToRawLongBits(userPersonalData.getWeight()));
        spEditor.putBoolean("gender",userPersonalData.isGender());

        spEditor.apply();
    }

    public UserPersonalData getPersonalData(){
        double size = Double.longBitsToDouble(userLocalDataBase.getLong("size", Double.doubleToLongBits(0.00)));
        double weight = Double.longBitsToDouble(userLocalDataBase.getLong("weight",Double.doubleToLongBits(0.00)));
        int age=userLocalDataBase.getInt("age", 0);
        int activity_level=userLocalDataBase.getInt("activity_level",0);
        boolean gender=userLocalDataBase.getBoolean("gender", false);

        return new UserPersonalData(size,weight,age,activity_level,gender);

    }

    public boolean getEmailVerified(){
        return userLocalDataBase.getBoolean("verified", false);
    }

    public void storeUserData(UserPublic user){
        SharedPreferences.Editor spEditor=userLocalDataBase.edit();
        spEditor.putString("email",user.getEmail());

        spEditor.putString("fullname",user.getName());
        spEditor.putString("user_uid",user.getUniquefirebasebId());
        spEditor.putString("pictureuri",user.getProfilePhotoUri());
        spEditor.putLong("onlineSince",user.getOnlineSince());
        spEditor.apply();
    }

    public User getLoggedInUser(){
        String email=userLocalDataBase.getString("email", "");
        String code=userLocalDataBase.getString("code","");
        String fullname=userLocalDataBase.getString("fullname", "");
        String pictureuri=userLocalDataBase.getString("pictureuri","");
        String user_uid=userLocalDataBase.getString("user_uid","");
        long onlineSince=userLocalDataBase.getLong("onlineSince",System.currentTimeMillis());

        User user=new User();
        UserPublic userPublic=new UserPublic();
        userPublic.setEmail(email);
        userPublic.setName(fullname);
        userPublic.setOnlineSince(onlineSince);
        userPublic.setProfilePhotoUri(pictureuri);
        userPublic.setUniquefirebasebId(user_uid);
        user.setUserPublic(userPublic);


        return user;
    }


    //call with true if logged in
    public void setUserLoggedIn(boolean loggedIn){
        SharedPreferences.Editor spEditor=userLocalDataBase.edit();
        spEditor.putBoolean("loggedIn", loggedIn);
        spEditor.apply();

    }
    public boolean getUserLoggedIn(){
        return userLocalDataBase.getBoolean("loggedIn", false);
    }

    // if logged in and data not refresh work offline
    public void setUserDataRefreshed(boolean refreshed){
        SharedPreferences.Editor spEditor=userLocalDataBase.edit();
        spEditor.putBoolean("refresh_user_data", refreshed);
        spEditor.apply();

    }

    public boolean getUserDataRefreshed(){
        return userLocalDataBase.getBoolean("refresh_user_data", false);
    }

    /*













    public boolean getPhoneRequest(){
        return userLocalDataBase.getBoolean("phone_request", false);
    }



    //call with true if registered
    public void setUserRegisterd(boolean loggedIn){
        SharedPreferences.Editor spEditor=userLocalDataBase.edit();
        spEditor.putBoolean("registered", loggedIn);
        spEditor.apply();

    }

    public boolean getUserRegisterd(){
        return userLocalDataBase.getBoolean("registered", false);
    }


*/
}
