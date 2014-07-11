package com.media30.zglosproblem.mobileapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import uk.co.senab.photoview.PhotoViewAttacher;


public class DetailsActivity extends Activity {

    Report report;
    private PhotoViewAttacher mAttacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_details);
        if(getIntent().getExtras() == null) this.finish();
        report = getIntent().getExtras().getParcelable("report");
        if(report == null) this.finish();
        TextView tvDesc = (TextView)findViewById(R.id.tvDesc);
        tvDesc.setText(report.getDescription());
        TextView tvPos = (TextView)findViewById(R.id.tvPos);
        StringBuilder text = new StringBuilder();
        text.append("Lat: ").append(report.getLocation().latitude);
        text.append("\nLng: ").append(report.getLocation().longitude);
        tvPos.setText(text.toString());

        TextView tvDate = (TextView)findViewById(R.id.tvDate);
        tvDate.setText(report.getDate());

        //obrazek
        ImageView iv = (ImageView)findViewById(R.id.ivImage);
        mAttacher = new PhotoViewAttacher(iv);
        String imgUrl = report.getImageUrl();
        if(!TextUtils.isEmpty(imgUrl)){
            ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);
            new ImageDownloaderTask(iv, report, getApplicationContext(), progressBar, mAttacher).execute(imgUrl);
        }else{
            iv.setVisibility(View.GONE);
        }
        TextView tvTitle = (TextView)findViewById(R.id.tvReport);
        tvTitle.setText("Zgłoszenie nr: " + report.getId());
        TextView tvCat = (TextView)findViewById(R.id.tvCategory);
        tvCat.setText(report.getCatString());

        if(report.getSenderInfo() != null){
            TextView tvInfoTitle = (TextView)findViewById(R.id.tvSendByLabel);
            tvInfoTitle.setVisibility(View.VISIBLE);
            TextView tvInfo = (TextView)findViewById(R.id.tvSendBy);
            tvInfo.setVisibility(View.VISIBLE);
            tvInfo.setText(report.getSenderInfo());
        }
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

    public void homeClick(View v){
        final ImageButton ib = (ImageButton)v;
        HelperClass.onClickImageButton(this, ib, R.drawable.domekok_shadow, R.drawable.domekczerwony);
        Intent i = new Intent(DetailsActivity.this, WelcomeActivity.class);
        if(Build.VERSION.SDK_INT > 10) {
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }else {
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        startActivity(i);
    }

    public void startClick(View v){
        MainActivity.startReport(this);
    }
}
