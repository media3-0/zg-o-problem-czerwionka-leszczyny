package com.media30.zglosproblem.mobileapp;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.media30.zglosproblem.mobileapp.R;

public class InfoDetailsActivity extends ActionBarActivity {

    Information info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_info_details);

        if(getIntent().getExtras() == null) this.finish();
        info = getIntent().getExtras().getParcelable("info");
        if(info == null) this.finish();
        TextView tv = (TextView)findViewById(R.id.tvTitle);
        tv.setText(info.getTitle());
        tv = (TextView)findViewById(R.id.tvInfo);
        tv.setText(info.getInfo());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.info_details, menu);
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

    public void fbClick(View view){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, info.getInfo());
        startActivity(Intent.createChooser(intent, "Udostępnij informację"));
    }
}
