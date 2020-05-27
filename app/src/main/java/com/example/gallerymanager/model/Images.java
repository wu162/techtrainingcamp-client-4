package com.example.gallerymanager.model;

public class Images {
    private static int currentPos=0;

    public static int getCurrentPos() {
        return currentPos;
    }

    public static void setCurrentPos(int currentPos) {
        Images.currentPos = currentPos;
    }
}
