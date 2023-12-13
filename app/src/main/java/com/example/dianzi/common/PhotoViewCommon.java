package com.example.dianzi.common;

import android.app.Activity;
import android.app.Application;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.example.dianzi.MainApplication;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.BufferedInputStream;
import java.io.InputStream;

public class PhotoViewCommon {

//    public static ActivityResultLauncher createImageLaucher(Fragment fragment, PhotoView photoView) {
//        ActivityResultLauncher imageLauncher = fragment.registerForActivityResult(
//                new ActivityResultContracts.StartActivityForResult(),
//                new ActivityResultCallback<ActivityResult>() {
//                    @Override
//                    public void onActivityResult(ActivityResult result) {
//                        if (result.getResultCode() == Activity.RESULT_OK) {
//                            Intent resultData = result.getData();
//                            if (resultData != null) {
//                                Uri uri = resultData.getData();
//                                PhotoViewCommon.showImage(photoView, uri);
//                            }
//                        }
//                    }
//                }
//        );
//
//        return imageLauncher;
//
//    }
    public static void showImage(PhotoView photoView, Uri uri) {
        try {

            InputStream stream = MainApplication.instance.getContentResolver().openInputStream(uri);
            Bitmap bmp = BitmapFactory.decodeStream(new BufferedInputStream(stream));

            photoView.setImageBitmap(bmp);
            stream.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}
