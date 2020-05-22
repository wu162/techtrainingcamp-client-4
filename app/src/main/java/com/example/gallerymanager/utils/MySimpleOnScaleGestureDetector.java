package com.example.gallerymanager.utils;

import android.content.Context;
import android.graphics.Matrix;
import android.util.Log;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

public class MySimpleOnScaleGestureDetector extends ScaleGestureDetector {

    public MySimpleOnScaleGestureDetector(Context context, OnScaleGestureListener listener) {
        super(context, listener);
    }

    public static class MySimpleOnScaleGestureListener implements ScaleGestureDetector.OnScaleGestureListener {

        private ImageView imageView;
        private float factor;
        private static final float MAX_FACTOR = 3.0f;
        private static final float MIN_FACTOR = 1.0f;

        public MySimpleOnScaleGestureListener(ImageView imageView) {
            super();
            this.imageView = imageView;
            factor = 1.0f;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            factor*=detector.getScaleFactor();
            imageView.setScaleX(factor);
            imageView.setScaleY(factor);
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {

        }

        public void setFactor(float factor) {
            this.factor = factor;
        }
    }


}
