package com.example.gallerymanager.utils;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class BindAdapter {

    @BindingAdapter("imageUrl")
    public static void bindImageUrl(ImageView view,String imageUrl){
        RequestOptions options=new RequestOptions()
//                .centerCrop()
                .dontAnimate();

        Glide.with(view)
                .load(imageUrl)
                .apply(options)
                .into(view);
    }
}
