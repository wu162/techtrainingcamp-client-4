package com.example.gallerymanager.ui.preview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import android.os.Bundle;
import android.view.Window;

import com.example.gallerymanager.R;
import com.example.gallerymanager.databinding.ActivityPreviewBinding;
import com.example.gallerymanager.utils.StatusBar;

public class PreviewActivity extends AppCompatActivity {

    public static final String IMAGE_PATH="imagePath";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StatusBar.hideStatusBar(this);
        super.onCreate(savedInstanceState);

        ActivityPreviewBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_preview);
        String imagePath=getIntent().getStringExtra(IMAGE_PATH);
        binding.setImagePath(imagePath);
    }
}
