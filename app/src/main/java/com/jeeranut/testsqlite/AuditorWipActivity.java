package com.jeeranut.testsqlite;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jcheewj on 10/02/2018.
 */

public class AuditorWipActivity extends AppCompatActivity {
    Spinner spnAuditorWipWorkOrder;
    EditText etxtAuditorWipModel,etxtAuditorWipPartNumber;
    ListView listvAuditorWip;
    TextView txtvAuditorWipQty,txtvAuditorWipCount,txtvAuditorWipSummary;

    DatabaseHelper myDb;
    MediaPlayer ringBeep,ringBell;
    ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auditorwip);
        myDb = new DatabaseHelper(this);

        txtvAuditorWipQty  = (TextView)findViewById(R.id.txtvAuditorWipQty);
        txtvAuditorWipCount = (TextView)findViewById(R.id.txtvAuditorWipCount);
        txtvAuditorWipSummary = (TextView)findViewById(R.id.txtvAuditorWipSummary);
        spnAuditorWipWorkOrder = (Spinner)findViewById(R.id.spnAuditorWipWorkOrder);
        etxtAuditorWipModel = (EditText)findViewById(R.id.etxtAuditorWipModel);
        etxtAuditorWipPartNumber = (EditText)findViewById(R.id.etxtAuditorWipPartNumber);
        listvAuditorWip = (ListView)findViewById(R.id.listvAuditorWip);
        etxtAuditorWipModel.setFocusable(false);


        listWorkOrderName();
        eventOnchangedSpnAuditorWipWorkOrder();
        eventOnEnteredAuditorWipPartname();

    }

    public void eventOnchangedSpnAuditorWipWorkOrder()
    {
        spnAuditorWipWorkOrder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                //Default Value
                String selectedItem = "";
                String model = "";

                try{
                    selectedItem = (String)spnAuditorWipWorkOrder.getSelectedItem();
                }catch (Exception e)
                {
                    Toast.makeText(AuditorWipActivity.this, "Not found value which selected ." , Toast.LENGTH_LONG).show();
                }

                Toast.makeText(AuditorWipActivity.this, selectedItem, Toast.LENGTH_LONG).show();


                Cursor c = myDb.select("SELECT model FROM wips WHERE workorder='" + selectedItem + "'");

                if(c.getCount()>0){
                    while(c.moveToNext()){
                        model = c.getString(0);
                    }
                }

                else
                {
                    Toast.makeText(AuditorWipActivity.this,"Not found this wip",Toast.LENGTH_LONG).show();
                }

                c = myDb.select("SELECT parts.id _id,partname,qty,series,status FROM parts LEFT JOIN wips ON parts.pid=wips.id WHERE jobtype='1' AND workorder='" + selectedItem +"'");

                CursorAdapter curAdapter = new CursorCustomAdapter(getApplicationContext(),c,false);

                etxtAuditorWipModel.setText(model);

                listvAuditorWip.setAdapter(curAdapter);
                etxtAuditorWipPartNumber.clearFocus();
                etxtAuditorWipPartNumber.requestFocus();

            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        //End of spnAuditorWipWorkOrder.setOnItemSelectedListener

    }
    //End of eventOnchangedSpnAuditorWipWorkOrder()

    public void eventOnEnteredAuditorWipPartname()
    {

        etxtAuditorWipPartNumber.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    Cursor c;

                    // Split part number
                    String var = etxtAuditorWipPartNumber.getText().toString();
                    String[] var1 =  var.split(" ");

                    //Check length of array before assign to another variable
                    if(var1.length != 3)
                    {
                        ringBell = MediaPlayer.create(AuditorWipActivity.this,R.raw.ringing);
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

                        Toast.makeText(AuditorWipActivity.this,"Part number not match !!! Partnumber : " + etxtAuditorWipPartNumber.getText() ,Toast.LENGTH_SHORT).show();

                        clrEtxtAuditorWipPartNumber();
                        return false;
                    }
                    String partnumber = var1[0].trim();
                    String series = var1[2];
                    String qty = var1[1];
                    txtvAuditorWipQty.setText(qty);


                    //Select Partnumber to check Does it have in db?
                    c = myDb.select("SELECT partname,qty,series,status FROM parts,wips WHERE jobtype='1' AND wips.id = parts.pid AND workorder='" + (String)spnAuditorWipWorkOrder.getSelectedItem() + "' AND partname='"+ partnumber +"' AND series='"+ series+"'");

                    if(c.getCount()==0)
                    {

                        ringBell = MediaPlayer.create(AuditorWipActivity.this,R.raw.ringing);
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

                        Toast.makeText(AuditorWipActivity.this,"Partnumber not found !!!. Partnumber : " + etxtAuditorWipPartNumber.getText().toString() ,Toast.LENGTH_SHORT).show();

                        clrEtxtAuditorWipPartNumber();
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

                            ringBell = MediaPlayer.create(AuditorWipActivity.this,R.raw.ringing1);
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

                            Toast.makeText(AuditorWipActivity.this,"This partnumber is duplicated !!!. Partnumber : " + etxtAuditorWipPartNumber.getText(),Toast.LENGTH_SHORT).show();

                            clrEtxtAuditorWipPartNumber();
                            return false;
                        }

                        //Update Status to 1(was Checked)
                        boolean res = myDb.updatePartsStatus(partnumber,series);

                        if(res == true){
                            ringBeep = MediaPlayer.create(AuditorWipActivity.this,R.raw.beep1);
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

                            Toast.makeText(AuditorWipActivity.this,"Part number : " + etxtAuditorWipPartNumber.getText() + " checked ." ,Toast.LENGTH_SHORT).show();

                            c = myDb.select("SELECT parts.id _id,partname,qty,series,status FROM parts LEFT JOIN wips ON parts.pid=wips.id WHERE jobtype='1' AND workorder='" + (String)spnAuditorWipWorkOrder.getSelectedItem() +"'");
                            CursorAdapter curAdapter = new CursorCustomAdapter(getApplicationContext(),c,false);
                            listvAuditorWip.setAdapter(curAdapter);

                            int i=0;
                            int pos=0;
                            while(i<c.getCount()){
                                c.moveToPosition(i);
                                if( partnumber.equals(c.getString(c.getColumnIndexOrThrow("partname"))) && series.equals(c.getString(c.getColumnIndexOrThrow("series")))){
                                    pos=i;
                                }
                                i++;
                            }
                            listvAuditorWip.setSelection(pos);


                            String sumQtyAuditee = "0";
                            String sumQtyAuditor = "0";
                            String countPartAuditee = "0";
                            String countPartAuditor = "0";

                            c = myDb.select("SELECT sum(qty) sqty,count(partname) cpartname FROM parts,wips WHERE jobtype='1' AND wips.id = parts.pid AND workorder='" + (String)spnAuditorWipWorkOrder.getSelectedItem() + "' AND partname='"+ partnumber +"'");
                            if(c.getCount()>0){
                                while(c.moveToNext()){
                                    sumQtyAuditee = c.getString(c.getColumnIndexOrThrow("sqty"));
                                    countPartAuditee = c.getString(c.getColumnIndexOrThrow("cpartname"));
                                }
                            }

                            c = myDb.select("SELECT sum(qty) sqty,count(partname) cpartname FROM parts,wips WHERE jobtype='1' AND wips.id = parts.pid AND status='1' AND workorder='" + (String)spnAuditorWipWorkOrder.getSelectedItem() + "' AND partname='"+ partnumber +"'");
                            if(c.getCount()>0){
                                while (c.moveToNext()){
                                    sumQtyAuditor = c.getString(c.getColumnIndexOrThrow("sqty"));
                                    countPartAuditor = c.getString(c.getColumnIndexOrThrow("cpartname"));
                                }
                            }

                            txtvAuditorWipCount.setText(countPartAuditor+"/"+countPartAuditee);
                            txtvAuditorWipSummary.setText(sumQtyAuditor+"/"+sumQtyAuditee);


                            clrEtxtAuditorWipPartNumber();

                            return true;
                        }
                        else{

                            ringBell = MediaPlayer.create(AuditorWipActivity.this,R.raw.ringing);
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

                            Toast.makeText(AuditorWipActivity.this ,"Part number : " + etxtAuditorWipPartNumber.getText().toString() + " not checked ." ,Toast.LENGTH_LONG).show();
                            clrEtxtAuditorWipPartNumber();
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
    //End of eventOnEnteredAuditorWipPartname()


    public void listWorkOrderName()
    {
        myDb = new DatabaseHelper(this);
        Cursor c = myDb.selectWorkOrderName();

        //String[] wipName = new String [c.getCount()];
        List<String> list = new ArrayList<String>();
        list.add("Please Select WorkOrder");

        c.moveToFirst();
        for(int i = 0 ; i < c.getCount() ; i++){
            list.add(c.getString(0));
            // wipName[i] = c.getString(0);
            c.moveToNext();
        }

        myDb.close();
        adapter = new ArrayAdapter<String>(AuditorWipActivity.this,android.R.layout.simple_spinner_dropdown_item,android.R.id.text1,list);
        spnAuditorWipWorkOrder.setAdapter(adapter);
    }

    public void clrEtxtAuditorWipPartNumber()
    {
        etxtAuditorWipPartNumber.setText("");
        etxtAuditorWipPartNumber.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                etxtAuditorWipPartNumber.clearFocus();
                etxtAuditorWipPartNumber.requestFocus();
            }
        });
    }




}
//End of class AuditorWipActivity
