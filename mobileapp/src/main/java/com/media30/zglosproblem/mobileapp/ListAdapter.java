package com.media30.zglosproblem.mobileapp;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ListAdapter extends ArrayAdapter<Report> {

    private int layoutRes;

    public ListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public ListAdapter(Context context, int resource, List<Report> items) {
        super(context, resource, items);
        layoutRes = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if(v == null){
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(layoutRes, null);
        }
        final Report report = getItem(position);
        if(report != null){
            ImageView iv = (ImageView)v.findViewById(R.id.listItemImageView);
            TextView tv = (TextView)v.findViewById(R.id.listItemDescription);

            if(tv != null)
                tv.setText(report.getDescription());
            String thumbUrl = report.getThumbUrl();
            if(!TextUtils.isEmpty(thumbUrl)){
                new ImageDownloaderTask(iv).execute(thumbUrl);
            }else{
                iv.setImageDrawable(getContext().getResources().getDrawable(R.drawable.no_image));
            }
        }
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), DetailsActivity.class);
                intent.putExtra("report", report);
                getContext().startActivity(intent);
            }
        });
        return v;
    }
}
