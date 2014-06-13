package com.media30.zglosproblem.mobileapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.loopj.android.http.*;
import org.apache.http.Header;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class SummaryActivity extends Activity {

    boolean description;
    boolean location;
    boolean image;

    TextView tvSending;
    ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
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
        ImageButton cb = (ImageButton)findViewById(R.id.ibZdjecie);
        if(TextUtils.isEmpty(sp.getString(MainActivity.IMAGE, ""))){
            cb.setImageDrawable(getResources().getDrawable(R.drawable.sprawdzdjecie));
            image = false;
        }else{
            cb.setImageDrawable(getResources().getDrawable(R.drawable.sprawdzdjecieok));
            image = true;
        }

        cb = (ImageButton)findViewById(R.id.ibMiejsce);
        if(sp.getLong(MainActivity.POS_LAT, 0) == 0){
            cb.setImageDrawable(getResources().getDrawable(R.drawable.sprawdzmiejsce));
            location = false;
        }else{
            cb.setImageDrawable(getResources().getDrawable(R.drawable.sprawdzmiejsceok));
            location = true;
        }

        cb = (ImageButton)findViewById(R.id.ibOpis);
        if(TextUtils.isEmpty(sp.getString(MainActivity.DESCRIPTION, "")) || TextUtils.isEmpty(sp.getString(MainActivity.CAT_STRING, ""))){
            cb.setImageDrawable(getResources().getDrawable(R.drawable.sprawdzopis));
            description = false;
        }else{
            cb.setImageDrawable(getResources().getDrawable(R.drawable.sprawdzopisok));
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
        editor.putString(MainActivity.CAT_STRING, "");
        editor.putInt(MainActivity.CAT, -1);
        editor.putInt(MainActivity.SUBCAT, -1);
        editor.commit();
    }

    static void clearStackAndBackToMain(Context to){
        Intent intent = new Intent(to, MainActivity.class);
        if(Build.VERSION.SDK_INT > 10) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }else {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        to.startActivity(intent);
    }

    static void cancelWizard(final Context to){
        //wraca do głównej aktywności czyszcząc wszystkie wprowadzone przez użytkownika dane!
        AlertDialog.Builder dialog = new AlertDialog.Builder(to);
        dialog.setMessage(to.getResources().getString(R.string.cancel_message));
        dialog.setPositiveButton(to.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                clearStackAndBackToMain(to);
            }
        });
        dialog.setNegativeButton(to.getString(R.string.no), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                // nic nie rób
            }
        });
        dialog.show();
    }

    public void homeClick(View view){
        SummaryActivity.cancelWizard(this);
    }

    public void wsteczClick(View view){
        this.finish();
    }

    private Bitmap decodeFile(File f) {
        try {
            // decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            FileInputStream stream1 = new FileInputStream(f);
            BitmapFactory.decodeStream(stream1, null, o);
            stream1.close();

            // Find the correct scale value. It should be the power of 2.
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < 1500 || height_tmp / 2 < 1500)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            FileInputStream stream2 = new FileInputStream(f);
            Bitmap bitmap = BitmapFactory.decodeStream(stream2, null, o2);
            stream2.close();
            return bitmap;
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private File resizeImage(String filepath){
        String imageFileName = "resized";
        File storageDir = this.getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File file = null;
        try {
            file = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
            file.deleteOnExit();

            //Bitmap bitmap = BitmapFactory.decodeFile(filepath);
            Bitmap bitmap = this.decodeFile(new File(filepath));

            final Double maxSize = 1500.0;

            Double OWidth = Double.valueOf(bitmap.getWidth());
            Double OHeight = Double.valueOf(bitmap.getHeight());
            Double NWidth;
            Double NHeight;
            if(OWidth > OHeight){
                if(OWidth > maxSize) {
                    NWidth = maxSize;
                    Double factor = OWidth / NWidth;
                    NHeight = OHeight / factor;
                }else{
                    NWidth = OWidth;
                    NHeight = OHeight;
                }
            }else{
                if(OHeight > maxSize) {
                    NHeight = maxSize;
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
        SharedPreferences dsp = PreferenceManager.getDefaultSharedPreferences(this);
        RequestParams rp = new RequestParams();
        rp.put("description", sp.getString(MainActivity.DESCRIPTION, ""));
        rp.put("pos_lat", String.valueOf(Double.longBitsToDouble(sp.getLong(MainActivity.POS_LAT,0))));
        rp.put("pos_lng", String.valueOf(Double.longBitsToDouble(sp.getLong(MainActivity.POS_LNG,0))));
        rp.put("cat", String.valueOf(sp.getInt(MainActivity.CAT, 1000)));
        rp.put("subcat", String.valueOf(sp.getInt(MainActivity.SUBCAT, 1000)));
        rp.put("catstring", sp.getString(MainActivity.CAT_STRING, ""));
        if(dsp.getBoolean("prefApproval", false)){
            rp.put("ident", "1");
            rp.put("imie", dsp.getString("prefName",""));
            rp.put("nazwisko", dsp.getString("prefSurname",""));
            rp.put("numer", dsp.getString("prefPhone",""));
            rp.put("email", dsp.getString("prefEmail",""));
        }
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
                            clearStackAndBackToMain(SummaryActivity.this);
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
                //Log.e("onFailure", responseBody);
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
            return;
        }
        sendReport();
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
