package com.epul.ProjetMobile.tools;

import android.annotation.TargetApi;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Pierre on 30/01/2016.
 */
@TargetApi(Build.VERSION_CODES.M)
public class detailOnScrollChangeListener implements View.OnScrollChangeListener {
    private Drawable drawable;
    private TextView title;

    public detailOnScrollChangeListener(Drawable drawable, TextView title) {
        this.drawable = drawable;
        this.title = title;
    }

    @Override
    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        drawable.setAlpha(getAlphaforActionBar(v.getScrollY()));
    }

    private int getAlphaforActionBar(int scrollY) {
        int minDist = 0,maxDist = 650;
        if(scrollY>maxDist){
            title.setVisibility(View.VISIBLE);
            return 255;
        }
        else if(scrollY<minDist){
            return 0;
        }
        else {
            int alpha = 0;
            alpha = (int)  ((255.0/maxDist)*scrollY);
            title.setVisibility(View.INVISIBLE);
            return alpha;
        }
    }
}
