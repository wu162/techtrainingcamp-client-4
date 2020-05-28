package com.example.gallerymanager.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.gallerymanager.utils.IOUtils;
import com.example.gallerymanager.utils.PixUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class EditableView extends View {

    private ArrayList<PathInfo> paths;
    private Paint mPaint;
    private float mPreX,mPreY;
    private Bitmap bitmap;
    private Canvas mBmpCanvas;
    private Path mPath;

    public EditableView(Context context) {
        this(context,null);
    }

    public EditableView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public EditableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if(bitmap!=null){
            mBmpCanvas=new Canvas(bitmap);
        }
        mPaint=new Paint();
        mPaint.setColor(Color.parseColor("#ff0000"));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(5);
        mPath=new Path();
        paths=new ArrayList<>();
    }

    public void bindData(String imageUrl){
        int widthPx=PixUtils.getImageWidth(imageUrl);
        int heightPx= PixUtils.getImageHeight(imageUrl);
        bindData(widthPx,heightPx, PixUtils.getScreenWidth(),PixUtils.getScreenHeight(),imageUrl);
    }

    private void bindData(int widthPx,int heightPx,final int screenWidth,final int screenHeight, String imageUrl) {
        setSize(widthPx,heightPx,screenWidth,screenHeight,imageUrl);
    }

    private void setSize(int widthPx, int heightPx, int screenWidth, int screenHeight, String imageUrl) {
        int finalWidth=widthPx,finalHeight=heightPx;
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) getLayoutParams();
        if(finalWidth>=screenWidth){
            finalWidth=screenWidth;
            finalHeight=(int)((finalWidth/(double)widthPx)*heightPx);
        }else{
            params.leftMargin=params.rightMargin=(screenWidth-finalWidth)/2;
        }
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
        this.bitmap = IOUtils.getBitmapByFile(imageUrl, finalWidth, finalHeight);
        this.mBmpCanvas=new Canvas(bitmap);
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:{
                mPath=new Path();
                paths.add(new PathInfo(mPath,mPaint.getColor(),mPaint.getStrokeWidth()));
                mPath.moveTo(event.getX(),event.getY());
                mPreX=event.getX();
                mPreY=event.getY();
                return true;
            }
            case MotionEvent.ACTION_MOVE:{
                float endX=(mPreX+event.getX())/2;
                float endY=(mPreY+event.getY())/2;
                mPath.quadTo(mPreX,mPreY,endX,endY);
                mPreX=event.getX();
                mPreY=event.getY();
                invalidate();
            }
            break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mBmpCanvas!=null){
            for(PathInfo pathInfo:paths){
                mPaint.setColor(pathInfo.mColor);
                mPaint.setStrokeWidth(pathInfo.mWidth);
                mBmpCanvas.drawPath(pathInfo.mPath,mPaint);
            }
        }
        canvas.drawBitmap(bitmap,0,0,mPaint);
    }

    public void setColor(String color) {
        mPaint.setColor(Color.parseColor(color));
    }

    public void setWidth(float width) {
        mPaint.setStrokeWidth(width);
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void clearPath() {
        paths.clear();
    }

    //    public void saveToImage() {
//        this.setDrawingCacheEnabled(true);
//        this.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
//        this.setDrawingCacheBackgroundColor(Color.WHITE);
//        // 把一个View转换成图片
//        Bitmap bitmap = loadBitmapFromView(this);
//
//        FileOutputStream fos;
//        try {
//            // 手机根目录
//            File sdRoot = Environment.getExternalStorageDirectory();
//            File file = new File(sdRoot, "/test.PNG");
//            if(!file.exists()) {
//                file.createNewFile();
//            }
//            fos = new FileOutputStream(file);
//
//            bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
//
//            fos.flush();
//            fos.close();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        this.destroyDrawingCache();
//    }
//
//    private Bitmap loadBitmapFromView(View v) {
//        int w = v.getWidth();
//        int h = v.getHeight();
//
//        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
//        Canvas c = new Canvas(bmp);
//
//        c.drawColor(Color.WHITE);
//        /** 如果不设置canvas画布为白色，则生成透明 */
//
////        v.layout(0, 0, w, h);
//        v.draw(c);
//
//        return bmp;
//    }

    class PathInfo{

        public Path mPath;
        public int mColor;
        public float mWidth;

        public PathInfo(Path path, int color, float width) {
            mPath = path;
            mColor = color;
            mWidth = width;
        }
    }
}
