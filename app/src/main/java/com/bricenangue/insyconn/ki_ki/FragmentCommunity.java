package com.bricenangue.insyconn.ki_ki;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentCommunity extends Fragment {


    public FragmentCommunity() {
        // Required empty public constructor
    }

    public static FragmentCommunity newInstance() {

        return new FragmentCommunity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_community, container, false);
    }

}
