package com.example.iopd.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.iopd.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class Place2Fragment extends Fragment {


    public Place2Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_place2, container, false);
        return root;
    }

}
