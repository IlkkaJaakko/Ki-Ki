package com.bricenangue.insyconn.ki_ki;


import android.os.Bundle;
import android.support.annotation.StringDef;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentHome#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentHome extends Fragment implements View.OnClickListener {


    private TextView textViewWaterIntake, textViewStepsCount, textViewStepsGoal, textViewStepsDistance
            ,textViewStepstime, textViewStepsCalorieBurned;
    private ImageButton imageButtonAddWater, imageButtonReduceWater;
    private int waterIntake=0;
    FirebaseUser firebaseUser;
    FirebaseAuth auth;
    DatabaseReference databaseReference;
    UserSharedPreference userSharedPreference;
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);
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
