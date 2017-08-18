package com.bricenangue.insyconn.ki_ki.fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bricenangue.insyconn.ki_ki.Models.UserPersonalData;
import com.bricenangue.insyconn.ki_ki.R;
import com.bricenangue.insyconn.ki_ki.UserSharedPreference;
import com.bricenangue.insyconn.ki_ki.asynctasks.FetchCaloriesAsync;
import com.bricenangue.insyconn.ki_ki.asynctasks.FetchHydrationAsync;
import com.bricenangue.insyconn.ki_ki.asynctasks.FetchNutritionAsync;
import com.bricenangue.insyconn.ki_ki.asynctasks.FetchStepsCountAsync;
import com.bricenangue.insyconn.ki_ki.asynctasks.GetCaloriesAsyncTask;
import com.bricenangue.insyconn.ki_ki.interfaceClasses.CaloriesInterface;
import com.bricenangue.insyconn.ki_ki.interfaceClasses.HydrationInterface;
import com.bricenangue.insyconn.ki_ki.interfaceClasses.NutritionInterface;
import com.bricenangue.insyconn.ki_ki.interfaceClasses.StepCounterInterface;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessActivities;
import com.google.android.gms.fitness.HistoryApi;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DataReadResult;
import com.google.android.gms.fitness.result.DataSourcesResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static com.google.android.gms.fitness.data.Field.FIELD_VOLUME;

import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;

import com.google.android.gms.fitness.FitnessStatusCodes;
import com.google.android.gms.fitness.data.DataSet;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentHome#newInstance} factory method to
 * create an instance of this fragment.
 * the Fitness API should be implemented here to show daily step recording and hydration recording and also return daily data
 */
public class FragmentHome extends Fragment implements  View.OnClickListener{

    private TextView textViewWaterIntake, textViewStepsCount, textViewStepsGoal, textViewStepsDistance
            ,textViewStepstime, textViewStepsCalorieBurned, textViewWaterIntakeInLiter, textViewCarbIntake
            , textViewProteinIntake, textViewFatIntake;
    private ImageButton imageButtonAddWater, imageButtonReduceWater,imageButtonCheckMeal;
    private AdView mAdView;
    private int waterIntake=0;
    FirebaseUser firebaseUser;
    FirebaseAuth auth;
    DatabaseReference databaseReference;
    UserSharedPreference userSharedPreference;
    private VideoController mVideoController;

    public static final String TAG = "GoogleFit";
    private GoogleApiClient mClient = null;
    private Session mSession;
    private final String SESSION_NAME = "dummy session";
    private double weight, size;

    private View  view;
    private OnDataPointListener mListener;
    private static String LOG_TAG = "EXAMPLE";
    private SimpleDateFormat  originalFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);




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
        MobileAds.initialize(getContext(), getContext().getString(R.string.app_ad_mob_id));

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         view = inflater.inflate(R.layout.fragment_home, container, false);
        imageButtonAddWater=(ImageButton) view.findViewById(R.id.imageButton_homeFragment_add_water);
        imageButtonReduceWater=(ImageButton)view.findViewById(R.id.imageButton_homeFragment_reduce_water);
        imageButtonCheckMeal =(ImageButton)view.findViewById(R.id.fragment_home_diet_plan_next_meal_check);

        textViewStepsCalorieBurned=(TextView)view.findViewById(R.id.textView_homeFragment_steps_calories_burned);
        textViewWaterIntake=(TextView)view.findViewById(R.id.textView_homeFragment_glas_water);
        textViewStepsCount=(TextView)view.findViewById(R.id.textView_homeFragment_steps_count);
        textViewStepsGoal=(TextView)view.findViewById(R.id.textView_homeFragment_steps_goal);
        textViewStepsDistance=(TextView)view.findViewById(R.id.textView_homeFragment_steps_distance);
        textViewStepstime=(TextView)view.findViewById(R.id.textView_homeFragment_steps_duration);
        textViewWaterIntakeInLiter=(TextView)view.findViewById(R.id.textView_homeFragment_water_intake_in_liter);
        textViewCarbIntake=(TextView)view.findViewById(R.id.textView_homeFragment_diet_card_value);
        textViewProteinIntake=(TextView)view.findViewById(R.id.textView_homeFragment_protein_value);
        textViewFatIntake=(TextView)view.findViewById(R.id.textView_homeFragment_fat_value);


        imageButtonReduceWater.setOnClickListener(this);
        imageButtonAddWater.setOnClickListener(this);
        imageButtonCheckMeal.setOnClickListener(this);
        if (waterIntake==0){
            imageButtonReduceWater.setEnabled(false);
            textViewWaterIntakeInLiter.setText("= " +String.valueOf(waterIntake * 0.3f) + " liter");
        }


        mAdView = (AdView) view.findViewById(R.id.adView);

        /*
        mAdView.setVideoOptions(new VideoOptions.Builder()
                .setStartMuted(true)
                .build());

        mVideoController = mAdView.getVideoController();
        mVideoController.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
            @Override
            public void onVideoEnd() {
                Log.d(LOG_TAG, "Video playback is finished.");
                super.onVideoEnd();
            }
        });

        // Set an AdListener for the AdView, so the Activity can take action when an ad has finished
        // loading.
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                if (mVideoController.hasVideoContent()) {
                    Log.d(LOG_TAG, "Received an ad that contains a video asset.");
                } else {
                    Log.d(LOG_TAG, "Received an ad that does not contain a video asset.");
                }
            }
        });

        mAdView.loadAd(new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                // Check the LogCat to get your test device ID
                .addTestDevice("CABE77E1D702B78A66603DA3FF8ACDAC")
                .build());

        */


        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                // Check the LogCat to get your test device ID
                .addTestDevice("CABE77E1D702B78A66603DA3FF8ACDAC")
                .build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Toast.makeText(getContext(), "Ad is loaded!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdClosed() {
                Toast.makeText(getContext(), "Ad is closed!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Toast.makeText(getContext(), "Ad failed to load! error code: " + errorCode, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLeftApplication() {
                Toast.makeText(getContext(), "Ad left application!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdOpened() {
                Toast.makeText(getContext(), "Ad is opened!", Toast.LENGTH_SHORT).show();
            }
        });



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
                saveHydration(1 * 0.3f);
                imageButtonReduceWater.setEnabled(true);

                break;
            case R.id.imageButton_homeFragment_reduce_water:
                // waterinter * 0.3 l
               checkWaterIntake();


                break;
            case R.id.fragment_home_diet_plan_next_meal_check:

                imageButtonCheckMeal.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_check_circle_black_24dp));
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
                .addApi(Fitness.SENSORS_API)
                .addApi(Fitness.RECORDING_API)
                .addApi(Fitness.HISTORY_API)
                .addApi(Fitness.SESSIONS_API)
                .addScope(new Scope(Scopes.FITNESS_BODY_READ_WRITE))
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
                               Calendar c = Calendar.getInstance();
                                SimpleDateFormat  dateformat = new SimpleDateFormat("yyyy-MM-dd");
                               String  selectedDate =    dateformat.format(c.getTime());
                                fetchUserGoogleFitData(selectedDate);
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

        Fitness.SensorsApi.findDataSources(mClient, new
                DataSourcesRequest.Builder()
                // At least one datatype must be specified.

                .setDataTypes(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                // Can specify whether data type is raw or derived.
                .setDataSourceTypes(DataSource.TYPE_DERIVED)
                .build())
                .setResultCallback(new ResultCallback<DataSourcesResult>() {
                    @Override
                    public void onResult(DataSourcesResult dataSourcesResult) {
                        Log.i(TAG, "Result: " +
                                dataSourcesResult.getStatus().toString());
                        for (DataSource dataSource :
                                dataSourcesResult.getDataSources()) {
                            Log.i(TAG, "Data source found: " +
                                    dataSource.toString());
                            Log.i(TAG, "Data Source type: " +
                                    dataSource.getDataType().getName());


                            if
                                    (dataSource.getDataType().equals(DataType.TYPE_STEP_COUNT_DELTA)
                                    && mListener == null) {
                                Log.i(TAG, "Data source for STEP_COUNT_DELTA found!  Registering.");
                                registerFitnessDataListener(dataSource,
                                        DataType.TYPE_STEP_COUNT_CUMULATIVE);
                            }
                        }
                    }
                });
        // [END find_data_sources]

        //get total steps count

        new FetchStepsCountAsync(mClient, getContext(), new StepCounterInterface() {
            @Override
            public void getSteps(long totalDailySteps) {
                textViewStepsCount.setText(String.valueOf(totalDailySteps));
                //1.31233595801 steps =  0.001 kilometers
                // 1steps = 0,761999999997242 meter = 0.000761999999997242 km
                String distance = String.format(Locale.FRANCE,"%.2f",( totalDailySteps * 0.000761999999997242))
                        +" " + getString(R.string.fragment_home_diet_plan_km_text);

                textViewStepsDistance.setText(distance);
            }
        }).execute();


        //get calories count
        new FetchCaloriesAsync(mClient, new CaloriesInterface() {
            @Override
            public void getCalories(float totalDailySteps) {
                DecimalFormat df = new DecimalFormat("#.##");
                String formatted = df.format(totalDailySteps);

                textViewStepsCalorieBurned.setText(formatted
                        +" " +getString(R.string.fragment_home_diet_plan_kcal_text));
            }
        }).execute();


        //saveNutrition();
        fetchHydration();


        UserPersonalData personalData =userSharedPreference.getPersonalData();
        if (personalData!=null){

            weight =personalData.getWeight();
            size = personalData.getSize();

            //saveUserHeight((float) size);
           // saveUserWeight((float) weight);
        }
    }


    public void fetchUserGoogleFitData(String date) {
        if (mClient != null && mClient.isConnected() && mClient.hasConnectedApi(Fitness.HISTORY_API)) {


            Date d1 = null;
            try {
                d1 = originalFormat.parse(date);
            } catch (Exception ignored) {

            }
            Calendar calendar = Calendar.getInstance();

            try {
                calendar.setTime(d1);
            } catch (Exception e) {
                calendar.setTime(new Date());
            }


            DataReadRequest readRequest = queryDateFitnessData(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            new GetCaloriesAsyncTask(readRequest, mClient).execute();


            Log.d("HistoryAPI", "Connected");

        }else{


            Log.d("HistoryAPI", "Not connected");


        }
    }

    private DataReadRequest queryDateFitnessData(int year, int month, int day_of_Month) {

        Calendar startCalendar = Calendar.getInstance(Locale.getDefault());


        startCalendar.set(Calendar.YEAR, year);
        startCalendar.set(Calendar.MONTH, month);
        startCalendar.set(Calendar.DAY_OF_MONTH, day_of_Month);

        startCalendar.set(Calendar.HOUR_OF_DAY, 23);
        startCalendar.set(Calendar.MINUTE, 59);
        startCalendar.set(Calendar.SECOND, 59);
        startCalendar.set(Calendar.MILLISECOND, 999);
        long endTime = startCalendar.getTimeInMillis();


        startCalendar.set(Calendar.HOUR_OF_DAY, 0);
        startCalendar.set(Calendar.MINUTE, 0);
        startCalendar.set(Calendar.SECOND, 0);
        startCalendar.set(Calendar.MILLISECOND, 0);
        long startTime = startCalendar.getTimeInMillis();

        DataSource ESTIMATED_STEP_DELTAS = new DataSource.Builder()
                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .setType(DataSource.TYPE_DERIVED)
                .setStreamName("estimated_steps")
                .setAppPackageName("com.google.android.gms")
                .build();

        return new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_CALORIES_EXPENDED, DataType.AGGREGATE_CALORIES_EXPENDED)
                .aggregate(ESTIMATED_STEP_DELTAS, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .bucketByActivitySegment(1, TimeUnit.MILLISECONDS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

    }




    private void registerFitnessDataListener(DataSource dataSource, DataType typeStepCountDelta) {

        mListener = new OnDataPointListener() {
            @Override
            public void onDataPoint(DataPoint dataPoint) {
                for (Field field : dataPoint.getDataType().getFields()) {
                    Value val = dataPoint.getValue(field);
                    Log.i(TAG, "Detected DataPoint field: " + field.getName());
                    Log.i(TAG, "Detected DataPoint value: " + val);
                    final int value = val.asInt();

                    if (field.getName().compareToIgnoreCase("steps") == 0) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textViewStepsCount.setText(String.valueOf(value));
                                //1.31233595801 steps =  0.001 kilometers
                                // 1steps = 0,761999999997242 meter = 0.000761999999997242 km
                                String distance = String.format(Locale.FRANCE,"%.2f",( value * 0.000761999999997242))
                                        +" " + getString(R.string.fragment_home_diet_plan_km_text);

                                textViewStepsDistance.setText(distance);
                            }
                        });
                    }
                }
            }
        };

        Fitness.SensorsApi.add(
                mClient,
                new SensorRequest.Builder()
                        .setDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                        .setSamplingRate(1, TimeUnit.SECONDS)
                        .build(),
                mListener)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            Log.i(TAG, "Sensor Listener registered!");
                        } else {
                            Log.i(TAG, "Sensor Listener not registered.");
                        }
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        buildFitnessClient();

        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
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
        final DataSource nutritionSource = new DataSource.Builder()
                .setDataType(DataType.TYPE_NUTRITION)
                .setAppPackageName(getContext())
                .setType(DataSource.TYPE_RAW)
                .build();
/*
        DataPoint banana = DataPoint.create(nutritionSource);
        banana.setTimestamp(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        banana.getValue(Field.FIELD_FOOD_ITEM).setString("banana");
        banana.getValue(Field.FIELD_MEAL_TYPE).setInt(Field.MEAL_TYPE_SNACK);
        banana.getValue(Field.FIELD_NUTRIENTS).setKeyValue(Field.NUTRIENT_TOTAL_FAT, 0.4f);
        banana.getValue(Field.FIELD_NUTRIENTS).setKeyValue(Field.NUTRIENT_SODIUM, 1f);
        banana.getValue(Field.FIELD_NUTRIENTS).setKeyValue(Field.NUTRIENT_SATURATED_FAT, 0.1f);
        banana.getValue(Field.FIELD_NUTRIENTS).setKeyValue(Field.NUTRIENT_PROTEIN, 1.3f);
        banana.getValue(Field.FIELD_NUTRIENTS).setKeyValue(Field.NUTRIENT_TOTAL_CARBS, 27.0f);
        banana.getValue(Field.FIELD_NUTRIENTS).setKeyValue(Field.NUTRIENT_CHOLESTEROL, 0.0f);
        banana.getValue(Field.FIELD_NUTRIENTS).setKeyValue(Field.NUTRIENT_CALORIES, 105.0f);
        banana.getValue(Field.FIELD_NUTRIENTS).setKeyValue(Field.NUTRIENT_SUGAR, 14.0f);
        banana.getValue(Field.FIELD_NUTRIENTS).setKeyValue(Field.NUTRIENT_DIETARY_FIBER, 3.1f);
        banana.getValue(Field.FIELD_NUTRIENTS).setKeyValue(Field.NUTRIENT_POTASSIUM, 422f);

        DataSet dataSet = DataSet.create(nutritionSource);
        dataSet.add(banana);
        PendingResult<Status> result = Fitness.HistoryApi.insertData(mClient, dataSet);

*/
        //get nutrition data

        new FetchNutritionAsync(mClient, new NutritionInterface() {
            @Override
            public void getNutritionData(Value totalNutritionData) {

               float fat = totalNutritionData.getKeyValue("fat.total");
                float protein = totalNutritionData.getKeyValue("protein");

                float carb = totalNutritionData.getKeyValue("carbs.total");

                textViewFatIntake.setText(" " + String.format(Locale.FRANCE,"%.1f", fat) +" g");
                textViewProteinIntake.setText(" " + String.format(Locale.FRANCE,"%.1f", protein) +" g");
                textViewCarbIntake.setText(" " + String.format(Locale.FRANCE,"%.1f", carb) +" g");


            }
        }).execute();
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
       // buildFitnessClient();

    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mClient!=null){

            mClient.stopAutoManage(getActivity());
            mClient.disconnect();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
        if (mClient!=null){

            mClient.stopAutoManage(getActivity());
            mClient.disconnect();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mClient!=null){

            mClient.stopAutoManage(getActivity());
            mClient.disconnect();
        }
    }

    public  void saveUserHeight(float heightCentimiters) {
        // to post data
        float height = heightCentimiters / 100.0f;
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();

        DataSet heightDataSet = createDataForRequest(
                DataType.TYPE_HEIGHT,    // for height, it would be DataType.TYPE_HEIGHT
                DataSource.TYPE_RAW,
                height,                  // weight in kgs
                startTime,              // start time
                endTime,                // end time
                TimeUnit.MILLISECONDS                // Time Unit, for example, TimeUnit.MILLISECONDS
        );

        PendingResult<Status> heightInsertStatus =
                Fitness.HistoryApi.insertData(mClient, heightDataSet);
    }


    public  void saveUserWeight(float weight) {
        // to post data
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();

        DataSet weightDataSet = createDataForRequest(
                DataType.TYPE_WEIGHT,    // for height, it would be DataType.TYPE_HEIGHT
                DataSource.TYPE_RAW,
                weight,                  // weight in kgs
                startTime,              // start time
                endTime,                // end time
                TimeUnit.MILLISECONDS                // Time Unit, for example, TimeUnit.MILLISECONDS
        );

        PendingResult<Status>  weightInsertStatus =
                Fitness.HistoryApi.insertData(mClient, weightDataSet);
    }

    public DataSet createDataForRequest(DataType dataType,
                                               int dataSourceType,
                                               Object values,
                                               long startTime,
                                               long endTime,
                                               TimeUnit timeUnit) {
        DataSource dataSource = new DataSource.Builder()
                .setAppPackageName(getContext())
                .setDataType(dataType)
                .setType(dataSourceType)
                .build();

        DataSet dataSet = DataSet.create(dataSource);
        DataPoint dataPoint = dataSet.createDataPoint().setTimeInterval(startTime, endTime, timeUnit);

        if (values instanceof Integer) {
            dataPoint = dataPoint.setIntValues((Integer) values);
        } else {
            dataPoint = dataPoint.setFloatValues((Float) values);
        }

        dataSet.add(dataPoint);

        return dataSet;
    }
}
