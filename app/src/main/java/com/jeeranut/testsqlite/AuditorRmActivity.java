package com.jeeranut.testsqlite;

import android.app.Activity;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oPuKo on 18/02/2018.
 */

public class AuditorRmActivity extends AppCompatActivity {
    DatabaseHelper myDb;
    MediaPlayer ringBeep,ringBell;

    Spinner spnAuditorRmProcess,spnAuditorRmLocation;
    EditText etxtAuditorRmPartNumber;
    TextView txtvAuditorRmQty,txtvAuditorRmCount,txtvAuditorRmSummary;
    ListView listvAuditorRm;

    ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auditorrm);

        myDb = new DatabaseHelper(this);

        spnAuditorRmProcess = (Spinner) findViewById(R.id.spnAuditorRmProcess);
        spnAuditorRmLocation = (Spinner)findViewById(R.id.spnAuditorRmLocation);
        etxtAuditorRmPartNumber = (EditText)findViewById(R.id.etxtAuditorRmPartNumber);
        txtvAuditorRmCount = (TextView)findViewById(R.id.txtvAuditorRmCount);
        txtvAuditorRmQty = (TextView)findViewById(R.id.txtvAuditorRmQty);
        txtvAuditorRmSummary = (TextView)findViewById(R.id.txtvAuditorRmSummary);
        listvAuditorRm = (ListView)findViewById(R.id.listvAuditorRm);

        listProcessname();
        eventOnchangedSpnAuditorProcess();
        eventOnchangedSpnAuditorLocation();
        eventOnEnteredAuditorRmPartnumber();

    }

    public void listProcessname()
    {
        myDb = new DatabaseHelper(this);
        Cursor c = myDb.selectProcessName();

        //String[] processName = new String [c.getCount()];
        List<String> list = new ArrayList<String>();
        list.add("Please Select Process Name");

        c.moveToFirst();
        for(int i = 0 ; i < c.getCount() ; i++){
            list.add(c.getString(0));
            //processName[i] = c.getString(0);
            c.moveToNext();
        }

        myDb.close();
        adapter = new ArrayAdapter<String>(AuditorRmActivity.this,android.R.layout.simple_spinner_dropdown_item,android.R.id.text1,list);
        spnAuditorRmProcess.setAdapter(adapter);
    }

    public void eventOnchangedSpnAuditorProcess()
    {
        spnAuditorRmProcess.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                Cursor c = myDb.selectLocation(spnAuditorRmProcess.getSelectedItem().toString());

                //String[] location = new String[c.getCount()];
                List<String> list = new ArrayList<String>();
                list.add("Please Select Location");

                c.moveToFirst();
                for (int i = 0; i < c.getCount(); i++) {
                    list.add(c.getString(0));
                    //location[i] = c.getString(0);
                    c.moveToNext();
                }

                myDb.close();
                adapter = new ArrayAdapter<String>(AuditorRmActivity.this, android.R.layout.simple_spinner_dropdown_item, android.R.id.text1, list);
                spnAuditorRmLocation.setAdapter(adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });
        //End of setOnItemSelectedListener()
    }
    //End of eventOnchangedSpnAuditorProcess()


    public void eventOnchangedSpnAuditorLocation()
    {
        spnAuditorRmLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                //Default Value
                String processName = "";
                String location = "";

                try{
                    processName = (String)spnAuditorRmProcess.getSelectedItem();
                    location = (String)spnAuditorRmLocation.getSelectedItem();
                }catch (Exception e)
                {
                    Toast.makeText(AuditorRmActivity.this, "Not found value which selected ." , Toast.LENGTH_SHORT).show();
                }

                Toast.makeText(AuditorRmActivity.this, processName + " : " + location, Toast.LENGTH_SHORT).show();


                Cursor c = myDb.select("SELECT parts.id _id,partname,qty,series,status FROM parts LEFT JOIN rms ON parts.pid=rms.id WHERE jobtype='2' AND processname='" + processName +"' AND location='"+location+"'");

                CursorAdapter curAdapter = new CursorCustomAdapter(getApplicationContext(),c,false);

                listvAuditorRm.setAdapter(curAdapter);

//                etxtAuditorWipPartNumber.clearFocus();
//                etxtAuditorWipPartNumber.requestFocus();

            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        //End of spnAuditorRmSpn.setOnItemSelectedListener()

    }
    //End of eventOnchangedSpnAuditorRmProcess()


    public void eventOnEnteredAuditorRmPartnumber()
    {

        etxtAuditorRmPartNumber.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    Cursor c;

                    // Split part number
                    String var = etxtAuditorRmPartNumber.getText().toString();
                    String[] var1 =  var.split(" ");

                    //Check length of array before assign to another variable
                    if(var1.length != 3)
                    {

                        ringBell = MediaPlayer.create(AuditorRmActivity.this,R.raw.ringing);
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

                        Toast.makeText(AuditorRmActivity.this,"Part number not match !!! Partnumber : " + etxtAuditorRmPartNumber.getText() ,Toast.LENGTH_SHORT).show();

                        clrEtxtAuditorRmPartNumber();
                        return false;
                    }
                    String partnumber = var1[0].trim();
                    String series = var1[2].trim();
                    String qty = var1[1];
                    txtvAuditorRmQty.setText(qty);


                    //Select Partnumber to check Does it have in db?
                    c = myDb.select("SELECT partname,qty,series,status FROM parts,rms WHERE jobtype='2' AND rms.id = parts.pid AND processname='" + (String)spnAuditorRmProcess.getSelectedItem() + "' AND location='" + (String)spnAuditorRmLocation.getSelectedItem() + "' AND partname='"+ partnumber +"' AND series='"+ series+"'");

                    if(c.getCount()==0)
                    {

                        ringBell = MediaPlayer.create(AuditorRmActivity.this,R.raw.ringing);
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

                        Toast.makeText(AuditorRmActivity.this,"Partnumber not found !!!. Partnumber : " + etxtAuditorRmPartNumber.getText().toString() ,Toast.LENGTH_SHORT).show();

                        clrEtxtAuditorRmPartNumber();
                        return false;
                    }
                    else
                    {

                        String status = "0";
                        while(c.moveToNext())
                        {
                            status = c.getString(c.getColumnIndexOrThrow("status"));
                        }

                        if(status.equals("1"))
                        {

                            ringBell = MediaPlayer.create(AuditorRmActivity.this,R.raw.ringing1);
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

                            Toast.makeText(AuditorRmActivity.this,"This partnumber is duplicated !!!. Partnumber : " + etxtAuditorRmPartNumber.getText(),Toast.LENGTH_SHORT).show();

                            clrEtxtAuditorRmPartNumber();
                            return false;
                        }

                        //Update Status to 1(was Checked)
                        boolean res = myDb.updatePartsStatus(partnumber,series);

                        if(res == true){

                            ringBeep = MediaPlayer.create(AuditorRmActivity.this,R.raw.beep1);
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

                            Toast.makeText(AuditorRmActivity.this,"Part number : " + etxtAuditorRmPartNumber.getText() + " checked ." ,Toast.LENGTH_LONG).show();

                            c = myDb.select("SELECT parts.id _id,partname,qty,series,status FROM parts LEFT JOIN rms ON parts.pid=rms.id WHERE jobtype='2' AND processname='" + (String)spnAuditorRmProcess.getSelectedItem() +"' AND location='" + (String)spnAuditorRmLocation.getSelectedItem() + "'");
                            CursorAdapter curAdapter = new CursorCustomAdapter(getApplicationContext(),c,false);
                            listvAuditorRm.setAdapter(curAdapter);

                            int i=0;
                            int pos=0;
                            while(i<c.getCount()){
                                c.moveToPosition(i);
                                if( partnumber.equals(c.getString(c.getColumnIndexOrThrow("partname"))) && series.equals(c.getString(c.getColumnIndexOrThrow("series")))){
                                    pos=i;
                                }
                                i++;
                            }
                            listvAuditorRm.setSelection(pos);

                            String sumQtyAuditee = "0";
                            String sumQtyAuditor = "0";
                            String countPartAuditee = "0";
                            String countPartAuditor = "0";

                            c = myDb.select("SELECT sum(qty) sqty,count(partname) cpartname FROM parts,rms WHERE jobtype='2' AND rms.id = parts.pid AND processname='" + (String)spnAuditorRmProcess.getSelectedItem() + "' AND location='" + (String)spnAuditorRmLocation.getSelectedItem() + "' AND partname='"+ partnumber +"'");
                            if(c.getCount()>0){
                                while(c.moveToNext()){
                                    sumQtyAuditee = c.getString(c.getColumnIndexOrThrow("sqty"));
                                    countPartAuditee = c.getString(c.getColumnIndexOrThrow("cpartname"));
                                }
                            }

                            c = myDb.select("SELECT sum(qty) sqty,count(partname) cpartname FROM parts,rms WHERE jobtype='2' AND rms.id = parts.pid AND status='1' AND processname='" + (String)spnAuditorRmProcess.getSelectedItem() + "' AND location='" + (String)spnAuditorRmLocation.getSelectedItem() + "' AND partname='"+ partnumber +"'");
                            if(c.getCount()>0){
                                while (c.moveToNext()){
                                    sumQtyAuditor = c.getString(c.getColumnIndexOrThrow("sqty"));
                                    countPartAuditor = c.getString(c.getColumnIndexOrThrow("cpartname"));
                                }
                            }

                            txtvAuditorRmCount.setText(countPartAuditor+"/"+countPartAuditee);
                            txtvAuditorRmSummary.setText(sumQtyAuditor+"/"+sumQtyAuditee);


                            clrEtxtAuditorRmPartNumber();

                            return true;
                        }
                        else{

                            ringBell = MediaPlayer.create(AuditorRmActivity.this,R.raw.ringing);
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

                            Toast.makeText(AuditorRmActivity.this ,"Part number : " + etxtAuditorRmPartNumber.getText().toString() + " not checked ." ,Toast.LENGTH_LONG).show();
                            clrEtxtAuditorRmPartNumber();
                            return false;
                        }

                    }


                } else
                {
                    return false;
                }
            }
        });
        //End of setOnKeyListener()


    }
    //End of eventOnEnteredAuditorRmPartname()


    public void clrEtxtAuditorRmPartNumber()
    {
        etxtAuditorRmPartNumber.setText("");
        etxtAuditorRmPartNumber.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                etxtAuditorRmPartNumber.clearFocus();
                etxtAuditorRmPartNumber.requestFocus();
            }
        });
    }



}
