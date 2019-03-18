package com.example.iopd;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.github.vipulasri.timelineview.TimelineView;

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
        LayoutInflater mInflater =
                (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null)
            convertView = mInflater.inflate(R.layout.listview_row, parent, false);

        TextView textView = (TextView)convertView.findViewById(R.id.TLname);
        textView.setText(processName[position]);

        TimelineView timelineView= (TimelineView) convertView.findViewById(R.id.timeline4);

        return convertView;
    }

    protected void passProcess(int step){
        int position = (int) getItemId(step);
    }
}
