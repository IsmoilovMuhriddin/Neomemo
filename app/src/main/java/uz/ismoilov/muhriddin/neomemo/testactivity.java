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

public class testactivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
   private CursorAdapter cursorAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testactivity);

       // DBOpenHelper helper = new DBOpenHelper(this);
      //  SQLiteDatabase database = helper.getWritableDatabase();

        insertNote("New Note.");
        //display notes to screen using recycle view

       String[] from = {DBOpenHelper.NOTE_TEXT};
        int[] to = {android.R.id.text1};
        cursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, null, from, to, 0);
        //changed from recycle view from listview
        ListView list = (ListView) findViewById(R.id.recyclerMemo);
        list.setAdapter(cursorAdapter);

        getLoaderManager().initLoader(0, null, this);

    }

    private void insertNote(String noteText) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.NOTE_TEXT, noteText);
        Uri noteUri = getContentResolver().insert(NotesProvider.CONTENT_URI,
                values);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, NotesProvider.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }
}
