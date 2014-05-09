package com.media30.zglosproblem.mobileapp;

import android.app.Application;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.*;


public class MainActivity extends ActionBarActivity {

    //deklaracje stałych do przesyłania danych
    public final static String IMAGE = "com.media30.zglosproblem.mobileapp.image";
    public final static String POSITION = "com.media30.zglosproblem.mobileapp.position";
    public final static String DESCRIPTION = "com.media30.zglosproblem.mobileapp.description";

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
}
