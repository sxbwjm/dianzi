package com.example.dianzi.common;

import android.graphics.Bitmap;
import android.os.Handler;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OCR {
    Bitmap bitmap = null;
    Text text = null;

    public OCR(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
    public void recognize(Handler handler) {
        TextRecognizer recognizer =
                TextRecognition.getClient(new ChineseTextRecognizerOptions.Builder().build());
        InputImage inputImage = InputImage.fromBitmap(bitmap, 0);
        recognizer.process(inputImage).addOnSuccessListener(new OnSuccessListener<Text>() {
            @Override
            public void onSuccess(Text text) {
                OCR.this.text = text;
                handler.sendMessage(handler.obtainMessage(1, "OK"));
            }



        });
    }

    public TransactionImage getImage() {
        String result = text.getText();
        System.out.println(result);

//        if(result.contains("客户") ||result.contains("称重") ) {
//            return  new TransactionImageNew(bitmap, text);
//        } else if (result.contains("水分")) {
//            return new TransactionImageResult(bitmap, text);
//        } else {
//            return null;
//        }
        if (result.contains("水分")) {
            return new TransactionImageResult(bitmap, text);
        } else {
            return new TransactionImageNew(bitmap, text);
        }

    }
}
