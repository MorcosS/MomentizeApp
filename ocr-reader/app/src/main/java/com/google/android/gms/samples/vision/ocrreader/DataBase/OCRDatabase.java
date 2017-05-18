package com.google.android.gms.samples.vision.ocrreader.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by MorcosS on 9/10/16.
 */

public class OCRDatabase extends SQLiteOpenHelper {
    private static final String CREATE_TABLE_OCR = "CREATE TABLE "
            + "OCR_Item" + "(" + "ID" + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + "OCR_Letter"
            + " STRING,"+"OCR_Number String,"+"Ocr_Repeatance Integer"+")";
    public OCRDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_OCR);
    }
    public OCRDatabase(Context context) {
        super(context, "OCR_Item", null, 1);

    }

    public boolean insertOCR (String OCR_Letter,String OCR_Number,int Ocr_Repeatance){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("OCR_Letter",OCR_Letter);
        values.put("OCR_Number",OCR_Number);
        values.put("Ocr_Repeatance", Ocr_Repeatance);
        long OCR_row = db.insert("OCR_Item", null, values);
       // db.close();
        if (OCR_row==-1){
            return false;
        }else{
            return true;
        }
    }

    public String getOCR(String OCR_Letter) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT OCR_Number"  + " FROM OCR_Item where OCR_Letter = '"+OCR_Letter
                +"' ORDER BY "+"OCR_Repeatance DESC";

        Cursor c = db.rawQuery(selectQuery, null);
       // db.close();
        if (c == null || ! c.moveToFirst()) {
            return null;
        }
        return c.getString(0);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public int getOCRID(String OCR_Letter,String OCR_Number){
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT ID"  + " FROM OCR_Item where OCR_Letter = '"+OCR_Letter+"' and OCR_Number="+OCR_Number;
        Cursor c = db.rawQuery(selectQuery, null);
      //  db.close();
        if (c == null || ! c.moveToFirst()) {
            return -1;
        }
        return c.getInt(0);
    }

    public int getOCR_Repeatance(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT OCR_Repeatance"  + " FROM OCR_Item where ID = "+id;
        Cursor c = db.rawQuery(selectQuery, null);
     //   db.close();
        if (c == null || ! c.moveToFirst()) {
            return -1;
        }
    return c.getInt(0);
    }

    public boolean UpdateOCR(int id,int newRepeatance){
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("OCR_Repeatance", newRepeatance);
        long OCR_row = db.update("OCR_Item",values,"ID = "+id,null);
     //   db.close();
        if (OCR_row==-1){
            return false;
        }else{
            return true;
        }
    }
}
