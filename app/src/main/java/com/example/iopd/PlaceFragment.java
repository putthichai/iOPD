package com.example.iopd;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlaceFragment extends Fragment {

    public GridView mgridView;


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

        mgridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((MainMenuActivity)getActivity()).setViewPager(4);
            }
        });
        return root;
    }

}

class GridAdapter extends BaseAdapter {
    Context context;
    String string="123456789";

    public GridAdapter(Context c){
        context = c;
    }

    @Override
    public int getCount() {
        return string.length();
    }

    @Override
    public Object getItem(int position) {
        return string.charAt(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        TextView textView = new TextView(context);
        textView.setText(String.valueOf(string.charAt(position)));
        return textView;
    }
}
