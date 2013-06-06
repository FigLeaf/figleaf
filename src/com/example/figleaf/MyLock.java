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
    int change;              //��¼�ϴθı��λ��
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
            int index = -1; //��¼����λ��
            if (v == buttonconfirm) {
                String pwd = pwd_in_text.getText().toString().trim();
                int count = getInfo();  //��ȡ���ݿ���һ�ֶ�������
                for (int i = 0; i < count; i++) {                 //���������Ƿ����������
                    if (pwd.equals(PWArray[i])) {   //�������
                        index = i;
                        break;
                    }
                }
                if (index == -1) {
                    Toast.makeText(MyLock.this, "Wrong Password", Toast.LENGTH_LONG).show();
                } else {
                    if (index != change) {             //�����������
                        Log.i("recover", String.valueOf(change));
                        if (change != 0) {
                            recover(); //�ָ�������
                        }
                        dirtyArray[change] = "0";            //����
                        update(idArray[change], change);//��������


                        if (index != 0) {
                            hidefile(index);
                        }                    //�����ļ�
                        dirtyArray[index] = "1";
                        update(idArray[index], index);//�������ݿ���Ϣ

                    }

                    System.exit(0);
                }


            }

        }
    };

    public int getInfo() {
        SQLiteDatabase db = mHelper.getWritableDatabase();      //��ȡ���ݿ�����
        Cursor c = db.query(TABLE_NAME, new String[]{ID, NAME, PW, PICPATH, DIRTY}, null, null, null, null, ID);
        int idIndex = c.getColumnIndex(ID);
        int nameIndex = c.getColumnIndex(NAME);
        int pwIndex = c.getColumnIndex(PW);     //��������е��к�
        int picIndex = c.getColumnIndex(PICPATH);
        int dirIndex = c.getColumnIndex(DIRTY);
        idArray = new int[c.getCount()];            //�������id��int�������
        PWArray = new String[c.getCount()];
        picPathArray = new String[c.getCount()];            //�������ͼƬ��ַ��String�������
        nameArray = new String[c.getCount()];
        dirtyArray = new String[c.getCount()];
        int i = 0;
        for (c.moveToFirst(); !(c.isAfterLast()); c.moveToNext()) {
            idArray[i] = c.getInt(idIndex);
            nameArray[i] = c.getString(nameIndex);
            PWArray[i] = c.getString(pwIndex);          //��pW��ӵ�String������
            picPathArray[i] = c.getString(picIndex);            //��·����ӵ�String������
            dirtyArray[i] = c.getString(dirIndex);
            Log.i(nameArray[i], dirtyArray[i]);
            if (dirtyArray[i].equals("1")) {
                change = i;
                Log.i("getInfo", String.valueOf(change));
            }
            //��¼�ϴ�ʹ�õ��û�λ��
            i++;
        }
        c.close();              //�ر�Cursor����
        db.close();             //�ر�SQLiteDatabase����
        return i;
    }

    protected void recover() {

        if (!picPathArray[change].equals("0") && !picPathArray[change].equals(null)) {//��ֹû������
            Log.i("recover", picPathArray[change]);
            String path = picPathArray[change];
            File file = new File(path);
            String newpath = file.getParentFile().getParent() + "/" + file.getName();  //�ĳ���ȷ·��·��
            picPathArray[change] = newpath;   //�ָ���ȷ��ַ
            Log.i("recover", newpath);
            File newfile = new File(newpath);
            file.renameTo(newfile);
            file.delete();
            Uri localUri = Uri.fromFile(newfile);//�㲥��Ϣ��ˢ�����
            Intent localIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri);
            sendBroadcast(localIntent);

        }


    }

    protected void hidefile(int i) {
        if (!picPathArray[i].equals("0") && !picPathArray[i].equals(null)) {
            File file = new File(picPathArray[i]);                //����·��������ļ�
            String fpath = file.getParentFile().getPath();
            File newfile = new File(fpath + "/.hide/" + file.getName());   //���������ļ���
            file.renameTo(newfile);
            String s = newfile.getPath();
            picPathArray[i] = s;                   //�����ļ�·��
            Log.i("hideFile", s);
            file.delete();
            Uri localUri = Uri.fromFile(file);//�㲥��Ϣ��ˢ�����
            Intent localIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri);
            sendBroadcast(localIntent);

        }


    }

    protected void update(int id, int i) {
        SQLiteDatabase db = mHelper.getWritableDatabase();      //������ݿ����
        ContentValues values = new ContentValues();
        values.put(NAME, nameArray[i]);
        values.put(PW, PWArray[i]);
        values.put(PICPATH, picPathArray[i]);
        values.put(DIRTY, dirtyArray[i]);

        db.update(TABLE_NAME, values, ID + "=?", new String[]{id + ""});    //��������
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
