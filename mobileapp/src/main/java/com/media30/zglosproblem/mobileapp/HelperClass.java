package com.media30.zglosproblem.mobileapp;

import android.content.Context;
import android.os.Handler;
import android.widget.ImageButton;

public class HelperClass {
    static public void onClickImageButton(final Context context, final ImageButton ib, int resOn, final int resOff){
        ib.setImageDrawable(context.getResources().getDrawable(resOn));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(ib != null && context != null)
                    ib.setImageDrawable(context.getResources().getDrawable(resOff));
            }
        }, 200);
    }
}
