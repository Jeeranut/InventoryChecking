package com.jeeranut.testsqlite;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TableRow.LayoutParams;

/**
 * Created by jcheewj on 18/01/2018.
 */

public class ResultActivity extends Activity{
    DatabaseHelper myDb;

    TableLayout tblResult;
    EditText etxtPartNo;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);

        myDb = new DatabaseHelper(this);

        tblResult = (TableLayout)findViewById(R.id.tblResult);
        etxtPartNo = (EditText)findViewById(R.id.etxtPartNo);

        showData(myDb.getAllData());
        insert();



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
                row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT));

                // inner for loop
                for (int j = 0; j < cols; j++) {

                    if (j == 0) {
                        width = 70;
                    } else if (j == 1) {
                        width = 200;
                    } else if (j == 2) {
                        width = 200;
                    } else {
                        width = 200;
                    }

                    TextView tv = new TextView(this);
                    tv.setLayoutParams(new LayoutParams(width,
                            LayoutParams.WRAP_CONTENT));
                    //tv.setBackgroundResource(R.drawable.cell_shape);
                    tv.setGravity(Gravity.CENTER);
                    //tv.setTextSize(18);
                    tv.setPadding(0, 5, 0, 5);

                    if(j==0){
                        tv.setText(i+1+"");
                    }
                    else {
                        tv.setText(c.getString(j));
                    }
                    row.addView(tv);

                }

                c.moveToNext();

                tblResult.addView(row);

            }
        }
        //sqlcon.close();
    }



    public void insert()
    {

        etxtPartNo.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    String var = etxtPartNo.getText().toString();
                    String[] var1 =  var.split(" ");
                    String name = var1[2];
                    String surname = var1[1];
                    String marks = var1[0];

                    boolean res =  myDb.insertData(
                            name,
                            surname,
                            marks);

//                        AddData(v);

                    if(res == true)
                    {
                        Toast.makeText(ResultActivity.this,"Data Inserted",Toast.LENGTH_LONG).show();
                        showData(myDb.getLatestData("tb_student"));
                    }
                    else
                    {
                        Toast.makeText(ResultActivity.this,"Data not inserted",Toast.LENGTH_LONG).show();
                    }

                    etxtPartNo.setText("");
//                    showData();

                    return res;
                }
                return false;
            }
        });



//        boolean res = true;
//        return res;
    }


    public void showMessage(String title,String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
}
