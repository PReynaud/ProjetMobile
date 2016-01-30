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
public class listSlideListener implements SlidingUpPanelLayout.PanelSlideListener {
    private ImageView imageToRotate;

    public listSlideListener(ImageView imageToRotate) {
        this.imageToRotate = imageToRotate;
    }

    @Override
    public void onPanelSlide(View view, float v) {
        //.setRotation(180);
        RotateAnimation anim = new RotateAnimation(0f, 180f,
                imageToRotate.getDrawable().getBounds().width()/2,
                imageToRotate.getDrawable().getBounds().height()/2);
        anim.setInterpolator(new LinearInterpolator());
        anim.setDuration(100);
        anim.setAnimationListener(new Animation.AnimationListener(){
              @Override
              public void onAnimationStart(Animation animation) {

              }

              @Override
              public void onAnimationEnd(Animation animation) {
                    imageToRotate.setRotation(imageToRotate.getRotation() + 180);
              }

              @Override
              public void onAnimationRepeat(Animation animation) {

              }
          });
        imageToRotate.startAnimation(anim);
    }

    @Override
    public void onPanelCollapsed(View view) {
        //imageToRotate.setRotation(0);
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
