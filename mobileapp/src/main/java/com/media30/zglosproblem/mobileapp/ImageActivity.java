package com.media30.zglosproblem.mobileapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.*;
import android.widget.ImageView;
import android.graphics.Bitmap;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ImageActivity extends ActionBarActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_SELECT_PHOTO = 2;
    private ImageView iv;
    private String mCurrentPhotoPath;
    private File photoFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_image);
        iv = (ImageView)findViewById(R.id.imageView);
        iv.setAdjustViewBounds(true);
        iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = this.getApplicationContext().getExternalFilesDir(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    public void takePicture(View view){
        //robienie zdjęcia
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Błąd podczas tworzenia pliku zdjęcia!")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //użytkownik wie o błędzie
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    public void pickPicture(View view){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_SELECT_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //zdjęcie zrobione aparatem
            if(photoFile != null && photoFile.exists()) {
                Bitmap imageBitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                iv.setImageBitmap(imageBitmap);
            }
        }else if(requestCode == REQUEST_SELECT_PHOTO && resultCode == RESULT_OK){
            //zdjęcie wybrane z galerii
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(
                    selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String filePath = cursor.getString(columnIndex);
            cursor.close();


            Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);
            iv.setImageBitmap(yourSelectedImage);
        }
    }

    public void wsteczClick(View view){
        //Przycisk "Wstecz"
        this.finish();
    }

    public void dalejClick(View view){
        //Przycisk "Dalej"
        Intent intent = new Intent(this, LocationActivity.class);
        startActivity(intent);
    }

}
