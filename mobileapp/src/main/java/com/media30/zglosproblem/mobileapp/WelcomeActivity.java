package com.media30.zglosproblem.mobileapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;


public class WelcomeActivity extends Activity {

    static int unreadInfoCount = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_welcome);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_MENU || super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.welcome, menu);
        return true;
    }

    public void menuClick(View view) {
        final ImageButton ib = (ImageButton)view;
        HelperClass.onClickImageButton(this, ib, R.drawable.img5_shadow, R.drawable.img5);
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void zglosClick(View view){
        final ImageButton ib = (ImageButton)view;
        HelperClass.onClickImageButton(this, ib, R.drawable.img4_shadow, R.drawable.img4);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void infoClick(View view){
        final ImageButton ib = (ImageButton)view;
        HelperClass.onClickImageButton(this, ib, R.drawable.img2_shadow, R.drawable.img2);
        if(!isNetworkAvailable()){
            AlertDialog.Builder dialog = new AlertDialog.Builder(WelcomeActivity.this);
            dialog.setMessage(getResources().getString(R.string.no_internet_message));
            dialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    ib.setImageDrawable(getResources().getDrawable(R.drawable.img2));
                }
            });
            dialog.show();
            return;
        }
        Intent intent = new Intent(this, InfoActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        final TextView tvUnreadBadge = (TextView)findViewById(R.id.tvUnreadBadge);
        if(unreadInfoCount > 0){
            tvUnreadBadge.setText(String.valueOf(unreadInfoCount));
            tvUnreadBadge.setVisibility(View.VISIBLE);
        }else{
            tvUnreadBadge.setVisibility(View.GONE);
        }
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.get(MainActivity.HOST + "infolist.php", new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(JSONArray response) {
                if(response.length() > 0) {
                    ArrayList<Information> infoList = new ArrayList<Information>(response.length());
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            infoList.add(new Information(response.getJSONObject(i)));
                        } catch (JSONException e) {
                            Log.e("JSONException", e.toString());
                        }
                    }

                    SharedPreferences prefs = getSharedPreferences("readInfoPrefs", 0);
                    String[] readArray = prefs.getString("readinfo","").split("\\|");
                    ArrayList<String> readList = new ArrayList<String>();
                    int unreadCounter = 0;
                    for(String t : readArray) {
                        if (!TextUtils.isEmpty(t))
                            readList.add(t);
                    }
                    for(Information info : infoList){
                        if(!readList.contains(String.valueOf(info.getId())))
                            unreadCounter++;
                    }

                    if(unreadCounter > 0){
                        tvUnreadBadge.setVisibility(View.VISIBLE);
                        tvUnreadBadge.setText(String.valueOf(unreadCounter));
                    }else{
                        tvUnreadBadge.setVisibility(View.GONE);
                    }
                    WelcomeActivity.unreadInfoCount = unreadCounter;
                }
                Log.d("onSuccessJson", response.toString());
            }

            @Override
            public void onFailure(String responseBody, Throwable error) {
                //
            }
        });
    }
}
