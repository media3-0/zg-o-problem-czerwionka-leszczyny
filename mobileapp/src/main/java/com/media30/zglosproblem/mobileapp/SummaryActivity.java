package com.media30.zglosproblem.mobileapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.loopj.android.http.*;
import org.apache.http.Header;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class SummaryActivity extends ActionBarActivity {

    boolean description;
    boolean location;
    boolean image;

    TextView tvSending;
    ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_summary);
        tvSending = (TextView)findViewById(R.id.tvSending);
        progress = (ProgressBar)findViewById(R.id.progressBar);
    }

    private void showSending(){
        tvSending.setVisibility(View.VISIBLE);
        progress.setVisibility(View.VISIBLE);
    }

    private void hideSending(){
        tvSending.setVisibility(View.INVISIBLE);
        progress.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sp = getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);
        CheckBox cb = (CheckBox)findViewById(R.id.cbImage);
        if(TextUtils.isEmpty(sp.getString(MainActivity.IMAGE, ""))){
            cb.setChecked(false);
            image = false;
        }else{
            cb.setChecked(true);
            image = true;
        }

        cb = (CheckBox)findViewById(R.id.cbLocation);
        if(sp.getLong(MainActivity.POS_LAT, 0) == 0){
            cb.setChecked(false);
            location = false;
        }else{
            cb.setChecked(true);
            location = true;
        }

        cb = (CheckBox)findViewById(R.id.cbDescription);
        if(TextUtils.isEmpty(sp.getString(MainActivity.DESCRIPTION, ""))){
            cb.setChecked(false);
            description = false;
        }else{
            cb.setChecked(true);
            description = true;
        }
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_MENU || super.onKeyDown(keyCode, event);
    }

    private void clearPrefs(){
        SharedPreferences sp = getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(MainActivity.IMAGE, "");
        editor.putLong(MainActivity.POS_LAT, 0);
        editor.putLong(MainActivity.POS_LNG, 0);
        editor.putString(MainActivity.DESCRIPTION, "");
        editor.commit();
    }

    private void clearStackAndBackToMain(){
        Intent intent = new Intent(SummaryActivity.this, MainActivity.class);
        if(Build.VERSION.SDK_INT > 10) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }else {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        startActivity(intent);
    }

    public void anulujClick(View view){
        //wraca do głównej aktywności czyszcząc wszystkie wprowadzone przez użytkownika dane!
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage(this.getResources().getString(R.string.cancel_message));
        dialog.setPositiveButton(this.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                clearPrefs();
                clearStackAndBackToMain();
            }
        });
        dialog.setNegativeButton(this.getString(R.string.no), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                // nic nie rób
            }
        });
        dialog.show();

    }

    private File resizeImage(String filepath){
        String imageFileName = "resized";
        File storageDir = this.getApplicationContext().getExternalFilesDir(
                Environment.DIRECTORY_PICTURES);
        File file = null;
        try {
            file = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
            file.deleteOnExit();

            Bitmap bitmap = BitmapFactory.decodeFile(filepath);

            Double OWidth = Double.valueOf(bitmap.getWidth());
            Double OHeight = Double.valueOf(bitmap.getHeight());
            Double NWidth;
            Double NHeight;
            if(OWidth > OHeight){
                if(OWidth > 1500) {
                    NWidth = 1500.0;
                    Double factor = OWidth / NWidth;
                    NHeight = OHeight / factor;
                }else{
                    NWidth = OWidth;
                    NHeight = OHeight;
                }
            }else{
                if(OHeight > 1500) {
                    NHeight = 1500.0;
                    Double factor = OHeight / NHeight;
                    NWidth = OWidth / factor;
                }else{
                    NWidth = OWidth;
                    NHeight = OHeight;
                }
            }

            Bitmap bm = Bitmap.createScaledBitmap(bitmap, NWidth.intValue(), NHeight.intValue(), true);
            FileOutputStream ostream = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 75, ostream);
            ostream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public void sendReport(){
        //wysyłanie zgłoszenia
        showSending();
        AsyncHttpClient client = new AsyncHttpClient();
        SharedPreferences sp = getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);
        RequestParams rp = new RequestParams();
        rp.put("description", sp.getString(MainActivity.DESCRIPTION, ""));
        rp.put("pos_lat", String.valueOf(Double.longBitsToDouble(sp.getLong(MainActivity.POS_LAT,0))));
        rp.put("pos_lng", String.valueOf(Double.longBitsToDouble(sp.getLong(MainActivity.POS_LNG,0)))); //odwrotnie Double.parseDouble
        if(!TextUtils.isEmpty(sp.getString(MainActivity.IMAGE, ""))){
            try {
                rp.put("file", resizeImage(sp.getString(MainActivity.IMAGE, "")));
            } catch (FileNotFoundException e) {
                //nie ma takiego pliku (nie ma prawa wyrzucić)
            }
        }
        client.post(MainActivity.HOST + "upload.php", rp, new TextHttpResponseHandler(){

            @Override
            public void onProgress(int bytesWritten, int totalSize) {
                super.onProgress(bytesWritten, totalSize);
                //postęp
                progress.setMax(totalSize);
                progress.setProgress(bytesWritten);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseBody) {
                Log.d("onSuccess", "Status code: " + statusCode + "\n" + responseBody);
                //parsowanie wyniku
                if(responseBody.contains("ok")){
                    //sukces
                    AlertDialog.Builder dialog = new AlertDialog.Builder(SummaryActivity.this);
                    dialog.setMessage(getResources().getString(R.string.send_success_message));
                    dialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            //wyjdź do ekranu głównego
                            clearPrefs();
                            clearStackAndBackToMain();
                        }
                    });
                    dialog.show();
                }else{
                    //błąd na serwerze
                    AlertDialog.Builder dialog = new AlertDialog.Builder(SummaryActivity.this);
                    dialog.setMessage("Błąd podczas wysyłania na serwer. Odpowiedź:\n" + responseBody);
                    dialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            //nic
                        }
                    });
                    dialog.show();
                }
            }

            @Override
            public void onFailure(String responseBody, Throwable error) {
                Log.e("onFailure", responseBody);
                AlertDialog.Builder dialog = new AlertDialog.Builder(SummaryActivity.this);
                dialog.setMessage("Błąd podczas wysyłania na serwer. Odpowiedź:\n" + responseBody);
                dialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        //nic
                    }
                });
                dialog.show();
                hideSending();
            }
        });
    }

    public void wyslijClick(View view){
        if(!description || !location){
            AlertDialog.Builder dialog = new AlertDialog.Builder(SummaryActivity.this);
            dialog.setMessage(getResources().getString(R.string.no_loc_desc_message));
            dialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    //nic
                }
            });
            dialog.show();
            return;
        }

        if(!image){
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage(this.getResources().getString(R.string.no_image_message));
            dialog.setPositiveButton(this.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    sendReport();
                }
            });
            dialog.setNegativeButton(this.getString(R.string.no), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // nic nie rób
                }
            });
            dialog.show();
        }else{
            sendReport();
        }

    }

    public void pictureClick(View view){
        Intent intent = new Intent(this, ImageActivity.class);
        startActivity(intent);
    }

    public void locationClick(View view){
        Intent intent = new Intent(this, LocationActivity.class);
        startActivity(intent);
    }

    public void descriptionClick(View view){
        Intent intent = new Intent(this, DescriptionActivity.class);
        startActivity(intent);
    }
}
