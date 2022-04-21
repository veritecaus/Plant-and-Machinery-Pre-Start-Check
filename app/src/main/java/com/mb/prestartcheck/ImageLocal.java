package com.mb.prestartcheck;

import android.graphics.Bitmap;
import android.net.Uri;

public class ImageLocal {
    public Uri uri;
    private Bitmap thumbNail;
    private Bitmap displayImage;

    public Uri getUri() { return uri;}
    public  Bitmap getThumbNail() { return this.thumbNail;}
    public  Bitmap getDisplayImage() {
        return this.displayImage;
    }

    public  void setDisplayImage(Bitmap bitmap) {
        this.displayImage = bitmap;
    }
    public  void setThumbNail(Bitmap bitmap) {
        this.thumbNail = bitmap;
    }

    public ImageLocal(Uri uri, Bitmap bm)

    {
        this.uri = uri;
        this.thumbNail = bm;
    }
}
