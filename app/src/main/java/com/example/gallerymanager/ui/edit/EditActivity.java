package com.example.gallerymanager.ui.edit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.gallerymanager.R;
import com.example.gallerymanager.databinding.ActivityEditBinding;
import com.example.gallerymanager.utils.IOUtils;
import com.example.gallerymanager.utils.StatusBar;
import com.example.gallerymanager.view.EditableView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StatusBar.hideStatusBar(this);
        super.onCreate(savedInstanceState);
        ActivityEditBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_edit);
        String imageUrl=getIntent().getStringExtra("imageUrl");
        binding.setImageUrl(imageUrl);
        binding.editImage.bindData(imageUrl);

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

    }
}
