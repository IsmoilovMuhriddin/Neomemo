package uz.ismoilov.muhriddin.neomemo;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import at.markushi.ui.CircleButton;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "firebaseLog" ;
    private static final int RC_SIGN_IN =100 ;
    ListView listview = null;
    DatabaseReference fb;
    RecyclerView recyclerMemo;
    MyAdapter recyclerAdapter;
    RecyclerView.LayoutManager recyclerLayout;
    int ChildCounts=0;
    Button btn_new_memo;
    Button btn_Close;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser fUser;


    public List<ListViewItem> items;
    private String fUserId;

    //menu Item added
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
    //menu Item finished



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    fUser = mAuth.getCurrentUser();
                    fUserId = fUser.getUid();
                    btn_new_memo = (Button) findViewById(R.id.btn_new_memo);


                    recyclerMemo = (RecyclerView)findViewById(R.id.recyclerMemo);
                    recyclerMemo.setHasFixedSize(true);

                    recyclerLayout = new LinearLayoutManager(MainActivity.this);
                    recyclerMemo.setLayoutManager(recyclerLayout);
                    items = new ArrayList<>();


                    fb = FirebaseDatabase.getInstance().getReference();
                    getItemsFromFire();
                    recyclerAdapter = new MyAdapter(items);
                    recyclerMemo.setAdapter(recyclerAdapter);

                    recyclerAdapter.setClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int pos = recyclerMemo.indexOfChild(v);
                            ListViewItem ls = recyclerAdapter.getListviewItem(pos);
                            int id = ls.getId();
                            Intent i = new Intent(MainActivity.this,WritingMemoActivity.class);

                            i.putExtra("id",id);
                            i.putExtra("ListViewItem",ls);
                            i.putExtra("ChildCounts",ChildCounts);
                            startActivity(i);

                        }
                    });

                    btn_new_memo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(MainActivity.this,WritingMemoActivity.class);
                            i.putExtra("id",-1);
                            i.putExtra("ChildCounts",ChildCounts);
                            startActivity(i);


                        }
                    });





                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Intent i = new Intent(MainActivity.this,SignInActivity.class);
                    startActivity(i);
                }
                // ...
            }
        };

    }
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    public void getItemsFromFire(){
        Log.v("function","here executed");
        final DatabaseReference notes = fb.child("users").child(fUserId.toString());

        Query qAll = notes.limitToFirst(100);
        Query qlast = notes.orderByKey().limitToLast(1);
        qlast.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot noteLast :dataSnapshot.getChildren()){

                    ChildCounts = Integer.parseInt(noteLast.getKey().toString());

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Log.v("Query","here executed");
        qAll.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.v("Snapshot","here executed");
                for (DataSnapshot note:dataSnapshot.getChildren()){
                        Log.v("Note",note.toString());
                        ListViewItem listitem =new ListViewItem();
                    for(DataSnapshot  noteDetails:note.getChildren()){
                            Log.v("NOTE Details","key = "+ noteDetails.getKey()+" => "+noteDetails.getValue());
                            if(noteDetails.getKey().toString().equals("memoDate")){
                                listitem.setMemoDate(noteDetails.getValue().toString());
                            }
                            else if(noteDetails.getKey().toString().equals("memoText")){

                                listitem.setMemoText(noteDetails.getValue().toString());
                            }
                            else if(noteDetails.getKey().toString().equals("id")){

                                listitem.setId(Integer.parseInt(noteDetails.getValue().toString()));
                            }
                            else if(noteDetails.getKey().toString().equals("LocalPath")){}
                            else if(noteDetails.getKey().toString().equals("ImageName")){}
                            else if(noteDetails.getKey().toString().equals("id")){}
                            else if(noteDetails.getKey().toString().equals("id")){}

                            else {

                                Log.v("NOTES","NOT FOUND??????????????????");
                            }
                        }
                        items.add(listitem);
                        recyclerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }



}
