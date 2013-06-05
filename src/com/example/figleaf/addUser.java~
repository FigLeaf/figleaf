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
	Button buttonAddYes,buttonAddNo;
	EditText textUserName,textUserPW,textUserPW2;
	myOpenHelper myHelper;
	String picPath;

	
	
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
		picPath=new String();
		
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
			strArray[0]=textUserName.getText().toString().trim();  
			strArray[1]=textUserPW.getText().toString().trim();
			strArray[2]=textUserPW2.getText().toString().trim();
			SQLiteDatabase db = myHelper.getWritableDatabase();	//获得数据库对象
			Cursor c = db.query(myOpenHelper.TABLE_NAME,         //检查是否先设置rootPW
					new String[]{NAME,PW}, ID+"=?", new String[]{1+""}, null, null, null); //寻找root的存放位置
			
			if(c.getCount()==0){
				Toast.makeText(addUser.this, "请先设置root密码！", Toast.LENGTH_LONG).show();	
			}else if(!strArray[1].equals(strArray[2])){                //先判断两次密码是否相同
				Toast.makeText(addUser.this, "两次密码输入不相同！", Toast.LENGTH_LONG).show();	
			}else{
					ContentValues values = new ContentValues();
					values.put(NAME, strArray[0]);
					values.put(PW, strArray[1]);//存入密码
					values.put(PICPATH, picPath);//存入图片地址
					values.put(DIRTY, "0");
					long count = db.insert(TABLE_NAME, ID, values);			//插入数据
					db.close();
					if(count == -1){
						Toast.makeText(this, "创建用户失败！", Toast.LENGTH_LONG).show();
					}
					else{
						Toast.makeText(this, "创建用户成功！", Toast.LENGTH_LONG).show();
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
	            	   if(which==0){                       //进入相册，选择图片
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
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data){

	   	if (resultCode != RESULT_OK) {        //此处的 RESULT_OK 是系统自定义得一个常量

	   	    Log.e(TAG,"ActivityResult resultCode error");

	        return;

	    }
	    Bitmap bm = null;
	    //外界的程序访问ContentProvider所提供数据 可以通过ContentResolver接口
	    ContentResolver resolver = getContentResolver();
	    //此处的用于判断接收的Activity是不是你想要的那个
	    if (requestCode == 1) {
	        try {
	            Uri originalUri = data.getData();        //获得图片的uri
	            bm = MediaStore.Images.Media.getBitmap(resolver, originalUri);        //显得到bitmap图片
	            //这里开始的第二部分，获取图片的路径：
	            String[] proj = {MediaStore.Images.Media.DATA};
	            //好像是android多媒体数据库的封装接口，具体的看Android文档
	            @SuppressWarnings("deprecation")
				Cursor cursor = managedQuery(originalUri, proj, null, null, null);
	            //按我个人理解 这个是获得用户选择的图片的索引值
	            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	            //将光标移至开头 ，这个很重要，不小心很容易引起越界
	            cursor.moveToFirst();
	            //最后根据索引值获取图片路径
	            String path = cursor.getString(column_index);
	            Toast.makeText(this, path, Toast.LENGTH_LONG).show();
	            picPath=path;                         //记录图片路径
//	            File file = new File(path);                //根据路径，获得文件
//	            String fpath=file.getParentFile().getPath();
//	            Toast.makeText(this, fpath, Toast.LENGTH_LONG).show();
//	            String fname=file.getName();
//	            Toast.makeText(this, fname, Toast.LENGTH_LONG).show();
//	            File newfile = new File(fpath + "/" +fname+"x");
//	            file.renameTo(newfile);
//	            Toast.makeText(this, newfile.getName(), Toast.LENGTH_LONG).show();
//	            file.delete();	
//	            Uri localUri = Uri.fromFile(file);//广播消息，刷新相册
//	            Intent localIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri);
//	            sendBroadcast(localIntent);
	        }catch (IOException e) {

	            Log.e(TAG,e.toString()); 

	        }

	    }

	 

	}
}
