package com.example.gallerymanager.ui.preview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import android.os.Bundle;
import android.view.Window;

import com.example.gallerymanager.R;
import com.example.gallerymanager.databinding.ActivityPreviewBinding;
import com.example.gallerymanager.utils.StatusBar;

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
        binding.setImagePath(images.get(index));
    }
}
