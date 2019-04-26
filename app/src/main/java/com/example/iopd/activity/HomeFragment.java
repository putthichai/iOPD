package com.example.iopd.activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.iopd.R;
import com.example.iopd.activity.MainMenuActivity;
import com.example.iopd.app.Patient;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment{
    private static final String TAG = "";
    protected TextView queue;
    protected TextView date;
    protected TextView timeStart,timeEnd;
    protected TextView doing;
    protected TextView where;
    protected TextView when;
    protected TextView name;
    protected TextView right, status;
    private CardView suggestion,process;
    private int stateForTrement;
    Handler handle;
    Runnable runable;
    private FragmentActivity mFrgAct;
    private Intent mIntent;

    SwipeRefreshLayout swipeRefreshLayout;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        queue = root.findViewById(R.id.queueNo);
        where = root.findViewById(R.id.proWhere);
        when = root.findViewById(R.id.proWhen);
        name = root.findViewById(R.id.name);
        right = root.findViewById(R.id.right);
        suggestion = root.findViewById(R.id.suggestion);
        timeStart = root.findViewById(R.id.apTimeStart);
        timeEnd = root.findViewById(R.id.apTimeEnd);
        doing = root.findViewById(R.id.proDoing);
        date = root.findViewById(R.id.apDate);
        process = root.findViewById(R.id.processLayer);
        status = root.findViewById(R.id.statusHome);
        View settingCV = root.findViewById(R.id.settingCV);
       settingCV.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               ((MainMenuActivity)getActivity()).setViewPager(5);
           }
       });

        suggestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainMenuActivity)getActivity()).setViewPager(1);

            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                handle = new Handler();
                runable = new Runnable() {

                    @Override
                    public void run() {

                        swipeRefreshLayout.setRefreshing(false);
                        ((MainMenuActivity)getActivity()).checkAppointment();
                        if(MainMenuActivity.getQueueNo() != 0){
                            ((MainMenuActivity)getActivity()).checkAppointment();
                            boolean temp =((MainMenuActivity)getActivity()).checkStatusInProccess();
                            if(temp == true){
                                ((MainMenuActivity)getActivity()).checkQueue();
                                ((MainMenuActivity)getActivity()).checkProcess();
                            }

                            reloadValur();
                        }

                        handle.removeCallbacks(runable); // stop runable.
                    }
                };
                handle.postDelayed(runable, 3000); // delay 3 s.
            }
        });

        return root;
    }


    protected void changeState(String state, String place, int remain){

        if(state == null){
            state = "-";
        }
        if(place == null){
            place = "-";
        }

        when.setText(String.valueOf(remain));
        where.setText(place);
        doing.setText(state);
    }

    protected void updateQueue(int tempInt){
        queue.setText(String.valueOf(tempInt));
    }

    protected void updateStatus(String statusQueue){
        status.setText(statusQueue);
    }

    protected void setAppointment(String tempDate){
        date.setText(tempDate);
    }

    protected void setTime(String start,String end){
        timeStart.setText(start);
        timeEnd.setText(end);
    }

    @Override
    public void onStart() {
        super.onStart();
       reloadValur();


    }

    protected void reloadValur(){
        Patient patient = ((MainMenuActivity)getActivity()).getPatient();
        setAppointment(patient.getAppointment());
        setTime(patient.getTimeStart(),patient.getTimeEnd());
        updateQueue(((MainMenuActivity)getActivity()).getQueueNo());
        changeState(((MainMenuActivity)getActivity()).getState(),((MainMenuActivity)getActivity()).getTargetLocation(),((MainMenuActivity)getActivity()).getRemainQueue());
        updateStatus(((MainMenuActivity)getActivity()).getStatusQueue());
    }

}
