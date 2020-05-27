package com.example.gallerymanager.ui.home;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.app.SharedElementCallback;
import androidx.core.content.ContextCompat;
import androidx.core.view.OneShotPreDrawListener;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gallerymanager.R;
import com.example.gallerymanager.model.Images;
import com.example.gallerymanager.ui.preview.PreviewActivity;
import com.example.gallerymanager.utils.GridItemDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private HomeViewModel mViewModel;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setSupportActionBar(findViewById(R.id.home_toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        getPermission();
        setupRecyclerView();
        prepareTransitions();
    }

    private void prepareTransitions() {
        setExitSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                RecyclerView.ViewHolder viewholder = recyclerView.findViewHolderForAdapterPosition(Images.getCurrentPos());
                sharedElements.put(names.get(0), viewholder.itemView.findViewById(R.id.home_item_image));
            }
        });
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.imageList);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        HomeAdapter adapter = new HomeAdapter(new ArrayList<>());
        adapter.setClickListener(new HomeAdapter.onItemClickListener() {
            @Override
            public void onClick(View v, ArrayList<String> images, int index) {
                Images.setCurrentPos(index);
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(HomeActivity.this,
                        v, ViewCompat.getTransitionName(v));
                Intent intent = new Intent(HomeActivity.this, PreviewActivity.class);
                intent.putStringArrayListExtra(PreviewActivity.IMAGES, images);
//                HomeActivity.this.startActivity(intent);
                ActivityCompat.startActivityForResult(HomeActivity.this, intent, 2, optionsCompat.toBundle());
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
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]), 1);
        } else {
            mViewModel.loadImagesPath(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    boolean allGranted = true;
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                            allGranted = false;
                            break;
                        }
                    }
                    if (allGranted) {
                        mViewModel.loadImagesPath(this);
                    } else {
                        finish();
                    }
                }
                break;
        }
    }
}
