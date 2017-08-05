package uz.ismoilov.muhriddin.neomemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.List;

public class WritingMemoActivity extends AppCompatActivity {
    ImageButton imageMemoEdit;
    EditText editMemoText;
    Button btnDatePick;
    Button btnSaveEdit;
    Button BtnCloseEdit;
    RecyclerView recyclerMemo;
    DatabaseReference fb;
    int ChildCounts;
    int id;
    private FirebaseUser fUser;
    private String fUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing_memo);

        editMemoText = (EditText) findViewById(R.id.editMemoText);
        btnDatePick = (Button) findViewById(R.id.btnDatePick);
        btnSaveEdit = (Button) findViewById(R.id.btnSaveEdit);
        BtnCloseEdit = (Button) findViewById(R.id.BtnCloseEdit);
        fb = FirebaseDatabase.getInstance().getReference();
        fUser =  FirebaseAuth.getInstance().getCurrentUser();
        fUserId = fUser.getUid();
        Intent i = getIntent();
        Log.v("Intent","");
        handleIntent(i);








    }

    public void handleIntent(Intent i){
        id = i.getIntExtra("id",-2);
        if(id==-1){
            ChildCounts = i.getIntExtra("ChildCounts",2);

            btnSaveEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    saveNew();
                }
            });

        }
        else if(id==-2){

        }
        else if(id>0){
            ListViewItem ls =(ListViewItem) i.getSerializableExtra("ListViewItem");
            editMemoText.setText(ls.getMemoText().toString());
            btnDatePick.setText(ls.getMemoDate().toString());
            btnSaveEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    saveNew();
                }
            });
        }



    }

    public void saveNew(){
        DatabaseReference notes = fb.child("users").child(fUserId.toString());

        if(id==-1){
            id = ChildCounts+1;

        }

        ListViewItem ls = new ListViewItem();
        ls.setMemoText(editMemoText.getText().toString());
        ls.setMemoDate(btnDatePick.getText().toString());
        ls.setId(id);
        notes.child(id+"").setValue(ls);
        Intent i = new Intent(WritingMemoActivity.this,MainActivity.class);
        i.putExtra("id",-1);
        startActivity(i);


    }

}


