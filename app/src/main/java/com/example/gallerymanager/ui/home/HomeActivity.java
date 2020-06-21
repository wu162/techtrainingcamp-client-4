package com.example.gallerymanager.ui.home;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import com.example.gallerymanager.ui.receive.ReceiveFileActivity;
import com.example.gallerymanager.ui.send.SendFileActivity;
import com.example.gallerymanager.utils.GridItemDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private HomeViewModel mViewModel;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private TextView toolbarText;
    private HomeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        getPermission();
        setupRecyclerView();
        prepareTransitions();
        observeMenuChange();
    }

    private void observeMenuChange() {
        toolbarText = findViewById(R.id.toolbar_text);
        mViewModel.isChoosing.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isChoosing) {
                changeMenuVisibility(isChoosing);
                toolbarText.setText(isChoosing ? "选择文件" : "相册");
                adapter.setChoosing(isChoosing);
                adapter.clearSelectedItem();
            }
        });
    }

    private void prepareTransitions() {
        setExitSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {


                RecyclerView.ViewHolder viewholder = recyclerView.findViewHolderForAdapterPosition(Images.getCurrentPos());
                if (viewholder == null) {
                    return;
                }
                sharedElements.put(names.get(0), viewholder.itemView.findViewById(R.id.home_item_image));
            }
        });
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.imageList);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new HomeAdapter(new ArrayList<>());
        adapter.setClickListener(new HomeAdapter.onItemClickListener() {
            @Override
            public void onClick(View v, ArrayList<String> images, int index) {
                if (mViewModel.isChoosing.getValue()) {
                    if(adapter.getSelectedItems().contains((Object) index)){
                        adapter.removeSelectedItem(index);
                    }else{
                        adapter.addSelectedItem(index);
                    }
                    toolbarText.setText(String.format("已选%s张图片",adapter.getSelectedItems().size()));
                } else {
                    Images.setCurrentPos(index);
                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(HomeActivity.this,
                            v, ViewCompat.getTransitionName(v));
                    Intent intent = new Intent(HomeActivity.this, PreviewActivity.class);
                    intent.putStringArrayListExtra(PreviewActivity.IMAGES, images);
//                HomeActivity.this.startActivity(intent);
                    ActivityCompat.startActivity(HomeActivity.this, intent, optionsCompat.toBundle());
                }
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

        if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.CHANGE_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_send:
                chooseFile();
                break;
            case R.id.toolbar_receive:
                startActivity(new Intent(this, ReceiveFileActivity.class));
                break;
            case R.id.toolbar_confirm:
                sendFile();
                break;
            case R.id.toolbar_cancel:
                cancelSendFile();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void cancelSendFile() {
        mViewModel.isChoosing.setValue(false);
    }

    private void sendFile() {
        mViewModel.isChoosing.setValue(false);
        if(mViewModel.imagePaths.getValue().size()==0){
            return;
        }
        Intent intent = new Intent(this, SendFileActivity.class);
        intent.putStringArrayListExtra("files_send",convertPosToPath(adapter.getSelectedItems()));
        startActivity(intent);
    }

    private ArrayList<String> convertPosToPath(ArrayList<Integer> selectedItems) {
        ArrayList<String> imageList = mViewModel.imagePaths.getValue();
        ArrayList<String> filesToSend=new ArrayList<>();
        for(Integer pos:selectedItems){
            filesToSend.add(imageList.get(pos));
        }
        return filesToSend;
    }

    private void chooseFile() {
        mViewModel.isChoosing.setValue(true);
    }


    private void changeMenuVisibility(boolean isChoosing) {
        if (toolbar.getMenu().findItem(R.id.toolbar_send) != null) {
            toolbar.getMenu().findItem(R.id.toolbar_send).setVisible(!isChoosing);
            toolbar.getMenu().findItem(R.id.toolbar_receive).setVisible(!isChoosing);
            toolbar.getMenu().findItem(R.id.toolbar_confirm).setVisible(isChoosing);
            toolbar.getMenu().findItem(R.id.toolbar_cancel).setVisible(isChoosing);
        }
    }
}
