package com.mb.prestartcheck;

import android.graphics.Bitmap;

public class ParameterImageSelect {
    private final String uri;
    private final long questionId;
    private final int imageIdx;
    private final Bitmap thumbnail;

    public String getUri() { return uri;}
    public long getQuestionId() { return questionId;}
    public int getImageIdx() { return imageIdx;}
    public Bitmap getThumbnail() { return thumbnail;}

    public ParameterImageSelect(String u, long qid, int imgIdx, Bitmap tn)
    {
        uri = u;
        questionId = qid;
        imageIdx = imgIdx;
        thumbnail = tn;
    }
}
