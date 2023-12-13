package com.example.dianzi.common;

import com.example.dianzi.common.Config;

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

    public static Date strToDate(String date) {
        DateFormat df = new SimpleDateFormat(Config.DATE_FORMAT);
        try {
            return  df.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getAmountText(float amount) {
        String text = currencyFormatter.format(amount);
        if(text.endsWith(".00")) {
            text = text.substring(0, text.length() - 3);
        } else if(text.endsWith("0")) {
            text = text.substring(0, text.length() - 1);
        }
        return text;
    }
}
