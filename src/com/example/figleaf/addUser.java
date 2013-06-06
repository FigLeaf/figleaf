package com.example.figleaf;

import static com.example.figleaf.myOpenHelper.*;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class addUser extends Activity implements OnClickListener {
    private static final String TAG = "PIC";
    Button buttonSelectHide;
    Button buttonAddYes, buttonAddNo;
    EditText textUserName, textUserPW, textUserPW2;
    myOpenHelper myHelper;
    String picPath;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_user_add);
        myHelper = new myOpenHelper(this, myOpenHelper.DB_NAME, null, 1);
        buttonSelectHide = (Button) this.findViewById(R.id.selectHide);
        buttonSelectHide.setOnClickListener(this);
        buttonAddYes = (Button) this.findViewById(R.id.addYes);
        buttonAddNo = (Button) this.findViewById(R.id.addNo);
        buttonAddYes.setOnClickListener(this);
        buttonAddNo.setOnClickListener(this);
        textUserName = (EditText) this.findViewById(R.id.userName);
        textUserPW = (EditText) this.findViewById(R.id.userPW);
        textUserPW2 = (EditText) this.findViewById(R.id.userPW2);
        picPath = new String();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void onClick(View v) {
        if (v == buttonSelectHide) {
            createDialog();
        } else if (v == buttonAddNo) {
            finish();
        } else if (v == buttonAddYes) {
            String[] strArray = new String[3];
            strArray[0] = textUserName.getText().toString().trim();
            strArray[1] = textUserPW.getText().toString().trim();
            strArray[2] = textUserPW2.getText().toString().trim();
            SQLiteDatabase db = myHelper.getWritableDatabase(); //�����ݿ����
            Cursor c = db.query(myOpenHelper.TABLE_NAME,         //����Ƿ�������rootPW
                    new String[]{NAME, PW}, ID + "=?", new String[]{1 + ""}, null, null, null); //Ѱ��root�Ĵ��λ��

            if (c.getCount() == 0) {
                Toast.makeText(addUser.this, "��������root���룡", Toast.LENGTH_LONG).show();
            } else if (!strArray[1].equals(strArray[2])) {                //���ж����������Ƿ���ͬ
                Toast.makeText(addUser.this, "�����������벻��ͬ��", Toast.LENGTH_LONG).show();
            } else {
                ContentValues values = new ContentValues();
                values.put(NAME, strArray[0]);
                values.put(PW, strArray[1]);//��������
                values.put(PICPATH, picPath);//����ͼƬ��ַ
                values.put(DIRTY, "0");
                long count = db.insert(TABLE_NAME, ID, values);         //�������
                db.close();
                if (count == -1) {
                    Toast.makeText(this, "�����û�ʧ�ܣ�", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "�����û��ɹ���", Toast.LENGTH_LONG).show();
                }
            }
        }

    }

    protected void createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.selectHide)
                .setItems(R.array.selectItems, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        if (which == 0) {                       //������ᣬѡ��ͼƬ
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(intent, 1);
                        }

                    }
                });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_OK) {        //�˴��� RESULT_OK ��ϵͳ�Զ����һ������

            Log.e(TAG, "ActivityResult resultCode error");

            return;

        }
        Bitmap bm = null;
        //���ĳ������ContentProvider���ṩ��� ����ͨ��ContentResolver�ӿ�
        ContentResolver resolver = getContentResolver();
        //�˴��������жϽ��յ�Activity�ǲ�������Ҫ���Ǹ�
        if (requestCode == 1) {
            try {
                Uri originalUri = data.getData();        //���ͼƬ��uri
                bm = MediaStore.Images.Media.getBitmap(resolver, originalUri);        //�Եõ�bitmapͼƬ
                //���￪ʼ�ĵڶ����֣���ȡͼƬ��·����
                String[] proj = {MediaStore.Images.Media.DATA};
                //������android��ý����ݿ�ķ�װ�ӿڣ�����Ŀ�Android�ĵ�
                @SuppressWarnings("deprecation")
                Cursor cursor = managedQuery(originalUri, proj, null, null, null);
                //���Ҹ������ ����ǻ���û�ѡ���ͼƬ������ֵ
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                //�����������ͷ ���������Ҫ����С�ĺ���������Խ��
                cursor.moveToFirst();
                //���������ֵ��ȡͼƬ·��
                String path = cursor.getString(column_index);
                Toast.makeText(this, path, Toast.LENGTH_LONG).show();
                picPath = path;                         //��¼ͼƬ·��
//              File file = new File(path);                //���·��������ļ�
//              String fpath=file.getParentFile().getPath();
//              Toast.makeText(this, fpath, Toast.LENGTH_LONG).show();
//              String fname=file.getName();
//              Toast.makeText(this, fname, Toast.LENGTH_LONG).show();
//              File newfile = new File(fpath + "/" +fname+"x");
//              file.renameTo(newfile);
//              Toast.makeText(this, newfile.getName(), Toast.LENGTH_LONG).show();
//              file.delete();  
//              Uri localUri = Uri.fromFile(file);//�㲥��Ϣ��ˢ�����
//              Intent localIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri);
//              sendBroadcast(localIntent);
            } catch (IOException e) {

                Log.e(TAG, e.toString());

            }

        }


    }
}
