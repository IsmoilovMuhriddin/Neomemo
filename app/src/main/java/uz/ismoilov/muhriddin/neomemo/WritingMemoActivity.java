package uz.ismoilov.muhriddin.neomemo;
import android.Manifest;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class WritingMemoActivity extends AppCompatActivity {
    private static final String TAG = "PHOTO";
    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final int CAMERA_PERMISSION_REQUEST_CODE =1247 ;
    private static final int REQUEST_CODE_BROWSE = 1234;
    static final int REQUEST_TAKE_PHOTO = 1;

    TextView memoDateTV;
    EditText editMemoText;
    ImageButton imageMemoEdit;
    ImageButton btnDatePick;
    Button btnSaveEdit;
    Button BtnCloseEdit;
    RecyclerView recyclerMemo;
    ImageView Imgview;

    ListViewItem ls;
    StorageReference storageRef;
    DatabaseReference fb;
    FirebaseStorage fb_storage;
    StorageReference usersStorageRef;
    int ChildCounts;
    int id;
    private FirebaseUser fUser;
    private String fUserId;


    private String DateOfMemo;
    private String TimeOfMemo;
    private ImageButton cameraImgButton;
    private Uri imgUri;
    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing_memo);
        memoDateTV = (TextView) findViewById(R.id.memoDateTV);

        editMemoText = (EditText) findViewById(R.id.editMemoText);
        btnDatePick = (ImageButton) findViewById(R.id.btnDatePick);
        imageMemoEdit =(ImageButton)findViewById(R.id.imageMemoEdit);
        btnSaveEdit = (Button) findViewById(R.id.btnSaveEdit);
        BtnCloseEdit = (Button) findViewById(R.id.BtnCloseEdit);
        Imgview = (ImageView) findViewById(R.id.Imgview);

        cameraImgButton = (ImageButton) findViewById(R.id.cameraImgButton);

        ls = new ListViewItem();
        fb = FirebaseDatabase.getInstance().getReference();
        fb_storage = FirebaseStorage.getInstance();

        storageRef = fb_storage.getReference();
        usersStorageRef = storageRef.child("users");


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
        imageMemoEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                browseIntent();
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
            ListViewItem lsComing =(ListViewItem) i.getSerializableExtra("ListViewItem");
            editMemoText.setText(lsComing.getMemoText().toString());
            String [] ar = lsComing.getMemoDate().toString().split(" ");
            setDateOfMemo(ar[0]);
            setTimeOfMemo(ar[1]);
            ls.setMemoDate(lsComing.getMemoDate());
            memoDateTV.setText(lsComing.getMemoDate());
            Bitmap bitmap = null;
            try {
                Uri filepath = Uri.fromFile(new File(lsComing.getLocalPath()));

                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
               if(bitmap !=null) {
                   Imgview.setImageBitmap(bitmap);
                   ls.setLocalPath(lsComing.getLocalPath());

                   ls.setFirebasePath(lsComing.getFirebasePath());
               }
               } catch (IOException e) {
                e.printStackTrace();
            }
            Imgview.setImageBitmap(bitmap);
            btnSaveEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    saveNew();
                }
            });
        }



    }

    public String getTimeOfMemo() {
        return TimeOfMemo;
    }

    public void setTimeOfMemo(String timeOfMemo) {
        TimeOfMemo = timeOfMemo;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
        Log.i(TAG,"RESULT_OK");
            if (requestCode == REQUEST_TAKE_PHOTO) {
                Log.i(TAG, "RESULT_Request");
                onCaptureImageResult(data);
            }
            if (requestCode == REQUEST_CODE_BROWSE && resultCode == RESULT_OK && data != null && data.getData() != null) {
                filePath = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                    Imgview.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                /* catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
            }
         }

    }

    public String getImageExt(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    public void browseIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE_BROWSE);

    }
    private void cameraIntent(){
        Log.i(TAG,"Intent");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Log.i(TAG,"startactivity");
        startActivityForResult(intent, REQUEST_TAKE_PHOTO);
    }

    @SuppressWarnings("VisibleForTests")
    public void saveNew(){
        DatabaseReference notes = fb.child("users").child(fUserId.toString());

        if(id==-1){
            id = ChildCounts+1;

        }

        //upload image
        uploadFile();

        ls.setMemoText(editMemoText.getText().toString());

        ls.setMemoDate(getDateOfMemo()+" "+getTimeOfMemo());
        ls.setId(id);
        notes.child(id+"").setValue(ls);
        Intent i = new Intent(WritingMemoActivity.this,MainActivity.class);
        i.putExtra("id",-1);
        startActivity(i);


    }
    private void onCaptureImageResult(Intent data) {
        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
        ContextWrapper wrapper = new ContextWrapper(getApplicationContext());
        //thumbnail.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        Log.i(TAG,"thumbnail");
        File file = wrapper.getDir("Images",MODE_PRIVATE);
        String FileName = "IMG_"+System.currentTimeMillis() + ".png";

        file = new File(file,FileName );

        try{
            OutputStream stream = null;
            stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);


            ls.setLocalPath(file.getPath());


            Uri filepath = Uri.fromFile(new File(ls.getLocalPath()));


            Log.i(TAG,"write"+file.getPath());
            stream.flush();
            stream.close();

        }catch (IOException e) // Catch the exception
        {
            e.printStackTrace();
        }
      Imgview.setImageBitmap(bitmap);
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
    public String getDateOfMemo() {
        return DateOfMemo;
    }
    public void setDateOfMemo(String dateOfMemo) {
        DateOfMemo = dateOfMemo;
    }


    //this method will upload the file
    private void uploadFile() {

        //if there is a file to upload
        if (filePath != null) {
            //displaying a progress dialog while upload is going on
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();
            ls.setLocalPath(filePath.toString());
            StorageReference userImages = usersStorageRef.child(fUserId.toString()).child("images");
            String FileName = filePath.getLastPathSegment();

            StorageReference currentImagesRef = userImages.child(FileName);
            Log.i(TAG,FileName);
            currentImagesRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @SuppressWarnings("VisibleForTests")
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //if the upload is successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();
                           Uri downloadUri =  taskSnapshot.getDownloadUrl();
                            ls.setFirebasePath(downloadUri.toString());
                            //and displaying a success toast
                            Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                            //and displaying error message
                            Toast.makeText(getApplicationContext(),ls.getFirebasePath(), Toast.LENGTH_LONG).show();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //if the upload is not successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();

                            //and displaying error message
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @SuppressWarnings("VisibleForTests")
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //calculating progress percentage
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            //displaying percentage in progress dialog
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        }
        //if there is not any file
        else {
            //you can display an error toast
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void chooseDateBtn(View view) {

        int year = Integer.parseInt(ls.getMemoDate().substring(0, 4));
        int month = Integer.parseInt(ls.getMemoDate().substring(5, 7));
        int day = Integer.parseInt(ls.getMemoDate().substring(8, 10));
        //Toast.makeText(this, "" + year + month + day, Toast.LENGTH_SHORT).show();
        DatePickerDialog dialog = new DatePickerDialog(this, null, year, month -1 , day);
        dialog.show();
        dialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                String date = y+"/"+m+"/"+d;
                ls.setMemoDate(date);
                memoDateTV.setText(ls.getMemoDate());
            }
        });

    }
}


