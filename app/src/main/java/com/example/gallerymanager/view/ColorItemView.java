package com.example.gallerymanager.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class ColorItemView extends View {

    private int color;
    private Paint mPaint;

    public ColorItemView(Context context) {
        this(context,null);
    }

    public ColorItemView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ColorItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint=new Paint();
        color=Color.parseColor("#ff0000");
    }

    public void setColor(String color) {
        this.color = Color.parseColor(color);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(color);
        canvas.drawCircle(getMeasuredWidth()/2,getMeasuredHeight()/2,32,mPaint);
    }
}
