package com.example.dianzi.common;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.example.dianzi.MainApplication;
import com.example.dianzi.common.Config;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CommonFunc {
    static  NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("zh", "CN"));
    public static String getSystemDateString() {
        DateFormat df = new SimpleDateFormat(Config.DATE_FORMAT);
        return df.format(new Date());
    }


    public static Date strToDate(String str) {
        DateFormat df = new SimpleDateFormat(Config.DATE_FORMAT);
        try {
            return  df.parse(str);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static String dateToStr(Date date) {
        DateFormat df = new SimpleDateFormat(Config.DATE_FORMAT);
        return  df.format(date);
    }

    public static String getAmountText(float amount) {
        String text = currencyFormatter.format(amount);
//        if(text.endsWith(".00")) {
//            text = text.substring(0, text.length() - 3);
//        } else if(text.endsWith("0")) {
//            text = text.substring(0, text.length() - 1);
//        }
        return text.substring(0, text.length() - 1);
    }

    public static String getAmountTextInt(float amount) {
        String text = currencyFormatter.format(amount);
        return text.substring(0, text.length() - 3);
    }

    public static Bitmap getBitmap(Uri uri) {
        try {
            InputStream stream = MainApplication.instance.getContentResolver().openInputStream(uri);
            Bitmap bmp = BitmapFactory.decodeStream(new BufferedInputStream(stream));
            stream.close();
            return bmp;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
