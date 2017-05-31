package com.bricenangue.insyconn.ki_ki;


import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.StringDef;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.DecimalFormat;


public class FragmentMyKiKi extends Fragment implements View.OnClickListener {


    private Button btn_size, btn_age, btn_gender, btn_weight;
    private TextView username, activities_description, calorie_intake;
    private ImageButton btn_Activity_1, btn_Activity_2, btn_Activity_3, btn_Activity_4;
    private ImageView user_profile;
    private FirebaseUser user;
    private FirebaseAuth auth;
    private ProgressDialog progressBar;
    private double age=0, size=0, weight =0;
    private boolean gender; // true for female , false for male
    private int level;



    public FragmentMyKiKi() {
        // Required empty public constructor
    }


    public static FragmentMyKiKi newInstance() {
        return new FragmentMyKiKi();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_age:
                break;
            case R.id.button_size:
                break;
            case R.id.button_gender:
                break;
            case R.id.button_weight:
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

    private void markAsSelected(int i){
        String kilocal="";
        switch (i){
            case 1:
                btn_Activity_4.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_lifting_grey));
                btn_Activity_3.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_runner_grey));
                btn_Activity_2.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_squat_grey));
                btn_Activity_1.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_walking));
                activities_description.setText(getString(R.string.activities_description_1));
                 kilocal =calcuteBMR(179,24,71.4,false,1)+" "+ getString(R.string.kilocalories);

                calorie_intake.setText(kilocal);
                break;
            case 2:
                btn_Activity_4.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_lifting_grey));
                btn_Activity_3.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_runner_grey));
                btn_Activity_2.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_squat));
                btn_Activity_1.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_walking_grey));
                activities_description.setText(getString(R.string.activities_description_2));
                kilocal = calcuteBMR(size,age,weight,gender,2)+" "+ getString(R.string.kilocalories);
                calorie_intake.setText( kilocal);
                break;
            case 3:
                btn_Activity_4.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_lifting_grey));
                btn_Activity_3.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_runner));
                btn_Activity_2.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_squat_grey));
                btn_Activity_1.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_walking_grey));
                activities_description.setText(getString(R.string.activities_description_3));
                kilocal =calcuteBMR(size,age,weight,gender,3)+" "+ getString(R.string.kilocalories);
                calorie_intake.setText(kilocal);
                break;
            case 4:
                btn_Activity_4.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_lifting));
                btn_Activity_3.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_runner_grey));
                btn_Activity_2.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_squat_grey));
                btn_Activity_1.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_walking_grey));
                activities_description.setText(getString(R.string.activities_description_4));
                kilocal =calcuteBMR(size,age,weight,gender,4)+" "+ getString(R.string.kilocalories);
                calorie_intake.setText(kilocal);
                break;
            default:
                btn_Activity_4.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_lifting_grey));
                btn_Activity_3.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_runner_grey));
                btn_Activity_2.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_squat_grey));
                btn_Activity_1.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_walking_grey));
                activities_description.setText(getString(R.string.textView_mykiki_myactivities_description));
                kilocal ="0 "+ getString(R.string.kilocalories);
                calorie_intake.setText(kilocal);

        }
    }

    private String calcuteBMR(double size, double age, double weight, boolean gender, int level ){
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
        double BMR = 0;
        if (gender){
            BMR = (655 + (9.5 * weight) + (1.9 * size) - (4.7 * age))* activity_level ;
        }else {
            BMR = (66 + (13.8 * weight) + (5.0 * size) - (6.8 * age)) * activity_level;
        }
        return new DecimalFormat("#.##").format(BMR);
    }
}
