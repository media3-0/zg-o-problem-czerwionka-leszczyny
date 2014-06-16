package com.media30.zglosproblem.mobileapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class PhonesListAdapter extends ArrayAdapter<Phone> {

    private int layoutRes;

    public PhonesListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public PhonesListAdapter(Context context, int resource, List<Phone> items) {
        super(context, resource, items);
        layoutRes = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /*View v = convertView;
        if(v == null){
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(layoutRes, null);
        }*/
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(layoutRes, parent, false);
        final Phone phone = getItem(position);
        if(phone != null){
            TextView tvDesc = (TextView)v.findViewById(R.id.tvPhoneDesc);
            TextView tvPhone = (TextView)v.findViewById(R.id.tvPhone);

            if(tvDesc != null)
                tvDesc.setText(phone.getDescription());
            if(tvPhone != null) {
                tvPhone.setText(phone.getPhone());
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
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phone.getPhone()));
                getContext().startActivity(intent);
            }
        });
        return v;
    }
}
