package com.bricenangue.insyconn.ki_ki;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

/**
 * Created by bricenangue on 01.06.17.
 */

public class MyDialogFragment extends DialogFragment implements View.OnClickListener {

    private RadioGroup radioGroup;
    private RadioButton radioButton_male, radioButton_female;

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.male_radio_button){
            ((UserNameListener)getTargetFragment()).onFinishUserDialog(false);

        }else {
            ((UserNameListener)getTargetFragment()).onFinishUserDialog(true);

        }

        dismiss();
    }


    public interface UserNameListener {
        void onFinishUserDialog(boolean user_gender);
    }

    // Empty constructor required for DialogFragment
    public MyDialogFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragement, container);
        radioGroup = (RadioGroup) view.findViewById(R.id.mykiki_radiogroup);
        radioButton_female = (RadioButton) view.findViewById(R.id.female_radio_button);
        radioButton_male = (RadioButton) view.findViewById(R.id.male_radio_button);

        radioButton_male.setOnClickListener(this);
        radioButton_female.setOnClickListener(this);
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        getDialog().setTitle("Please enter username");

        return view;
    }

}
