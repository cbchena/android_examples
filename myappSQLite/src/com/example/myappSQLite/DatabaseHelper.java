package com.example.myappSQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper{

    private static final int VERSION = 2;
    private static final String TAG = "DatabaseHelper";

    // 带全部参数的构造函数，此构造函数必不可少
    public DatabaseHelper(Context context, String name, CursorFactory factory,
                          int version) {
        super(context, name, factory, version);

    }

    // 带两个参数的构造函数，调用的其实是带三个参数的构造函数
    public DatabaseHelper(Context context,String name){
        this(context, name, VERSION);
    }

    // 带三个参数的构造函数，调用的是带所有参数的构造函数
    public DatabaseHelper(Context context, String name, int version){
        this(context, name, null,version);
    }
    //创建数据库
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "create a Database");

        //创建数据库sql语句
        String sql = "create table uset_chat_info(id varchar(50), userId varchar(50), json varchar(500), status int)";

        //执行创建数据库操作
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //创建成功，日志输出提示
        Log.i(TAG,"update a Database");
    }

}

