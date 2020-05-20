package com.example.gallerymanager.ui.home;

import android.annotation.SuppressLint;
import android.app.Activity;

import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.gallerymanager.utils.GalleryUtils;

import java.util.ArrayList;

public class HomeViewModel extends ViewModel {
    public MutableLiveData<ArrayList<String>> imagePaths=new MutableLiveData<>();

    public HomeViewModel() {
    }

    @SuppressLint("RestrictedApi")
    public void loadImagesPath(Activity activity){
        ArchTaskExecutor.getIOThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                imagePaths.postValue(GalleryUtils.getImagesPath(activity));
            }
        });
    }

}
