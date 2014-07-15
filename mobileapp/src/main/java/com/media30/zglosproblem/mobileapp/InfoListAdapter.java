package com.media30.zglosproblem.mobileapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class InfoListAdapter extends ArrayAdapter<Information> {

    private int layoutRes;
    private List<String> readList;

    public InfoListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public InfoListAdapter(Context context, int resource, List<Information> items) {
        super(context, resource, items);
        layoutRes = resource;
        SharedPreferences prefs = context.getSharedPreferences("readInfoPrefs", 0);
        String[] readArray = prefs.getString("readinfo","").split("\\|");
        readList = new ArrayList<String>();
        for(String t : readArray){
            if(!TextUtils.isEmpty(t))
                readList.add(t);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(layoutRes, parent, false);
        final Information info = getItem(position);
        if(info != null){
            TextView tvTitle = (TextView)v.findViewById(R.id.tvInfoTitle);

            if(tvTitle != null) {
                tvTitle.setText(info.getTitle());
                if(!readList.contains(String.valueOf(info.getId())))
                    tvTitle.setTypeface(Typeface.DEFAULT_BOLD);
            }
        }
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final LinearLayout ll = (LinearLayout)view;
                final Context context = getContext();
                ll.setBackgroundColor(context.getResources().getColor(android.R.color.darker_gray));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(ll != null && context != null)
                            ll.setBackgroundColor(context.getResources().getColor(android.R.color.white));
                    }
                }, 200);

                if(!readList.contains(String.valueOf(info.getId()))) {
                    SharedPreferences prefs = context.getSharedPreferences("readInfoPrefs", 0);
                    SharedPreferences.Editor editor = prefs.edit();
                    StringBuilder sb = new StringBuilder();
                    sb.append(prefs.getString("readinfo", "")).append(info.getId()).append("|");
                    editor.putString("readinfo", sb.toString());
                    editor.commit();
                    readList.add(String.valueOf(info.getId()));
                    WelcomeActivity.unreadInfoCount--;
                }
                TextView tvTitle = (TextView)ll.findViewById(R.id.tvInfoTitle);
                tvTitle.setTypeface(Typeface.DEFAULT);
                Intent intent = new Intent(getContext(), InfoDetailsActivity.class);
                intent.putExtra("info", info);
                getContext().startActivity(intent);
            }
        });
        return v;
    }
}
