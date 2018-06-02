package com.jeeranut.testsqlite;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;

import jxl.Cell;
import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.write.Formula;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;


/**
 * Created by jcheewj on 10/02/2018.
 */

public class AuditorActivity extends AppCompatActivity
{
    DatabaseHelper myDb;
    Button btnAuditorWip,btnAuditorRm,btnAuditorExport,btnAuditorExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auditor);
        myDb = new DatabaseHelper(this);

        btnAuditorWip = (Button)findViewById(R.id.btnAuditorWip);
        btnAuditorRm = (Button)findViewById(R.id.btnAuditorRm);
        btnAuditorExport = (Button)findViewById(R.id.btnAuditorExport);
        btnAuditorExit = (Button)findViewById(R.id.btnAuditorExit);

        linkWip();
        linkRm();
        eventOnClickBtnAuditorExit();
        exportExcel();
    }
    //End of onCreate()


    public void linkWip()
    {
        btnAuditorWip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open Result
                Intent newActivity = new Intent(AuditorActivity.this,AuditorWipActivity.class);
                startActivity(newActivity);
            }
        });
    }
    //End of LinkWip()

    public void linkRm()
    {
        btnAuditorRm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open Result
                Intent newActivity = new Intent(AuditorActivity.this,AuditorRmActivity.class);
                startActivity(newActivity);
            }
        });
    }
    //End of LinkRm()

    public void eventOnClickBtnAuditorExit()
    {
        btnAuditorExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open Result
                onBackPressed();
            }
        });
    }

    public void exportExcel()
    {
        btnAuditorExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Cursor cursor,cPartsAuditor,cParts ;
                Calendar c = Calendar.getInstance();
                String month = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);

                //---------------------------------------------------------->
                //Start of Export Auditor WIP
                //---------------------------------------------------------->
                File sd = Environment.getExternalStorageDirectory();
                File directory = new File(sd.getAbsolutePath() + "/Export/Auditor/Wips");

                cursor = myDb.select("SELECT ID,workorder,model,time_added FROM wips");
                if(cursor.getCount()>0)
                {
                    while (cursor.moveToNext())
                    {
                        String ID = cursor.getString(cursor.getColumnIndex("ID"));
                        String workorder = cursor.getString(cursor.getColumnIndex("workorder"));
                        String model = cursor.getString(cursor.getColumnIndex("model"));
                        String date = cursor.getString(cursor.getColumnIndex("time_added"));

                        workorder.replace('/','_');
                        String csvFile =  workorder+".xls";


                        //create directory if not exist
                        if (!directory.isDirectory()) {
                            directory.mkdirs();
                        }
                        try {

                            //file path
                            File file = new File(directory, csvFile);
                            WorkbookSettings wbSettings = new WorkbookSettings();
                            wbSettings.setLocale(new Locale("en", "EN"));
                            WritableWorkbook workbook;
                            workbook = Workbook.createWorkbook(file, wbSettings);
                            //Excel sheet name. 0 represents first sheet
                            WritableSheet sheet = workbook.createSheet(workorder, 0);

                            // Create cell font and format
                            WritableFont cellFont = new WritableFont(WritableFont.ARIAL, 10,WritableFont.BOLD);
                            WritableCellFormat cellFormat = new WritableCellFormat(cellFont);
                            cellFormat.setBorder(Border.ALL, BorderLineStyle.THICK);
                            cellFormat.setAlignment(Alignment.CENTRE);

                            WritableFont cellFontHeader = new WritableFont(WritableFont.ARIAL, 12,WritableFont.BOLD);
                            WritableCellFormat cellFormatHeader = new WritableCellFormat(cellFontHeader);
                            WritableFont cellFontHeader1 = new WritableFont(WritableFont.ARIAL, 10,WritableFont.BOLD);
                            WritableCellFormat cellFormatHeader1 = new WritableCellFormat(cellFontHeader1);

                            //WritableFont cellFontHeader1 = new WritableFont(WritableFont.ARIAL, 10,WritableFont.BOLD);
                            WritableCellFormat cellFormatListPart = new WritableCellFormat();
                            cellFormatListPart.setBorder(Border.ALL,BorderLineStyle.THICK);

                            WritableCellFormat cellFormatApproveLeft = new WritableCellFormat();
                            cellFormatApproveLeft.setBorder(Border.LEFT,BorderLineStyle.THICK);

                            WritableCellFormat cellFormatApproveRight = new WritableCellFormat();
                            cellFormatApproveRight.setBorder(Border.RIGHT,BorderLineStyle.THICK);

                            WritableCellFormat cellFormatApproveBottomLeft = new WritableCellFormat();
                            cellFormatApproveBottomLeft.setBorder(Border.BOTTOM,BorderLineStyle.THICK);
                            cellFormatApproveBottomLeft.setBorder(Border.LEFT,BorderLineStyle.THICK);

                            WritableCellFormat cellFormatApproveBottomRight = new WritableCellFormat();
                            cellFormatApproveBottomRight.setBorder(Border.BOTTOM,BorderLineStyle.THICK);
                            cellFormatApproveBottomRight.setBorder(Border.RIGHT,BorderLineStyle.THICK);


                            // First row month of the report
                            sheet.addCell(new Label(0,0,"Inventory list : " + month,cellFormatHeader ));
                            sheet.addCell(new Label(0,1,"JOHNSON CONTROLS-HITACHI COMPONENTS (THAILAND) CO., LTD." ,cellFormatHeader));

                            sheet.addCell(new Label(0, 3, "Inventory Date : ",cellFormatHeader1));
                            sheet.addCell(new Label(2, 3, date));
                            sheet.addCell(new Label(0, 4, "Process Name : " ,cellFormatHeader1));
                            sheet.addCell(new Label(2, 4, workorder ));

                            sheet.addCell(new Label(0, 5, "Operator Name : " ,cellFormatHeader1));

                            sheet.addCell(new Label(0, 6, "Working Process :" ,cellFormatHeader1));
                            sheet.addCell(new Label(2, 6, "(W/H AUTO)" ));

                            sheet.addCell(new Label(6, 4, "Work Order : " + workorder));
                            sheet.addCell(new Label(6, 5, "Model : "+ model));

                            sheet.addCell(new Label(0, 8, "No. ",cellFormat));
                            sheet.mergeCells(1, 8, 2, 8);
                            sheet.addCell(new Label(1, 8, "Part Name",cellFormat));
                            sheet.mergeCells(3, 8, 4, 8);
                            sheet.addCell(new Label(3, 8, "Qty by Auditee",cellFormat));
                            sheet.mergeCells(5, 8, 6, 8);
                            sheet.addCell(new Label(5, 8, "Qty by Auditor",cellFormat));
                            sheet.addCell(new Label(7, 8, "Diff",cellFormat));
                            sheet.addCell(new Label(8, 8, "Remark",cellFormat));

//                            CellView cell=sheet.getColumnView(0);
//                            cell.setAutosize(true);
//                            sheet.setColumnView(8, cell);


                            //cParts = myDb.select("SELECT partname,sum(qty) qty FROM parts WHERE jobtype='1' AND pid='" + ID + "' GROUP BY partname ORDER BY partname");
                            //cPartsAuditor = myDb.select("SELECT partname,sum(qty) qty FROM parts WHERE jobtype='1' AND status='1' AND pid='" + ID + "' GROUP BY partname ORDER BY partname");
                            cParts = myDb.select("SELECT p.partname,sum(qty) qty, ifnull(p1.qty1,'0') qty1 " +
                                    "FROM parts p LEFT JOIN" +
                                    "(SELECT partname,sum(qty) qty1 " +
                                    "FROM parts " +
                                    "WHERE jobtype='1' AND pid='"+ID+"' AND status='1' " +
                                    "GROUP BY partname,status " +
                                    ") AS p1 ON p.partname=p1.partname " +
                                    "WHERE p.jobtype='1' AND p.pid='"+ ID +"' " +
                                    "GROUP BY p.partname " +
                                    "ORDER BY p.partname");

                            int i =0;
                            if (cParts.moveToFirst()) {
                                do {
                                    String partname = cParts.getString(cParts.getColumnIndex("partname"));
                                    String qtyAuditee = cParts.getString(cParts.getColumnIndex("qty"));
                                    String qtyAuditor = cParts.getString(cParts.getColumnIndex("qty1"));

                                    i = cParts.getPosition() + 1;
                                    sheet.addCell(new Label(0,i+8,i+"",cellFormatListPart));

                                    sheet.mergeCells(1, i+8, 2, i+8);
                                    sheet.addCell(new Label(1, i+8, partname,cellFormatListPart));

                                    sheet.mergeCells(3, i+8, 4, i+8);
                                    sheet.addCell(new Label(3, i+8, qtyAuditee,cellFormatListPart));

                                    sheet.mergeCells(5, i+8, 6, i+8);
                                    sheet.addCell(new Label(5, i+8, qtyAuditor,cellFormatListPart));

                                    sheet.addCell(new Formula(7,i+8,"F"+(i+9)+"-D"+(i+9) ,cellFormatListPart));

                                    sheet.addCell(new Label(8, i+8, "",cellFormatListPart));

                                } while (cParts.moveToNext());
                            }
                            //End of While

                            //Start of Count By Box
                            sheet.mergeCells(1, i+10, 2, i+10);
                            sheet.addCell(new Label(1, i+10, "Count By",cellFormat));
                                for (int j = i + 11; j < i + 11 + 4; j++) {
                                    if (j < i + 11 + 3) {
                                        sheet.addCell(new Label(1, j, "", cellFormatApproveLeft));
                                    } else {
                                        sheet.addCell(new Label(1, j, "", cellFormatApproveBottomLeft));
                                    }

                                }
                            for (int j = i + 11; j < i + 11 + 4; j++) {
                                if (j < i + 11 + 3) {
                                    sheet.addCell(new Label(2, j, "", cellFormatApproveRight));
                                } else {
                                    sheet.addCell(new Label(2, j, "", cellFormatApproveBottomRight));
                                }

                            }
                            //End of Count By Box

                            //Start of Checked By Box
                            sheet.mergeCells(3, i+10, 4, i+10);
                            sheet.addCell(new Label(3, i+10, "Checked By",cellFormat));
                            for (int j = i + 11; j < i + 11 + 4; j++) {
                                if (j < i + 11 + 3) {
                                    sheet.addCell(new Label(3, j, "", cellFormatApproveLeft));
                                } else {
                                    sheet.addCell(new Label(3, j, "", cellFormatApproveBottomLeft));
                                }

                            }
                            for (int j = i + 11; j < i + 11 + 4; j++) {
                                if (j < i + 11 + 3) {
                                    sheet.addCell(new Label(4, j, "", cellFormatApproveRight));
                                } else {
                                    sheet.addCell(new Label(4, j, "", cellFormatApproveBottomRight));
                                }

                            }
                            //End of Checked By Box

                            //Start of Approved By Box
                            sheet.mergeCells(5, i+10, 6, i+10);
                            sheet.addCell(new Label(5, i+10, "Approved By",cellFormat));
                            for (int j = i + 11; j < i + 11 + 4; j++) {
                                if (j < i + 11 + 3) {
                                    sheet.addCell(new Label(5, j, "", cellFormatApproveLeft));
                                } else {
                                    sheet.addCell(new Label(5, j, "", cellFormatApproveBottomLeft));
                                }

                            }
                            for (int j = i + 11; j < i + 11 + 4; j++) {
                                if (j < i + 11 + 3) {
                                    sheet.addCell(new Label(6, j, "", cellFormatApproveRight));
                                } else {
                                    sheet.addCell(new Label(6, j, "", cellFormatApproveBottomRight));
                                }

                            }
                            //End of Approved By Box

                            //Start of Audited By Box
                            sheet.mergeCells(7, i+10, 8, i+10);
                            sheet.addCell(new Label(7, i+10, "Audited By",cellFormat));
                            for (int j = i + 11; j < i + 11 + 4; j++) {
                                if (j < i + 11 + 3) {
                                    sheet.addCell(new Label(7, j, "", cellFormatApproveLeft));
                                } else {
                                    sheet.addCell(new Label(7, j, "", cellFormatApproveBottomLeft));
                                }

                            }
                            for (int j = i + 11; j < i + 11 + 4; j++) {
                                if (j < i + 11 + 3) {
                                    sheet.addCell(new Label(8, j, "", cellFormatApproveRight));
                                } else {
                                    sheet.addCell(new Label(8, j, "", cellFormatApproveBottomRight));
                                }

                            }
                            //End of Audited By Box


                            //Close workbook
                            workbook.write();
                            workbook.close();

                            Toast.makeText(getApplication(),"Data Exported in a Excel Sheet (WIP)", Toast.LENGTH_SHORT).show();

                        }
                        catch (Exception e) {
                            Toast.makeText(getApplication(),"WIP : "+e.getMessage(), Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                    //End of While
                }
                //End of IF
                cursor.close();
                //End of Export Auditor Wip

                //---------------------------------------------------------->
                //Start of Export Auditor RM
                //---------------------------------------------------------->
                sd = Environment.getExternalStorageDirectory();
                directory = new File(sd.getAbsolutePath() + "/Export/Auditor/RMs");

                cursor = myDb.select("SELECT * FROM rms");
                if(cursor.getCount()>0)
                {
                    while (cursor.moveToNext())
                    {
                        String ID = cursor.getString(cursor.getColumnIndex("ID"));
                        String processname = cursor.getString(cursor.getColumnIndex("processname"));
                        String location = cursor.getString(cursor.getColumnIndex("location"));
                        String date = cursor.getString(cursor.getColumnIndex("time_added"));


                        String csvFile =  processname+"_"+location+".xls";


                        //create directory if not exist
                        if (!directory.isDirectory()) {
                            directory.mkdirs();
                        }
                        try {

                            //file path
                            File file = new File(directory, csvFile);
                            WorkbookSettings wbSettings = new WorkbookSettings();
                            wbSettings.setLocale(new Locale("en", "EN"));
                            WritableWorkbook workbook;
                            workbook = Workbook.createWorkbook(file, wbSettings);
                            //Excel sheet name. 0 represents first sheet
                            WritableSheet sheet = workbook.createSheet(processname+"_"+location, 0);

                            // Create cell font and format
                            WritableFont cellFont = new WritableFont(WritableFont.ARIAL, 10,WritableFont.BOLD);
                            WritableCellFormat cellFormat = new WritableCellFormat(cellFont);
                            cellFormat.setBorder(Border.ALL, BorderLineStyle.THICK);
                            cellFormat.setAlignment(Alignment.CENTRE);

                            WritableFont cellFontHeader = new WritableFont(WritableFont.ARIAL, 12,WritableFont.BOLD);
                            WritableCellFormat cellFormatHeader = new WritableCellFormat(cellFontHeader);
                            WritableFont cellFontHeader1 = new WritableFont(WritableFont.ARIAL, 10,WritableFont.BOLD);
                            WritableCellFormat cellFormatHeader1 = new WritableCellFormat(cellFontHeader1);

                            WritableCellFormat cellFormatListPart = new WritableCellFormat();
                            cellFormatListPart.setBorder(Border.ALL,BorderLineStyle.THICK);

                            WritableCellFormat cellFormatApproveLeft = new WritableCellFormat();
                            cellFormatApproveLeft.setBorder(Border.LEFT,BorderLineStyle.THICK);

                            WritableCellFormat cellFormatApproveRight = new WritableCellFormat();
                            cellFormatApproveRight.setBorder(Border.RIGHT,BorderLineStyle.THICK);

                            WritableCellFormat cellFormatApproveBottomLeft = new WritableCellFormat();
                            cellFormatApproveBottomLeft.setBorder(Border.BOTTOM,BorderLineStyle.THICK);
                            cellFormatApproveBottomLeft.setBorder(Border.LEFT,BorderLineStyle.THICK);

                            WritableCellFormat cellFormatApproveBottomRight = new WritableCellFormat();
                            cellFormatApproveBottomRight.setBorder(Border.BOTTOM,BorderLineStyle.THICK);
                            cellFormatApproveBottomRight.setBorder(Border.RIGHT,BorderLineStyle.THICK);

                            // First row month of the report
                            sheet.addCell(new Label(0,0,"Inventory list : " + month,cellFormatHeader ));
                            sheet.addCell(new Label(0,1,"JOHNSON CONTROLS-HITACHI COMPONENTS (THAILAND) CO., LTD." ,cellFormatHeader));

                            sheet.addCell(new Label(0, 3, "Inventory Date : ",cellFormatHeader1));
                            sheet.addCell(new Label(2, 3, date));
                            sheet.addCell(new Label(0, 4, "Process Name : " ,cellFormatHeader1));
                            sheet.addCell(new Label(2, 4, processname ));

                            sheet.addCell(new Label(0, 5, "Operator Name : " ,cellFormatHeader1));

                            sheet.addCell(new Label(0, 6, "Working Process :" ,cellFormatHeader1));
                            sheet.addCell(new Label(2, 6, "(W/H AUTO)" ));

                            sheet.addCell(new Label(6, 4, "Process Name : " + processname,cellFormatHeader1));
                            sheet.addCell(new Label(6, 5, "Location : "+ location,cellFormatHeader1));

                            sheet.addCell(new Label(0, 8, "No. ",cellFormat));
                            sheet.mergeCells(1, 8, 2, 8);
                            sheet.addCell(new Label(1, 8, "Part Name",cellFormat));
                            sheet.mergeCells(3, 8, 4, 8);
                            sheet.addCell(new Label(3, 8, "Qty by Auditee",cellFormat));
                            sheet.mergeCells(5, 8, 6, 8);
                            sheet.addCell(new Label(5, 8, "Qty by Auditor",cellFormat));
                            sheet.addCell(new Label(7, 8, "Diff",cellFormat));
                            sheet.addCell(new Label(8, 8, "Remark",cellFormat));

//                            CellView cell=sheet.getColumnView(0);
//                            cell.setAutosize(true);
//                            sheet.setColumnView(8, cell);


                            //cParts = myDb.select("SELECT partname,sum(qty) qty FROM parts WHERE jobtype='1' AND pid='" + ID + "' GROUP BY partname ORDER BY partname");
                            //cPartsAuditor = myDb.select("SELECT partname,sum(qty) qty FROM parts WHERE jobtype='1' AND status='1' AND pid='" + ID + "' GROUP BY partname ORDER BY partname");
                            cParts = myDb.select("SELECT p.partname,sum(qty) qty, ifnull(p1.qty1,'0') qty1 " +
                                    "FROM parts p LEFT JOIN" +
                                    "(SELECT partname,sum(qty) qty1 " +
                                    "FROM parts " +
                                    "WHERE jobtype='2' AND pid='"+ID+"' AND status='1' " +
                                    "GROUP BY partname,status " +
                                    ") AS p1 ON p.partname=p1.partname " +
                                    "WHERE p.jobtype='2' AND p.pid='"+ ID +"' " +
                                    "GROUP BY p.partname " +
                                    "ORDER BY p.partname");


                            int i =0;
                            if (cParts.moveToFirst()) {
                                do {
                                    String partname = cParts.getString(cParts.getColumnIndex("partname"));
                                    String qtyAuditee = cParts.getString(cParts.getColumnIndex("qty"));
                                    String qtyAuditor = cParts.getString(cParts.getColumnIndex("qty1"));

                                    i = cParts.getPosition() + 1;
                                    sheet.addCell(new Label(0,i+8,i+"",cellFormatListPart));

                                    sheet.mergeCells(1, i+8, 2, i+8);
                                    sheet.addCell(new Label(1, i+8, partname,cellFormatListPart));

                                    sheet.mergeCells(3, i+8, 4, i+8);
                                    sheet.addCell(new Label(3, i+8, qtyAuditee,cellFormatListPart));

                                    sheet.mergeCells(5, i+8, 6, i+8);
                                    sheet.addCell(new Label(5, i+8, qtyAuditor,cellFormatListPart));

                                    sheet.addCell(new Formula(7,i+8,"F"+(i+9)+"-D"+(i+9) ,cellFormatListPart));

                                    sheet.addCell(new Label(8, i+8, "",cellFormatListPart));

                                } while (cParts.moveToNext());
                            }

                            //Start of Count By Box
                            sheet.mergeCells(1, i+10, 2, i+10);
                            sheet.addCell(new Label(1, i+10, "Count By",cellFormat));
                            for (int j = i + 11; j < i + 11 + 4; j++) {
                                if (j < i + 11 + 3) {
                                    sheet.addCell(new Label(1, j, "", cellFormatApproveLeft));
                                } else {
                                    sheet.addCell(new Label(1, j, "", cellFormatApproveBottomLeft));
                                }

                            }
                            for (int j = i + 11; j < i + 11 + 4; j++) {
                                if (j < i + 11 + 3) {
                                    sheet.addCell(new Label(2, j, "", cellFormatApproveRight));
                                } else {
                                    sheet.addCell(new Label(2, j, "", cellFormatApproveBottomRight));
                                }

                            }
                            //End of Count By Box

                            //Start of Checked By Box
                            sheet.mergeCells(3, i+10, 4, i+10);
                            sheet.addCell(new Label(3, i+10, "Checked By",cellFormat));
                            for (int j = i + 11; j < i + 11 + 4; j++) {
                                if (j < i + 11 + 3) {
                                    sheet.addCell(new Label(3, j, "", cellFormatApproveLeft));
                                } else {
                                    sheet.addCell(new Label(3, j, "", cellFormatApproveBottomLeft));
                                }

                            }
                            for (int j = i + 11; j < i + 11 + 4; j++) {
                                if (j < i + 11 + 3) {
                                    sheet.addCell(new Label(4, j, "", cellFormatApproveRight));
                                } else {
                                    sheet.addCell(new Label(4, j, "", cellFormatApproveBottomRight));
                                }

                            }
                            //End of Checked By Box

                            //Start of Approved By Box
                            sheet.mergeCells(5, i+10, 6, i+10);
                            sheet.addCell(new Label(5, i+10, "Approved By",cellFormat));
                            for (int j = i + 11; j < i + 11 + 4; j++) {
                                if (j < i + 11 + 3) {
                                    sheet.addCell(new Label(5, j, "", cellFormatApproveLeft));
                                } else {
                                    sheet.addCell(new Label(5, j, "", cellFormatApproveBottomLeft));
                                }

                            }
                            for (int j = i + 11; j < i + 11 + 4; j++) {
                                if (j < i + 11 + 3) {
                                    sheet.addCell(new Label(6, j, "", cellFormatApproveRight));
                                } else {
                                    sheet.addCell(new Label(6, j, "", cellFormatApproveBottomRight));
                                }

                            }
                            //End of Approved By Box

                            //Start of Audited By Box
                            sheet.mergeCells(7, i+10, 8, i+10);
                            sheet.addCell(new Label(7, i+10, "Audited By",cellFormat));
                            for (int j = i + 11; j < i + 11 + 4; j++) {
                                if (j < i + 11 + 3) {
                                    sheet.addCell(new Label(7, j, "", cellFormatApproveLeft));
                                } else {
                                    sheet.addCell(new Label(7, j, "", cellFormatApproveBottomLeft));
                                }

                            }
                            for (int j = i + 11; j < i + 11 + 4; j++) {
                                if (j < i + 11 + 3) {
                                    sheet.addCell(new Label(8, j, "", cellFormatApproveRight));
                                } else {
                                    sheet.addCell(new Label(8, j, "", cellFormatApproveBottomRight));
                                }

                            }
                            //End of Audited By Box

                            //Close workbook
                            workbook.write();
                            workbook.close();

                            Toast.makeText(getApplication(),"Data Exported in a Excel Sheet (RM)", Toast.LENGTH_SHORT).show();

                        }
                        catch (Exception e) {
                            Toast.makeText(getApplication(),"RM : " +e.getMessage(), Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                    //End of While
                }
                //End of IF
                cursor.close();



            }
            //End of OnClick()
        });
        //End of SetlistenerOnClick
    }
    //End of exportExcel()

}
//End of Auditor Class