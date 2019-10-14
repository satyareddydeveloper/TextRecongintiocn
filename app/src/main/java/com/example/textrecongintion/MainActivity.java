package com.example.textrecongintion;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.L;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class MainActivity extends AppCompatActivity {
    EditText mResultEt;
    ImageView mPreviewImageViw;
    Uri image_uri;
    public static final int CAMERA_REQUEST_CODE=200;
    public static final int STORAGE_REQUEST_CODE=300;
    public static final int IMAGE_PICK_GALLERY=400;
    public static final int IMAGE_PICK_CAMERA=500;
    String cameraPermisiongs[];
    String storagePermisssions[];



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("click + button to Insert Image");
        mResultEt=findViewById(R.id.resultEDt);
        mPreviewImageViw=findViewById(R.id.ImaageIv);
        cameraPermisiongs = new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermisssions=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu,menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id==R.id.addImage){
            showImageImportDialog();
        }
            return super.onOptionsItemSelected(item);
    }

    private void  showImageImportDialog() {

        String[] items ={"camera","gallery"};
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle("Select Image");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                if (which==0){
                    if (!checkCameraPermission()){
                        requexsstCameraPermision();
                    }else{
                        pickCamera();
                    }

                }if (which==1){
                    if (!checkStroragePermission()){
                        requestStoragePermision();
                    }else{
                        pickGallery();
                    }

                }


            }
        });
        builder.create().show();
    }

    private void pickGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_GALLERY);
    }

    private void pickCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"New Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Image to Text");
        image_uri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        ,values);
        Intent Cameeraintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Cameeraintent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(Cameeraintent,IMAGE_PICK_CAMERA);

    }

    private void requestStoragePermision() {
        ActivityCompat.requestPermissions(this,storagePermisssions,STORAGE_REQUEST_CODE);






    }

    private boolean checkStroragePermission() {
        boolean result= ContextCompat.checkSelfPermission(this
                ,Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
return result;





    }

    private void requexsstCameraPermision() {
        ActivityCompat.requestPermissions(this,cameraPermisiongs,CAMERA_REQUEST_CODE);

    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission
                .CAMERA)== (PackageManager.PERMISSION_GRANTED);
        boolean result1= ContextCompat.checkSelfPermission(this
                ,Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return result && result1;



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[]
            permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case CAMERA_REQUEST_CODE:
                if (grantResults.length>0){
                    boolean cameraAccepted = grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted){
                        pickCamera();
                    }else {
                        Toast.makeText(getApplicationContext(),"PermissionDenied", Toast.LENGTH_LONG).show();

                    }
                }
                break;
            case  STORAGE_REQUEST_CODE:
                if (grantResults.length>0){
                    boolean storageAccepted = grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    if ( storageAccepted){
                        pickGallery();
                    }else {
                        Toast.makeText(getApplicationContext(),"PermissionDenied", Toast.LENGTH_LONG).show();

                    }
                }

                        break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY) {
                CropImage.activity(data.getData()).setGuidelines(CropImageView.
                        Guidelines.ON).start(this);


            }
            if (requestCode == IMAGE_PICK_CAMERA) {
                CropImage.activity(image_uri).setGuidelines(CropImageView.Guidelines.ON).start(this);


            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                mPreviewImageViw.setImageURI(resultUri);
                BitmapDrawable bitmapDrawable = (BitmapDrawable) mPreviewImageViw
                        .getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();
                TextRecognizer textRecognizer = new TextRecognizer.Builder
                        (getApplicationContext()).build();
                if (!textRecognizer.isOperational()) {
                    Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_LONG).show();
                } else {
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> items = textRecognizer.detect(frame);
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < items.size(); i++) {
                        TextBlock myItme = items.valueAt(i);
                        stringBuilder.append(myItme.getValue());
                        stringBuilder.append("\n");
                        Toast.makeText(getApplicationContext(),""+myItme,Toast.LENGTH_LONG).show();

                    }
                    mResultEt.setText(stringBuilder.toString());
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(getApplicationContext(), "" + error, Toast.LENGTH_LONG).show();

            }


        }
    }
}
