package com.example.gallerymanager.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatImageView;

import com.example.gallerymanager.utils.MatrixEvaluator;
import com.example.gallerymanager.utils.MyGestureDetectorListener;
import com.example.gallerymanager.utils.MySimpleOnScaleGestureDetector;
import com.example.gallerymanager.utils.PixUtils;

public class MyImageView extends AppCompatImageView {


    private Context mContext;
    private MySimpleOnScaleGestureDetector.MySimpleOnScaleGestureListener mOnScaleGestureListener;
    private MySimpleOnScaleGestureDetector mScaleGestureDetector;
    private GestureDetector mGestureDetector;
    private int lastPointerCount;
    private boolean isCanDrag;
    private float mLastX;
    private float mLastY;
    private boolean isCheckTopAndBottom;
    private boolean isCheckLeftAndRight;
    private double mTouchSlop;

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
        mTouchSlop= ViewConfiguration.get(context).getScaledTouchSlop();
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
        params.height=finalHeight;
        setLayoutParams(params);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleGestureDetector.onTouchEvent(event);
        mGestureDetector.onTouchEvent(event);
        float x = 0, y = 0;

        final int pointerCount = event.getPointerCount();

        for (int i = 0; i < pointerCount; i++)
        {
            x += event.getX(i);
            y += event.getY(i);
        }
        x = x / pointerCount;
        y = y / pointerCount;


        if (pointerCount != lastPointerCount)
        {
            isCanDrag = false;
            mLastX = x;
            mLastY = y;
        }


        lastPointerCount = pointerCount;
        RectF rectF = getMatrixRectF();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (rectF.width() > getWidth() || rectF.height() > getHeight())
                {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (rectF.width() > getWidth() || rectF.height() > getHeight())
                {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                float dx = x - mLastX;
                float dy = y - mLastY;

                if (!isCanDrag) {
                    isCanDrag = isCanDrag(dx, dy);
                }
                if (isCanDrag) {
                    if (getDrawable() != null) {
                        if (getMatrixRectF().left == 0 && dx > 0)
                        {
                            getParent().requestDisallowInterceptTouchEvent(false);
                        }

                        if (getMatrixRectF().right == getWidth() && dx < 0)
                        {
                            getParent().requestDisallowInterceptTouchEvent(false);
                        }
                        isCheckLeftAndRight = isCheckTopAndBottom = true;
                        // 如果宽度小于屏幕宽度，则禁止左右移动
                        if (rectF.width() < getWidth()) {
                            dx = 0;
                            isCheckLeftAndRight = false;
                        }
                        // 如果高度小雨屏幕高度，则禁止上下移动
                        if (rectF.height() < getHeight()) {
                            dy = 0;
                            isCheckTopAndBottom = false;
                        }
                        mOnScaleGestureListener.getScaleMatrix().postTranslate(dx, dy);
                        checkMatrixBounds();
                        setImageMatrix(mOnScaleGestureListener.getScaleMatrix());
                    }
                }
                mLastX = x;
                mLastY = y;
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                lastPointerCount = 0;
                break;
        }


        return true;
    }

    private void checkMatrixBounds()
    {
        RectF rect = getMatrixRectF();

        float deltaX = 0, deltaY = 0;
        final float viewWidth = getWidth();
        final float viewHeight = getHeight();
        if (rect.top > 0 && isCheckTopAndBottom)
        {
            deltaY = -rect.top;
        }
        if (rect.bottom < viewHeight && isCheckTopAndBottom)
        {
            deltaY = viewHeight - rect.bottom;
        }
        if (rect.left > 0 && isCheckLeftAndRight)
        {
            deltaX = -rect.left;
        }
        if (rect.right < viewWidth && isCheckLeftAndRight)
        {
            deltaX = viewWidth - rect.right;
        }
        mOnScaleGestureListener.getScaleMatrix().postTranslate(deltaX, deltaY);
    }

    private boolean isCanDrag(float dx, float dy)
    {
        return Math.sqrt((dx * dx) + (dy * dy)) >= mTouchSlop;
    }

    public void reset() {
        animateTo(new Matrix());
        mOnScaleGestureListener.setScaleMatrix(new Matrix());
        setImageMatrix(mOnScaleGestureListener.getScaleMatrix());
    }

    private void animateTo(Matrix matrix) {
        ObjectAnimator animator=ObjectAnimator.ofObject(this,"imageMatrix",new MatrixEvaluator(),mOnScaleGestureListener.getScaleMatrix(),matrix);
        animator.setDuration(300);
        animator.start();
    }

    public RectF getMatrixRectF()
    {
        Matrix matrix=mOnScaleGestureListener.getScaleMatrix();
        RectF rect = new RectF();
        Drawable d = getDrawable();
        if (null != d)
        {
            rect.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            matrix.mapRect(rect);
        }
        return rect;
    }
}
