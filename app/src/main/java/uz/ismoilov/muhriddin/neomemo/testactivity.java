package uz.ismoilov.muhriddin.neomemo;
import android.content.ContentValues;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.CursorAdapter;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

//background thread using loader interface

public class testactivity extends AppCompatActivity {
   private CursorAdapter cursorAdapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_create_sample:
                insertSampleData();
                break;
            case R.id.action_delete_all:

                break;
        }

        return super.onOptionsItemSelected(item);
    }
    private void insertSampleData() {
        insertNote("Simple note");
        insertNote("Multi-line\nnote");
        insertNote("Very long note with a lot of text that exceeds the width of the screen");

    }

    private void insertNote(String noteText) {
        ContentValues values = new ContentValues();}



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testactivity);

       // DBOpenHelper helper = new DBOpenHelper(this);
      //  SQLiteDatabase database = helper.getWritableDatabase();

    }

}
