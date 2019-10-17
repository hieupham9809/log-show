package com.example.logshowjava.service;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.logshowjava.R;

public class CustomArrayAdapter extends ArrayAdapter<String>  {
    Context context;
    String[] priorites;
    private OnSelectItemListener listener;

    public void setOnSelectItemListener(OnSelectItemListener listener){
        this.listener = listener;
    }

    public CustomArrayAdapter(Context context, int resource, String[] objects) {
        super(context, resource, objects);
        this.context = context;
        this.priorites = objects;
    }

    @Override
    public View getDropDownView(final int position, View convertView, ViewGroup parent) {
        TextView v = (TextView) super.getView(position, convertView, parent).findViewById(android.R.id.text1);



        v.setText(getItem(position));
//        v.setTextColor(Color.parseColor("#000000"));
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null){
                    listener.onSelected(getItem(position));
                }
                Log.d("ZINGLOGSHOW","click meeeeeeeeeee");
            }
        });
        return v;
    }
    @Override
    public String getItem(int position) {
        return priorites[position];
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){

            LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

            convertView = inflater.inflate(R.layout.simple_spinner_item, null );
            TextView textView = convertView.findViewById(android.R.id.text1);
//            textView.setText(priorites[position]);
//            textView.setText("0");
//            Log.d("ZINGLOGSHOW","textView "+ textView.getText());


            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("ZINGLOGSHOW","click meeeeeeeeeee");
                }
            });
        }
        return convertView;
    }

    interface OnSelectItemListener{
        void onSelected (String priority);
    }
}
