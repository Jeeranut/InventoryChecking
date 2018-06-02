package com.jeeranut.testsqlite;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

/**
 * Created by oPuKo on 18/02/2018.
 */

public class AuditeeRmActivity extends AppCompatActivity {
    DatabaseHelper myDb;
    MediaPlayer ringBeep,ringBell;

    TextView txtvAuditeeRmQty,txtvAuditeeRmCount,txtvAuditeeRmSummary;
    EditText etxtAuditeeRmProcess,etxtAuditeeRmLocation,etxtAuditeeRmPartNumber;
    ListView listvAuditeeRm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auditeerm);

        myDb = new DatabaseHelper(this);

        txtvAuditeeRmCount = (TextView)findViewById(R.id.txtvAuditeeRmCount);
        txtvAuditeeRmQty = (TextView)findViewById(R.id.txtvAuditeeRmQty);
        txtvAuditeeRmSummary = (TextView)findViewById(R.id.txtvAuditeeRmSummary);
        etxtAuditeeRmLocation = (EditText)findViewById(R.id.etxtAuditeeRmLocation);
        etxtAuditeeRmProcess = (EditText)findViewById(R.id.etxtAuditeeRmProcess);
        etxtAuditeeRmPartNumber = (EditText)findViewById(R.id.etxtAuditeeRmPartNumber);
        listvAuditeeRm = (ListView)findViewById(R.id.listvAuditeeRm);

        eventOnEnterEtxtAuditeeRmLocation();
        insertPart();

    }


    public void eventOnEnterEtxtAuditeeRmLocation()
    {
        etxtAuditeeRmLocation.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    //Default Value
                    String pid = "0";

                    //String model = "";


                    //Check and insert rm table
                    Cursor c = myDb.select("SELECT * FROM rms WHERE processname='" + etxtAuditeeRmProcess.getText() + "' AND location='"+etxtAuditeeRmLocation.getText() +"'");

                    //Check work order before insert to database If pid = 0 migth be insert not success
                    if (c.getCount() > 0 && c != null) {
                        while (c.moveToNext()) {
                            pid = c.getString(0);
                        }
                        c = myDb.select("SELECT parts.id _id,partname,qty,series,status FROM parts LEFT JOIN rms ON parts.pid=rms.id WHERE jobtype='2' AND pid='" + pid + "'");

                        CursorAdapter adapter = new CursorCustomAdapter(getApplicationContext(),c,false);

                        listvAuditeeRm.setAdapter(adapter);
                        Toast.makeText(AuditeeRmActivity.this,"Selected from Process : " + etxtAuditeeRmProcess.getText().toString() + " : " + etxtAuditeeRmLocation.getText().toString() ,Toast.LENGTH_LONG).show();
                        return true;
                    }
                    else
                    {
                        listvAuditeeRm.setAdapter(null);
                    }

                }
                return false;
            }
            //End of onKey()
        });
        //End of setOnKeyListener()
    }
    //End of eventOnEnterEtxtAuditeeRmLocation()


    public void insertPart()
    {
        etxtAuditeeRmPartNumber.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    // Split part number
                    String var = etxtAuditeeRmPartNumber.getText().toString();
                    String[] var1 =  var.split(" ");

                    //Check length of array before assign to another variable
                    if(var1.length != 3)
                    {

                        ringBell = MediaPlayer.create(AuditeeRmActivity.this,R.raw.ringing);
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

                        Toast.makeText(AuditeeRmActivity.this,"Part number not match !!! Partnumber : " + etxtAuditeeRmPartNumber.getText() ,Toast.LENGTH_SHORT).show();
                        clearEtxtAuditeeRmPartNumber();
                        return false;
                    }
                    String partnumber = var1[0].trim();
                    String series = var1[2];
                    String qty = var1[1];
                    txtvAuditeeRmQty.setText(qty);

                    //Default Value
                    String pid = "0";
                    boolean res1 = false;

                    pid = insertRm();

                    if(pid.equals("0")) {

                        ringBell = MediaPlayer.create(AuditeeRmActivity.this,R.raw.ringing);
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

                        Toast.makeText(AuditeeRmActivity.this, "Problem about get PID !!! ", Toast.LENGTH_LONG).show();
                        return false;
                    }

                    else {
                        res1 = myDb.insertParts(
                                partnumber,
                                pid,
                                series,
                                qty,
                                2,
                                0
                        );
                    }

                    if (res1 == true) {

                        ringBeep = MediaPlayer.create(AuditeeRmActivity.this,R.raw.beep1);
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

                        //Toast.makeText(AuditeeRmActivity.this, "Data Inserted ", Toast.LENGTH_SHORT).show();

                        Cursor c = myDb.select("SELECT parts.id _id,partname,qty,series,status FROM parts LEFT JOIN rms ON parts.pid=rms.id WHERE jobtype='2' AND pid='" + pid +"'");

                        CursorAdapter adapter = new CursorCustomAdapter(getApplicationContext(),c,false);
                        listvAuditeeRm.setAdapter(adapter);

                        listvAuditeeRm.setSelection(adapter.getCount()-1);

                        etxtAuditeeRmProcess.setFocusable(false);
                        etxtAuditeeRmLocation.setFocusable(false);
//                        showData(myDb.getLatestData("parts"));
                    } else {

                        ringBell = MediaPlayer.create(AuditeeRmActivity.this,R.raw.ringing1);
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

                        clearEtxtAuditeeRmPartNumber();
                        Toast.makeText(AuditeeRmActivity.this, "Part number and Series Duplicate on this rm or other rm !!!!. " + partnumber + " " + qty+ " " + series, Toast.LENGTH_LONG).show();
                    }

                    //Default value
                    String sqty = "0";
                    String cpartname = "0";

                    //Select Count and Summary from table parts
                    Cursor c = myDb.select("SELECT sum(qty) sqty , count(partname) cpartname FROM parts WHERE jobtype='2' AND partname='"+ partnumber +"' AND pid='"+pid+"'");

                    if(c.getCount()>0){
                        while(c.moveToNext()){
                            sqty = c.getString(c.getColumnIndexOrThrow("sqty"));
                            cpartname = c.getString(c.getColumnIndexOrThrow("cpartname"));
                        }
                    }
                    txtvAuditeeRmCount.setText(cpartname);
                    txtvAuditeeRmSummary.setText(sqty);


                    clearEtxtAuditeeRmPartNumber();



//                  showData();

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

    public String insertRm() {

        //Check and insert rm table
        Cursor cur = myDb.select("SELECT * FROM rms WHERE processname='" + etxtAuditeeRmProcess.getText().toString() + "' AND location='"+ etxtAuditeeRmLocation.getText().toString() +"'"
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
            boolean res = myDb.insertRms(etxtAuditeeRmProcess.getText().toString(), etxtAuditeeRmLocation.getText().toString());
            //select wip id
            if (res == true) {
                pid = myDb.selectRmId(etxtAuditeeRmProcess.getText().toString(),etxtAuditeeRmLocation.getText().toString());
                return pid;
            }
            else
            {
                Toast.makeText(AuditeeRmActivity.this,"Can't insert this rm, Please Check process name and location name !!!!.",Toast.LENGTH_LONG);
                return pid;
            }
        }

    }

    public void clearEtxtAuditeeRmPartNumber()
    {
        etxtAuditeeRmPartNumber.setText("");
        etxtAuditeeRmPartNumber.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                etxtAuditeeRmPartNumber.clearFocus();
                etxtAuditeeRmPartNumber.requestFocus();
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
