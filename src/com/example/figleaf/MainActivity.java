package com.example.figleaf;

import static com.example.figleaf.myOpenHelper.*;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class MainActivity extends Activity implements OnClickListener {
    private Button setRootButton,addUserButton;
    private myOpenHelper mHelper;
    private String [] nameArray;
    private String [] PWArray;
    private int [] idArray;
    private ListView lv;
    private CheckBox mSetOnOff;
    private boolean mIsLockScreenOn;
    private final String LOCK_SCREEN_ON_OFF = "lock_screen_on_off";
    
    BaseAdapter myAdapter = new BaseAdapter(){             //为listview写适配器
        
        public int getCount() {
            if(nameArray!= null){           //如果姓名数组不为空
                return nameArray.length;
            }
            else {
                return 0;                   //如果姓名数组为空则返回0
            }
        }
        

        
        public View getView(int position, View convertView, ViewGroup parent) {   //适配器李添加内容
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
            tv2.setGravity(Gravity.CENTER_VERTICAL);    //设置TextView控件在父容器中的位置
            TextView tv3 = new TextView(MainActivity.this);
            tv3.setText("["+idArray[position]+"]");
            tv3.setTextSize(28);
            tv3.setTextColor(Color.BLACK);
            tv3.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            tv3.setGravity(Gravity.CENTER_VERTICAL);    //设置TextView控件在父容器中的位置
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
        
        //listView的初始化
         lv = (ListView)findViewById(R.id.lv);
         lv.setAdapter(myAdapter);
         lv.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> arg0, View view, int position,
                        long id) {
                }
        });
         
         //checkbox的初始化
         SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);     
            mIsLockScreenOn = prefs.getBoolean(LOCK_SCREEN_ON_OFF, false);
            // set checkbox with saved value.
            mSetOnOff = (CheckBox) this.findViewById(R.id.setonoff);        
            mSetOnOff.setChecked(mIsLockScreenOn);
            mSetOnOff.setOnCheckedChangeListener(new OnCheckedChangeListener(){
                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                        boolean isChecked) {
                    // TODO Auto-generated method stub
                    if(buttonView.isChecked()){
                        mSetOnOff.setText("Set LockScreen ON. [Now is on]");
                        //check and save
                        EnableSystemKeyguard(false);
                }
                    else{
                        mSetOnOff.setText("Set LockScreen ON. [Now is off]");
                        EnableSystemKeyguard(true);
                    }
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
    protected void onResume() {
        getInfo();
        myAdapter.notifyDataSetChanged();
        super.onResume();
    }
    

     @Override
        protected void onStop() {
            // TODO Auto-generated method stub
            super.onStop();
            
            mIsLockScreenOn = mSetOnOff.isChecked();
            

            // save the setting before leaving.
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
            editor.putBoolean(LOCK_SCREEN_ON_OFF, mIsLockScreenOn);
            editor.commit();
                    
        }
        
        void EnableSystemKeyguard(boolean bEnable){
            KeyguardManager mKeyguardManager=null;
            KeyguardLock mKeyguardLock=null; 
            
            mKeyguardManager = (KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);  
            mKeyguardLock = mKeyguardManager.newKeyguardLock(""); 
            if(bEnable)
                mKeyguardLock.reenableKeyguard();
            else
                mKeyguardLock.disableKeyguard();
        }
        
     
