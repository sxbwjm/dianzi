package com.example.dianzi.entity;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.dianzi.MainApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;

@Entity(tableName = "transaction_data")
public class TransactionData implements Serializable {
    @NonNull @ColumnInfo(name = "transaction_id") @PrimaryKey(autoGenerate = true)
    public long transactionId;
    @NonNull @ColumnInfo(name = "input_date")
    public String inputDate;
    @NonNull @ColumnInfo(name = "sequence")
    public String sequence;
    @ColumnInfo(name = "send_name")
    public String senderName;
    @ColumnInfo(name = "plate_number")
    public String plateNumber;
    @ColumnInfo(name = "account_name")
    public String accountName;

    public float weight;
    public float price;

    public float receivable;
    @ColumnInfo(name = "receive_date")
    public String receiveDate;

    public float fee;
    public float payable;

    public String note;

    @ColumnInfo(name = "cashflow_payable_id")
    public long cashflowPayableId;
    @ColumnInfo(name = "cashflow_receivable_id")
    public long cashflowReceivableId;
  //  @ColumnInfo(name = "pay_date")
 //   public String payDate;

   // @ColumnInfo(name="payable_batch_id")
   // public long payableBatchId;

   // @ColumnInfo(name="bank_statement_id")
   // public long bankStatementId;

    public void updateFeePayable() {
        fee = weight / 100;
        payable = (float)Math.ceil(receivable - fee);
    }


    protected void saveImage(Bitmap bitmap, File file) {
        try {
            FileOutputStream sf = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, sf);
            sf.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    public void saveTransactionImage(Bitmap bitmap) {
        File file = getTransactionImageFile();
        saveImage(bitmap, file);
    }

    public void saveResultImage(Bitmap bitmap) {
        File file = getResultImageFile();
        saveImage(bitmap, file);
    }
    public File getTransactionImageFile(){
        File dir = MainApplication.instance.getImageFolder();
        String fileName = "transaction_" + inputDate + sequence + ".png";
        return new File(dir, fileName);

    }

    public File getResultImageFile(){
        File dir = MainApplication.instance.getImageFolder();
        String fileName = "result_" + inputDate + sequence + ".png";
        return new File(dir, fileName);
    }

    @Override
    public String toString() {
        return String.format("%s,%s%s,%s,%s,%f,%f", senderName,inputDate,sequence, plateNumber, accountName, weight, receivable);
    }
}
