package com.bricenangue.insyconn.ki_ki;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.StringDef;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
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
import java.util.Locale;


public class FragmentMyKiKi extends Fragment implements View.OnClickListener
, MyDialogFragment.UserNameListener, DatePickerDialog.OnDateSetListener{


    private Button btn_size, btn_age, btn_gender, btn_weight;
    private TextView username, activities_description, calorie_intake, calorie_loose_weight;
    private ImageButton btn_Activity_1, btn_Activity_2, btn_Activity_3, btn_Activity_4;
    private ImageView user_profile;
    private FirebaseUser user;
    private FirebaseAuth auth;
    private ProgressDialog progressBar;
    private double  size=0, weight =0;
    private boolean gender; // true for female , false for male
    private int level, age=0;
    private UserPersonalData userPersonalData;
    private UserSharedPreference userSharedPreference;



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


        userPersonalData= userSharedPreference.getPersonalData();
        if (userPersonalData.getAge()==0 || userPersonalData.getSize() ==0 ){
            //set all default
            btn_Activity_4.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_lifting_grey));
            btn_Activity_3.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_runner_grey));
            btn_Activity_2.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_squat_grey));
            btn_Activity_1.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_walking_grey));

        }else {

            gender=userPersonalData.isGender();
            size =userPersonalData.getSize();
            weight = userPersonalData.getWeight();
            age = userPersonalData.getAge();
            level=userPersonalData.getActivity_level();

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

    }

    private void loadImage(final String value) {
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

        if ((12-current_month) > month){
            age =current_year - year - 1;
        }else if ((12-current_month) < month){
            age = current_year - year;
        }else if ((12 -current_year) == month){
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
}
