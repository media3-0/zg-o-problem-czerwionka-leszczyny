package com.media30.zglosproblem.mobileapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.*;


public class MainActivity extends ActionBarActivity {

    //localhost debug
    //final public static String HOST = "http://192.168.1.11/zgloszenie/";

    //host release
    final public static String HOST = "http://zgloszenia.aktywnaczerwionka.pl/";

    //deklaracje stałych do przesyłania danych
    public final static String SHARED_PREFS = "PREFS_PRIVATE";
    public final static String IMAGE = "PREF_IMAGE";
    public final static String POS_LAT = "PREF_POSITION_LAT";
    public final static String POS_LNG = "PREF_POSITION_LNG";
    public final static String DESCRIPTION = "PREF_DESCRIPTION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            //przycisk "Ustawienia"
            pokazUstawienia();
            return true;
        }
        if(id == R.id.action_exit){
            //wyjście z aplikacji (wyrzucenie całości z pamięci telefonu)
            //System.exit(0);
            quit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void pokazUstawienia(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void menuClick(View view){
        pokazUstawienia();
    }

    public void startClick(View view){
        //czyszczenie danych
        SharedPreferences sp = getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(MainActivity.IMAGE, "");
        editor.putLong(MainActivity.POS_LAT, 0);
        editor.putLong(MainActivity.POS_LNG, 0);
        editor.putString(MainActivity.DESCRIPTION, "");
        editor.commit();
        //startowanie kreatora
        Intent intent = new Intent(this, ImageActivity.class);
        startActivity(intent);
    }

    public void quit() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(startMain);
    }

    public void listyClick(View view){
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
    }
}
