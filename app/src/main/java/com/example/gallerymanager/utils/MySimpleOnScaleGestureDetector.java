package com.example.gallerymanager.utils;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

import com.example.gallerymanager.view.MyImageView;

public class MySimpleOnScaleGestureDetector extends ScaleGestureDetector {

    public MySimpleOnScaleGestureDetector(Context context, OnScaleGestureListener listener) {
        super(context, listener);
    }

    public static class MySimpleOnScaleGestureListener implements ScaleGestureDetector.OnScaleGestureListener {

        private MyImageView imageView;
        private float scale;
        private static final float MAX_FACTOR = 4.0f;

        private final float[] matrixValues=new float[9];

        private Matrix mScaleMatrix=new Matrix();

        public MySimpleOnScaleGestureListener(MyImageView imageView) {
            super();
            this.imageView = imageView;
            scale = 1.0f;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scale=getScale();
            float scaleFactor=detector.getScaleFactor();
//            Log.d("MySimpleOnScaleGesture1",""+scaleFactor);
            if(scaleFactor*scale<1.0f){
                scaleFactor=1.0f/scale;
            }
            if(scaleFactor*scale>MAX_FACTOR){
                scaleFactor=MAX_FACTOR/scale;
            }
            mScaleMatrix.postScale(scaleFactor,scaleFactor,detector.getFocusX(),detector.getFocusY());
            checkBorderAndCenterWhenScale();
            imageView.setImageMatrix(mScaleMatrix);
            return true;
        }

        private void checkBorderAndCenterWhenScale()
        {

            RectF rect = imageView.getMatrixRectF();
            float deltaX = 0;
            float deltaY = 0;

            int width = imageView.getWidth();
            int height = imageView.getHeight();

            // 如果宽或高大于屏幕，则控制范围
            if (rect.width() >= width)
            {
                if (rect.left > 0)
                {
                    deltaX = -rect.left;
                }
                if (rect.right < width)
                {
                    deltaX = width - rect.right;
                }
            }
            if (rect.height() >= height)
            {
                if (rect.top > 0)
                {
                    deltaY = -rect.top;
                }
                if (rect.bottom < height)
                {
                    deltaY = height - rect.bottom;
                }
            }
            // 如果宽或高小于屏幕，则让其居中
            if (rect.width() < width)
            {
                deltaX = width * 0.5f - rect.right + 0.5f * rect.width();
            }
            if (rect.height() < height)
            {
                deltaY = height * 0.5f - rect.bottom + 0.5f * rect.height();
            }
//            Log.e(TAG, "deltaX = " + deltaX + " , deltaY = " + deltaY);

            mScaleMatrix.postTranslate(deltaX, deltaY);

        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {

        }

        public void setScale(float scale) {
            this.scale = scale;
        }

        public float getScale() {
            mScaleMatrix.getValues(matrixValues);
            return matrixValues[Matrix.MSCALE_X];
        }

        public Matrix getScaleMatrix() {
            return mScaleMatrix;
        }

        public void setScaleMatrix(Matrix scaleMatrix) {
            mScaleMatrix = scaleMatrix;
        }
    }


}
