package uz.ismoilov.muhriddin.neomemo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by Sarvarkhuja' on 8/6/2017.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ProgrammingKnowledge on 4/3/2015.
 */
public class DBOpenHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "memo.db";
    public static final String TABLE_NAME = "memo_table";
    public static final String COL_1 = "SQL_ID";
    public static final String COL_2 = "FIREBASEPATH";
    public static final String COL_3 = "LASTEDITED";
    public static final String COL_4 = "ISUPDATED";
    public static final String COL_5 = "IMAGENAME";
    public static final String COL_6 = "LOCALPATH";
    public static final String COL_7 = "FireBase";
    public static final String COL_8 = "USERNAME";

    public DBOpenHelper (Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME +" (ID INTEGER PRIMARY KEY AUTOINCREMENT,NAME TEXT,SURNAME TEXT,MARKS INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String firebasePath,String listEdited,String isUpdated,
                              String imageName, String localPath, String firebase, String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,firebasePath);
        contentValues.put(COL_3,listEdited);
        contentValues.put(COL_4,isUpdated);
        contentValues.put(COL_5,imageName);
        contentValues.put(COL_6,localPath);
        contentValues.put(COL_7,firebase);
        contentValues.put(COL_8,username);
        long result = db.insert(TABLE_NAME,null ,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME,null);
        return res;
    }

    public boolean updateData(String id, String firebasePath,String listEdited,String isUpdated, String imageName,
                              String localPath, String firebase, String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, id);
        contentValues.put(COL_2,firebasePath);
        contentValues.put(COL_3,listEdited);
        contentValues.put(COL_4,isUpdated);
        contentValues.put(COL_5,imageName);
        contentValues.put(COL_6,localPath);
        contentValues.put(COL_7,firebase);
        contentValues.put(COL_8,username);

        db.update(TABLE_NAME, contentValues, "ID = ?",new String[] { id });
        return true;
    }

    public Integer deleteData (String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = ?",new String[] {id});
    }
}