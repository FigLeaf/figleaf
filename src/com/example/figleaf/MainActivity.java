package com.example.figleaf;

import static com.example.figleaf.myOpenHelper.*;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity implements OnClickListener {
    Button setRootButton,addUserButton;
    myOpenHelper mHelper;
    String [] nameArray;
    String [] PWArray;
    int [] idArray;
    ListView lv;
    BaseAdapter myAdapter = new BaseAdapter(){             //Ϊlistviewд������
		
		public int getCount() {
			if(nameArray!= null){		    //����������鲻Ϊ��
				return nameArray.length;
			}
			else {
				return 0;					//�����������Ϊ���򷵻�0
			}
		}
		
		public Object getItem(int arg0) {
			return null;
		}
		
		public long getItemId(int arg0) {
			return 0;
		}
		
		public View getView(int position, View convertView, ViewGroup parent) {   //���������������
			LinearLayout ll = new LinearLayout(MainActivity.this);
			ll.setOrientation(LinearLayout.HORIZONTAL);
			TextView tv = new TextView(MainActivity.this);
			tv.setText(nameArray[position]);
			tv.setTextSize(32);
			tv.setTextColor(Color.BLACK);
			tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			tv.setGravity(Gravity.CENTER_VERTICAL);
			TextView tv2 = new TextView(MainActivity.this);
			tv2.setText("["+PWArray[position]+"]");
			tv2.setTextSize(28);
			tv2.setTextColor(Color.BLACK);
			tv2.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			tv2.setGravity(Gravity.CENTER_VERTICAL);	//����TextView�ؼ��ڸ������е�λ��
			TextView tv3 = new TextView(MainActivity.this);
			tv3.setText("["+idArray[position]+"]");
			tv3.setTextSize(28);
			tv3.setTextColor(Color.BLACK);
			tv3.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			tv3.setGravity(Gravity.CENTER_VERTICAL);	//����TextView�ؼ��ڸ������е�λ��
			ll.addView(tv3);
			ll.addView(tv);
			ll.addView(tv2); 
			
			return ll;
		}
	};
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mHelper=new myOpenHelper(this, DB_NAME, null, 1);
		
		setRootButton=(Button)this.findViewById(R.id.setRootPW);
		setRootButton.setOnClickListener(this);
		addUserButton=(Button)this.findViewById(R.id.addNewUser);
		addUserButton.setOnClickListener(this);
		
		 lv = (ListView)findViewById(R.id.lv);
	     lv.setAdapter(myAdapter);
	     lv.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View view, int position,
						long id) {
//					Intent intent= new Intent(wyf.wpf.Sample_10_6.this,wyf.wpf.DetailActivity.class);
//					intent.putExtra("cmd", 0);		//0�����ѯ��ϵ�ˣ�1���������ϵ��
//					intent.putExtra("id", contactsId[position]);
//					startActivity(intent);
				}
		});
		
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
		if(v==setRootButton){
			Intent i=new Intent(this,setRootPW.class);
	        startActivity(i); 
		}
		else if(v==addUserButton){
			Intent i=new Intent(this,addUser.class);
			startActivity(i);
		}
	}
	
	 @Override
	protected void onResume() {
		getInfo();
		myAdapter.notifyDataSetChanged();
		super.onResume();
	}
	
	//�����ݿ��ȡ��Ϣ
	public void getInfo(){
		SQLiteDatabase db = mHelper.getWritableDatabase();		//��ȡ���ݿ�����
    	Cursor c = db.query(TABLE_NAME, new String[]{ID,NAME,PW}, null, null, null, null, ID);
    	int idIndex = c.getColumnIndex(ID);
    	int nameIndex = c.getColumnIndex(NAME);		//��������е��к�
    	int pwIndex = c.getColumnIndex(PW);
    	idArray = new int[c.getCount()];			//�������id��int�������
    	nameArray = new String[c.getCount()];			//�������������String�������
    	PWArray = new String[c.getCount()];	
    	int i=0;			
    	for(c.moveToFirst();!(c.isAfterLast());c.moveToNext()){
    		idArray[i] = c.getInt(idIndex);
    		nameArray[i] = c.getString(nameIndex);			//��������ӵ�String������
    		PWArray[i] = c.getString(pwIndex);			//��pW��ӵ�String������
    		i++;
    	}
    	c.close();				//�ر�Cursor����
    	db.close();				//�ر�SQLiteDatabase����
	}

}
