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
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.support.v7.widget.PopupMenu;
import android.widget.TextView;


public class DescriptionActivity extends ActionBarActivity {

    private EditText et;
    private SharedPreferences sp;
    private TextView tvCat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_description);
        et = (EditText)findViewById(R.id.editText);
        tvCat = (TextView)findViewById(R.id.tvCat);
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
        String cat = sp.getString(MainActivity.CAT_STRING, "");
        tvCat.setText(cat);
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

    public void pickCategory(View view){
        //wybór kategorii
        PopupMenu popupMenu = new PopupMenu(this, view);
        final Menu menu = popupMenu.getMenu();
        for(int i = 0; i < Categories.categories.length; i++){
            if(Categories.subcategories[i].length > 0){
                Menu submenu = menu.addSubMenu(1001, i + 1000, 0, Categories.categories[i]);
                for(int j = 0; j < Categories.subcategories[i].length; j++){
                    submenu.add(i, j, 0, Categories.subcategories[i][j]);
                }
            }else{
                menu.add(1000, 1000, 1000, Categories.categories[i]);
            }
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(menuItem.getGroupId() > 1000)
                    return true;
                SharedPreferences.Editor editor = sp.edit();
                if(menuItem.getGroupId() == 1000){
                    tvCat.setText(Categories.categories[Categories.categories.length - 1]);
                    editor.putString(MainActivity.CAT_STRING, Categories.categories[Categories.categories.length - 1]);
                    editor.putInt(MainActivity.CAT, 1000);
                    editor.putInt(MainActivity.SUBCAT, 1000);
                }else{
                    StringBuilder text = new StringBuilder();
                    text.append(Categories.categories[menuItem.getGroupId()]);
                    text.append("> \n");
                    text.append(Categories.subcategories[menuItem.getGroupId()][menuItem.getItemId()]);
                    tvCat.setText(text.toString());
                    editor.putString(MainActivity.CAT_STRING, text.toString());
                    editor.putInt(MainActivity.CAT, menuItem.getGroupId());
                    editor.putInt(MainActivity.SUBCAT, menuItem.getItemId());
                }
                editor.commit();
                return true;
            }
        });
        popupMenu.show();
    }
}
