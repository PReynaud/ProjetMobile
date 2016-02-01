package com.epul.ProjetMobile.tools;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

/**
 * Created by Pierre on 30/01/2016.
 */
public class ListSlideListener implements SlidingUpPanelLayout.PanelSlideListener {
    private ImageView imageToRotate;

    public ListSlideListener(ImageView imageToRotate) {
        this.imageToRotate = imageToRotate;
    }

    @Override
    public void onPanelSlide(View view, float v) {
        imageToRotate.animate().rotation(180);
    }

    @Override
    public void onPanelCollapsed(View view) {
        imageToRotate.animate().rotation(0);
    }

    @Override
    public void onPanelExpanded(View view) {
    }

    @Override
    public void onPanelAnchored(View view) {
    }

    @Override
    public void onPanelHidden(View view) {
    }
}
