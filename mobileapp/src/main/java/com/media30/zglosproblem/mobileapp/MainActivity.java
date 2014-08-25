package com.media30.zglosproblem.mobileapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.*;
import android.widget.ImageButton;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;


public class MainActivity extends Activity {

    //localhost debug
//    final public static String HOST = "http://192.168.1.11/zgloszenie/";

    //host release
    final public static String HOST = "http://zgloszenia.aktywnaczerwionka.pl/";

    //deklaracje stałych do przesyłania danych
    public final static String SHARED_PREFS = "PREFS_PRIVATE";
    public final static String IMAGE = "PREF_IMAGE";
    public final static String POS_LAT = "PREF_POSITION_LAT";
    public final static String POS_LNG = "PREF_POSITION_LNG";
    public final static String DESCRIPTION = "PREF_DESCRIPTION";
    public final static String CAT_STRING = "CAT_STRING";
    public final static String CAT = "CAT";
    public final static String SUBCAT = "SUBCAT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_MENU || super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void pokazUstawienia(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void menuClick(View view){
        pokazUstawienia();
    }

    static public void startReport(Context context){
        //czyszczenie danych
        SharedPreferences sp = context.getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(MainActivity.IMAGE, "");
        editor.putLong(MainActivity.POS_LAT, 0);
        editor.putLong(MainActivity.POS_LNG, 0);
        editor.putString(MainActivity.DESCRIPTION, "");
        editor.putString(MainActivity.CAT_STRING, "");
        editor.putInt(MainActivity.CAT, -1);
        editor.putInt(MainActivity.SUBCAT, -1);
        editor.commit();
        //startowanie kreatora
        Intent intent = new Intent(context, ImageActivity.class);
        context.startActivity(intent);
    }

    public void startClick(View view){
        final ImageButton ib = (ImageButton)view;
        HelperClass.onClickImageButton(this, ib, R.drawable.start_shadow, R.drawable.start);
        if(!isNetworkAvailable()){
            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setMessage(getResources().getString(R.string.no_internet_message));
            dialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    //
                }
            });
            dialog.show();
            return;
        }

        SharedPreferences dsp = PreferenceManager.getDefaultSharedPreferences(this);
        if(!dsp.getBoolean("prefApproval", false)){
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage(this.getResources().getString(R.string.no_approval_question));
            dialog.setPositiveButton(this.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    startReport(MainActivity.this);
                }
            });
            dialog.setNegativeButton(this.getString(R.string.no), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    pokazUstawienia();
                }
            });
            dialog.show();
            return;
        }
        startReport(this);
    }

    public void listyClick(View view){
        final ImageButton ib = (ImageButton)view;
        HelperClass.onClickImageButton(this, ib, R.drawable.img3_shadow, R.drawable.img3);
        if(!isNetworkAvailable()){
            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setMessage(getResources().getString(R.string.no_internet_message));
            dialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    //ib.setImageDrawable(getResources().getDrawable(R.drawable.img3));
                }
            });
            dialog.show();
            return;
        }
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
    }

    public void homeClick(View view){
        final ImageButton ib = (ImageButton)view;
        HelperClass.onClickImageButton(this, ib, R.drawable.domekok_shadow, R.drawable.domekczerwony);
        Intent i = new Intent(MainActivity.this, WelcomeActivity.class);
        if(Build.VERSION.SDK_INT > 10) {
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }else {
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        startActivity(i);
    }

    public void telClick(View view){
        final ImageButton ib = (ImageButton)view;
        HelperClass.onClickImageButton(this, ib, R.drawable.telefon_shadow, R.drawable.tel);
        Intent intent = new Intent(this, PhonesActivity.class);
        startActivity(intent);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
