package com.example.iopd.activity;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.iopd.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlaceFragment extends Fragment {

    private GridView mgridView;
    private TextView remainQueue;


    public PlaceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_place, container, false);
        mgridView = root.findViewById(R.id.placeGrid);
        mgridView.setAdapter(new GridAdapter(getActivity()));
        remainQueue = root.findViewById(R.id.waitTime);


        mgridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((MainMenuActivity)getActivity()).setViewPager(4);
            }
        });
        return root;
    }

    protected void setRemainQueue(int no){
        if(no != 0){
            remainQueue.setText(String.valueOf(no));
        }

    }

}

class GridAdapter extends BaseAdapter {
    Context context;


    public GridAdapter(Context c){
        context = c;
    }

    @Override
    public int getCount() {
        return mThumbIds.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ImageView imageView;
        if(view == null){
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(250, 200));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(6, 6, 6, 6);
        }else{
            imageView = (ImageView) view;
        }
        imageView.setImageResource(mThumbIds[position]);
        return imageView;
    }

    private Integer[] mThumbIds = {
            R.drawable.sl1, R.drawable.sl2,
            R.drawable.sl3, R.drawable.sl4,
    };
}
