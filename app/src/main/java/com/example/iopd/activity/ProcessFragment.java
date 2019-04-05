package com.example.iopd.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
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
    private CustomAdapter adapter;


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
        adapter = new CustomAdapter(root.getContext(), process, order);
        listView.setAdapter(adapter);
    }

    protected void passProcess(int step){
        adapter.passProcess(step);
    }

}
