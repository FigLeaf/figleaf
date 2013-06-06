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

public class setRootPW extends Activity implements OnClickListener {
    myOpenHelper myHelper;
    EditText textOldPW, textNewPW, textNewPW2;
    Button buttonYes, buttonNo;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.root_set);
        myHelper = new myOpenHelper(this, myOpenHelper.DB_NAME, null, 1);
        textOldPW = (EditText) this.findViewById(R.id.oldPW);
        textNewPW = (EditText) this.findViewById(R.id.newPW);
        textNewPW2 = (EditText) this.findViewById(R.id.newPW2);
        buttonYes = (Button) this.findViewById(R.id.rootYes);
        buttonNo = (Button) this.findViewById(R.id.rootNo);
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
        SQLiteDatabase db = myHelper.getWritableDatabase();
        Cursor c = db.query(myOpenHelper.TABLE_NAME,
                new String[]{NAME, PW, PICPATH, DIRTY}, ID + "=?", new String[]{1 + ""}, null, null, null);
        if (c.getCount() == 0) {
            ContentValues values = new ContentValues();
            values.put(NAME, "root");
            values.put(PW, "123456");
            values.put(PICPATH, "0");
            values.put(DIRTY, "1");
            db.insert(TABLE_NAME, ID, values);
            c = db.query(myOpenHelper.TABLE_NAME,
                    new String[]{NAME, PW}, ID + "=?", new String[]{1 + ""}, null, null, null);
        }
        c.moveToFirst();
        String oldRootPW = c.getString(1);

        if (v == buttonNo) {
            finish();
        } else if (v == buttonYes) {                  //change the root PW
            String[] strArray = new String[3];
            strArray[0] = textOldPW.getText().toString().trim();
            strArray[1] = textNewPW.getText().toString().trim();
            strArray[2] = textNewPW2.getText().toString().trim();
            if (!strArray[1].equals(strArray[2])) {
                Toast.makeText(setRootPW.this, "Password doesn't match the confirmation", Toast.LENGTH_LONG).show();
            } else if (!oldRootPW.equals(strArray[0])) {
                Toast.makeText(setRootPW.this, "Wrong Old Password!", Toast.LENGTH_LONG).show();
            } else {
                ContentValues values = new ContentValues();
                String rootName = "root";
                values.put(NAME, rootName);
                values.put(PW, strArray[1]);
                values.put(PICPATH, "0");
                values.put(DIRTY, "0");
                int count = db.update(TABLE_NAME, values, ID + "=?", new String[]{1 + ""});
                if (count == -1) {
                    Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "OK", Toast.LENGTH_LONG).show();
                }
            }
            c.close();
            db.close();
        }

    }

}
