package com.example.myappSQLite;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MyActivity extends Activity implements OnClickListener{

    private final static String TAG = "MyActivity";

    // 声明五个控件对象
    Button createDatabase=null;
    Button updateDatabase=null;
    Button insert=null;
    Button update=null;
    Button query=null;
    Button delete=null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        findViews();
    }

    private void findViews() {
        //根据控件ID得到控件
        createDatabase = (Button) this.findViewById(R.id.createDatabase);
        updateDatabase = (Button) this.findViewById(R.id.updateDatabase);
        insert = (Button) this.findViewById(R.id.insert);
        update = (Button) this.findViewById(R.id.update);
        query = (Button) this.findViewById(R.id.query);
        delete = (Button) this.findViewById(R.id.delete);
        //添加监听器
        createDatabase.setOnClickListener(this);
        updateDatabase.setOnClickListener(this);
        insert.setOnClickListener(this);
        update.setOnClickListener(this);
        query.setOnClickListener(this);
        delete.setOnClickListener(this);
    }

    private int number = 0;

    @Override
    public void onClick(View v) {
        //判断所触发的被监听控件，并执行命令
        switch(v.getId()){

            //创建数据库
            case R.id.createDatabase:
                //创建一个DatabaseHelper对象
                DatabaseHelper dbHelper1 = new DatabaseHelper(MyActivity.this, "test_db");

                //取得一个只读的数据库对象
                SQLiteDatabase db1 = dbHelper1.getReadableDatabase();

                break;
            //更新数据库，进行版本升级
            case R.id.updateDatabase:
                DatabaseHelper dbHelper2 = new DatabaseHelper(MyActivity.this, "test_db", 2);
                SQLiteDatabase db2 = dbHelper2.getReadableDatabase();

                break;
            //插入数据
            case R.id.insert:
                //创建存放数据的ContentValues对象
                ContentValues values = new ContentValues();

                //像ContentValues中存放数据
                String time = String.valueOf(System.currentTimeMillis());
                values.put("id", time);
                values.put("userId", "zone_date");
                values.put("json","zhangsazhangsanzhangsann" + time);
                values.put("status", 1);
                DatabaseHelper dbHelper3 = new DatabaseHelper(MyActivity.this, "test_db");
                SQLiteDatabase db3 = dbHelper3.getWritableDatabase();

                //数据库执行插入命令
                db3.insert("uset_chat_info", null, values);
                break;

            //更新数据信息
            case R.id.update:
                DatabaseHelper dbHelper4 = new DatabaseHelper(MyActivity.this, "test_db");
                SQLiteDatabase db4 = dbHelper4.getWritableDatabase();
                ContentValues values2 = new ContentValues();
                values2.put("json", "111111111111111");
//                db4.update("uset_chat_info", values2, "id=?", new String[]{"123456"});
                break;

            //查询信息
            case R.id.query:
                DatabaseHelper dbHelper5 = new DatabaseHelper(MyActivity.this, "test_db");
                SQLiteDatabase db5 = dbHelper5.getReadableDatabase();

                DatabaseHelper dbHelper6 = new DatabaseHelper(MyActivity.this, "test_db");
                SQLiteDatabase db6 = dbHelper6.getReadableDatabase();

                /**
                public android.database.Cursor query(
                    java.lang.String table,
                    java.lang.String[] columns,
                    java.lang.String selection,
                    java.lang.String[] selectionArgs,
                    java.lang.String groupBy,
                    java.lang.String having,
                    java.lang.String orderBy,
                    java.lang.String limit) // 要显示多少条记录
                 */

                //创建游标对象
//                Cursor cursor = db5.query("uset_chat_info",
//                        new String[]{"id", "userId", "json", "status"}, "userId=?",
//                        new String[]{"zone_date"}, null, null, null, "10");

                Cursor cursor = db6.rawQuery("select count(*)from uset_chat_info where userId=?", new String[]{"zone_date"});

                //游标移到第一条记录准备获取数据
                cursor.moveToFirst();

                // 获取数据中的LONG类型数据
                Long count = cursor.getLong(0);

                int readNumber = (count >= 5 + number)?5: (int) (count - number);
                if (readNumber <= 0) return;

                cursor = db6.rawQuery("select * from uset_chat_info where userId=? limit " + readNumber + " offset " + (count - 5 - number),
                        new String[]{"zone_date"});

                //利用游标遍历所有数据对象
                while(cursor.moveToNext()){
                    String json = cursor.getString(cursor.getColumnIndex("json"));

                    //日志打印输出
                    Log.i(TAG,"query-->" + json);
                }

                number += 5;
                db6.close();
                break;
            //删除记录
            case R.id.delete:
                DatabaseHelper dbHelper7 = new DatabaseHelper(MyActivity.this, "test_db");
                SQLiteDatabase db7 = dbHelper7.getWritableDatabase();
                db7.delete("uset_chat_info", "userId=?", new String[]{"zone_date"});
                break;
            default:
                Log.i(TAG,"error");
                break;
        }
    }
}
