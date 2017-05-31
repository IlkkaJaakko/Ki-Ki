package com.bricenangue.insyconn.ki_ki;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookRequestError;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ui.ResultCodes;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LaunchActivity extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN_EMAIL = 101;
    private static final int RC_SIGN_IN_FB = 102;
    private static final int RC_SIGN_IN = 103;
    private GoogleApiClient mGoogleApiClient;
    private Button  sigm_in_email;
    private FirebaseAuth auth;
    private LoginButton sign_in_facebook;
    private CallbackManager callbackManager;
    private String email, uid, name;
    private long onlineSince;
    private SignInButton button_google;

    private UserSharedPreference userSharedPreference;
    private FirebaseUser firebaseUser;

    private ProgressDialog progressBar;

    private FirebaseAuth.AuthStateListener firebaseAuthListner;


    public boolean haveNetworkConnection() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);



        userSharedPreference=new UserSharedPreference(this);
        auth= FirebaseAuth.getInstance();
        callbackManager= CallbackManager.Factory.create();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        if (haveNetworkConnection()){
            if (isUserLogin()){
                if (AccessToken.getCurrentAccessToken()!=null){
                    procideForFacebook(auth.getCurrentUser(),auth.getCurrentUser().getPhotoUrl().toString());
                }else if (auth.getCurrentUser().getProviderData().get(1).getProviderId().equals(getString(R.string.google_firebase_provider_id))
                        || mGoogleApiClient.isConnected()){

                    procideForFacebook(auth.getCurrentUser(),auth.getCurrentUser().getPhotoUrl().toString());


                }else {
                    procideForEmail(auth.getCurrentUser());

                }
            }
        }

        sigm_in_email=(Button)findViewById(R.id.button_sing_in_with_email);
        sign_in_facebook=(LoginButton) findViewById(R.id.button_sign_in_with_facebook);
        button_google=(SignInButton) findViewById(R.id.signin_google_button);

// permissions:  Arrays.asList("email","public_profile")

        sign_in_facebook.setReadPermissions(Arrays.asList("email","public_profile"));
        sign_in_facebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                showProgressbar();
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                removePermission();
                Toast.makeText(getApplicationContext(), getString(R.string.facebook_connection_canceled),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                removePermission();
                Toast.makeText(getApplicationContext(), getString(R.string.facebook_connection_error),Toast.LENGTH_LONG).show();
            }
        });

        sigm_in_email.setOnClickListener(this);
        button_google.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.button_sing_in_with_email:

                startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                        .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                        .setTheme(R.style.AppTheme)
                        .setTosUrl("").build(),RC_SIGN_IN_EMAIL);

                break;
            case R.id.signin_google_button:

                signIn();

                break;

        }
    }


    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        showProgressbar();
        if(requestCode == RC_SIGN_IN_EMAIL){
            if(resultCode == RESULT_OK){
                loginUserEmail();
            }
            if(resultCode == RESULT_CANCELED){
                //cancel
                dismissProgressbar();
                Toast.makeText(getApplicationContext(), getString(R.string.result_cancel)+"\n" + data.toString(),Toast.LENGTH_LONG).show();
            }
            // No network
            if (resultCode == ResultCodes.RESULT_NO_NETWORK) {
                dismissProgressbar();
                Toast.makeText(getApplicationContext(), getString(R.string.alertDialog_no_internet_connection),Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }else {
            callbackManager.onActivityResult(requestCode,resultCode,data);
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Toast.makeText(getApplicationContext(),connectionResult.getErrorMessage(),Toast.LENGTH_SHORT).show();
    }

    private void handleSignInResult(GoogleSignInResult result) {
//        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            firebaseAuthWithGoogle(acct);
//            updateUI(true);
        } else {
            Toast.makeText(getApplicationContext(),result.getStatus().toString(),Toast.LENGTH_SHORT).show();

            // Signed out, show unauthenticated UI.
//            updateUI(false);
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        AuthCredential credential= GoogleAuthProvider.getCredential(acct.getIdToken(),null);
        auth.signInWithCredential(credential).addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }else {
                    loginUserGoogle(acct);
                }
            }


        });
    }

    private void loginUserGoogle(GoogleSignInAccount acct) {

        String personName = acct.getDisplayName();
        String personGivenName = acct.getGivenName();
        String personFamilyName = acct.getFamilyName();
        String personEmail = acct.getEmail();
        String personId = acct.getId();
        Uri personPhoto = acct.getPhotoUrl();
        firebaseUser=auth.getCurrentUser();

        email=personEmail;
        uid=firebaseUser.getUid();
        if (personName==null || personName.isEmpty()){
            name= personGivenName +" " +personFamilyName;
        }else{
            name = personName;
        }

        onlineSince = System.currentTimeMillis();

        final String photoUrl =  personPhoto.toString();

        final DatabaseReference referenceUserProfile = FirebaseDatabase.getInstance().getReference()
                .child(ConfigApp.FIREBASE_APP_URL_USERS)
                .child(uid);

        final UserPublic userfb=new UserPublic();
        userfb.setEmail(email);
        userfb.setName(name);
        userfb.setUniquefirebasebId(uid);
        userfb.setOnlineSince(onlineSince);
        userfb.setProfilePhotoUri(photoUrl);



        Map<String,Object> children=new HashMap<>();


        //save user in firebase realtime database
        final DatabaseReference refUSerPublice=referenceUserProfile.child("userPublic");

        children.put("/email",userfb.getEmail());
        children.put("/name",userfb.getName());
        children.put("/uniquefirebasebId",userfb.getUniquefirebasebId());
       // children.put("/chatId", FirebaseInstanceId.getInstance().getToken());
        children.put("/onlineSince", userfb.getOnlineSince());
        // children.put("/profilePhotoUri", userfb.getProfilePhotoUri());

        refUSerPublice.updateChildren(children).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isComplete() && task.isSuccessful()){
                    procideForFacebook(firebaseUser,photoUrl);
                }else {
                    showErrorSignInAndRelaunch(getString(R.string.Error_while_updating_user_profile_information));
                    dismissProgressbar();

                }
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        auth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),getString(R.string.unable_to_connect_to_server_facebook_error), Toast.LENGTH_LONG).show();
                    dismissProgressbar();
                }else {
                    loginUserFacebook();
                }
            }

        });
    }

    private void loginUserFacebook() {

        firebaseUser=auth.getCurrentUser();

        email=firebaseUser.getEmail();
        uid=firebaseUser.getUid();
        name = firebaseUser.getDisplayName();
        onlineSince = System.currentTimeMillis();

        String facebookUserId = firebaseUser.getProviderData().get(1).getUid();
        final String photoUrl = "https://graph.facebook.com/" + facebookUserId + "/picture?type=large";

        final DatabaseReference referenceUserProfile =FirebaseDatabase.getInstance().getReference()
                .child(ConfigApp.FIREBASE_APP_URL_USERS)
                .child(uid);

        final UserPublic userfb=new UserPublic();
        userfb.setEmail(email);
        userfb.setName(name);
        userfb.setUniquefirebasebId(uid);
        userfb.setOnlineSince(onlineSince);
        userfb.setProfilePhotoUri(photoUrl);



        Map<String,Object> children=new HashMap<>();

        setFirebasePhotoUri(firebaseUser,photoUrl);
        //save user in firebase realtime database
        final DatabaseReference refUSerPublice=referenceUserProfile.child("userPublic");

        children.put("/email",userfb.getEmail());
        children.put("/name",userfb.getName());
        children.put("/uniquefirebasebId",userfb.getUniquefirebasebId());
     //   children.put("/chatId", FirebaseInstanceId.getInstance().getToken());
        children.put("/onlineSince", userfb.getOnlineSince());
        children.put("/profilePhotoUri", userfb.getProfilePhotoUri());

        refUSerPublice.updateChildren(children).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isComplete() && task.isSuccessful()){
                    procideForFacebook(firebaseUser,photoUrl);
                }else {
                    showErrorSignInAndRelaunch(getString(R.string.Error_while_updating_user_profile_information));
                    dismissProgressbar();

                }
            }
        });

    }

    private void setFirebasePhotoUri(FirebaseUser user, String uri){
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(Uri.parse(uri))
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
//                            Log.d(TAG, "User profile updated.");
                        }
                    }
                });
    }

    private void loginUserEmail() {

        firebaseUser =auth.getCurrentUser();



        firebaseUser=auth.getCurrentUser();

        email=firebaseUser.getEmail();
        uid=firebaseUser.getUid();
        name = firebaseUser.getDisplayName();
        onlineSince = System.currentTimeMillis();

        final DatabaseReference referenceUserProfile =FirebaseDatabase.getInstance().getReference()
                .child(ConfigApp.FIREBASE_APP_URL_USERS)
                .child(uid);

        final UserPublic userfb=new UserPublic();
        userfb.setEmail(email);
        userfb.setName(name);
        userfb.setUniquefirebasebId(uid);
        userfb.setOnlineSince(onlineSince);

        Map<String,Object> children=new HashMap<>();



        final boolean emailVerified=firebaseUser.isEmailVerified();
        userSharedPreference.setEmailVerified(emailVerified);


        //save user in firebase realtime database
        final DatabaseReference refUSerPublice=referenceUserProfile.child("userPublic");

        children.put("/email",userfb.getEmail());
        children.put("/name",userfb.getName());
        children.put("/uniquefirebasebId",userfb.getUniquefirebasebId());
       // children.put("/chatId", FirebaseInstanceId.getInstance().getToken());
        children.put("/onlineSince", userfb.getOnlineSince());

        refUSerPublice.updateChildren(children).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isComplete() && task.isSuccessful()){
                    if ( !firebaseUser.isEmailVerified()){

                        verifyMail(firebaseUser);

                    }else {
                        procideForEmail(firebaseUser);
                    }

                }else {
                    showErrorSignInAndRelaunch(getString(R.string.Error_while_updating_user_profile_information));
                    dismissProgressbar();

                }
            }
        });

    }



    private void procideForFacebook(final FirebaseUser user, final String urlPicture) {


        final DatabaseReference ref=FirebaseDatabase.getInstance().getReference()
                .child(ConfigApp.FIREBASE_APP_URL_USERS).
                        child(user.getUid()).child("userPublic")
                ;
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChildren()){

                    /*userSharedPreference.storeUserData(dataSnapshot.getValue(UserPublic.class));
                    userSharedPreference.setUserLoggedIn(true);
                    userSharedPreference.setUserDataRefreshed(true);
                    */

                    if (!dataSnapshot.hasChild("profilePhotoUri")){
                        ref.child("profilePhotoUri").setValue(urlPicture);
                        setFirebasePhotoUri(user,urlPicture);
                    }

                    startActivity(new Intent(LaunchActivity.this,HomePageActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    dismissProgressbar();
                    finish();

                }else {
                    dismissProgressbar();
                    Toast.makeText(getApplicationContext()
                            ,getString(R.string.problem_while_loading_user_data_not_identify)
                            ,Toast.LENGTH_LONG).show();

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                dismissProgressbar();
                Toast.makeText(getApplicationContext(),databaseError.getMessage()
                        ,Toast.LENGTH_SHORT).show();

            }
        });
    }



    private void procideForEmail(FirebaseUser currentUser) {

    }


    private void verifyMail(final FirebaseUser user) {
/*
        final AlertDialog alertDialog =
                new AlertDialog.Builder(LauncherActivity.this).setTitle(
                        getString(R.string.alertDialogverifyemail_title))
                        .setIcon(getResources().getDrawable(R.drawable.ic_warning_black_24dp))
                        .setMessage(getString(R.string.alertDialogverifyemail_message) +" " +user.getEmail()+"\n\n\n"+getString( R.string.email_verified_plesase_log_out)) .create();
        alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, getString(R.string.button_logout)
                , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //user.sendEmailVerification();
                        showLogoutProgressbar();

                        if (AccessToken.getCurrentAccessToken()!=null){

                            GraphRequest delPermRequest = new GraphRequest(AccessToken.getCurrentAccessToken(), "/"+user.getProviderData().get(1).getUid()+"/permissions/", null, HttpMethod.DELETE, new GraphRequest.Callback() {
                                @Override
                                public void onCompleted(GraphResponse graphResponse) {
                                    if(graphResponse!=null){
                                        FacebookRequestError error =graphResponse.getError();
                                        if(error!=null){
                                            //Log.e(TAG, error.toString());
                                            dismissProgressbar();
                                            Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
                                        }else {
                                            LoginManager.getInstance().logOut();
                                            auth.signOut();
                                            userSharedPreference.clearUserData();
                                            startActivity(new Intent(LauncherActivity.this,LauncherActivity.class)
                                                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                                            Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                                            Intent.FLAG_ACTIVITY_NEW_TASK)
                                            );
                                            Firebase.goOffline();
                                            dismissProgressbar();
                                            finish();

                                        }
                                    }
                                }
                            });
                            // Log.d(TAG,"Executing revoke permissions with graph path" + delPermRequest.getGraphPath());
                            delPermRequest.executeAsync();
                        }else {
                            auth.signOut();
                            userSharedPreference.clearUserData();
                            startActivity(new Intent(LauncherActivity.this,LauncherActivity.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                            Intent.FLAG_ACTIVITY_NEW_TASK)
                            );
                            Firebase.goOffline();
                            dismissProgressbar();
                            finish();


                        }
                        alertDialog.dismiss();

                    }

                });
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.button_continue)
                , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //user.sendEmailVerification();
                        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getApplicationContext()
                                        ,getString(R.string.problem_while_loading_user_data_verifaction_email_resent)
                                                +" "+ user.getEmail(),Toast.LENGTH_LONG).show();
                            }
                        });

                        procideForEmail(user);

                        alertDialog.dismiss();
                        dismissProgressbar();

                    }

                });
        alertDialog.setCancelable(false);
        alertDialog.show();
        */

    }

/*
    private void procideForEmail(final FirebaseUser user) {
        final DatabaseReference ref=FirebaseDatabase.getInstance().getReference()
                .child(ConfigApp.FIREBASE_APP_URL_USERS).
                        child(user.getUid()).child("userPublic")
                ;
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChildren()){
                    userSharedPreference.setUserNumberofAds(dataSnapshot
                            .child("numberOfAds").getValue(long.class));

                    userSharedPreference.storeUserData(dataSnapshot.getValue(UserPublic.class));
                    userSharedPreference.setUserLoggedIn(true);
                    userSharedPreference.setUserDataRefreshed(true);

                    userSharedPreference.setEmailVerified(user.isEmailVerified());


                    if (dataSnapshot.hasChild("Location")){

                        Locations locations=dataSnapshot.child("Location")
                                .getValue(Locations.class);
                        userSharedPreference.storeUserLocation(locations.getName()
                                , locations.getNumberLocation()
                                ,locations.getNameCountry(),
                                locations.getNumberCountry());
                        startActivity(new Intent(LauncherActivity.this,CategoryActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        dismissProgressbar();
                        finish();

                    }else {

                        startActivity(new Intent(LauncherActivity.this,LocationsActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        dismissProgressbar();
                        finish();

                    }
                }else {
                    dismissProgressbar();
                    Toast.makeText(getApplicationContext()
                            ,getString(R.string.problem_while_loading_user_data_not_identify)
                            ,Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                dismissProgressbar();
                Toast.makeText(getApplicationContext(),databaseError.getMessage()
                        ,Toast.LENGTH_SHORT).show();

            }
        });
    }


    */
    void removePermission(){
        dismissProgressbar();
        LoginManager.getInstance().logOut();
    }

    private boolean isUserLogin(){
        if(auth.getCurrentUser() != null){
            return true;
        }
        return false;
    }

    private void showErrorSignInAndRelaunch(String message){
        Toast.makeText(getApplicationContext()
                ,message
                ,Toast.LENGTH_SHORT).show();
    }

    private void showProgressbar(){
        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(false);
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setMessage(getString(R.string.progress_dialog_connecting));
        progressBar.show();
    }

    private void showLogoutProgressbar(){
        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(false);
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setMessage(getString(R.string.progress_dialog_disconnecting));
        progressBar.show();
    }

    private void dismissProgressbar(){
        if (progressBar!=null){
            progressBar.dismiss();
        }
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
