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
 * Created by jcheewj on 25/02/2018.
 */

public class AuditeeWipDeleteActivity extends AppCompatActivity {
    Spinner spnAuditeeWipDeleteWorkOrder;
    EditText etxtAuditeeWipDeleteModel,etxtAuditeeWipDeletePartNumber;
    ListView listvAuditeeWipDelete;


    DatabaseHelper myDb;
    ArrayAdapter<String> adapter;
    Cursor curItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auditeewipdelete);
        myDb = new DatabaseHelper(this);

        spnAuditeeWipDeleteWorkOrder = (Spinner)findViewById(R.id.spnAuditeeWipDeleteWorkOrder);
        etxtAuditeeWipDeleteModel = (EditText)findViewById(R.id.etxtAuditeeWipDeleteModel);
        etxtAuditeeWipDeletePartNumber = (EditText)findViewById(R.id.etxtAuditeeWipDeletePartNumber);
        listvAuditeeWipDelete = (ListView)findViewById(R.id.listvAuditeeWipDelete);
        etxtAuditeeWipDeleteModel.setFocusable(false);


        listWorkOrderName();
        eventOnchangedSpnAuditeeWipDeleteWorkOrder();
        eventOnEnteredAuditorWipPartname();
        onClickDelete();

    }

    public void eventOnchangedSpnAuditeeWipDeleteWorkOrder()
    {
        spnAuditeeWipDeleteWorkOrder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                //Default Value
                String selectedItem = "";
                String model = "";

                try{
                    selectedItem = (String)spnAuditeeWipDeleteWorkOrder.getSelectedItem();
                }catch (Exception e)
                {
                    Toast.makeText(AuditeeWipDeleteActivity.this, "Not found value which selected ." , Toast.LENGTH_SHORT).show();
                }

                Toast.makeText(AuditeeWipDeleteActivity.this, selectedItem, Toast.LENGTH_SHORT).show();


                Cursor c = myDb.select("SELECT model FROM wips WHERE workorder='" + selectedItem + "'");

                if(c.getCount()>0){
                    while(c.moveToNext()){
                        model = c.getString(0);
                    }
                }

                else
                {
                    Toast.makeText(AuditeeWipDeleteActivity.this,"Not found this wip",Toast.LENGTH_SHORT).show();
                }

                curItem = myDb.select("SELECT parts.id _id,partname,qty,series,status FROM parts LEFT JOIN wips ON parts.pid=wips.id WHERE jobtype='1' AND workorder='" + selectedItem +"'");

                CursorAdapter curAdapter = new CursorCustomAdapter(getApplicationContext(),curItem,false);

                etxtAuditeeWipDeleteModel.setText(model);

                listvAuditeeWipDelete.setAdapter(curAdapter);
                etxtAuditeeWipDeletePartNumber.clearFocus();
                etxtAuditeeWipDeletePartNumber.requestFocus();

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

        etxtAuditeeWipDeletePartNumber.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    Cursor c;

                    // Split part number
                    String var = etxtAuditeeWipDeletePartNumber.getText().toString();
                    String[] var1 =  var.split(" ");

                    //Check length of array before assign to another variable
                    if(var1.length != 3)
                    {
                        Toast.makeText(AuditeeWipDeleteActivity.this,"Part number not match !!! Partnumber : " + etxtAuditeeWipDeletePartNumber.getText() ,Toast.LENGTH_LONG).show();

                        clrEtxtAuditeeWipDeletePartNumber();
                        return false;
                    }
                    String partnumber = var1[0].trim();
                    String series = var1[2];
                    String qty = var1[1];


                    //Select Partnumber to check Does it have in db?
                    c = myDb.select("SELECT partname,qty,series,status FROM parts,wips WHERE jobtype='1' AND wips.id = parts.pid AND workorder='" + (String)spnAuditeeWipDeleteWorkOrder.getSelectedItem() + "' AND partname='"+ partnumber +"' AND series='"+ series+"'");

                    if(c.getCount()==0)
                    {
                        Toast.makeText(AuditeeWipDeleteActivity.this,"Partnumber not found !!!. Partnumber : " + etxtAuditeeWipDeletePartNumber.getText().toString() ,Toast.LENGTH_SHORT).show();
                        clrEtxtAuditeeWipDeletePartNumber();
                        return false;
                    }
                    else
                    {
                        showDialog(partnumber,series);
                        clrEtxtAuditeeWipDeletePartNumber();
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
    //End of eventOnEnteredAuditorWipPartname()

    public void onClickDelete()
    {
        listvAuditeeWipDelete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {


                curItem.moveToPosition(position);
                String partnumber = curItem.getString(1);
                String series = curItem.getString(3);

                showDialog(partnumber,series);
//                Toast.makeText(getApplicationContext(),
//                        "Click :"+position+"  Item : " + partnumber + " , " + series , Toast.LENGTH_SHORT).show();

                clrEtxtAuditeeWipDeletePartNumber();

            }
        });
    }

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
            //wipName[i] = c.getString(0);
            c.moveToNext();
        }

        myDb.close();
        adapter = new ArrayAdapter<String>(AuditeeWipDeleteActivity.this,android.R.layout.simple_spinner_dropdown_item,android.R.id.text1,list);
        spnAuditeeWipDeleteWorkOrder.setAdapter(adapter);
    }

    public void clrEtxtAuditeeWipDeletePartNumber()
    {
        etxtAuditeeWipDeletePartNumber.setText("");
        etxtAuditeeWipDeletePartNumber.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                etxtAuditeeWipDeletePartNumber.clearFocus();
                etxtAuditeeWipDeletePartNumber.requestFocus();
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
                                Toast.makeText(AuditeeWipDeleteActivity.this ,"Part number : " + etxtAuditeeWipDeletePartNumber.getText().toString() + " not deleted ." ,Toast.LENGTH_SHORT).show();
                                clrEtxtAuditeeWipDeletePartNumber();
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
                                    Toast.makeText(AuditeeWipDeleteActivity.this,"Part number : " + etxtAuditeeWipDeletePartNumber.getText() + " deleted ." ,Toast.LENGTH_SHORT).show();

                                    curItem = myDb.select("SELECT parts.id _id,partname,qty,series,status FROM parts LEFT JOIN wips ON parts.pid=wips.id WHERE jobtype='1' AND workorder='" + (String)spnAuditeeWipDeleteWorkOrder.getSelectedItem() +"'");
                                    CursorAdapter curAdapter = new CursorCustomAdapter(getApplicationContext(),curItem,false);
                                    listvAuditeeWipDelete.setAdapter(curAdapter);

                                    clrEtxtAuditeeWipDeletePartNumber();


                                }
                                else{
                                    Toast.makeText(AuditeeWipDeleteActivity.this ,"Part number : " + etxtAuditeeWipDeletePartNumber.getText().toString() + " not deleted ." ,Toast.LENGTH_SHORT).show();
                                    clrEtxtAuditeeWipDeletePartNumber();

                                }
                            }
                        }).show();
    }


}
