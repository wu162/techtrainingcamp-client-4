package com.example.gallerymanager.ui.preview;

import androidx.lifecycle.ViewModel;

public class PreviewViewModel extends ViewModel {

    private int lastPage=0;

    public PreviewViewModel(int lastPage) {
        this.lastPage=lastPage;
    }

    public int getLastPage() {
        return lastPage;
    }

    public void setLastPage(int lastPage) {
        this.lastPage = lastPage;
    }
}
