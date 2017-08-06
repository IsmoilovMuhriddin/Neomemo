package uz.ismoilov.muhriddin.neomemo;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.ListView;

//background thread using loader interface

public class testactivity extends AppCompatActivity {
   private CursorAdapter cursorAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testactivity);

       // DBOpenHelper helper = new DBOpenHelper(this);
      //  SQLiteDatabase database = helper.getWritableDatabase();



    }


}
