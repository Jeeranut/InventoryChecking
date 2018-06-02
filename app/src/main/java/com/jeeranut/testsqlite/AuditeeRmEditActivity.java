package com.jeeranut.testsqlite;

import android.app.Activity;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jcheewj on 02/03/2018.
 */

public class AuditeeRmEditActivity extends AppCompatActivity {
    DatabaseHelper myDb;

    Spinner spnAuditeeRmEditProcessName,spnAuditeeRmEditLocation;
    EditText etxtAuditeeRmEditLocation,etxtAuditeeRmEditProcessName;
    Button btnAuditeeRmEditSave;
    
    ArrayAdapter<String> adapter;
    String pid ="";
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.auditeermedit);
        myDb = new DatabaseHelper(this);

        spnAuditeeRmEditProcessName = (Spinner)findViewById(R.id.spnAuditeeRmEditProcessName);
        spnAuditeeRmEditLocation = (Spinner)findViewById(R.id.spnAuditeeRmEditLocation);
        etxtAuditeeRmEditProcessName = (EditText)findViewById(R.id.etxtAuditeeRmEditProcessName);
        etxtAuditeeRmEditLocation = (EditText)findViewById(R.id.etxtAuditeeRmEditLocation);
        btnAuditeeRmEditSave = (Button)findViewById(R.id.btnAuditeeRmEditSave);


        listProcessname();
        eventOnchangedSpnAuditeeRmEditLocation();
        eventOnchangedSpnAuditeeRmEditProcess();
        eventOnClickBtnAuditeeWipEditSave();
        
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
        adapter = new ArrayAdapter<String>(AuditeeRmEditActivity.this,android.R.layout.simple_spinner_dropdown_item,android.R.id.text1,list);
        spnAuditeeRmEditProcessName.setAdapter(adapter);
    }

    public void eventOnClickBtnAuditeeWipEditSave()
    {
        btnAuditeeRmEditSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open Result
                showDialog();
            }
        });
    }

    public void eventOnchangedSpnAuditeeRmEditProcess()
    {
        spnAuditeeRmEditProcessName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                Cursor c = myDb.selectLocation(spnAuditeeRmEditProcessName.getSelectedItem().toString());

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
                adapter = new ArrayAdapter<String>(AuditeeRmEditActivity.this, android.R.layout.simple_spinner_dropdown_item, android.R.id.text1, list);
                spnAuditeeRmEditLocation.setAdapter(adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });
        //End of setOnItemSelectedListener()
    }
    //End of eventOnchangedSpnAuditorProcess()


    public void eventOnchangedSpnAuditeeRmEditLocation()
    {
        spnAuditeeRmEditLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                //Default Value
                String processName = "";
                String location = "";
                pid = "";

                try{
                    processName = (String)spnAuditeeRmEditProcessName.getSelectedItem();
                    location = (String)spnAuditeeRmEditLocation.getSelectedItem();
                }catch (Exception e)
                {
                    Toast.makeText(AuditeeRmEditActivity.this, "Not found value which selected ." , Toast.LENGTH_LONG).show();
                }

                pid = myDb.selectRmId(processName,location);
                if(pid.equals("0")){
                    Toast.makeText(AuditeeRmEditActivity.this,"Not found this RM",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    etxtAuditeeRmEditProcessName.setText(processName);
                    etxtAuditeeRmEditLocation.setText(location);
                    Toast.makeText(AuditeeRmEditActivity.this,"This Rm is selected.",Toast.LENGTH_SHORT).show();
                }


            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        //End of spnAuditeeRmEditSpn.setOnItemSelectedListener()

    }
    //End of eventOnchangedSpnAuditeeRmEditProcess()


    private void showDialog() {

        new AlertDialog.Builder(this)
                .setTitle("Would you like to edit ?")
                .setMessage("Would you like to edit this RM ?")
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
                                //Toast.makeText(AuditeeWipEditActivity.this ,"Part number : " + etxtAuditeeWipDeletePartNumber.getText().toString() + " not deleted ." ,Toast.LENGTH_SHORT).show();
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
                                boolean res = myDb.updateRm(pid,etxtAuditeeRmEditProcessName.getText().toString(),etxtAuditeeRmEditLocation.getText().toString());

                                if(res == true){
                                    listProcessname();
                                    etxtAuditeeRmEditProcessName.setText("");
                                    etxtAuditeeRmEditLocation.setText("");

                                    Toast.makeText(AuditeeRmEditActivity.this,"This Rm Process : " + etxtAuditeeRmEditProcessName.getText() + " , Location : "+ etxtAuditeeRmEditLocation.getText() + " edited ." ,Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    etxtAuditeeRmEditProcessName.setText("");
                                    etxtAuditeeRmEditLocation.setText("");
                                    Toast.makeText(AuditeeRmEditActivity.this ,"This Rm : " + etxtAuditeeRmEditProcessName.getText()+ " , Location : "+ etxtAuditeeRmEditLocation.getText() + " edited ." ,Toast.LENGTH_SHORT).show();

                                }
                            }
                        }).show();
    }
    


}
