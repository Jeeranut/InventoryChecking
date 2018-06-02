package com.jeeranut.testsqlite;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {
    Context mContext;
    String[] strName1,strName2,strName3;
    Cursor c ;

    int[] resId;

    public CustomAdapter(Context context, Cursor c) {
        this.mContext= context;
        this.c = c;
//        this.strName1 = strName1;
//        this.strName2 = strName2;
//        this.strName3 = strName3;
//        this.resId = resId;



    }

    public int getCount() {
        return strName1.length;
    }

    public Object getItem(int position) {
        return this.strName1;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater mInflater =
                (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        if(view == null)
            view = mInflater.inflate(R.layout.listitem, parent, false);



            TextView textView1 = (TextView) view.findViewById(R.id.txtvCol1);
            textView1.setText(strName1[position]);

            TextView textView2 = (TextView) view.findViewById(R.id.txtvCol2);
            textView2.setText(strName2[position]);

            TextView textView3 = (TextView) view.findViewById(R.id.txtvCol3);
            textView3.setText(strName3[position]);

//        TextView textView3 = (TextView)view.findViewById(R.id.txtvCol4);
//        textView3.setText(strName3[position]);



        return view;
    }
}