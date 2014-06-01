package com.media30.zglosproblem.mobileapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.*;
import android.widget.ImageView;
import android.graphics.Bitmap;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ImageActivity extends Activity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_SELECT_PHOTO = 2;
    private ImageView iv;
    private File photoFile = null;
    private boolean imageSet = false;

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
    public boolean onPrepareOptionsMenu (Menu menu) {
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_MENU || super.onKeyDown(keyCode, event);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = this.getApplicationContext().getExternalFilesDir(
                Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }

    private Bitmap decodeFile(File f){
        try {
            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f),null,o);

            //The new size we want to scale to
            final int REQUIRED_SIZE=500;

            //Find the correct scale value. It should be the power of 2.
            int scale=1;
            while(o.outWidth/scale/2>=REQUIRED_SIZE && o.outHeight/scale/2>=REQUIRED_SIZE)
                scale*=2;

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {}
        return null;
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sp = getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);
        if(!TextUtils.isEmpty(sp.getString(MainActivity.IMAGE, "")) && !imageSet){
            Bitmap imageBitmap = decodeFile(new File(sp.getString(MainActivity.IMAGE, "")));
            iv.setImageBitmap(imageBitmap);
        }
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
        SharedPreferences sp = getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //zdjęcie zrobione aparatem
            if(photoFile != null && photoFile.exists()) {
                Bitmap imageBitmap = decodeFile(new File(photoFile.getAbsolutePath()));
                iv.setImageBitmap(imageBitmap);
                imageSet = true;
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(MainActivity.IMAGE, photoFile.getAbsolutePath());
                editor.commit();
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


            Bitmap yourSelectedImage = decodeFile(new File(filePath));
            iv.setImageBitmap(yourSelectedImage);
            imageSet = true;
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(MainActivity.IMAGE, filePath);
            editor.commit();
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
