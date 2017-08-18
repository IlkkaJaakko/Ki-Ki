package com.bricenangue.insyconn.ki_ki.fragments;


import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.bricenangue.insyconn.ki_ki.Models.UserPersonalData;
import com.bricenangue.insyconn.ki_ki.R;
import com.bricenangue.insyconn.ki_ki.UserSharedPreference;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class FragmentMyKiKi extends Fragment implements View.OnClickListener
, MyDialogFragment.UserNameListener, DatePickerDialog.OnDateSetListener{

    public static final String TAG = "GoogleFit";

    private Button btn_size, btn_age, btn_gender, btn_weight;
    private TextView username, activities_description, calorie_intake, calorie_loose_weight;
    private ImageButton btn_Activity_1, btn_Activity_2, btn_Activity_3, btn_Activity_4;
    private ImageView user_profile;
    private FirebaseUser user;
    private FirebaseAuth auth;
    private ProgressDialog progressBar;
    private double  size=0.00, weight =0.00;
    private boolean gender; // true for female , false for male
    private int level, age=0;
    private UserPersonalData userPersonalData;
    private UserSharedPreference userSharedPreference;

    private GoogleApiClient mClient = null;

    public FragmentMyKiKi() {
        // Required empty public constructor
    }


    public static FragmentMyKiKi newInstance() {
        return new FragmentMyKiKi();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userSharedPreference =new UserSharedPreference(getActivity());
        auth=FirebaseAuth.getInstance();
        if (auth!=null){
            user=auth.getCurrentUser();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view= inflater.inflate(R.layout.fragment_mykiki, container, false);
        btn_age = (Button)view.findViewById(R.id.button_age);
        btn_size = (Button)view.findViewById(R.id.button_size);
        btn_gender = (Button)view.findViewById(R.id.button_gender);
        btn_weight = (Button)view.findViewById(R.id.button_weight);

        btn_Activity_1 = (ImageButton)view.findViewById(R.id.imageView_activity_level_1);
        btn_Activity_2 = (ImageButton)view.findViewById(R.id.imageView_activity_level_2);
        btn_Activity_3 = (ImageButton)view.findViewById(R.id.imageView_activity_level_3);
        btn_Activity_4 = (ImageButton)view.findViewById(R.id.imageView_activity_level_4);

        username = (TextView)view.findViewById(R.id.textView_mykiki_username);
        activities_description = (TextView)view.findViewById(R.id.textView_myactivities_description);
        calorie_intake = (TextView)view.findViewById(R.id.textView_recommended_calories);
        calorie_loose_weight = (TextView)view.findViewById(R.id.textView_calories_to_loose_weight);

        user_profile=(ImageView)view.findViewById(R.id.imageView_mykiki_userpic);

        btn_Activity_4.setOnClickListener(this);
        btn_Activity_2.setOnClickListener(this);
        btn_Activity_3.setOnClickListener(this);
        btn_Activity_1.setOnClickListener(this);
        user_profile.setOnClickListener(this);
        btn_weight.setOnClickListener(this);
        btn_gender.setOnClickListener(this);
        btn_size.setOnClickListener(this);
        btn_age.setOnClickListener(this);



        return view;
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
    public void onPause() {
       
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
                                }

                                @Override
                                public void onConnectionSuspended(int i) {
                                    // If your connection to the sensor gets lost at some point,
                                    // you'll be able to determine the reason and react to it here.
                                    if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                        Log.w(TAG, "Connection lost.  Cause: Network Lost.");
                                    } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
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

    @Override
    public void onClick(View view) {
        // close existing dialog fragments
        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag("fragment_edit_name");
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }
        switch (view.getId()){
            case R.id.button_age:

                pickAge();
                break;
            case R.id.button_size:
                pickSize();
                break;
            case R.id.button_gender:
                MyDialogFragment alertFragment = new MyDialogFragment();
                alertFragment.setTargetFragment(FragmentMyKiKi.this, 206);
                alertFragment.show(manager, "fragment_edit_name");
                break;
            case R.id.button_weight:
                pickWeight();
                break;
            case R.id.imageView_activity_level_1:
               markAsSelected(1);
                break;
            case R.id.imageView_activity_level_2:
                markAsSelected(2);
                break;
            case R.id.imageView_activity_level_3:
                markAsSelected(3);
                break;
            case R.id.imageView_activity_level_4:
                markAsSelected(4);
                break;
            case R.id.imageView_mykiki_userpic:
                break;


        }
    }

    private void initializedUserdata(){

        userPersonalData= userSharedPreference.getPersonalData();

        DatabaseReference refUser= FirebaseDatabase.getInstance().getReference()
                .child("UserPersonalData")
                .child(user.getUid());

            if (userPersonalData.getAge()==0 || userPersonalData.getSize() ==0 ){

                // check on the server set all default
                refUser.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot!=null){
                            level = dataSnapshot.child("activity_level").getValue(Integer.class) !=null
                                    ? dataSnapshot.child("activity_level").getValue(Integer.class): 0;

                            age = dataSnapshot.child("age").getValue(Integer.class) !=null
                            ? dataSnapshot.child("age").getValue(Integer.class) : 0;

                            gender = dataSnapshot.child("gender").getValue(Boolean.class)!=null
                                    ? dataSnapshot.child("gender").getValue(Boolean.class) : false;

                            size = dataSnapshot.child("size").getValue(Double.class)!=null
                                    ? dataSnapshot.child("size").getValue(Integer.class) : 0;

                            weight = dataSnapshot.child("weight").getValue(Double.class)!=null
                                    ? dataSnapshot.child("weight").getValue(Integer.class) : 0;

                            userPersonalData.setActivity_level(level);
                            userPersonalData.setGender(gender);
                            userPersonalData.setAge(age);
                            userPersonalData.setWeight(weight);
                            userPersonalData.setSize(size);

                            userSharedPreference.storePersonalData(userPersonalData);
                            setButtonsText(gender,weight,size,age,level);


                        }else {
                            //  set all default
                            btn_Activity_4.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_lifting_grey));
                            btn_Activity_3.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_runner_grey));
                            btn_Activity_2.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_squat_grey));
                            btn_Activity_1.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_walking_grey));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



            }else {

                gender=userPersonalData.isGender();
                size =userPersonalData.getSize();
                weight = userPersonalData.getWeight();
                age = userPersonalData.getAge();
                level=userPersonalData.getActivity_level();

                setButtonsText(gender,weight,size,age,level);

            }


    }

    private void setButtonsText (boolean gender, double weight, double size, int age, int level){
        btn_size.setText(String.valueOf(size)+ " cm");
        btn_weight.setText(String.valueOf(weight)+ " Kg");
        btn_age.setText(String.valueOf(age)+ " ans");
        if (gender){
            // it is a female
            btn_gender.setText(getString(R.string.isAFemale));

        }else {
            // it is a male
            btn_gender.setText(getString(R.string.isMale));
        }
        markAsSelected(level);

    }

    @Override
    public void onResume() {
        super.onResume();
        buildFitnessClient();
    }


    @Override
    public void onStart() {
        super.onStart();
        DatabaseReference refUser= FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(user.getUid())
                .child("userPublic");
        refUser.child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                username.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                username.setText(getString(R.string.unknown_unsername));
            }
        });


        refUser.child("profilePhotoUri").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                loadImage(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //username.setText(getString(R.string.unknown_unsername));
            }
        });
        initializedUserdata();


    }


    private void loadImage(final String value) {
        if (value!=null && !value.isEmpty()){
            Picasso.with(getContext()).load(value).networkPolicy(NetworkPolicy.OFFLINE)
                    .fit().centerInside()
                    .into(user_profile, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(getContext()).load(value)
                                    .fit().centerInside().into(user_profile);


                        }
                    });
        }

    }

    private void markAsSelected(int i){
        String kilocal="";
        String kilocalLooseWeight="";
        userPersonalData.setActivity_level(i);
        savePersonalData(userPersonalData);
        switch (i){
            case 1:
                btn_Activity_4.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_lifting_grey));
                btn_Activity_3.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_runner_grey));
                btn_Activity_2.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_squat_grey));
                btn_Activity_1.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_walking));
                activities_description.setText(getString(R.string.activities_description_1));
                level=1;
                 kilocal = new DecimalFormat("#.##").format(calcuteBMR(size,age,weight,gender,level))
                         +" "+ getString(R.string.kilocalories);

                kilocalLooseWeight = new DecimalFormat("#.##")
                        .format(calcuteBMR(size,age,weight,gender,level)-(calcuteBMR(size,age,weight,gender,level) * 0.35))
                        +" "+ getString(R.string.kilocalories);

                calorie_loose_weight.setText(kilocalLooseWeight);
                calorie_intake.setText(kilocal);
                break;
            case 2:
                btn_Activity_4.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_lifting_grey));
                btn_Activity_3.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_runner_grey));
                btn_Activity_2.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_squat));
                btn_Activity_1.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_walking_grey));
                activities_description.setText(getString(R.string.activities_description_2));
                level=2;
                kilocal = new DecimalFormat("#.##").format(calcuteBMR(size,age,weight,gender,level))
                        +" "+ getString(R.string.kilocalories);
                kilocalLooseWeight = new DecimalFormat("#.##")
                        .format(calcuteBMR(size,age,weight,gender,level)-(calcuteBMR(size,age,weight,gender,level) * 0.35))
                        +" "+ getString(R.string.kilocalories);
                calorie_loose_weight.setText(kilocalLooseWeight);

                calorie_intake.setText( kilocal);
                break;
            case 3:
                btn_Activity_4.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_lifting_grey));
                btn_Activity_3.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_runner));
                btn_Activity_2.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_squat_grey));
                btn_Activity_1.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_walking_grey));
                activities_description.setText(getString(R.string.activities_description_3));
                level=3;
                kilocal =new DecimalFormat("#.##").format(calcuteBMR(size,age,weight,gender,level))
                        +" "+ getString(R.string.kilocalories);
                kilocalLooseWeight = new DecimalFormat("#.##")
                        .format(calcuteBMR(size,age,weight,gender,level)-(calcuteBMR(size,age,weight,gender,level) * 0.35))
                        +" "+ getString(R.string.kilocalories);
                calorie_loose_weight.setText(kilocalLooseWeight);
                calorie_intake.setText(kilocal);
                break;
            case 4:
                btn_Activity_4.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_lifting));
                btn_Activity_3.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_runner_grey));
                btn_Activity_2.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_squat_grey));
                btn_Activity_1.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_walking_grey));
                activities_description.setText(getString(R.string.activities_description_4));
                level=4;
                kilocal = new DecimalFormat("#.##").format(calcuteBMR(size,age,weight,gender,level))
                        +" "+ getString(R.string.kilocalories);
                kilocalLooseWeight = new DecimalFormat("#.##")
                        .format(calcuteBMR(size,age,weight,gender,level)-(calcuteBMR(size,age,weight,gender,level) * 0.35))
                        +" "+ getString(R.string.kilocalories);
                calorie_loose_weight.setText(kilocalLooseWeight);

                calorie_intake.setText(kilocal);
                break;
            default:
                btn_Activity_4.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_lifting_grey));
                btn_Activity_3.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_runner_grey));
                btn_Activity_2.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_squat_grey));
                btn_Activity_1.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_walking_grey));
                activities_description.setText(getString(R.string.textView_mykiki_myactivities_description));
                kilocal ="0 "+ getString(R.string.kilocalories);
                level=0;
                calorie_loose_weight.setText(kilocal);
                calorie_intake.setText(kilocal);

        }
    }




    private double calcuteBMR(double size, double age, double weight, boolean gender, int level ){
        double activity_level=0;
        switch (level){
            case 1:
                activity_level=1.5;
                break;
            case 2:
                activity_level=1.7;
                break;
            case 3:
                activity_level=1.9;
                break;
            case 4:
                activity_level=2.4;
                break;
            default:
                activity_level= 0.95;

        }
        /**
         * Frau: 655 + (9,5 x Gewicht in kg) + (1,9 x Größe in cm) – (4,7 x Alter in Jahren)

         Mann: 66 + (13,8 x Gewicht in kg) + (5,0 x Größe in cm) – (6,8 x Alter in Jahren)
         */
        double BMR = 0;
        if (gender){
            BMR = (655 + (9.5 * weight) + (1.9 * size) - (4.7 * age))* activity_level ;
        }else {
            BMR = (66 + (13.8 * weight) + (5.0 * size) - (6.8 * age)) * activity_level;
        }
        return BMR;
    }

    @Override
    public void onFinishUserDialog(boolean gender) {
        if (gender){
            // it is a female
            btn_gender.setText(getString(R.string.isAFemale));

        }else {
            // it is a male
            btn_gender.setText(getString(R.string.isMale));
        }
        this.gender=gender;
        userPersonalData.setGender(gender);
        savePersonalData(userPersonalData);
        markAsSelected(level);
    }



    private void pickAge(){

        Calendar myCalendar = Calendar.getInstance();

        new DatePickerDialog(getContext(), this, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();

    }

    private void pickWeight(){
        InputMethodManager inputMethodManager =
                (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(
                btn_age.getApplicationWindowToken(),
                InputMethodManager.SHOW_FORCED, 0);

        final NumberPicker picker = new NumberPicker(getActivity());
        picker.setMinValue(60);
        picker.setMaxValue(350);

        final FrameLayout layout = new FrameLayout(getActivity());
        layout.addView(picker, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER));

        new AlertDialog.Builder(getActivity())
                .setView(layout)
                .setPositiveButton(R.string.setAge, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // do something with picker.getValue()
                        btn_weight.setText(String.valueOf(picker.getValue())+ " Kg");

                        weight=picker.getValue();
                        userPersonalData.setWeight((float) weight);
                        saveUserWeight((float) weight);
                        savePersonalData(userPersonalData);
                        markAsSelected(level);
                    }
                })
                .show();
    }

    private void pickSize(){
        InputMethodManager inputMethodManager =
                (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(
                btn_age.getApplicationWindowToken(),
                InputMethodManager.SHOW_FORCED, 0);

        final NumberPicker picker = new NumberPicker(getActivity());
        picker.setMinValue(100);
        picker.setMaxValue(290);

        final FrameLayout layout = new FrameLayout(getActivity());
        layout.addView(picker, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER));

        new AlertDialog.Builder(getActivity())
                .setView(layout)
                .setPositiveButton(R.string.setAge, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // do something with picker.getValue()
                        btn_size.setText(String.valueOf(picker.getValue())+ " cm");

                        size=picker.getValue();
                        userPersonalData.setSize((float) size);
                        saveUserHeight((float) size);
                        savePersonalData(userPersonalData);
                        markAsSelected(level);
                    }
                })
                .show();
    }

    private void savePersonalData(UserPersonalData userPersonalData){
        //save localy
        userSharedPreference.storePersonalData(userPersonalData);


        //save in firebase
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("UserPersonalData")
                .child(user.getUid());

        reference.setValue(userPersonalData);


    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) { //year, month,

        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRANCE);
        Calendar myCalendar = Calendar.getInstance();
        int current_year = myCalendar.get(Calendar.YEAR);
        int current_month = myCalendar.get(Calendar.MONTH);
        int current_day = myCalendar.get(Calendar.DAY_OF_MONTH);

        if ((current_month + 1) > (month+1)){
            age =current_year - year - 1;
        }else if ((current_month +1) < (month+1)){
            age = current_year - year;
        }else if ((current_month +1) == (month+1)){
            if (current_day >= day){
                age=current_year - year;
            }else {
                age=current_year - year - 1;
            }
        }

        btn_age.setText(String.valueOf(age)+ " ans");

        userPersonalData.setAge(age);
        savePersonalData(userPersonalData);
        markAsSelected(level);
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
