package com.example.dianzi.common;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.example.dianzi.entity.TransactionData;
import com.google.mlkit.vision.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TransactionImageResult extends TransactionImage {
    int[] allPixels;
    int headerPositionStart;
    int headerPositionEnd;
    List<Integer> dataRowPositionEndList;
    public TransactionImageResult(Bitmap bitmap, Text text) {
        this.text = text;
        this.bitmap = bitmap;
    }
    public int analyzeRows() {
        // PhotoView photoView = binding.getRoot().findViewById(R.id.result_photo);
//        BitmapDrawable bitmapDrawable = (BitmapDrawable)photoView.getDrawable();
//        System.out.println("photo size:" + photoView.getWidth() + "," + photoView.getHeight());
//        float ration = (float)photoView.getHeight()/photoView.getWidth();
//        System.out.println("aspect ratio:" + (float)photoView.getHeight()/photoView.getWidth());
//        float singleRowRatio = 0.03f;

     //   Bitmap bitmapOrig = ((BitmapDrawable)photoView.getDrawable()).getBitmap();
        int pixelColorHeaderStart = Color.rgb(230, 233,240);
        int pixelColorHeaderEnd = Color.rgb(222, 226,230);
        int pixelColorDivider = Color.rgb(230, 233,240);

        headerPositionStart = -1;
        headerPositionEnd = -1;
        dataRowPositionEndList = new ArrayList<Integer>();

        int[] pixels = new int[bitmap.getHeight()];
        bitmap.getPixels(pixels,0, 1,0, 0, 1, bitmap.getHeight());
        for(int i=0;i < pixels.length; i++) {
            if(headerPositionStart < 0 && pixels[i] == pixelColorHeaderStart) {
                System.out.println("header start:" + i);
                headerPositionStart = i;
            } else if(pixels[i] == pixelColorHeaderEnd) {
                System.out.println("header end:" + i);
                headerPositionEnd = i;
            } else if(pixels[i] == pixelColorDivider) {
                System.out.println("new row end:" + i);
                dataRowPositionEndList.add(i);
            }

            // System.out.println(i + "-" + Color.red(pixels[i]) + "," + Color.green(pixels[i]) + "," + Color.blue(pixels[i]));
        }

        // in case the last border line is missing
        if(pixels.length - dataRowPositionEndList.get(dataRowPositionEndList.size() - 1) > 20) {
            dataRowPositionEndList.add(pixels.length - 1);
        }

        allPixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        final int headerPositionStartFinal = headerPositionStart;
        final int headerPositionEndFinal = headerPositionEnd;

        return dataRowPositionEndList.size();


    }

    public Bitmap buildNewImage( List<Integer> destRowNums) {
        bitmap.getPixels(allPixels,0, bitmap.getWidth(),0, 0, bitmap.getWidth(), bitmap.getHeight());
        //   List<Integer> destRowNums = new ArrayList<>();
        List<Integer> destPixels = new ArrayList<>();

        // test data

        int width = bitmap.getWidth();
        int totalHight = headerPositionEnd - headerPositionStart;
        for(int i = headerPositionStart * width; i < headerPositionEnd * width; i++) {
            destPixels.add(allPixels[i]);
        }

        //    int dataRowHeightTotal = 0;
        for(int r = 0 ; r < destRowNums.size(); r++) {
            int destRowNum = destRowNums.get(r);
            int rowStart = 0;
            int rowEnd = dataRowPositionEndList.get(destRowNum);
            if(destRowNum == 0) {
                rowStart = headerPositionEnd;
            } else{
                rowStart = dataRowPositionEndList.get(destRowNum - 1);
            }
            totalHight += rowEnd - rowStart;

            for(int i = rowStart * width; i < rowEnd * width; i++) {
                destPixels.add(allPixels[i]);
            }
        }

        Bitmap bitmapNew = Bitmap.createBitmap(width, totalHight, Bitmap.Config.ARGB_8888);
        bitmapNew.setPixels(destPixels.stream().mapToInt(Integer::intValue).toArray(), 0, width, 0, 0, width, totalHight);

        return bitmapNew;

    }

    public List<TransactionData> getTransactionDataList() {
        List<TransactionData> transactionDataList = new ArrayList<TransactionData>();
        List<Integer> rowYList = new ArrayList<Integer>();

        Matcher m;
        // detect how many rows
        for(Text.TextBlock block : text.getTextBlocks()) {

            String result = block.getText();
            m  = Pattern.compile("([0-9]{4})年([0-9]{2})月([0-9]{2})日").matcher(result);
            if(m.find()) {
                TransactionData transactionData = new TransactionData();
                transactionDataList.add(transactionData);
                rowYList.add(block.getBoundingBox().top);
                System.out.println("Y: " + block.getBoundingBox().top);
            }

        }

        for(Text.TextBlock block : text.getTextBlocks()) {
            String result = block.getText();
            System.out.println(block.getBoundingBox() + ":" + result);
            String plate = "";
            m  = Pattern.compile("[0-9]{4}").matcher(result);
//            if(m.find()) {
//                plate = m.group();
//                System.out.println("Y: " + block.getBoundingBox().top);
//                int row = rowYList.indexOf(block.getBoundingBox().top);
//                transactionDataList.get(row).plateNumber = plate;
//            }


        }

        return transactionDataList;
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

        String plate = "";
        m  = Pattern.compile("[0-9]{4}").matcher(result);
        if(m.find()) {
            plate = m.group();
        }
        System.out.println("plate:" + plate);

        float weight = 0;
        m = Pattern.compile("[0-9]{2}[.][0-9]{6}").matcher(result);
        if(m.find()) {
            weight = Float.parseFloat(m.group());
        }
        System.out.println("weight:" + weight);

        float totalSales = 0;
        m = Pattern.compile("[1-9]?[0-9]([,.])[0-9]{3}[.][0-9]").matcher(result);
        if(m.find()) {
            String temp = m.group();
            if(m.group(1).equals(".")) {
                temp = temp.replaceFirst("[.]", "");
            }
            totalSales = Float.parseFloat(temp.replace(",", ""));
        }
        System.out.println("totalSales:" + totalSales);

        float price = 0;
        for(Text.TextBlock block : text.getTextBlocks()) {
            m = Pattern.compile("^[0-9]{3}[.][0-9]").matcher(block.getText());
            if (m.find()) {
                price = Float.parseFloat(m.group().replace(",", ""));
                break;
            }
        }
        System.out.println("price:" + price);

        return transactionData;
    }


}
