package uz.ismoilov.muhriddin.neomemo;
import android.Manifest;

import android.app.Activity;
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
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class WritingMemoActivity extends AppCompatActivity {
    private static final String TAG = "PHOTO";
    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final int CAMERA_PERMISSION_REQUEST_CODE =1247 ;
    private static final int REQUEST_CODE_BROWSE = 1234;
    static final int REQUEST_TAKE_PHOTO = 1;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing_memo);

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
                imgUri = data.getData();
                //MEDIA GALLERY

                String[] projection = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(imgUri, projection, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(projection[0]);
                String filepath = cursor.getString(columnIndex);
                cursor.close();
                ls.setImageName(imgUri.getLastPathSegment());
                Log.i(TAG,"ABSOLUTEPATH "+filepath);
                try {
                    Bitmap bm = BitmapFactory.decodeFile(filepath);
                    Drawable drawable  = new BitmapDrawable(bm);
                    //Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);
                    Imgview.setImageBitmap(bm);
                    ls.setLocalPath(filepath);
                }
                finally {
                    Log.i(TAG,"Error o bitmao drawable");
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
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");

        startActivityForResult(Intent.createChooser(intent, "Select image"), REQUEST_CODE_BROWSE);
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

        StorageReference userImages = usersStorageRef.child(fUserId.toString()).child("images");
        if(ls.getLocalPath()!= null){
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setTitle("Uploading image");
            dialog.show();

            ContextWrapper wrapper = new ContextWrapper(getApplicationContext());
            File fileDir = wrapper.getDir("Images",MODE_PRIVATE);

            Uri file = Uri.fromFile(new File(ls.getLocalPath()));
            String FileName = ls.getLocalPath();
            StorageReference currentImagesRef = userImages.child(FileName.substring(FileName.indexOf("Images/")+7,FileName.length()));
            Log.i(TAG,FileName.substring(FileName.indexOf("Images/")+7,FileName.length()));


            UploadTask uploadTask = currentImagesRef.putFile(file);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    //Dimiss dialog when error
                    dialog.dismiss();
                    //Display err toast msg
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {

                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            //Show upload progress

                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "Please select image", Toast.LENGTH_SHORT).show();
        }

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

    public String getRealPathFromURI( Uri contentUri) {
        Cursor cursor = null;

            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = managedQuery(contentUri, proj, null, null, null);
            if(cursor==null) return null;

            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);

    }

}


