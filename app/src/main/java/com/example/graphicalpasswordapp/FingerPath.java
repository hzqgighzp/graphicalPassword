package com.example.graphicalpasswordapp;

import android.graphics.Path;

public class FingerPath {
    public int color, strokeWidth;
    public boolean emboss, blur;
    public Path path;

    public FingerPath(int color, int strokeWidth, boolean emboss, boolean blur, Path path) {
        this.color = color;
        this.strokeWidth = strokeWidth;
        this.emboss = emboss;
        this.blur = blur;
        this.path = path;
    }
}
