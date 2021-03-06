package com.bricenangue.insyconn.ki_ki.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import android.widget.Toast;

import com.bricenangue.insyconn.ki_ki.BuildConfig;
import com.bricenangue.insyconn.ki_ki.fragments.FragmentCommunity;
import com.bricenangue.insyconn.ki_ki.fragments.FragmentHome;
import com.bricenangue.insyconn.ki_ki.fragments.FragmentMyKiKi;
import com.bricenangue.insyconn.ki_ki.R;
import com.bricenangue.insyconn.ki_ki.UserSharedPreference;
import com.facebook.AccessToken;
import com.facebook.FacebookRequestError;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.login.LoginManager;
import com.firebase.client.Firebase;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import com.google.android.gms.fitness.data.Session;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.support.design.widget.Snackbar;


public class HomePageActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, ConnectionCallbacks {

    private static final String SELECTED_ITEM = "arg_selected_item";
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 24;

    public static final String TAG = "GoogleFit";
    private GoogleApiClient mClient = null;
    private Session mSession;
    private final String SESSION_NAME = "dummy session";

    private BottomNavigationView navigation;
    private int mSelectedItem;
    private FirebaseAuth auth;
    private ProgressDialog progressBar;
    private Fragment selectedFragment= null;


    public boolean haveNetworkConnection() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            selectFragment(item);
            return true;

        }

    };
    private UserSharedPreference userSharedPreference;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);


        userSharedPreference=new UserSharedPreference(this);
        auth= FirebaseAuth.getInstance();
        if (auth != null){
            user =auth.getCurrentUser();
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();
        mClient.connect();

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        MenuItem selectedItem;
        if (savedInstanceState != null) {
            mSelectedItem = savedInstanceState.getInt(SELECTED_ITEM, 0);
            selectedItem = navigation.getMenu().findItem(mSelectedItem).setChecked(true);
        } else {
            selectedItem = navigation.getMenu().getItem(0).setChecked(true);
        }
        selectFragment(selectedItem);

        AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
            @Override
            public void onSuccess(Account account) {

                Toast.makeText(getApplicationContext(),account.getId(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(AccountKitError accountKitError) {

            }
        });

    }


    public static GoogleApiClient googleFitBuild(Activity activity, GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener failedListener){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //.requestServerAuthCode(activity.getString(R.string.server_client_id), false)
                .requestEmail()
                .requestScopes(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE), new Scope(Scopes.FITNESS_BODY_READ_WRITE),
                        new Scope(Scopes.FITNESS_NUTRITION_READ_WRITE), new Scope(Scopes.FITNESS_LOCATION_READ_WRITE))
                .build();
        return new GoogleApiClient.Builder(activity)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addConnectionCallbacks(connectionCallbacks)
                .addOnConnectionFailedListener(failedListener)
                //.addApi(Plus.API)
                .addApi(Fitness.CONFIG_API)
                .addApi(Fitness.HISTORY_API)
                .addApi(Fitness.SESSIONS_API)
                .addApi(Fitness.RECORDING_API)
                .addApi(Fitness.BLE_API)
                .addApi(Fitness.SENSORS_API)
                .build();
    }



    @Override
    protected void onStart() {
        super.onStart();
<<<<<<< HEAD:app/src/main/java/com/bricenangue/insyconn/ki_ki/HomePageActivity.java
        mGoogleApiClient = googleFitBuild(this,this,this);
    }
=======
>>>>>>> Freelancer_Branch_changed:app/src/main/java/com/bricenangue/insyconn/ki_ki/activities/HomePageActivity.java

    }
    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(SELECTED_ITEM, mSelectedItem);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        MenuItem homeItem = navigation.getMenu().getItem(0);
        if (mSelectedItem != homeItem.getItemId()) {
            // select home item
            selectFragment(homeItem);
            homeItem.setChecked(true);
        } else {
            super.onBackPressed();
        }
    }

    private void selectFragment(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.navigation_home:
<<<<<<< HEAD:app/src/main/java/com/bricenangue/insyconn/ki_ki/HomePageActivity.java
               // readData();
                selectedFragment=FragmentHome.newInstance();
=======
                selectedFragment= FragmentHome.newInstance();
>>>>>>> Freelancer_Branch_changed:app/src/main/java/com/bricenangue/insyconn/ki_ki/activities/HomePageActivity.java
                break;
            case R.id.navigation_community:
                selectedFragment= FragmentCommunity.newInstance();
                break;
            case R.id.navigation_myKiKi:
                selectedFragment= FragmentMyKiKi.newInstance();
                break;
        }


        // update selected item
        mSelectedItem = item.getItemId();

        if (selectedFragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content, selectedFragment);
            transaction.commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);


        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_logout:
                if (!haveNetworkConnection()){
                    Toast.makeText(getApplicationContext(),getString(R.string.connection_to_server_not_aviable)
                            ,Toast.LENGTH_SHORT).show();
                }else {
                    loggout();
                }

                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    private void loggout() {
        final AlertDialog alertDialog =
                new AlertDialog.Builder(HomePageActivity.this)
                        .setIcon(getResources().getDrawable(R.drawable.ic_power_settings_new_black_24dp))
                        .setMessage(
                                getString(R.string.alertDialoglogout)+" " +user.getEmail())
                        .create();
        alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, getString(R.string.button_cancel)
                , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alertDialog.dismiss();

                    }

                });
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.button_logout)
                , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        showLogoutProgressbar();

                        if (AccessToken.getCurrentAccessToken()!=null){

                            GraphRequest delPermRequest = new GraphRequest(AccessToken.getCurrentAccessToken()
                                    , "/"+user.getProviderData().get(1).getUid()+"/permissions/", null, HttpMethod.DELETE, new GraphRequest.Callback() {
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
                                            startActivity(new Intent(HomePageActivity.this,LaunchActivity.class)
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
                        } else if (auth.getCurrentUser().getProviderData().get(1).getProviderId()
                                .equals(getString(R.string.google_firebase_provider_id))
                                || mClient.isConnected()){

                            signOut();
                            revokeAccess();
                            userSharedPreference.clearUserData();
                            startActivity(new Intent(HomePageActivity.this,LaunchActivity.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                            Intent.FLAG_ACTIVITY_NEW_TASK)
                            );
                            Firebase.goOffline();
                            dismissProgressbar();
                            finish();

                        }else {
                            auth.signOut();
                            userSharedPreference.clearUserData();
                            startActivity(new Intent(HomePageActivity.this,LaunchActivity.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                            Intent.FLAG_ACTIVITY_NEW_TASK)
                            );
                            Firebase.goOffline();
                            dismissProgressbar();
                            finish();


                        }
                    }
                });
        alertDialog.setCancelable(false);
        alertDialog.show();

    }

    private void signOut() {
        // Firebase sign out
        auth.signOut();

        // Google sign out
        Auth.GoogleSignInApi.signOut(mClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
//                        updateUI(null);
                    }
                });
    }

    // to disconnect from google
    private void revokeAccess() {
        // Firebase sign out
        auth.signOut();

        // Google revoke access
        Auth.GoogleSignInApi.revokeAccess(mClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
//                        updateUI(null);
                        Toast.makeText(getApplicationContext(),status.getStatusMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        dismissProgressbar();
        Log.w(TAG, "Google Play services connection failed. Cause: " +
                result.toString());
        Snackbar.make(
                HomePageActivity.this.findViewById(R.id.container),
                "Exception while connecting to Google Play services: " +
                        result.getErrorMessage(),
                Snackbar.LENGTH_INDEFINITE).show();
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
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Google API connected");

        Toast.makeText(getApplicationContext(),"connected",Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            Snackbar.make(
                    findViewById(R.id.container),
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(HomePageActivity.this,
                                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    })
                    .show();
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(HomePageActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted.
                mGoogleApiClient = googleFitBuild(this,this,this);

            } else {
                // Permission denied.

                // In this Activity we've chosen to notify the user that they
                // have rejected a core permission for the app since it makes the Activity useless.
                // We're communicating this message in a Snackbar since this is a sample app, but
                // core permissions would typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                Snackbar.make(
                        findViewById(R.id.container),
                        R.string.permission_denied_explanation,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        })
                        .show();
            }
        }
    }



<<<<<<< HEAD:app/src/main/java/com/bricenangue/insyconn/ki_ki/HomePageActivity.java

=======
           startActivity(new Intent(this, DietPlanActivity.class)
           .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

    }


>>>>>>> Freelancer_Branch_changed:app/src/main/java/com/bricenangue/insyconn/ki_ki/activities/HomePageActivity.java
}



