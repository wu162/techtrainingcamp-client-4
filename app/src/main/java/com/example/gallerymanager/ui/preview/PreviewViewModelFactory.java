package com.example.gallerymanager.ui.preview;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class PreviewViewModelFactory implements ViewModelProvider.Factory {

    private int lastPage;

    public PreviewViewModelFactory(int lastPage) {
        this.lastPage = lastPage;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(PreviewViewModel.class)) {
            return (T) new  PreviewViewModel(lastPage);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
