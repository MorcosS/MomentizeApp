package  com.google.android.gms.samples.vision.ocrreader.DataBase;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

/**
 * Created by MorcosS on 7/30/16.
 */
public class DBHelper extends SQLiteOpenHelper {


    private static final String CREATE_TABLE_Reading = "CREATE TABLE "
            + "Reading_Item" + "(" + "ID" + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + "Reading_No"
            + " STRING,"+"Person_ID String,"+"Reading_Time String"+")";

    public DBHelper(Context context) {
        super(context, "Order_Items", null, 1);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_Reading);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public boolean addOrder (String Reading_NO,String Person_ID,String Reading_Time){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Reading_Time",Reading_Time);
        values.put("Person_ID",Person_ID);
        values.put("Reading_No", Reading_NO);
        long movie_row = db.insert("Reading_Item", null, values);
        db.close(); // Closing database connection
        if (movie_row==-1){
            return false;
        }else{
            return true;
        }
    }

    public Cursor getOrder() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * "  + " FROM Reading_Item";
        Cursor c = db.rawQuery(selectQuery, null);
        if (c == null || ! c.moveToFirst()) return null;
        return c;
    }


    public void deleteOrder(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete("Reading_Item","ID = "+id,null);
    }

}
