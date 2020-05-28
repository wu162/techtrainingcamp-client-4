package com.example.gallerymanager.ui.preview;

import android.content.Intent;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.gallerymanager.R;
import com.example.gallerymanager.databinding.ActivityPreviewBinding;
import com.example.gallerymanager.model.Images;
import com.example.gallerymanager.ui.edit.EditActivity;
import com.example.gallerymanager.utils.StatusBar;
import com.example.gallerymanager.view.MyImageView;

import java.util.ArrayList;

public class PreviewActivity extends AppCompatActivity {

    public static final String IMAGES="images";
    private ActivityPreviewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StatusBar.hideStatusBar(this);
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_preview);
        ArrayList<String> images=getIntent().getStringArrayListExtra(IMAGES);

        PreviewAdapter adapter = new PreviewAdapter(images);
        binding.viewPagerImage.setAdapter(adapter);
        binding.viewPagerImage.setCurrentItem(Images.getCurrentPos(),false);
        binding.viewPagerImage.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                View lastView = binding.viewPagerImage.findViewWithTag(Images.getCurrentPos());
                if (lastView!=null){
                    MyImageView image = lastView.findViewById(R.id.preview_image);
                    image.reset();
                }
                Images.setCurrentPos(position);
            }
        });
        binding.viewPagerImage.setPageTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View view, float position) {
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

        ViewCompat.setTransitionName(binding.viewPagerImage,"image"+Images.getCurrentPos());

        binding.previewEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PreviewActivity.this, EditActivity.class);
                intent.putExtra("imageUrl",images.get(Images.getCurrentPos()));
                startActivity(intent);
            }
        });
    }


    @Override
    public void onBackPressed() {
//        supportFinishAfterTransition();
        super.onBackPressed();
    }
}
