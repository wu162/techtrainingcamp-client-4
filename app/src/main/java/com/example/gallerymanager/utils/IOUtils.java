package com.example.gallerymanager.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.view.View;

import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.gallerymanager.view.EditableView;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class IOUtils {

    public static Bitmap getBitmapByFile(String path, int width, int height) {
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, option);
        option.inSampleSize = Math.max(1, Math.min(option.outWidth /  width, option.outHeight / height));
        option.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, option).copy(Bitmap.Config.ARGB_8888,true);
    }


    @SuppressLint("RestrictedApi")
    public static LiveData<String> saveToImage(Context context, EditableView view) {
        //TODO 按照原图长宽保存
        final MutableLiveData<String> result=new MutableLiveData<>();
        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        view.setDrawingCacheBackgroundColor(Color.WHITE);
        Bitmap bitmap=loadBitmapFromView(view);

        ArchTaskExecutor.getIOThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                String storePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "galleryManager";
                File appDir = new File(storePath);
                if (!appDir.exists()) {
                    appDir.mkdir();
                }
                String fileName = System.currentTimeMillis() + ".jpg";
                File file = new File(appDir, fileName);
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    //通过io流的方式来压缩保存图片
                    boolean isSuccess = bitmap.compress(Bitmap.CompressFormat.JPEG, 60, fos);
                    fos.flush();
                    fos.close();

                    //把文件插入到系统图库
                    //MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);

                    //保存图片后发送广播通知更新数据库
                    Uri uri = Uri.fromFile(file);
                    context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                    if (isSuccess) {
                        throw new Exception();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        view.destroyDrawingCache();
        result.setValue("success");
        return result;
    }

    private static Bitmap loadBitmapFromView(View v) {
        int w = v.getWidth();
        int h = v.getHeight();

        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);

        c.drawColor(Color.WHITE);
        /** 如果不设置canvas画布为白色，则生成透明 */

//        v.layout(0, 0, w, h);
        v.draw(c);

        return bmp;
    }

    @SuppressLint("RestrictedApi")
    public static String getFileFromview(EditableView view){
        String result=null;

        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        view.setDrawingCacheBackgroundColor(Color.WHITE);
        Bitmap bitmap=loadBitmapFromView(view);

        String storePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "galleryManager";
        File appDir = new File(storePath);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            //通过io流的方式来压缩保存图片
            boolean isSuccess = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            if (!isSuccess) {
                throw new Exception();
            }
            result=file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            view.destroyDrawingCache();
            return result;
        }
    }

    public static File getFileDir() {
        String storePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "galleryManager";
        File file = new File(storePath);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                throw new RuntimeException("Create path error");
            }
        }
        return file;
    }
}
