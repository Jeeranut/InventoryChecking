package com.jeeranut.testsqlite;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jcheewj on 01/03/2018.
 */

public class AuditeeWipEditActivity extends AppCompatActivity {
    Spinner spnAuditeeWipEditWorkOrder;
    EditText etxtAuditeeWipEditModel,etxtAuditeeWipEditWorkOrder;
    Button btnAuditeeWipEditSave;

    DatabaseHelper myDb;
    ArrayAdapter<String> adapter;
    String pid ="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auditeewipedit);
        myDb = new DatabaseHelper(this);

        spnAuditeeWipEditWorkOrder = (Spinner)findViewById(R.id.spnAuditeeWipEditWorkOrder);
        etxtAuditeeWipEditWorkOrder = (EditText)findViewById(R.id.etxtAuditeeWipEditWorkOrder);
        etxtAuditeeWipEditModel = (EditText)findViewById(R.id.etxtAuditeeWipEditModel);
        btnAuditeeWipEditSave = (Button)findViewById(R.id.btnAuditeeWipEditSave);


        listWorkOrderName();
        eventOnClickBtnAuditeeWipEditSave();
        eventOnchangedSpnAuditeeWipEditWorkOrder();

    }

    public void eventOnClickBtnAuditeeWipEditSave()
    {
        btnAuditeeWipEditSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open Result
                showDialog();
            }
        });
    }

    public void listWorkOrderName()
    {
        myDb = new DatabaseHelper(this);
        Cursor c = myDb.selectWorkOrderName();


        List<String> list = new ArrayList<String>();
        list.add("Please Select Work Order");
        c.moveToFirst();
        for(int i = 0 ; i < c.getCount() ; i++){
            list.add(c.getString(0));
            c.moveToNext();
        }

        myDb.close();
        adapter = new ArrayAdapter<String>(AuditeeWipEditActivity.this,android.R.layout.simple_spinner_dropdown_item,android.R.id.text1,list);
        spnAuditeeWipEditWorkOrder.setAdapter(adapter);
    }

    public void eventOnchangedSpnAuditeeWipEditWorkOrder()
    {
        spnAuditeeWipEditWorkOrder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                //Default Value
                String selectedItem = "";
                String model = "";
                String workorder = "";

                try{
                    selectedItem = (String)spnAuditeeWipEditWorkOrder.getSelectedItem();
                }catch (Exception e)
                {
                    Toast.makeText(AuditeeWipEditActivity.this, "Not found value which selected ." , Toast.LENGTH_SHORT).show();
                }

                Toast.makeText(AuditeeWipEditActivity.this, selectedItem, Toast.LENGTH_SHORT).show();


                Cursor c = myDb.select("SELECT ID,workorder,model FROM wips WHERE workorder='" + selectedItem + "'");

                if(c.getCount()>0){
                    while(c.moveToNext()){
                        workorder = c.getString(c.getColumnIndex("workorder"));
                        model = c.getString(c.getColumnIndex("model"));
                        pid = c.getString(c.getColumnIndex("ID"));
                    }
                }

                else
                {
                    Toast.makeText(AuditeeWipEditActivity.this,"Not found this wip",Toast.LENGTH_SHORT).show();
                }

                etxtAuditeeWipEditWorkOrder.setText(workorder);
                etxtAuditeeWipEditModel.setText(model);


            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        //End of spnAuditeeWipEdit.setOnItemSelectedListener

    }
    //End of eventOnchangedSpnAuditeeWipEditWorkOrder()


    private void showDialog() {

        new AlertDialog.Builder(this)
                .setTitle("Would you like to edit ?")
                .setMessage("Would you like to edit this WIP ?")
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
                                boolean res = myDb.updateWip(pid,etxtAuditeeWipEditWorkOrder.getText().toString(),etxtAuditeeWipEditModel.getText().toString());

                                if(res == true){
                                    listWorkOrderName();
                                    etxtAuditeeWipEditWorkOrder.setText("");
                                    etxtAuditeeWipEditModel.setText("");

                                    Toast.makeText(AuditeeWipEditActivity.this,"This Wip : " + etxtAuditeeWipEditWorkOrder.getText() + " edited ." ,Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    etxtAuditeeWipEditWorkOrder.setText("");
                                    etxtAuditeeWipEditModel.setText("");
                                    Toast.makeText(AuditeeWipEditActivity.this ,"This Wip : " + etxtAuditeeWipEditWorkOrder.getText().toString() + " not edited ." ,Toast.LENGTH_SHORT).show();

                                }
                            }
                        }).show();
    }

}
