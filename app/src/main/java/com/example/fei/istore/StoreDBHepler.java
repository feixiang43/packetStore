package com.example.fei.istore;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by fei on 2017/6/4.
 */

public class StoreDBHepler extends SQLiteOpenHelper {

    private static final String TAG = "DBHelper";
    private static final String TABLE_NAME = "store_table";

    public StoreDBHepler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        Log.i(TAG,"creating db table");
        String createSQL = "create table " + TABLE_NAME +" (storeID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, password varchar(20), isEmpty INTEGER)";
        db.execSQL(createSQL);
        initTable(db, 1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        String sql = "DROP TABLE IF EXISTS "  + TABLE_NAME;
        db.execSQL(sql);
        onCreate(db);
    }

    /*
    * table init onCreate called
    * */
    public void initTable(SQLiteDatabase db , int totalDevice){
        int totalStore = totalDevice*12;
        int i=1;
        while(i <= totalStore){
            insert(db);
            i++;
        }
    }

    /*
    * get all table row
    * */
    public Cursor selectAll() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
        return cursor;
    }

    public Cursor selectByIndex(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        String where = "storeID = ?";
        String[] whereArgs = {Integer.toString(id)};
        String[] columns = {"storeID", "password", "isEmpty"};
        Cursor cursor = db.query(TABLE_NAME, columns, where, whereArgs, null, null, null);
        return cursor;
    }

    /*
    * get first empty row
    * */
    public Cursor getEmptyRow(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from store_table where isEmpty=1 order by storeID ASC", null);

        return cursor;
    }

    /*
    * private method add a new row
    * can not called out of this class
    * */
    private long insert(SQLiteDatabase db)
    {
        /* ContentValues */
        ContentValues cv = new ContentValues();
        cv.put("password", "");
        cv.put("isempty", 1);
        long row = db.insert(TABLE_NAME, null, cv);
        return row;
    }

    //删除操作
    public void deleteByIndex(int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = " storeID = ?";
        String[] whereValue ={ Integer.toString(id) };
        db.delete(TABLE_NAME, where, whereValue);
    }

    //修改操作
    public void updateByIndex(int id, String password ,int isEmpty)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = " storeID = ?";
        String[] whereValue = { Integer.toString(id) };
        ContentValues cv = new ContentValues();
        cv.put("password", password);
        cv.put("isEmpty", isEmpty);
        db.update(TABLE_NAME, cv, where, whereValue);
    }

    public void cleanByIndex(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String where = " storeID = ?";
        String[] whereValue = { Integer.toString(id) };
        ContentValues cv = new ContentValues();
        cv.put("password", "");
        cv.put("isEmpty", 1);
        db.update(TABLE_NAME, cv, where, whereValue);
    }

    public void cleanAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from store_table", null);
        int num = cursor.getCount();
        for (int i = 1; i <=num; i++){
            String where = " storeID = ?";
            String[] whereValue = { Integer.toString(i) };
            ContentValues cv = new ContentValues();
            cv.put("password", "");
            cv.put("isEmpty", 1);
            db.update(TABLE_NAME, cv, where, whereValue);
        }
    }
}
