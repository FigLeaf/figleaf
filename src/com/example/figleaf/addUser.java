package com.example.figleaf;

import static com.example.figleaf.myOpenHelper.*;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class addUser extends Activity implements OnClickListener {
	Button buttonSelectHide;
	Button buttonAddYes,buttonAddNo;
	EditText textUserName,textUserPW,textUserPW2;
	myOpenHelper myHelper;

	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_user_add);
		myHelper = new myOpenHelper(this, myOpenHelper.DB_NAME, null, 1);
		buttonSelectHide=(Button)this.findViewById(R.id.selectHide);
		buttonSelectHide.setOnClickListener(this);
		buttonAddYes=(Button)this.findViewById(R.id.addYes);
		buttonAddNo=(Button)this.findViewById(R.id.addNo);
		buttonAddYes.setOnClickListener(this);
		buttonAddNo.setOnClickListener(this);
		textUserName=(EditText)this.findViewById(R.id.userName);
		textUserPW=(EditText)this.findViewById(R.id.userPW);
		textUserPW2=(EditText)this.findViewById(R.id.userPW2);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void onClick(View v){
		if(v==buttonSelectHide){
			createDialog();
		}else if(v==buttonAddNo){
	        finish();
		}else if(v==buttonAddYes){
			String [] strArray=new String[3];
			strArray[0]=textUserName.getText().toString().trim();   //��ȡ������
			strArray[1]=textUserPW.getText().toString().trim();
			strArray[2]=textUserPW2.getText().toString().trim();
			SQLiteDatabase db = myHelper.getWritableDatabase();	//������ݿ����
			Cursor c = db.query(myOpenHelper.TABLE_NAME,         //����Ƿ�������rootPW
					new String[]{NAME,PW}, ID+"=?", new String[]{1+""}, null, null, null); //Ѱ��root�Ĵ��λ��
			
			if(c.getCount()==0){
				Toast.makeText(addUser.this, "��������root���룡", Toast.LENGTH_LONG).show();	
			}else if(!strArray[1].equals(strArray[2])){                //���ж����������Ƿ���ͬ
				Toast.makeText(addUser.this, "�����������벻��ͬ��", Toast.LENGTH_LONG).show();	
			}else{
					ContentValues values = new ContentValues();
					values.put(NAME, strArray[0]);
					values.put(PW, strArray[1]);     //��������
					long count = db.insert(TABLE_NAME, ID, values);			//��������
					db.close();
					if(count == -1){
						Toast.makeText(this, "�����û�ʧ�ܣ�", Toast.LENGTH_LONG).show();
					}
					else{
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
	               }
	            });
	    builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int which) {
	    				dialog.dismiss();
	                }
	            });
	    builder.create().show();
	}
}
