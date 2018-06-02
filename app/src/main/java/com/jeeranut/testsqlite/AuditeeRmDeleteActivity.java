package com.jeeranut.testsqlite;

import android.app.Activity;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jcheewj on 28/02/2018.
 */

public class AuditeeRmDeleteActivity extends AppCompatActivity {

    Spinner spnAuditeeRmDeleteProcess,spnAuditeeRmDeleteLocation;
    EditText etxtAuditeeRmDeletePartNumber;
    ListView listvAuditeeRmDelete;


    DatabaseHelper myDb;
    ArrayAdapter<String> adapter;
    Cursor curItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auditeermdelete);
        myDb = new DatabaseHelper(this);

        spnAuditeeRmDeleteProcess = (Spinner)findViewById(R.id.spnAuditeeRmDeleteProcess);
        spnAuditeeRmDeleteLocation = (Spinner)findViewById(R.id.spnAuditeeRmDeleteLocation);
        etxtAuditeeRmDeletePartNumber = (EditText)findViewById(R.id.etxtAuditeeRmDeletePartNumber);
        listvAuditeeRmDelete = (ListView)findViewById(R.id.listvAuditeeRmDelete);



        listProcessname();
        eventOnchangedSpnAuditeeRmDeleteProcess();
        eventOnchangedSpnAuditeeRmDeleteLocation();
        eventOnEnteredAuditeeRmDeletePartnumber();
        onClickDelete();

    }

    public void listProcessname()
    {
        myDb = new DatabaseHelper(this);
        Cursor c = myDb.selectProcessName();

        List<String> list = new ArrayList<String>();
        list.add("Please Select Process Name");
        String[] processName = new String [c.getCount()];

        c.moveToFirst();
        for(int i = 0 ; i < c.getCount() ; i++){
            //processName[i] = c.getString(0);
            list.add(c.getString(0));
            c.moveToNext();
        }

        myDb.close();
        adapter = new ArrayAdapter<String>(AuditeeRmDeleteActivity.this,android.R.layout.simple_spinner_dropdown_item,android.R.id.text1,list);
        spnAuditeeRmDeleteProcess.setAdapter(adapter);
    }

    public void eventOnchangedSpnAuditeeRmDeleteProcess()
    {
        spnAuditeeRmDeleteProcess.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                Cursor c = myDb.selectLocation(spnAuditeeRmDeleteProcess.getSelectedItem().toString());

                String[] location = new String[c.getCount()];
                List<String> list = new ArrayList<>();
                list.add("Please Select Location");

                c.moveToFirst();
                for (int i = 0; i < c.getCount(); i++) {

                        //location[i] = c.getString(0);
                    list.add(c.getString(0));
                    c.moveToNext();

                }

                myDb.close();
                adapter = new ArrayAdapter<String>(AuditeeRmDeleteActivity.this, android.R.layout.simple_spinner_dropdown_item, android.R.id.text1, list);
                spnAuditeeRmDeleteLocation.setAdapter(adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });
        //End of setOnItemSelectedListener()
    }
    //End of eventOnchangedSpnAuditorProcess()


    public void eventOnchangedSpnAuditeeRmDeleteLocation()
    {
        spnAuditeeRmDeleteLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                //Default Value
                String processName = "";
                String location = "";

                try{
                    processName = (String)spnAuditeeRmDeleteProcess.getSelectedItem();
                    location = (String)spnAuditeeRmDeleteLocation.getSelectedItem();
                }catch (Exception e)
                {
                    Toast.makeText(AuditeeRmDeleteActivity.this, "Not found value which selected ." , Toast.LENGTH_LONG).show();
                }

                Toast.makeText(AuditeeRmDeleteActivity.this, processName + " : " + location, Toast.LENGTH_SHORT).show();


                curItem = myDb.select("SELECT parts.id _id,partname,qty,series,status FROM parts LEFT JOIN rms ON parts.pid=rms.id WHERE jobtype='2' AND processname='" + processName +"' AND location='"+location+"'");

                CursorAdapter curAdapter = new CursorCustomAdapter(getApplicationContext(),curItem,false);

                listvAuditeeRmDelete.setAdapter(curAdapter);

//                etxtAuditorWipPartNumber.clearFocus();
//                etxtAuditorWipPartNumber.requestFocus();

            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        //End of spnAuditeeRmDeleteSpn.setOnItemSelectedListener()

    }
    //End of eventOnchangedSpnAuditeeRmDeleteProcess()


    public void eventOnEnteredAuditeeRmDeletePartnumber()
    {

        etxtAuditeeRmDeletePartNumber.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    Cursor c;

                    // Split part number
                    String var = etxtAuditeeRmDeletePartNumber.getText().toString();
                    String[] var1 =  var.split(" ");

                    //Check length of array before assign to another variable
                    if(var1.length != 3)
                    {
                        Toast.makeText(AuditeeRmDeleteActivity.this,"Part number not match !!! Partnumber : " + etxtAuditeeRmDeletePartNumber.getText() ,Toast.LENGTH_SHORT).show();

                        clrEtxtAuditeeRmDeletePartNumber();
                        return false;
                    }
                    String partnumber = var1[0].trim();
                    String series = var1[2].trim();
                    String qty = var1[1];
                    //txtvAuditorRmQty.setText(qty);


                    //Select Partnumber to check Does it have in db?
                    c = myDb.select("SELECT partname,qty,series,status FROM parts,rms WHERE jobtype='2' AND rms.id = parts.pid AND processname='" + (String)spnAuditeeRmDeleteProcess.getSelectedItem() + "' AND location='" + (String)spnAuditeeRmDeleteLocation.getSelectedItem() + "' AND partname='"+ partnumber +"' AND series='"+ series+"'");

                    if(c.getCount()==0)
                    {
                        Toast.makeText(AuditeeRmDeleteActivity.this,"Partnumber not found !!!. Partnumber : " + etxtAuditeeRmDeletePartNumber.getText().toString() ,Toast.LENGTH_SHORT).show();

                        clrEtxtAuditeeRmDeletePartNumber();
                        return false;
                    }
                    else
                    {
                        showDialog(partnumber,series);
                        clrEtxtAuditeeRmDeletePartNumber();
                        return true;
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

    public void onClickDelete()
    {
        listvAuditeeRmDelete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {


                curItem.moveToPosition(position);
                String partnumber = curItem.getString(1);
                String series = curItem.getString(3);

                showDialog(partnumber,series);
//                Toast.makeText(getApplicationContext(),
//                        "Click :"+position+"  Item : " + partnumber + " , " + series , Toast.LENGTH_SHORT).show();


            }
        });
    }

    public void clrEtxtAuditeeRmDeletePartNumber()
    {
        etxtAuditeeRmDeletePartNumber.setText("");
        etxtAuditeeRmDeletePartNumber.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                etxtAuditeeRmDeletePartNumber.clearFocus();
                etxtAuditeeRmDeletePartNumber.requestFocus();
            }
        });
    }


    private void showDialog(String part,String serie) {
        final String partnumber = part;
        final String series = serie;
        new AlertDialog.Builder(this)
                .setTitle("Would you like to delete ?")
                .setMessage("Would you like to delete this part ?")
                .setIcon(
                        getResources().getDrawable(
                                android.R.drawable.ic_dialog_alert))
                .setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                //Do Something Here
                                Toast.makeText(AuditeeRmDeleteActivity.this ,"Part number : " + etxtAuditeeRmDeletePartNumber.getText().toString() + " not deleted ." ,Toast.LENGTH_SHORT).show();
                                clrEtxtAuditeeRmDeletePartNumber();
                                dialog.cancel();
                            }
                        })
                .setPositiveButton(
                        "OK",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                //Do Something Here
                                boolean res = myDb.deletePart(partnumber,series);

                                if(res == true){
                                    Toast.makeText(AuditeeRmDeleteActivity.this,"Part number : " + etxtAuditeeRmDeletePartNumber.getText() + " deleted ." ,Toast.LENGTH_SHORT).show();

                                    curItem = myDb.select("SELECT parts.id _id,partname,qty,series,status FROM parts LEFT JOIN rms ON parts.pid=rms.id WHERE jobtype='2' AND processname='" + (String)spnAuditeeRmDeleteProcess.getSelectedItem() +"' AND location='" + (String)spnAuditeeRmDeleteLocation.getSelectedItem() + "'");
                                    CursorAdapter curAdapter = new CursorCustomAdapter(getApplicationContext(),curItem,false);
                                    listvAuditeeRmDelete.setAdapter(curAdapter);

                                    clrEtxtAuditeeRmDeletePartNumber();


                                }
                                else{
                                    Toast.makeText(AuditeeRmDeleteActivity.this ,"Part number : " + etxtAuditeeRmDeletePartNumber.getText().toString() + " not checked ." ,Toast.LENGTH_SHORT).show();
                                    clrEtxtAuditeeRmDeletePartNumber();

                                }
                            }
                        }).show();
    }


}
// End of Class