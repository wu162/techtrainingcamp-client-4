package com.example.gallerymanager.ui.preview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.example.gallerymanager.R;
import com.example.gallerymanager.databinding.ActivityPreviewBinding;
import com.example.gallerymanager.utils.StatusBar;
import com.example.gallerymanager.view.MyImageView;

import java.util.ArrayList;

public class PreviewActivity extends AppCompatActivity {

    public static final String IMAGES="images";

    public static final String INDEX="index";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StatusBar.hideStatusBar(this);
        super.onCreate(savedInstanceState);

        ActivityPreviewBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_preview);
        ArrayList<String> images=getIntent().getStringArrayListExtra(IMAGES);
        int index=getIntent().getIntExtra(INDEX,0);

        PreviewViewModel mViewModel= new ViewModelProvider(this,new PreviewViewModelFactory(index)).get(PreviewViewModel.class);

        PreviewAdapter adapter = new PreviewAdapter(images);
        binding.viewPagerImage.setAdapter(adapter);
        binding.viewPagerImage.setCurrentItem(index,false);
        binding.viewPagerImage.setOffscreenPageLimit(2);
        binding.viewPagerImage.setPageTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View view, float position) {
//                Log.d("PreviewActivity1",String.valueOf(position));
                if(Math.abs(position)<0.0001f){
                    MyImageView image = view.findViewById(R.id.preview_image);
                    image.reset();
                }
//                int pageWidth = view.getWidth();
//
//                if (position < -1) { // [-Infinity,-1)
//                    // This page is way off-screen to the left.
//                    view.setAlpha(0f);
//
//                } else if (position <= 0) { // [-1,0]
//                    // Use the default slide transition when moving to the left page
//                    view.setAlpha(1f);
//                    view.setTranslationX(0f);
//                    view.setScaleX(1f);
//                    view.setScaleY(1f);
//
//                } else if (position <= 1) { // (0,1]
//                    // Fade the page out.
//                    view.setAlpha(1 - position);
//
//                    // Counteract the default slide transition
//                    view.setTranslationX(pageWidth * -position);
//
//                    // Scale the page down (between MIN_SCALE and 1)
//                    float scaleFactor = 0.75f
//                            + (1 - 0.75f) * (1 - Math.abs(position));
//                    view.setScaleX(scaleFactor);
//                    view.setScaleY(scaleFactor);
//
//                } else { // (1,+Infinity]
//                    // This page is way off-screen to the right.
//                    view.setAlpha(0f);
//                }
            }
        });
//        binding.setImagePath(images.get(index));
    }
}
