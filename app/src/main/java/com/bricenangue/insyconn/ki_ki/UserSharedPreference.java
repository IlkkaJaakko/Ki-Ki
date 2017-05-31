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


    public boolean getEmailVerified(){
        return userLocalDataBase.getBoolean("verified", false);
    }


    /*
    public void storeUserData(UserPublic user){
        SharedPreferences.Editor spEditor=userLocalDataBase.edit();
        spEditor.putString("email",user.getEmail());
        if (user.getPhoneNumber()!=null){
            spEditor.putString("code",user.getPhoneNumber().getCode());
            spEditor.putString("phonenumber",user.getPhoneNumber().getPhoneNumber());
            spEditor.putInt("operatorCode",user.getPhoneNumber().getOperatorCode());
            spEditor.putString("operator",user.getPhoneNumber().getOperator());
        }
        spEditor.putString("fullname",user.getName());
        spEditor.putString("user_uid",user.getUniquefirebasebId());
        spEditor.putString("pictureuri",user.getProfilePhotoUri());
        spEditor.putLong("onlineSince",user.getOnlineSince());
        spEditor.apply();
    }

    public User getLoggedInUser(){
        String email=userLocalDataBase.getString("email", "");
        String phonenumber=userLocalDataBase.getString("phonenumber","");
        String code=userLocalDataBase.getString("code","");
        String fullname=userLocalDataBase.getString("fullname", "");
        String pictureuri=userLocalDataBase.getString("pictureuri","");
        String user_uid=userLocalDataBase.getString("user_uid","");
        String operator=userLocalDataBase.getString("operator","");
        int operatorCode=userLocalDataBase.getInt("operatorCode",-1);
        long onlineSince=userLocalDataBase.getLong("onlineSince",System.currentTimeMillis());

        User user=new User();
        UserPublic userPublic=new UserPublic();
        userPublic.setEmail(email);
        userPublic.setPhoneNumber(new PhoneNumber(code,phonenumber,operator,operatorCode));
        userPublic.setName(fullname);
        userPublic.setOnlineSince(onlineSince);
        userPublic.setProfilePhotoUri(pictureuri);
        userPublic.setUniquefirebasebId(user_uid);
        user.setUserPublic(userPublic);


        return user;
    }

    public void clearUserData(){
        SharedPreferences.Editor spEditor=userLocalDataBase.edit();
        spEditor.clear();
        spEditor.apply();
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

    public void setPhoneResquest(boolean requested){
        SharedPreferences.Editor spEditor=userLocalDataBase.edit();
        spEditor.putBoolean("phone_request", requested);
        spEditor.apply();

    }

    public boolean getRememberCountry(){
        return userLocalDataBase.getBoolean("rememberCountry", false);
    }

    public void setRememberCountry(boolean rememberCountry){
        SharedPreferences.Editor spEditor=userLocalDataBase.edit();
        spEditor.putBoolean("rememberCountry", rememberCountry);
        spEditor.apply();

    }

    public boolean getPhoneRequest(){
        return userLocalDataBase.getBoolean("phone_request", false);
    }

    public PhoneNumber getUserPhone(){
        String phonenumber=userLocalDataBase.getString("phonenumber","");
        String code=userLocalDataBase.getString("code","");
        String operator=userLocalDataBase.getString("operator","");
        int operatorCode=userLocalDataBase.getInt("operatorCode",-1);

        return new PhoneNumber(code,phonenumber,operator,operatorCode);

    }


    public void setUserPhone(PhoneNumber phone){
        SharedPreferences.Editor spEditor=userLocalDataBase.edit();

        spEditor.putString("code",phone.getCode());
        spEditor.putString("phonenumber",phone.getPhoneNumber());
        spEditor.putInt("operatorCode",phone.getOperatorCode());
        spEditor.putString("operator",phone.getOperator());

        spEditor.apply();
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
