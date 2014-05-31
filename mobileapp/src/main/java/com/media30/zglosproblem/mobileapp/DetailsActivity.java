package com.media30.zglosproblem.mobileapp;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;


public class DetailsActivity extends ActionBarActivity {

    Report report;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_details);
        report = getIntent().getExtras().getParcelable("report");
        if(report == null) this.finish();
        TextView tvDesc = (TextView)findViewById(R.id.tvDesc);
        tvDesc.setText(report.getDescription());
        TextView tvPos = (TextView)findViewById(R.id.tvPos);
        StringBuilder text = new StringBuilder();
        text.append("Lat: ").append(report.getLocation().latitude);
        text.append("\nLng: ").append(report.getLocation().longitude);
        tvPos.setText(text.toString());
        //obrazek
        ImageView iv = (ImageView)findViewById(R.id.ivImage);
        String imgUrl = report.getImageUrl();
        if(!TextUtils.isEmpty(imgUrl)){
            new ImageDownloaderTask(iv).execute(imgUrl);
        }
        TextView tvTitle = (TextView)findViewById(R.id.tvReport);
        tvTitle.setText("Zgłoszenie nr: " + report.getId());
        TextView tvCat = (TextView)findViewById(R.id.tvCat);
        tvCat.setText(report.getCatString());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.details, menu);
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_MENU || super.onKeyDown(keyCode, event);
    }
}
