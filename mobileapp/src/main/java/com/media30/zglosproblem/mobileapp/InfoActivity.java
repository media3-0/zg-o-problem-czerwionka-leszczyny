package com.media30.zglosproblem.mobileapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Adapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.media30.zglosproblem.mobileapp.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class InfoActivity extends ActionBarActivity {

    private ProgressDialog pd;
    private ArrayList<Information> infoList;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_info);

        listView = (ListView)findViewById(R.id.listView);

        AsyncHttpClient httpClient = new AsyncHttpClient();
        pd = ProgressDialog.show(this, "Proszę czekać", "Trwa pobieranie listy informacji", true, false);
        httpClient.get(MainActivity.HOST + "infolist.php", new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(JSONArray response) {
                if(response.length() > 0){
                    infoList = new ArrayList<Information>(response.length());
                    for(int i = 0; i < response.length(); i++){
                        try {
                            infoList.add(new Information(response.getJSONObject(i)));
                        }catch (JSONException e){
                            Log.e("JSONException", e.toString());
                        }
                    }
                    InfoListAdapter adapter = new InfoListAdapter(InfoActivity.this, R.layout.list_item_info, infoList);
                    listView.setAdapter(adapter);
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(), "Brak informacji", Toast.LENGTH_LONG);
                    toast.show();
                }
                pd.dismiss();
                Log.d("onSuccessJson", response.toString());
            }

            @Override
            public void onFailure(String responseBody, Throwable error) {
                pd.dismiss();
                Log.e("onFailureJson", responseBody);
                Toast toast = Toast.makeText(getApplicationContext(), "Błąd podczas pobierania listy", Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.info, menu);
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_MENU || super.onKeyDown(keyCode, event);
    }

    public void homeClick(View view){
        final ImageButton ib = (ImageButton)view;
        HelperClass.onClickImageButton(this, ib, R.drawable.domekok_shadow, R.drawable.domekczerwony);
        Intent i = new Intent(this, WelcomeActivity.class);
        if(Build.VERSION.SDK_INT > 10) {
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }else {
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        startActivity(i);
    }
}
