package com.example.gallerymanager.ui.home;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gallerymanager.R;
import com.example.gallerymanager.ui.preview.PreviewActivity;
import com.example.gallerymanager.utils.GalleryUtils;
import com.example.gallerymanager.utils.GridItemDecoration;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private HomeViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setSupportActionBar(findViewById(R.id.home_toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mViewModel= ViewModelProviders.of(this).get(HomeViewModel.class);
        getPermission();
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView=findViewById(R.id.imageList);
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
        HomeAdapter adapter = new HomeAdapter(new ArrayList<>());
        adapter.setClickListener(new HomeAdapter.onItemClickListener() {
            @Override
            public void onClick(View v,ArrayList<String> images,int index) {
                ActivityOptionsCompat optionsCompat=ActivityOptionsCompat.makeSceneTransitionAnimation(HomeActivity.this,
                        v,getString(R.string.image));
                Intent intent = new Intent(HomeActivity.this, PreviewActivity.class);
                intent.putStringArrayListExtra(PreviewActivity.IMAGES,images);
                intent.putExtra(PreviewActivity.INDEX,index);
//                HomeActivity.this.startActivity(intent);
                ActivityCompat.startActivity(HomeActivity.this,intent,optionsCompat.toBundle());
            }
        });
        recyclerView.setAdapter(adapter);
        GridItemDecoration decoration = new GridItemDecoration.Builder(this)
                .setHorizontalSpan(getResources().getDimension(R.dimen.gridgap))
                .setVerticalSpan(getResources().getDimension(R.dimen.gridgap))
                .setColor(getResources().getColor(R.color.white))
                .setShowLastLine(false)
                .build();
        recyclerView.addItemDecoration(decoration);
        mViewModel.imagePaths.observe(this, new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> imagePaths) {
                adapter.setImageList(imagePaths);
            }
        });
    }

    private void getPermission() {
        if(ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(HomeActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }else {
            mViewModel.loadImagesPath(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    mViewModel.loadImagesPath(this);
                }else{
                    finish();
                }
                break;
        }
    }
}
