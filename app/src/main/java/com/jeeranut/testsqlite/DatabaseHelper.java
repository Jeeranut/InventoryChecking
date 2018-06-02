package com.jeeranut.testsqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.AdapterView;

/**
 * Created by jcheewj on 05/01/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "student.db";
    public static final String TABLE_NAME = "tb_student";
    public static final String tblParts = "parts";
    public static final String tblWips  = "wips";
    public static final String tblRms = "rms";
    public static final String tblJobTypes = "jobtypes";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+ TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,NAME TEXT,SURNAME TEXT,MARKS INTEGER)" );
        db.execSQL("CREATE TABLE "+ tblParts + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,partname TEXT,pid INTEGER,qty INTEGER,series TEXT,jobtype INTEGER,status INTEGER,CONSTRAINT UN_Part UNIQUE (partname,series))" );
        db.execSQL("CREATE TABLE "+ tblRms + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,processname TEXT,location TEXT,time_added DEFAULT (datetime('now','localtime')),CONSTRAINT UN_Rm UNIQUE (processname,location))" );
        db.execSQL("CREATE TABLE "+ tblWips + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,workorder TEXT,model TEXT,time_added DEFAULT (datetime('now','localtime')),CONSTRAINT UN_Wip UNIQUE (workorder))" );
        db.execSQL("CREATE TABLE "+ tblJobTypes + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,typename TEXT UNIQUE)" );
        db.execSQL("INSERT INTO " + tblJobTypes + "(typename) VALUES('wip')" );
        db.execSQL("INSERT INTO " + tblJobTypes + "(typename) VALUES('rm')" );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + tblParts);
        db.execSQL("DROP TABLE IF EXISTS " + tblRms);
        db.execSQL("DROP TABLE IF EXISTS " + tblWips);
        db.execSQL("DROP TABLE IF EXISTS " + tblJobTypes);
        onCreate(db);
    }

    public void delete()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + TABLE_NAME + "'");
        db.delete(tblParts, null, null);
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + tblParts + "'");
        db.delete(tblRms, null, null);
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + tblRms + "'");
        db.delete(tblWips, null, null);
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + tblWips + "'");

        db.close();
    }

    public boolean insertData(String name , String surname,String mark)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME",name);
        contentValues.put("SURNAME",surname);
        contentValues.put("MARKS",mark);

        long res = db.insert(TABLE_NAME,null,contentValues);
        if(res == -1) {
            return false;
        }
        else{
            return true;
        }
    }



    public Cursor getAllData()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM "+ TABLE_NAME,null);
        return res;
    }

    public Cursor getLatestData(String table)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM "+ table + " ORDER BY ID DESC LIMIT 1",null);
        return res;
    }


    public Cursor select(String sql)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery(sql,null);
        return res;
    }

    public boolean updatePartsStatus(String partname,String series) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("status",1);
        long res = db.update(tblParts, contentValues, "partname ='"+partname+"' AND series='"+series+"'",null);

        if(res == 0) {
            return false;
        }
        else
        {
            return true;
        }
    }

    public boolean insertParts(String partname,String pid,String series,String qty,int jobtype,int status)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("partname",partname);
        contentValues.put("pid",pid);
        contentValues.put("qty",qty); //int
        contentValues.put("series",series);
        contentValues.put("jobtype",jobtype); //int
        contentValues.put("status",status); //int

        long res = db.insert("parts",null,contentValues);
        if(res == -1)
        {
            return false;
        }
        else
        {
            return true;
        }

//        db.insertOrThrow();
//        try {
//            db.execSQL("INSERT INTO parts (partname,pid,qty,series,jobtype,status) VALUES ('" + partname + "'," + pid + "," + qty + ",'" + series + "'," + jobtype + "," + status + ")");
//        }
//        catch(NumberFormatException nfe) {
//            System.out.println("Could not parse " + nfe);
//            return false;
//        }


    }

    public Boolean insertWips(String workorder,String model)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("workorder",workorder);
        contentValues.put("model",model);

        long res = db.insert("wips",null,contentValues);
        if(res == -1) {
            return false;
        }
        else{
//            Cursor c = db.rawQuery("SELECT * FROM wips ORDER BY ID DESC LIMIT 1",null);
            return true;
        }
    }


    public Boolean insertRms(String processname,String location){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("processname",processname);
        contentValues.put("location",location);

        long res = db.insert("rms",null,contentValues);
        if(res == -1) {
            return false;
        }
        else{
//            Cursor c = db.rawQuery("SELECT * FROM wips ORDER BY ID DESC LIMIT 1",null);
            return true;
        }
    }

    public String selectWipId(String workorder)
    {
        String pid = "0";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM wips WHERE workorder='"+workorder+"' ORDER BY ID DESC LIMIT 1",null);

        while (c.moveToNext()) {
            pid = c.getString(0);
        }
        return pid;
    }
    public String selectRmId(String processname,String location)
    {
        String pid = "0";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM rms WHERE processname='"+ processname +"' AND location='" + location + "' ORDER BY ID DESC LIMIT 1",null);

        while (c.moveToNext()) {
            pid = c.getString(0);
        }
        return pid;
    }

    public Cursor selectWorkOrderName()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT workorder FROM wips",null);


        return c;
    }

    public Cursor selectProcessName()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT DISTINCT processname FROM "+ tblRms + "",null);

        return c;
    }

    public Cursor selectLocation(String processname)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT location FROM "+ tblRms + " WHERE processname='"+processname+"'",null);

        return c;
    }

    public boolean deletePart(String partname , String series)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("status",1);

        return db.delete(tblParts, "partname='" + partname+"' AND series='"+series+"'" , null) > 0;

    }

    public boolean updateWip(String pid,String eWorkOrder,String eModel)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("workorder",eWorkOrder);
        contentValues.put("model",eModel);

        long res = db.update(tblWips, contentValues, "id='"+pid+"'",null);

        if(res == 0) {
            return false;
        }
        else
        {
            return true;
        }
    }

    public boolean updateRm(String pid,String eProcessName,String eLocation)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("processname",eProcessName);
        contentValues.put("location",eLocation);

        long res = db.update(tblRms, contentValues, "id='"+pid+"'",null);

        if(res == 0) {
            return false;
        }
        else
        {
            return true;
        }
    }

}
