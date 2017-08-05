package uz.ismoilov.muhriddin.neomemo;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class WritingMemoActivity extends AppCompatActivity {
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1888;
    private static final String TAG = "PHOTO";
    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final int CAMERA_PERMISSION_REQUEST_CODE =1247 ;
    ImageButton imageMemoEdit;
    EditText editMemoText;
    ImageButton btnDatePick;
    Button btnSaveEdit;
    Button BtnCloseEdit;
    RecyclerView recyclerMemo;
    DatabaseReference fb;
    ImageView Imgview;
    int ChildCounts;
    int id;
    private FirebaseUser fUser;
    private String fUserId;


    private String DateOfMemo;
    private String TimeOfMemo;
    private ImageButton cameraImgButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing_memo);

        editMemoText = (EditText) findViewById(R.id.editMemoText);
        btnDatePick = (ImageButton) findViewById(R.id.btnDatePick);
        btnSaveEdit = (Button) findViewById(R.id.btnSaveEdit);
        BtnCloseEdit = (Button) findViewById(R.id.BtnCloseEdit);
        Imgview = (ImageView) findViewById(R.id.Imgview);

        cameraImgButton = (ImageButton) findViewById(R.id.cameraImgButton);
        fb = FirebaseDatabase.getInstance().getReference();

        fUser =  FirebaseAuth.getInstance().getCurrentUser();
        fUserId = fUser.getUid();
        Intent i = getIntent();
        Log.v("Intent","");
        handleIntent(i);

        cameraImgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= 23 && checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

                    cameraIntent();
                } else if (Build.VERSION.SDK_INT < 23) {
                    cameraIntent();
                } else {
                    String[] perms = {Manifest.permission.CAMERA};
                    requestPermissions(perms, CAMERA_PERMISSION_REQUEST_CODE);
                }
            }
        });

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
            String [] ar = ls.getMemoDate().toString().split(" ");
            setDateOfMemo(ar[0]);
            setTimeOfMemo(ar[1]);
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

        ls.setMemoDate(getDateOfMemo()+" "+getTimeOfMemo());
        ls.setId(id);
        notes.child(id+"").setValue(ls);
        Intent i = new Intent(WritingMemoActivity.this,MainActivity.class);
        i.putExtra("id",-1);
        startActivity(i);


    }


    public String getDateOfMemo() {
        return DateOfMemo;
    }

    public void setDateOfMemo(String dateOfMemo) {
        DateOfMemo = dateOfMemo;
    }

    public String getTimeOfMemo() {
        return TimeOfMemo;
    }

    public void setTimeOfMemo(String timeOfMemo) {
        TimeOfMemo = timeOfMemo;
    }


    static final int REQUEST_TAKE_PHOTO = 1;


    private void cameraIntent(){
    Log.i(TAG,"Intent");
    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    Log.i(TAG,"startactivity");
    startActivityForResult(intent, REQUEST_TAKE_PHOTO);
}
    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        //thumbnail.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        Log.i(TAG,"thumbnail");
        File destination = new File(getFilesDir(),
                "IMG_"+System.currentTimeMillis() + ".png");
        Log.i(TAG,"FILE");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            Log.i(TAG,"write"+destination.toString());

            Log.i(TAG,"space"+destination.getTotalSpace());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Imgview.setImageBitmap(thumbnail);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
        Log.i(TAG,"RESULT_OK");
            if (requestCode == REQUEST_TAKE_PHOTO)
                Log.i(TAG,"RESULT_Request");
                onCaptureImageResult(data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == CAMERA_PERMISSION_REQUEST_CODE){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                cameraIntent();
            }
        }
    }
}


