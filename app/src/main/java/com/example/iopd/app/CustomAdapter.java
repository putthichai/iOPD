package com.example.iopd.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.iopd.R;

public class CustomAdapter extends BaseAdapter {

     Context mContext;
     String[] processName;
     int[] order;

    public CustomAdapter(Context context, String[] strings,int[] o){
        mContext = context;
        processName = strings;
        order = o;

    }

    @Override
    public int getCount() {
        return processName.length;
    }

    @Override
    public Object getItem(int position) {

        return null;
    }

    @Override
    public long getItemId(int position) {
        for(int i=0; i< order.length;i++){
            if(order[i]== position){
                return i;
            }
        }
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null){
            if(order[position] == 0 && position == order.length -1){
                convertView = mInflater.inflate(R.layout.statusdetail_last_out, parent, false);
                TextView textView = (TextView)convertView.findViewById(R.id.TLname);
                textView.setText(processName[position]);
            }else if(order[position] == 1 && position == order.length -1){
                convertView = mInflater.inflate(R.layout.statusdetail_last_in, parent, false);
                TextView textView = (TextView)convertView.findViewById(R.id.TLname);
                textView.setText(processName[position]);
            }
            else if(order[position] == 2 && position == order.length -1){
            convertView = mInflater.inflate(R.layout.statusdetail_last_miss, parent, false);
            TextView textView = (TextView)convertView.findViewById(R.id.TLname);
            textView.setText(processName[position]);
            }
            else if(order[position] == 1){
                convertView = mInflater.inflate(R.layout.statusdetail_in, parent, false);
                TextView textView = (TextView)convertView.findViewById(R.id.TLname);
                textView.setText(processName[position]);
            }else if(order[position] == 0){
                convertView = mInflater.inflate(R.layout.statusdetail_out, parent, false);
                TextView textView = (TextView)convertView.findViewById(R.id.TLname);
                textView.setText(processName[position]);
            }
            else if(order[position] == 2){
                convertView = mInflater.inflate(R.layout.statusdetail_miss, parent, false);
                TextView textView = (TextView)convertView.findViewById(R.id.TLname);
                textView.setText(processName[position]);
            }
        }


        return convertView;
    }

    public void passProcess(int step){
        int position = (int) getItemId(step);
    }
}
