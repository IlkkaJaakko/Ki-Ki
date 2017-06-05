package com.bricenangue.insyconn.ki_ki;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookRequestError;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.firebase.client.Firebase;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import com.google.android.gms.fitness.HistoryApi;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DataReadResult;
import com.google.android.gms.fitness.result.DataSourcesResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Scope;

import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessStatusCodes;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.result.DailyTotalResult;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.LogRecord;

import static com.google.android.gms.fitness.data.DataType.TYPE_STEP_COUNT_DELTA;
import static com.google.android.gms.fitness.data.Field.FIELD_STEPS;
import static java.util.concurrent.TimeUnit.SECONDS;


public class HomePageActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String SELECTED_ITEM = "arg_selected_item";

    private BottomNavigationView navigation;
    private int mSelectedItem;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth auth;
    private ProgressDialog progressBar;
    private Fragment selectedFragment= null;
    private static final String TAG = "HomePageActivity";


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
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mGoogleApiClient.connect();
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
                selectedFragment=FragmentHome.newInstance();
                break;
            case R.id.navigation_community:
                selectedFragment=FragmentCommunity.newInstance();
                break;
            case R.id.navigation_myKiKi:
                selectedFragment=FragmentMyKiKi.newInstance();
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
                                || mGoogleApiClient.isConnected()){

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
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
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
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
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

}



