package com.bricenangue.insyconn.ki_ki;


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
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DataSourcesResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
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
public class FragmentHome extends Fragment implements  View.OnClickListener {

    private TextView textViewWaterIntake, textViewStepsCount, textViewStepsGoal, textViewStepsDistance
            ,textViewStepstime, textViewStepsCalorieBurned;
    private ImageButton imageButtonAddWater, imageButtonReduceWater;
    private int waterIntake=0;
    FirebaseUser firebaseUser;
    FirebaseAuth auth;
    DatabaseReference databaseReference;
    UserSharedPreference userSharedPreference;

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

        imageButtonReduceWater.setOnClickListener(this);
        imageButtonAddWater.setOnClickListener(this);
        if (waterIntake==0){
            imageButtonReduceWater.setEnabled(false);
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
                textViewWaterIntake.setText(waterCount);
                imageButtonReduceWater.setEnabled(true);
                break;
            case R.id.imageButton_homeFragment_reduce_water:
                if (waterIntake > 0){
                    waterIntake--;
                    if (waterIntake==0){
                        imageButtonReduceWater.setEnabled(false);

                    }
                }else {
                    waterIntake=0;
                }

                 waterCount = String.format(Locale.FRANCE,"%02d", waterIntake);
                textViewWaterIntake.setText(waterCount);
                break;
        }
    }


}
