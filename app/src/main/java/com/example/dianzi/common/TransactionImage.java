package com.example.dianzi.common;

import android.graphics.Bitmap;

import com.example.dianzi.entity.TransactionData;
import com.google.mlkit.vision.text.Text;

import java.io.Serializable;

public abstract class TransactionImage implements Serializable {
    protected Bitmap bitmap;
    protected Text text;

    abstract public TransactionData getTransactionData();

    public Bitmap getBitmap() {
        return bitmap;
    }
}
