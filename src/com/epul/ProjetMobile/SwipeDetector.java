package com.epul.ProjetMobile;

import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * @Author Dimitri on 27/12/2015.
 * @Version 1.0
 */
public class SwipeDetector extends GestureDetector.SimpleOnGestureListener {
    protected static final int SWIPE_MIN_DISTANCE = 120;
    protected static final int SWIPE_THRESHOLD_VELOCITY = 200;

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
            onSwipeToDown();
            return false; // Top to bottom
        }
        if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
            onSwipeToUp();
            return false; // Bottom to top
        }
        if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
            onSwipeToRight();
            return false; // Left to right
        }
        if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
            onSwipeToLeft();
            return false; // Right to left
        }
        return false;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        onTouch();
        return super.onSingleTapUp(e);
    }

    public void onTouch() {
        //Does nothing -> To override only if you want to detect the movement
    }

    public void onSwipeToRight() {
        //Does nothing -> To override only if you want to detect the movement
    }

    public void onSwipeToLeft() {
        //Does nothing -> To override only if you want to detect the movement
    }

    public void onSwipeToUp() {
        //Does nothing -> To override only if you want to detect the movement
    }

    public void onSwipeToDown() {
        //Does nothing -> To override only if you want to detect the movement
    }
}
