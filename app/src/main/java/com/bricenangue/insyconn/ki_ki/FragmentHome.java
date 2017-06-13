package com.bricenangue.insyconn.ki_ki;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bricenangue.insyconn.ki_ki.asynctask.FetchCaloriesAsync;
import com.bricenangue.insyconn.ki_ki.asynctask.FetchHydrationAsync;
import com.bricenangue.insyconn.ki_ki.asynctask.FetchNutritionAsync;
import com.bricenangue.insyconn.ki_ki.asynctask.FetchStepsCountAsync;
import com.firebase.client.utilities.LogWrapper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DataSourcesResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.facebook.login.widget.ProfilePictureView.TAG;
import static com.google.android.gms.fitness.data.Field.FIELD_VOLUME;
import static java.util.concurrent.TimeUnit.SECONDS;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;

import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessStatusCodes;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.result.DailyTotalResult;

import java.util.concurrent.TimeUnit;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentHome#newInstance} factory method to
 * create an instance of this fragment.
 * the Fitness API should be implemented here to show daily step recording and hydration recording and also return daily data
 */
public class FragmentHome extends Fragment implements  View.OnClickListener{

    private TextView textViewWaterIntake, textViewStepsCount, textViewStepsGoal, textViewStepsDistance
            ,textViewStepstime, textViewStepsCalorieBurned, textViewWaterIntakeInLiter;
    private ImageButton imageButtonAddWater, imageButtonReduceWater;
    private int waterIntake=0;
    FirebaseUser firebaseUser;
    FirebaseAuth auth;
    DatabaseReference databaseReference;
    UserSharedPreference userSharedPreference;

    public static final String TAG = "GoogleFit";
    private GoogleApiClient mClient = null;
    private Session mSession;
    private final String SESSION_NAME = "dummy session";

    private View  view;


    public FragmentHome() {
        // Required empty public constructor
    }

    public static FragmentHome newInstance() {

        return new FragmentHome();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth=FirebaseAuth.getInstance();
        userSharedPreference=new UserSharedPreference(getContext());
        if (auth!=null){
            firebaseUser=auth.getCurrentUser();
        }





    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         view = inflater.inflate(R.layout.fragment_home, container, false);
        imageButtonAddWater=(ImageButton) view.findViewById(R.id.imageButton_homeFragment_add_water);
        imageButtonReduceWater=(ImageButton)view.findViewById(R.id.imageButton_homeFragment_reduce_water);

        textViewStepsCalorieBurned=(TextView)view.findViewById(R.id.textView_homeFragment_steps_calories_burned);
        textViewWaterIntake=(TextView)view.findViewById(R.id.textView_homeFragment_glas_water);
        textViewStepsCount=(TextView)view.findViewById(R.id.textView_homeFragment_steps_count);
        textViewStepsGoal=(TextView)view.findViewById(R.id.textView_homeFragment_steps_goal);
        textViewStepsDistance=(TextView)view.findViewById(R.id.textView_homeFragment_steps_distance);
        textViewStepstime=(TextView)view.findViewById(R.id.textView_homeFragment_steps_duration);
        textViewWaterIntakeInLiter=(TextView)view.findViewById(R.id.textView_homeFragment_water_intake_in_liter);

        imageButtonReduceWater.setOnClickListener(this);
        imageButtonAddWater.setOnClickListener(this);
        if (waterIntake==0){
            imageButtonReduceWater.setEnabled(false);
            textViewWaterIntakeInLiter.setText("= " +String.valueOf(waterIntake * 0.3f) + " liter");
        }


        return view;
    }





    @Override
    public void onClick(View view) {
        String waterCount="00";

        switch (view.getId()){
            case R.id.imageButton_homeFragment_add_water:

                waterIntake++;
                 waterCount = String.format(Locale.FRANCE,"%02d", waterIntake);
               // textViewWaterIntake.setText(waterCount);
                saveHydration(waterIntake * 0.3f);
                imageButtonReduceWater.setEnabled(true);

                break;
            case R.id.imageButton_homeFragment_reduce_water:
                // waterinter * 0.3 l
               checkWaterIntake();


                break;
        }


        //if ((waterIntake * 0.3f)>=Goal) {
        //         smiley
        //}
    }

    private void checkWaterIntake(){
        if (waterIntake > 0){
            waterIntake--;
            saveHydration(-1 * 0.3f);

            if (waterIntake==0){
                imageButtonReduceWater.setEnabled(false);

            }

        }else {
            waterIntake=0;
        }
    }
    /**
     * Build a {@link GoogleApiClient} to authenticate the user and allow the application
     * to connect to the Fitness APIs. The included scopes should match the scopes needed
     * by your app (see the documentation for details).
     * Use the {@link GoogleApiClient.OnConnectionFailedListener}
     * to resolve authentication failures (for example, the user has not signed in
     * before, or has multiple accounts and must specify which account to use).
     */
    private void buildFitnessClient() {
        // Create the Google API Client
        mClient = new GoogleApiClient.Builder(getContext())
                .addApi(Fitness.RECORDING_API)
                .addApi(Fitness.HISTORY_API)
                .addApi(Fitness.SESSIONS_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_NUTRITION_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_LOCATION_READ_WRITE))
                .addConnectionCallbacks(
                        new GoogleApiClient.ConnectionCallbacks() {

                            @Override
                            public void onConnected(Bundle bundle) {
                                Log.i(TAG, "Connected!!!");
                                // Now you can make calls to the Fitness APIs.  What to do?
                                // Subscribe to some data sources!
                                subscribe();
                            }

                            @Override
                            public void onConnectionSuspended(int i) {
                                // If your connection to the sensor gets lost at some point,
                                // you'll be able to determine the reason and react to it here.
                                if (i == ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                    Log.w(TAG, "Connection lost.  Cause: Network Lost.");
                                } else if (i == ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                    Log.w(TAG, "Connection lost.  Reason: Service Disconnected");
                                }
                            }
                        }
                )
                .enableAutoManage(getActivity(),  new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.w(TAG, "Google Play services connection failed. Cause: " +
                                result.toString());
                    }
                })
                .build();
    }

    /**
     * Record step data by requesting a subscription to background step data.
     */
    @SuppressLint("StaticFieldLeak")
    public void subscribe() {
        // To create a subscription, invoke the Recording API. As soon as the subscription is
        // active, fitness data will start recording.
        Fitness.RecordingApi.subscribe(mClient, DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            if (status.getStatusCode()
                                    == FitnessStatusCodes.SUCCESS_ALREADY_SUBSCRIBED) {
                                Log.i(TAG, "Existing subscription for activity detected.");
                            } else {
                                Log.i(TAG, "Successfully subscribed!");
                            }
                        } else {
                            Log.w(TAG, "There was a problem subscribing.");
                        }
                    }
                });

        //start here

        //get total steps count
        new FetchStepsCountAsync(mClient, getContext(), new StepCounterInterface() {
            @Override
            public void getSteps(long totalDailySteps) {
                textViewStepsCount.setText(String.valueOf(totalDailySteps));
                //1.31233595801 steps =  0.001 kilometers
                String distance = String.valueOf (( totalDailySteps * 1.31233595801) /0.001)
                        +" " + getString(R.string.fragment_home_diet_plan_km_text);

                textViewStepsDistance.setText(distance);
            }
        }).execute();

        //get calories count
        new FetchCaloriesAsync(mClient, new CaloriesInterface() {
            @Override
            public void getCalories(float totalDailySteps) {
                textViewStepsCalorieBurned.setText(String.valueOf(totalDailySteps)
                        +" " +getString(R.string.fragment_home_diet_plan_kcal_text));
            }
        }).execute();


        saveNutrition();
        fetchHydration();


    }

    private void fetchHydration(){
        new FetchHydrationAsync(mClient, new HydrationInterface() {
            @Override
            public void getHydrationData(float totalHydration) {
                //watercount *
                waterIntake=(int)(totalHydration/0.3f);
                if (waterIntake>0){
                    imageButtonReduceWater.setEnabled(true);
                }
                textViewWaterIntakeInLiter.setText("= " + String.format(Locale.FRANCE,"%.1f",waterIntake * 0.3f) + " liter");

                String waterCount = String.format(Locale.FRANCE,"%02d", (int)(totalHydration/0.3f));

                textViewWaterIntake.setText(waterCount);
            }
        }).execute();

    }

    private void saveNutrition(){
        //save nutrition data
        DataSource nutritionSource = new DataSource.Builder()
                .setDataType(DataType.TYPE_NUTRITION)
                .setAppPackageName(getContext())
                .setType(DataSource.TYPE_RAW)
                .build();

        DataPoint banana = DataPoint.create(nutritionSource);
        banana.setTimestamp(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        banana.getValue(Field.FIELD_FOOD_ITEM).setString("banana");
        banana.getValue(Field.FIELD_MEAL_TYPE).setInt(Field.MEAL_TYPE_SNACK);
        banana.getValue(Field.FIELD_NUTRIENTS).setKeyValue(Field.NUTRIENT_TOTAL_FAT, 0.4f);
        banana.getValue(Field.FIELD_NUTRIENTS).setKeyValue(Field.NUTRIENT_SODIUM, 1f);

        DataSet dataSet = DataSet.create(nutritionSource);
        dataSet.add(banana);
        PendingResult<Status> result = Fitness.HistoryApi.insertData(mClient, dataSet);

        //get nutrition data

        new FetchNutritionAsync(mClient).execute();
    }
    /**
     *
     * @param waterIntakeLiter the quantity o water intake in liter
     */
    private void saveHydration(final float waterIntakeLiter){
        //save hydration
        DataSource hydrationSource = new DataSource.Builder()
                .setDataType(DataType.TYPE_HYDRATION)
                .setType(DataSource.TYPE_RAW)
                .setAppPackageName(getContext())

                .build();

        DataPoint hydration = DataPoint.create(hydrationSource);
        hydration.setTimestamp(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        hydration.getValue(FIELD_VOLUME).setFloat(waterIntakeLiter);

        DataSet dataSet = DataSet.create(hydrationSource);
        dataSet.add(hydration);
        PendingResult<Status> result = Fitness.HistoryApi.insertData(mClient, dataSet);
        //get hydration data
       fetchHydration();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        buildFitnessClient();

    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mClient != null && mClient.isConnected()) {
            mClient.disconnect();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        mClient.stopAutoManage(getActivity());
        mClient.disconnect();
    }
}
