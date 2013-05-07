package com.example.figleaf;

import static com.example.figleaf.myOpenHelper.*;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class setRootPW  extends Activity implements OnClickListener{
	myOpenHelper myHelper;
	EditText textOldPW,textNewPW,textNewPW2;
	Button buttonYes,buttonNo;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.root_set);
		myHelper = new myOpenHelper(this, myOpenHelper.DB_NAME, null, 1);
		textOldPW=(EditText)this.findViewById(R.id.oldPW);
		textNewPW=(EditText)this.findViewById(R.id.newPW);
		textNewPW2=(EditText)this.findViewById(R.id.newPW2);
		buttonYes=(Button)this.findViewById(R.id.rootYes);
		buttonNo=(Button)this.findViewById(R.id.rootNo);
		buttonYes.setOnClickListener(this);
		buttonNo.setOnClickListener(this);
		
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = myHelper.getWritableDatabase();	 //获得数据库对象
		Cursor c = db.query(myOpenHelper.TABLE_NAME,         //将root密码存入1号位
				new String[]{NAME,PW}, ID+"=?", new String[]{1+""}, null, null, null); //寻找root的存放位置
		if(c.getCount()==0){                                 //如果没有先创建一个root
			ContentValues values = new ContentValues(); 
			values.put(NAME, "root");
			values.put(PW, "123456");                        //初始密码
			db.insert(TABLE_NAME, ID, values);			     //插入数据
			c = db.query(myOpenHelper.TABLE_NAME,           //将root密码存入1号位
						new String[]{NAME,PW}, ID+"=?", new String[]{1+""}, null, null, null); //寻找root的存放位置
		}
		c.moveToFirst();
		String oldRootPW=c.getString(1);               //获取旧密码
		
		if(v==buttonNo){
	        finish();
		}
		else if(v==buttonYes){                  //change the root PW
			String [] strArray=new String[3];
			strArray[0]=textOldPW.getText().toString().trim();   //获取旧密码
			strArray[1]=textNewPW.getText().toString().trim();
			strArray[2]=textNewPW2.getText().toString().trim();
			if(!strArray[1].equals(strArray[2])){                 //如果密码两次不同
				Toast.makeText(setRootPW.this, "两次密码输入不相同！", Toast.LENGTH_LONG).show();
			}else if(!oldRootPW.equals(strArray[0])){                                           //如果密码错误
				Toast.makeText(setRootPW.this, "原密码输入错误！", Toast.LENGTH_LONG).show();
			}else{                                                      
				ContentValues values = new ContentValues();
				String rootName="root";          //此名字是默认的，不用设置
				values.put(NAME, rootName);
				values.put(PW, strArray[1]);     //存入密码
				int count = db.update(TABLE_NAME, values, ID+"=?", new String[]{1+""});	//更新数据库 存到id=1的地方
				if(count == -1){
					Toast.makeText(this, "修改密码失败！", Toast.LENGTH_LONG).show();
				}
				else{
					Toast.makeText(this, "修改密码成功！", Toast.LENGTH_LONG).show();
				}
				}
			c.close();
			db.close();
		}
		
	}

}
