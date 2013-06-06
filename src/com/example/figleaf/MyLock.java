package com.example.figleaf;

import static com.example.figleaf.myOpenHelper.*;

import java.io.File;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MyLock extends Activity {

    Button buttonconfirm;
    EditText pwd_in_text;
    int change;              //记录上次改变的位置
    private myOpenHelper mHelper;
    private int[] idArray;
    private String[] PWArray;
    private String[] picPathArray;
    private String[] dirtyArray;
    private String[] nameArray;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lockscreen);
        buttonconfirm = (Button) this.findViewById(R.id.comfirm);
        pwd_in_text = (EditText) this.findViewById(R.id.pwd);
        buttonconfirm.setOnClickListener(listener);
        mHelper = new myOpenHelper(this, DB_NAME, null, 1);
        change = 0;
    }

    Button.OnClickListener listener = new Button.OnClickListener() {

        public void onClick(View v) {
            int index = -1; //记录命中位置
            if (v == buttonconfirm) {
                String pwd = pwd_in_text.getText().toString().trim();
                int count = getInfo();  //获取数据库里一种多少数据
                for (int i = 0; i < count; i++) {                 //查找数据是否有这个密码
                    if (pwd.equals(PWArray[i])) {   //如果命中
                        index = i;
                        break;
                    }
                }
                if (index == -1) {
                    Toast.makeText(MyLock.this, "Wrong Password", Toast.LENGTH_LONG).show();
                } else {
                    if (index != change) {             //如果换密码了
                        Log.i("recover", String.valueOf(change));
                        if (change != 0) {
                            recover(); //恢复被隐藏
                        }
                        dirtyArray[change] = "0";            //置零
                        update(idArray[change], change);//更新数据


                        if (index != 0) {
                            hidefile(index);
                        }                    //隐藏文件
                        dirtyArray[index] = "1";
                        update(idArray[index], index);//更新数据库信息

                    }

                    System.exit(0);
                }


            }

        }
    };

    public int getInfo() {
        SQLiteDatabase db = mHelper.getWritableDatabase();      //获取数据库连接
        Cursor c = db.query(TABLE_NAME, new String[]{ID, NAME, PW, PICPATH, DIRTY}, null, null, null, null, ID);
        int idIndex = c.getColumnIndex(ID);
        int nameIndex = c.getColumnIndex(NAME);
        int pwIndex = c.getColumnIndex(PW);     //获得姓名列的列号
        int picIndex = c.getColumnIndex(PICPATH);
        int dirIndex = c.getColumnIndex(DIRTY);
        idArray = new int[c.getCount()];            //创建存放id的int数组对象
        PWArray = new String[c.getCount()];
        picPathArray = new String[c.getCount()];            //创建存放图片地址的String数组对象
        nameArray = new String[c.getCount()];
        dirtyArray = new String[c.getCount()];
        int i = 0;
        for (c.moveToFirst(); !(c.isAfterLast()); c.moveToNext()) {
            idArray[i] = c.getInt(idIndex);
            nameArray[i] = c.getString(nameIndex);
            PWArray[i] = c.getString(pwIndex);          //将pW添加到String数组中
            picPathArray[i] = c.getString(picIndex);            //将路径添加到String数组中
            dirtyArray[i] = c.getString(dirIndex);
            Log.i(nameArray[i], dirtyArray[i]);
            if (dirtyArray[i].equals("1")) {
                change = i;
                Log.i("getInfo", String.valueOf(change));
            }
            //记录上次使用的用户位置
            i++;
        }
        c.close();              //关闭Cursor对象
        db.close();             //关闭SQLiteDatabase对象
        return i;
    }

    protected void recover() {

        if (!picPathArray[change].equals("0") && !picPathArray[change].equals(null)) {//防止没有内容
            Log.i("recover", picPathArray[change]);
            String path = picPathArray[change];
            File file = new File(path);
            String newpath = file.getParentFile().getParent() + "/" + file.getName();  //改成正确路径路径
            picPathArray[change] = newpath;   //恢复正确地址
            Log.i("recover", newpath);
            File newfile = new File(newpath);
            file.renameTo(newfile);
            file.delete();
            Uri localUri = Uri.fromFile(newfile);//广播消息，刷新相册
            Intent localIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri);
            sendBroadcast(localIntent);

        }


    }

    protected void hidefile(int i) {
        if (!picPathArray[i].equals("0") && !picPathArray[i].equals(null)) {
            File file = new File(picPathArray[i]);                //根据路径，获得文件
            String fpath = file.getParentFile().getPath();
            File newfile = new File(fpath + "/.hide/" + file.getName());   //创立隐藏文件夹
            file.renameTo(newfile);
            String s = newfile.getPath();
            picPathArray[i] = s;                   //更新文件路径
            Log.i("hideFile", s);
            file.delete();
            Uri localUri = Uri.fromFile(file);//广播消息，刷新相册
            Intent localIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri);
            sendBroadcast(localIntent);

        }


    }

    protected void update(int id, int i) {
        SQLiteDatabase db = mHelper.getWritableDatabase();      //获得数据库对象
        ContentValues values = new ContentValues();
        values.put(NAME, nameArray[i]);
        values.put(PW, PWArray[i]);
        values.put(PICPATH, picPathArray[i]);
        values.put(DIRTY, dirtyArray[i]);

        db.update(TABLE_NAME, values, ID + "=?", new String[]{id + ""});    //更新数据
        db.close();

    }


    //@Override
    //public boolean dispatchKeyEvent(KeyEvent event) {
    // TODO Auto-generated method stub
    //return super.dispatchKeyEvent(event);
    //  return true;
    //}


    @Override
    public void onAttachedToWindow() {
        // TODO Auto-generated method stub

        // try to disable home key.
        //this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
        //super.onAttachedToWindow();
    }


}
