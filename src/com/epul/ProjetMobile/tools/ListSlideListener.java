package com.epul.ProjetMobile.tools;

import android.view.View;
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
        float angle = v * (float) 180;
        imageToRotate.setRotation(angle);
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
