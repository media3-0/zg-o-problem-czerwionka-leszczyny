package com.media30.zglosproblem.mobileapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.EditText;


public class DescriptionActivity extends ActionBarActivity {

    private EditText et;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_description);
        et = (EditText)findViewById(R.id.editText);
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                //
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                //
            }

            @Override
            public void afterTextChanged(Editable editable) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(MainActivity.DESCRIPTION, et.getText().toString());
                editor.commit();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        String desc = sp.getString(MainActivity.DESCRIPTION, "");
        if(!TextUtils.isEmpty(desc))
            et.setText(desc);
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_MENU || super.onKeyDown(keyCode, event);
    }

    public void wsteczClick(View view){
        this.finish();
    }

    public void dalejClick(View view){
        Intent intent = new Intent(this, SummaryActivity.class);
        startActivity(intent);
    }
}
