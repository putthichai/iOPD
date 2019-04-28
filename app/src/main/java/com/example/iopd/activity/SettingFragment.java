package com.example.iopd.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import com.example.iopd.R;
import com.example.iopd.activity.MainMenuActivity;

import androidx.cardview.widget.CardView;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment {

    private Switch gps;


    public SettingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View root = inflater.inflate(R.layout.fragment_setting, container, false);
        CardView logout = root.findViewById(R.id.logout);
        gps = root.findViewById(R.id.acceptLocation);
        gps.setChecked(((MainMenuActivity)getActivity()).getStatusGPS());
        gps.setTextOff("Off");
        gps.setTextOn("On");
        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean switchState = gps.isChecked();
                if(switchState){
                    ((MainMenuActivity)getActivity()).turnOnGPS();
                    gps.setTextOn("On");
                    ((MainMenuActivity)getActivity()).setStatusGPS(true);
                    Log.d("ssssssssssssssssss","On");
                }else{
                    ((MainMenuActivity)getActivity()).turnOffGPS();
                    gps.setTextOff("Off");
                    ((MainMenuActivity)getActivity()).setStatusGPS(false);
                    Log.d("ssssssssssssssssss","Off");
                }
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainMenuActivity)getActivity()).logout();
            }
        });
        return root;
    }

    protected void checkStatusGPS(boolean b){
        gps.setChecked(b);
    }

    @Override
    public void onStart() {
        super.onStart();
        onReload();
    }

    protected void onReload(){
        boolean statusGPS =((MainMenuActivity)getActivity()).getStatusGPS();
        gps.setChecked(statusGPS);
    }
}
