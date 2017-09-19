package systembatarka.com.batarka.app.batarkasystem.Data;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

/**
 * Created by MorcosS on 8/6/16.
 */
public class DBHelperScanedIDs extends SQLiteOpenHelper {
    private static final String CREATE_TABLE_Movies = "CREATE TABLE "
            + "Scanned_Items" + "(" + "Scanned_ID" + " INTEGER," +"Scanned_Category" + " INTEGER)";

    public DBHelperScanedIDs(Context context) {
        super(context, "Scanned_Items", null, 1);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public DBHelperScanedIDs(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_Movies);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public boolean addOrder (int Scanned_ID, int Scanned_Category){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Scanned_ID",Scanned_ID);
        values.put("Scanned_Category",Scanned_Category);
        long movie_row = db.insert("Scanned_Items", null, values);
        db.close(); // Closing database connection
        if (movie_row==-1){
            return false;
        }else{
            return true;
        }
    }

    public Cursor getOrder(int category) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * "  + " FROM Scanned_Items where Scanned_Category = "+category;
        Cursor c = db.rawQuery(selectQuery, null);
        if (c == null || ! c.moveToFirst()) return null;
        return c;
    }

    public void deleteOrder(int id,int code){
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete("Scanned_Items","Scanned_ID = "+id+" and Scanned_Category= "+code,null);

    }


}
