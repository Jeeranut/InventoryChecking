package com.jeeranut.testsqlite;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by jcheewj on 12/02/2018.
 */

public class CursorCustomAdapter extends CursorAdapter{



    public CursorCustomAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.listitem, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {



        // Find fields to populate in inflated template
        TextView txtvNo = (TextView) view.findViewById(R.id.txtvCol1);
        TextView txtvPartname = (TextView) view.findViewById(R.id.txtvCol2);
        TextView txtvQty = (TextView) view.findViewById(R.id.txtvCol3);
        TextView txtvSeries = (TextView) view.findViewById(R.id.txtvCol4);
        // Extract properties from cursor

        int no = cursor.getPosition()+1;

        String partname = cursor.getString(cursor.getColumnIndexOrThrow("partname"));
        String qty = cursor.getString(cursor.getColumnIndexOrThrow("qty"));
        String series = cursor.getString(cursor.getColumnIndexOrThrow("series"));
        String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));


        // Populate fields with extracted properties
        if(status.equals("0")) {
            txtvNo.setTextColor(Color.RED);
            txtvPartname.setTextColor(Color.RED);
            txtvQty.setTextColor(Color.RED);
            txtvSeries.setTextColor(Color.RED);
        }
        else
        {
            txtvNo.setTextColor(Color.BLACK);
            txtvPartname.setTextColor(Color.BLACK);
            txtvQty.setTextColor(Color.BLACK);
            txtvSeries.setTextColor(Color.BLACK);
        }


        txtvNo.setText(no + "");
        txtvPartname.setText(partname);
        txtvQty.setText(qty);
        txtvSeries.setText(series);





//        tvPriority.setText(String.valueOf(priority));

    }
}
