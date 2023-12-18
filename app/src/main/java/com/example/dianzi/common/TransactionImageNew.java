package com.example.dianzi.common;

import android.graphics.Bitmap;

import com.example.dianzi.entity.TransactionData;
import com.google.mlkit.vision.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TransactionImageNew extends TransactionImage{
    public TransactionImageNew(Bitmap bitmap, Text text) {
        this.bitmap = bitmap;
        this.text = text;
    }



    public TransactionData getTransactionData() {
        String result = text.getText();
         System.out.println(result);
        Matcher m = null;
        TransactionData transactionData = new TransactionData();

        String transactionDate = "";
        m  = Pattern.compile("([0-9]{4})年([0-9]{2})月([0-9]{2})日").matcher(result);
        if(m.find()) {
            transactionDate = m.group(1) + m.group(2) + m.group(3);
        }
        System.out.println(transactionDate);




        String accountName = "";
        for(String name : Config.ACCOUNT_NAMES) {
            String nameForMatch = name.substring(name.length() - 1);
            System.out.println("name for match:" + nameForMatch);
            m = Pattern.compile(nameForMatch).matcher(result);
            if (m.find()) {
                accountName = name;
            }
        }
        System.out.println("name:" + accountName);

        String plate = "";
        String year = "";
        String no = "";
        String totalWeight = "";
        String otherWeight = "";
        String netWeight = "";

        for(Text.TextBlock block : text.getTextBlocks()) {
            m = Pattern.compile("[0-9 ]{3,}").matcher(block.getText());
            if (m.find()) {
                String match = m.group();
                if(match.replace(" ", "").isEmpty()) {
                    continue;
                }
                if(plate.isEmpty()) {
                    plate = match;
                } else if(year.isEmpty()) {
                    year = match;
                }
                else if(no.isEmpty()) {
                    no = match;
                } else if(totalWeight.isEmpty()) {
                    totalWeight = match;
                } else if (otherWeight.isEmpty()) {
                    otherWeight = match;
                } else if(netWeight.isEmpty()) {
                    netWeight = match;
                }

            }
        }


        System.out.println("plate:" + plate);
        System.out.println("no:" + no);
        System.out.println("totalWeight:" + totalWeight);
        System.out.println("otherWeight:" + otherWeight);
        System.out.println("netWeight:" + netWeight);

        no = no.replace(" ", "");
        if(no.length() == 12) {
            transactionData.inputDate = no.isEmpty() ? "" : no.substring(0, 8);
            transactionData.sequence = no.isEmpty() ? "" : no.substring(8, 12);
        }
        transactionData.plateNumber = plate;
        transactionData.accountName = accountName;
        if(!netWeight.isEmpty()) {
            transactionData.weight = Float.parseFloat(netWeight);
        }

        return transactionData;

    }

}
