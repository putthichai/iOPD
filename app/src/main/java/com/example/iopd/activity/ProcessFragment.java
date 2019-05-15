package com.example.iopd.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.iopd.app.CustomAdapter;
import com.example.iopd.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProcessFragment extends Fragment {

    private ListView listView;
    private  View root;



    public ProcessFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_process, container, false);
        listView = root.findViewById(R.id.processList);
        return root;
    }

    protected void setProcess(String[] process,int[] order){
        Log.d("oooooaaaaa",""+process.length+"     "+order.length);
        CustomAdapter adapter = new CustomAdapter(root.getContext(), process, order);
        listView.setAdapter(adapter);
        adapter = null;
    }


    @Override
    public void onResume() {
        super.onResume();
        setProcess(((MainMenuActivity)getActivity()).getAllprocessName(),((MainMenuActivity)getActivity()).getAllproessStatus());
    }
}
