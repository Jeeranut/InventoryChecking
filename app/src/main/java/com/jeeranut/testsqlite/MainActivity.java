package com.jeeranut.testsqlite;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity  {
    DatabaseHelper myDb ;
    //EditText  etxtName,etxtPartname,etxtRange;
    Button  btnAdd,btnViewAll,btnResult,btnClearData,btnAuditee,btnAuditor,btnExit,btnTest,btnViewPart,btnViewWip,btnViewRm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDb = new DatabaseHelper(this);


//        etxtName = (EditText)findViewById(R.id.etxtName);
//        etxtPartname = (EditText)findViewById(R.id.etxtPartname);
//        etxtRange = (EditText)findViewById(R.id.etxtRange);
//        btnAdd = (Button)findViewById(R.id.btnAdd);
//        btnViewAll = (Button)findViewById(R.id.btnViewAll);
//        btnResult = (Button)findViewById(R.id.btnResult);
        btnClearData = (Button)findViewById(R.id.btnClearData);
        btnAuditee = (Button)findViewById(R.id.btnAuditee);
        btnAuditor = (Button)findViewById(R.id.btnAuditor);
        btnExit = (Button)findViewById(R.id.btnExit);
//        btnTest = (Button)findViewById(R.id.btnTest);
//        btnViewPart = (Button)findViewById(R.id.btnViewParts);
//        btnViewWip = (Button)findViewById(R.id.btnViewWip);
//        btnViewRm = (Button)findViewById(R.id.btnViewRm);

//        insertpart();

//        viewPart();
//        AddData();
//        ViewAll();

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
                   Toast.makeText(MainActivity.this , "Permission has granted ." , Toast.LENGTH_SHORT);
        }

        eventOnClickbtnClearData();
//        LinkResult();
        LinkAuditee();
        LinkAuditor();
        eventOnClickBtnAuditorExit();





    }

//    public void LinkResult()
//    {
//        btnResult.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Open Result
//                Intent newActivity = new Intent(MainActivity.this,ResultActivity.class);
//                startActivity(newActivity);
//            }
//        });
//    }

    public void LinkAuditee()
    {
        btnAuditee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open Result
                Intent newActivity = new Intent(MainActivity.this,AuditeeActivity.class);
                startActivity(newActivity);

            }
        });
    }

    public void LinkAuditor()
    {
        btnAuditor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open Result
                Intent newActivity = new Intent(MainActivity.this,AuditorActivity.class);
                startActivity(newActivity);
            }
        });
    }

    public void eventOnClickBtnAuditorExit()
    {
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open Result
                onBackPressed();
            }
        });
    }

//    public void AddData()
//    {
//        btnAdd.setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        boolean res = false;
//                        String var1 = etxtRange.getText().toString();
//                        String[] var2 = var1.split(":");
//                        int start = Integer.parseInt(var2[0]);
//                        int stop = Integer.parseInt(var2[1]);
//
//                        for(int i = start ; i<=stop ; i++) {
//                            res = myDb.insertParts(
//                                    etxtPartname.getText().toString(),
//                                    etxtName.getText().toString(),
//                                    "s"+i,
//                                    "2000",
//                                    2,
//                                    0
//                                    );
//                        }
////                        AddData(v);
//
//                        if(res == true)
//                        {
//                            Toast.makeText(MainActivity.this,"Data Inserted",Toast.LENGTH_LONG).show();
//                        }
//                        else
//                        {
//                            Toast.makeText(MainActivity.this,"Data not inserted",Toast.LENGTH_LONG).show();
//                        }
//                    }
//                }
//        );
//    }


//    public void AddData()
//    {
//        btnAdd.setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        boolean res =  myDb.insertData(
//                                etxtName.getText().toString(),
//                                etxtPartname.getText().toString(),
//                                etxtRange.getText().toString());
//
////                        AddData(v);
//
//                        if(res == true)
//                            {
//                                Toast.makeText(MainActivity.this,"Data Inserted",Toast.LENGTH_LONG).show();
//                            }
//                        else
//                            {
//                                Toast.makeText(MainActivity.this,"Data not inserted",Toast.LENGTH_LONG).show();
//                            }
//                    }
//                }
//        );
//    }

//    public void ViewAll()
//    {
//        btnViewAll.setOnClickListener(
//                new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//               Cursor res = myDb.getAllData();
//               if(res.getCount()==0){
//                   // show message
//                   showMessage("Error","Nothing Found.");
//                   return;
//               }
//
//               StringBuffer buffer = new StringBuffer();
//               while(res.moveToNext())
//               {
//                   buffer.append("Id :"+ res.getString(0)+"\n");
//                   buffer.append("Name :"+ res.getString(1)+"\n");
//                   buffer.append("Surname :"+ res.getString(2)+"\n");
//                   buffer.append("Marks :"+ res.getString(3)+"\n\n");
//               }
//               //show all data
//                showMessage("Data",buffer.toString());
//
//
//            }
//        });
//    }

//    public void viewPart()
//    {
//        btnViewPart.setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Cursor res = myDb.select("SELECT * FROM parts");
//                        if(res.getCount()==0){
//                            // show message
//                            showMessage("Error","Nothing Found.");
//                            return;
//                        }
//
//                        StringBuffer buffer = new StringBuffer();
//                        while(res.moveToNext())
//                        {
//                            buffer.append("Id :"+ res.getString(0)+"\n");
//                            buffer.append("partname :"+ res.getString(1)+"\n");
//                            buffer.append("pid :"+ res.getString(2)+"\n");
//                            buffer.append("qty :"+ res.getString(3)+"\n");
//                            buffer.append("series :"+ res.getString(4)+"\n");
//                            buffer.append("jobtype :"+ res.getString(5)+"\n");
//                            buffer.append("status :"+ res.getString(6)+"\n\n");
//                        }
//                        //show all data
//                        showMessage("Data",buffer.toString());
//
//
//                    }
//                });
//    }

    public void eventOnClickbtnClearData()
    {
        btnClearData.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        showDialog();

                    }
                }
        );
    }



//    public void showMessage(String title,String message)
//    {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setCancelable(true);
//        builder.setTitle(title);
//        builder.setMessage(message);
//        builder.show();
//    }

//    public void insertpart()
//    {
//
//        btnTest.setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        boolean res =  myDb.insertParts(
//                                "fdsfsd",
//                                "1",
//                                "12345",
//                                "huhhu",
//                                1,
//                                0
//
//                                );
//
////                        AddData(v);
//
//                        if(res == true)
//                        {
//                            Toast.makeText(MainActivity.this,"Data Inserted",Toast.LENGTH_LONG).show();
//                        }
//                        else
//                        {
//                            Toast.makeText(MainActivity.this,"Data not inserted",Toast.LENGTH_LONG).show();
//                        }
//                    }
//                }
//        );
//    }


    private void showDialog() {

        new android.support.v7.app.AlertDialog.Builder(this)
                .setTitle("Would you like to Clear DATA ?")
                .setMessage("Would you like to Clear DATA ?")
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

                                myDb.delete();

                                File file = Environment.getExternalStorageDirectory();
                                File directory = new File(file.getAbsolutePath() + "/Export");

                                if (file.exists()) {
                                    String deleteCmd = "rm -r " + directory;
                                    Runtime runtime = Runtime.getRuntime();
                                    try {
                                        runtime.exec(deleteCmd);
                                    } catch (IOException e) { }
                                }

                                myDb.close();
                                Toast.makeText(MainActivity.this,"Data was cleared",Toast.LENGTH_SHORT).show();


                            }
                        }).show();
    }
    //End of ShowDialog Class





}
