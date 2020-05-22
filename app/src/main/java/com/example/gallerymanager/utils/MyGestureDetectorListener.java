package com.example.gallerymanager.utils;

import android.view.GestureDetector;
import android.view.MotionEvent;

import com.example.gallerymanager.view.MyImageView;

public class MyGestureDetectorListener extends GestureDetector.SimpleOnGestureListener {


    private MyImageView imageView;

    public MyGestureDetectorListener(MyImageView imageView) {
        this.imageView=imageView;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        imageView.reset();
        return true;
    }
}
