package com.jeeranut.testsqlite;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by jcheewj on 22/01/2018.
 */

public class AuditeeWipActivity extends AppCompatActivity {
    DatabaseHelper myDb;
    MediaPlayer ringBeep,ringBell;

    EditText etxtAuditeeWipWorkOrder,etxtAuditeeWipModel,etxtAuditeeWipPartNumber;
    TableLayout tblLayoutAuditeeWip;
    ListView listvAuditeeWip;
    TextView txtvAuditeeWipQty,txtvAuditeeWipCount,txtvAuditeeWipSummary;

    public int indNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auditeewip);
        myDb = new DatabaseHelper(this);


        txtvAuditeeWipQty = (TextView)findViewById(R.id.txtvAuditeeWipQty);
        txtvAuditeeWipCount = (TextView)findViewById(R.id.txtvAuditeeWipCount);
        txtvAuditeeWipSummary = (TextView)findViewById(R.id.txtvAuditeeWipSummary);
        etxtAuditeeWipWorkOrder = (EditText)findViewById(R.id.etxtAuditeeWipWorkOrder);
        etxtAuditeeWipModel = (EditText)findViewById(R.id.etxtAuditeeWipModel);
        etxtAuditeeWipPartNumber = (EditText)findViewById(R.id.etxtAuditeeWipPartNumber);
        listvAuditeeWip = (ListView)findViewById(R.id.listvAuditeeWip);

        insertPart();
        eventOnEnterEtxtAuditeeWipWorkOrder();
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("indNumber", indNumber);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        indNumber  = savedInstanceState.getInt("indNumber");
    }

    @Override public void onConfigurationChanged(final Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
    }

    public void showData(Cursor c)
    {
        int width;
        int rows,cols;

        //Cursor c = myDb.getAllData() ; //sqlcon.readEntry();
        //Check Cursor before get data
        if(c.getCount()>0) {
            rows = c.getCount();
            cols = c.getColumnCount();

            c.moveToFirst();

            // outer for loop
            for (int i = 0; i < rows; i++) {

                TableRow row = new TableRow(this);
                row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT));

                // inner for loop
                for (int j = 0; j < cols; j++) {


                    TextView tv = new TextView(this);
                    tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                            TableRow.LayoutParams.WRAP_CONTENT));
                    //tv.setBackgroundResource(R.drawable.cell_shape);
                    tv.setGravity(Gravity.CENTER);

                    //tv.setTextSize(18);
                    tv.setPadding(0, 5, 0, 5);

                    if(j==0){
                        ++indNumber;
                        tv.setText(indNumber+"");
                    }
                    else if(j!=2) {
                        tv.setText(c.getString(j));
                    }
                    row.addView(tv);

                }

                c.moveToNext();

                tblLayoutAuditeeWip.addView(row);

            }
        }
        //sqlcon.close();
    }

    public String insertWip() {


        //Check and insert wip table
        Cursor cur = myDb.select("SELECT * FROM wips WHERE workorder='" +
                etxtAuditeeWipWorkOrder.getText().toString() + "'"
        );

        //Check work order before insert to database If pid = 0 migth be insert not success
        String pid = "0";
        //Check Does it has value ?
        if (cur.getCount() > 0 && cur != null) {
            while (cur.moveToNext()) {
                pid = cur.getString(0);
            }
            return pid;
        } else {
            boolean res = myDb.insertWips(etxtAuditeeWipWorkOrder.getText().toString(), etxtAuditeeWipModel.getText().toString());
            //select wip id
            if (res == true) {
                pid = myDb.selectWipId(etxtAuditeeWipWorkOrder.getText().toString());
                return pid;
            }
            else
            {
                Toast.makeText(AuditeeWipActivity.this,"Can't insert this wip, Please check workorder name !!!!.",Toast.LENGTH_LONG);
                return pid;
            }
        }

    }


    public void eventOnEnterEtxtAuditeeWipWorkOrder()
    {
        etxtAuditeeWipWorkOrder.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    //Default Value
                    String pid = "0";
                    String model = "";

                    Cursor c = myDb.select("SELECT model FROM wips WHERE workorder='" + etxtAuditeeWipWorkOrder.getText() + "'");

                    if(c.getCount()>0){
                        while(c.moveToNext()){
                            model = c.getString(0);
                        }
                    }

                    //Check and insert wip table
                    c = myDb.select("SELECT * FROM wips WHERE workorder='" +
                            etxtAuditeeWipWorkOrder.getText().toString() + "'"
                    );

                    //Check work order before insert to database If pid = 0 migth be insert not success
                    if (c.getCount() > 0 && c != null) {
                        while (c.moveToNext()) {
                            pid = c.getString(0);
                        }
                        c = myDb.select("SELECT parts.id _id,partname,qty,series,status FROM parts LEFT JOIN wips ON parts.pid=wips.id WHERE jobtype='1' AND workorder='" + etxtAuditeeWipWorkOrder.getText().toString() +"'");

                        CursorAdapter adapter = new CursorCustomAdapter(getApplicationContext(),c,false);
                        etxtAuditeeWipModel.setText(model);


                        listvAuditeeWip.setAdapter(adapter);
                        Toast.makeText(AuditeeWipActivity.this,"Selected from workorder : " + etxtAuditeeWipWorkOrder.getText().toString() ,Toast.LENGTH_LONG).show();
                        return true;
                    }
                    else
                    {
                        listvAuditeeWip.setAdapter(null);
                    }

                }
                return false;
            }
            //End of onKey()
        });
        //End of setOnKeyListener()
    }
    //End of eventOnEnterEtxtAuditeeWipWorkOrder()

    public void insertPart()
    {
        etxtAuditeeWipPartNumber.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    // Split part number
                    String var = etxtAuditeeWipPartNumber.getText().toString();
                    String[] var1 =  var.split(" ");

                    //Check length of array before assign to another variable
                    if(var1.length != 3)
                    {

                        ringBell = MediaPlayer.create(AuditeeWipActivity.this,R.raw.ringing);
                        ringBell.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                // TODO Auto-generated method stub
                                mp.reset();
                                mp.release();
                                mp=null;
                            }

                        });
                        ringBell.start();


                        Toast.makeText(AuditeeWipActivity.this,"Part number not match !!! Partnumber : " + etxtAuditeeWipPartNumber.getText() ,Toast.LENGTH_LONG).show();
                        clearEtxtAuditeeWipPartNumber();
                        return false;
                    }
                    String partnumber = var1[0].trim();
                    String series = var1[2];
                    String qty = var1[1];
                    txtvAuditeeWipQty.setText(qty);
//                    int qty = 0;
//                    try {
//                        qty = Integer.parseInt(var1[2]);
//                    } catch(NumberFormatException nfe) {
//                        System.out.println("Could not parse " + nfe);
//                    }

//                    //Check Does it has value in etxtAuditeeWipWorkOrder
//                    if(etxtAuditeeWipWorkOrder.getText().toString()== "")
//                    {
//                        Toast.makeText(AuditeeWipActivity.this,"Please put the workorder name !!!",Toast.LENGTH_LONG).show();
//                        return false;
//                    }
//                    else {
//                        //Check and insert wip table
//                        Cursor cur = myDb.select("SELECT * FROM wips WHERE workorder='" +
//                                etxtAuditeeWipWorkOrder.getText().toString() + "'"
//                        );
//
//
//                        //Check work order before insert to database If pid = 0 migth be insert not success
//                        String pid = "0";
//                        if (cur.getCount() > 0 && cur != null)
//                        {
//                            while(cur.moveToNext())
//                            {
//                                pid = cur.getString(0);
//                            }
//
//                        }
//                        else
//                        {
//                            boolean res = myDb.insertWips(etxtAuditeeWipWorkOrder.getText().toString(), etxtAuditeeWipModel.getText().toString());
//                            //select wip id
//                            if (res == true) {
//                                pid = myDb.selectWipId();
//                            }
//                        }

                    //Default Value
                    String pid = "0";
                    boolean res1 = false;

                    pid = insertWip();

                    if(pid.equals("0")) {

                        ringBell = MediaPlayer.create(AuditeeWipActivity.this,R.raw.ringing);
                        ringBell.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                // TODO Auto-generated method stub
                                mp.reset();
                                mp.release();
                                mp=null;
                            }

                        });
                        ringBell.start();


                        Toast.makeText(AuditeeWipActivity.this, "Problem about get PID !!! ", Toast.LENGTH_LONG).show();
                        return false;
                    }
                    else {
                        res1 = myDb.insertParts(
                                partnumber,
                                pid,
                                series,
                                qty,
                                1,
                                0
                        );
                    }


//                        AddData(v);

                    if (res1 == true) {

                        ringBeep = MediaPlayer.create(AuditeeWipActivity.this,R.raw.beep1);
                        ringBeep.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                // TODO Auto-generated method stub
                                mp.reset();
                                mp.release();
                                mp=null;
                            }

                        });
                        ringBeep.start();




                        Cursor c = myDb.select("SELECT parts.id _id,partname,qty,series,status FROM parts LEFT JOIN wips ON parts.pid=wips.id WHERE jobtype='1' AND workorder='" + etxtAuditeeWipWorkOrder.getText().toString() +"'");

                        CursorAdapter adapter = new CursorCustomAdapter(getApplicationContext(),c,false);
                        listvAuditeeWip.setAdapter(adapter);


                        listvAuditeeWip.setSelection(adapter.getCount()-1);

                        etxtAuditeeWipWorkOrder.setFocusable(false);
                        etxtAuditeeWipModel.setFocusable(false);

                        //Toast.makeText(AuditeeWipActivity.this, "Data Inserted ." , Toast.LENGTH_SHORT).show();
                        //showData(myDb.getLatestData("parts"));
                    } else {

                        ringBell = MediaPlayer.create(AuditeeWipActivity.this,R.raw.ringing1);
                        ringBell.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                // TODO Auto-generated method stub
                                mp.reset();
                                mp.release();
                                mp=null;
                            }

                        });
                        ringBell.start();

                        clearEtxtAuditeeWipPartNumber();
                        Toast.makeText(AuditeeWipActivity.this, "Part number and Series Duplicate !!! " + partnumber + " " + qty+ " " + series, Toast.LENGTH_LONG).show();
                    }
                    //Default value
                    String sqty = "0";
                    String cpartname = "0";

                    //Select Count and Summary from table parts
                    Cursor c = myDb.select("SELECT sum(qty) sqty , count(partname) cpartname FROM parts WHERE jobtype='1' AND partname='"+ partnumber +"' AND pid='"+pid+"'");

                    if(c.getCount()>0){
                        while(c.moveToNext()){
                            sqty = c.getString(c.getColumnIndexOrThrow("sqty"));
                            cpartname = c.getString(c.getColumnIndexOrThrow("cpartname"));
                        }
                    }
                    txtvAuditeeWipCount.setText(cpartname);
                    txtvAuditeeWipSummary.setText(sqty);

                    clearEtxtAuditeeWipPartNumber();

                    return true;
                }
                else {
                    return false;
                }
            }

        });
        //End of OnKeyListener

    }
//End insertpart()

    public void clearEtxtAuditeeWipPartNumber()
    {
        etxtAuditeeWipPartNumber.setText("");
        etxtAuditeeWipPartNumber.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                etxtAuditeeWipPartNumber.clearFocus();
                etxtAuditeeWipPartNumber.requestFocus();
            }
        });
        hideKeyboard();
    }

    public void hideKeyboard()
    {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
