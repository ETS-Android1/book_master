package com.example.book_master_2;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class local_db {
    private Context context;
    private SQLiteDatabase database;
    private sqllite dbHelper;

    public local_db(Context c) {
        this.context = c;
    }

    public local_db open() throws SQLException {
        this.dbHelper = new sqllite(this.context);
        this.database = this.dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        this.dbHelper.close();
    }

    public void insert(String title, String description, String pageid, String thumb, String date, String time, String STATUS, String TYPE) {
        ContentValues contentValue = new ContentValues();

        contentValue.put(sqllite.title, title);
        contentValue.put(sqllite.description, description);
        contentValue.put(sqllite.pageid, pageid);
        contentValue.put(sqllite.thumb, thumb);
        contentValue.put(sqllite.date, date);
        contentValue.put(sqllite.time, time);
        contentValue.put(sqllite.SYNC_STATUS,  "YES");
        contentValue.put(sqllite.type,  TYPE);
        contentValue.put(sqllite.STATUS ,   STATUS);

        this.database.insert(sqllite.TABLE_ARTICLE, null, contentValue);
    }



    public Cursor GetAll(String type){
        // SQLiteDatabase db = this.getWritableDatabase();
        String idd="";
        String query1 = "SELECT * from "+ sqllite.TABLE_ARTICLE+" where type ='"+ type +"' order by id DESC ";
        Cursor cursor = database.rawQuery(query1,null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor GetVbyID_Sync(String TXNID){
        // SQLiteDatabase db = this.getWritableDatabase();
        String idd="";
        String query1 = "SELECT * from "+ sqllite.TABLE_ARTICLE+" where pageid ='"+ TXNID +"'";
        Cursor cursor = database.rawQuery(query1,null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }


}