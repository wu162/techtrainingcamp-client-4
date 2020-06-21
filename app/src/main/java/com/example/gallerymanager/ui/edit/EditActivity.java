package com.example.gallerymanager.ui.edit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.gallerymanager.R;
import com.example.gallerymanager.databinding.ActivityEditBinding;
import com.example.gallerymanager.utils.IOUtils;
import com.example.gallerymanager.utils.StatusBar;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class EditActivity extends AppCompatActivity {

    private boolean colorSelected=false;
    private boolean widthSelected=false;
    private boolean hideInteraction=false;

    private ArrayList<String> colorList=new ArrayList<>(Arrays.asList(
            "#ff0000",
            "#00ff00",
            "#0000ff",
            "#ffffff"
    ));
    private String tmpfilePath;
    private String cropfilePath;
    private ActivityEditBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StatusBar.hideStatusBar(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit);
        String imageUrl=getIntent().getStringExtra("imageUrl");
        binding.setImageUrl(imageUrl);
        binding.editImage.bindData(imageUrl);
        Glide.with(this)
                .load(imageUrl)
                .apply(new RequestOptions().dontAnimate())
                .into(binding.editImage);

        binding.editImage.setColor(colorList.get(0));
        binding.editImage.setWidth(binding.widthBar.getProgress());

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        binding.colorList.setLayoutManager(layoutManager);
        EditColorAdapter adapter = new EditColorAdapter(colorList);
        adapter.setOnItemClick(new EditColorAdapter.onItemClick() {
            @Override
            public void onClick(String color) {
                binding.editImage.setColor(color);
            }
        });
        binding.colorList.setAdapter(adapter);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL);
        itemDecoration.setDrawable(getResources().getDrawable(R.drawable.color_between));
        binding.colorList.addItemDecoration(itemDecoration);

        binding.setButtonSelected(1);
        binding.setHideInteraction(hideInteraction);

        binding.editButtonColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.setButtonSelected(binding.getButtonSelected()==1?0:1);
            }
        });

        binding.editButtonWidth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.setButtonSelected(binding.getButtonSelected()==2?0:2);
            }
        });

        binding.widthBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                binding.editImage.setWidth(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        binding.editSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IOUtils.saveToImage(EditActivity.this,binding.editImage).observe(EditActivity.this, new Observer<String>() {
                    @Override
                    public void onChanged(String s) {
                        if(s.equals("success")){
                            Toast.makeText(EditActivity.this,"保存成功",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(EditActivity.this,"保存失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        binding.editButtonCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tmpfilePath = IOUtils.getFileFromview(binding.editImage);
                cropfilePath = tmpfilePath.substring(0, tmpfilePath.length()-4)+"_crop"+".jpg";

                UCrop.Options options = new UCrop.Options();
                options.setFreeStyleCropEnabled(true);
                options.setHideBottomControls(true);
                options.setToolbarTitle("裁剪图片");

                UCrop.of(Uri.fromFile(new File(tmpfilePath)),Uri.fromFile(new File(cropfilePath)))
                        .withOptions(options)
                        .start(EditActivity.this);
            }
        });

//        binding.editButtonRotate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                binding.editImage.animate().rotation(90.0f);
//            }
//        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UCrop.REQUEST_CROP) {
            if (resultCode == RESULT_OK) {
                binding.editImage.bindData(cropfilePath);
                binding.editImage.clearPath();
//                Glide.with(this)
//                        .load(cropfilePath)
//                        .into(binding.editImage);
                Glide.with(this)
                        .load(cropfilePath)
                        .into(new SimpleTarget<Drawable>() {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                binding.editImage.setImageDrawable(resource);
                                new File(tmpfilePath).delete();
                                new File(cropfilePath).delete();
                            }
                        });
            }
//            new File(tmpfilePath).delete();
//            new File(cropfilePath).delete();
        } else if (resultCode == UCrop.RESULT_ERROR) {
//            final Throwable cropError = UCrop.getError(data);
            Toast.makeText(this, "发生错误", Toast.LENGTH_SHORT).show();
            new File(tmpfilePath).delete();
            new File(cropfilePath).delete();
        }
    }
}
