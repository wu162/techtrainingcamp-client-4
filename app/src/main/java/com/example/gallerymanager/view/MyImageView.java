package com.example.gallerymanager.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatImageView;

import com.example.gallerymanager.utils.MyGestureDetectorListener;
import com.example.gallerymanager.utils.MySimpleOnScaleGestureDetector;
import com.example.gallerymanager.utils.PixUtils;

public class MyImageView extends AppCompatImageView {


    private Context mContext;
    private MySimpleOnScaleGestureDetector.MySimpleOnScaleGestureListener mOnScaleGestureListener;
    private MySimpleOnScaleGestureDetector mScaleGestureDetector;
    private GestureDetector mGestureDetector;

    public MyImageView(Context context) {
        this(context,null);
    }

    public MyImageView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MyImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext=context;
        mOnScaleGestureListener = new MySimpleOnScaleGestureDetector.MySimpleOnScaleGestureListener(this);
        mScaleGestureDetector=new MySimpleOnScaleGestureDetector(mContext,mOnScaleGestureListener);
        mGestureDetector=new GestureDetector(mContext,new MyGestureDetectorListener(this));
    }

    public void bindData(String imageUrl){
        int widthPx=PixUtils.getImageWidth(imageUrl);
        int heightPx=PixUtils.getImageHeight(imageUrl);
        bindData(widthPx,heightPx, PixUtils.getScreenWidth(),PixUtils.getScreenHeight(),imageUrl);
    }

    private void bindData(int widthPx,int heightPx,final int screenWidth,final int screenHeight, String imageUrl) {
        setSize(widthPx,heightPx,screenWidth,screenHeight);
    }

    private void setSize(int widthPx, int heightPx, int screenWidth, int screenHeight) {
        int finalWidth=widthPx,finalHeight=heightPx;
        finalWidth=screenWidth;
        finalHeight=(int)((finalWidth/(double)widthPx)*heightPx);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
        if(finalHeight<screenHeight){
            params.topMargin=params.bottomMargin=(screenHeight-finalHeight)/2;
        }else{
            params.topMargin=0;
            finalWidth=(int)((screenHeight/(double)finalHeight)*finalWidth);
            finalHeight=screenHeight;
            params.leftMargin=params.rightMargin=(screenWidth-finalWidth)/2;
        }
        params.width=finalWidth;
//        params.width=100;
        params.height=finalHeight;
        setLayoutParams(params);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(mScaleGestureDetector.isInProgress()){
            this.getParent().requestDisallowInterceptTouchEvent(true);
        }else{
            this.getParent().requestDisallowInterceptTouchEvent(false);
        }
        mScaleGestureDetector.onTouchEvent(event);
        mGestureDetector.onTouchEvent(event);
        return true;
    }

    public void reset() {
//        setScaleX(1.0f);
//        setScaleY(1.0f);
        this.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300);
        mOnScaleGestureListener.setFactor(1.0f);
    }
}
