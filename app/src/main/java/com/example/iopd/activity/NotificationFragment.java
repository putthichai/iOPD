package com.example.iopd.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.iopd.R;



/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {

    private  View root;
    private CardView notific;
    private TextView title,message;


    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_notification, container, false);
        //notific = root.findViewById(R.id.cvNotification);
        title = root.findViewById(R.id.ndTopic);
        message = root.findViewById(R.id.ndDetail);
        setAdapterNotification(((MainMenuActivity)getActivity()).getSubject(),((MainMenuActivity)getActivity()).getMessage());
        return root;
    }

    protected void setAdapterNotification(String topic,String detail){
       title.setText(topic);
       message.setText(detail);
    }


}
