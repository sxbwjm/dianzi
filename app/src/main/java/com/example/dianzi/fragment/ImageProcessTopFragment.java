package com.example.dianzi.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.dianzi.MainApplication;
import com.example.dianzi.R;
import com.example.dianzi.common.PhotoViewCommon;
import com.example.dianzi.databinding.FragmentImageProcessTopBinding;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageProcessTopFragment extends Fragment {

    private FragmentImageProcessTopBinding binding;

    ActivityResultLauncher imageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK) {
                        Intent resultData = result.getData();
                        if(resultData != null) {
                            Uri uri = resultData.getData();
                            PhotoViewCommon.showImage(binding.resultPhoto, uri);
                            binding.resultPhotoNew.setImageDrawable(null);
                            processImage(binding.resultPhoto);
                        }
                    }
                }
            }
    );

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentImageProcessTopBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonOpenPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                imageLauncher.launch(intent);


            }
        });

        binding.buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });

        binding.buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bitmap = ((BitmapDrawable)binding.resultPhotoNew.getDrawable()).getBitmap();

                MediaStore.Images.Media.insertImage(view.getContext().getContentResolver(),bitmap, "", "" );
                Toast.makeText(getContext(), "saved", Toast.LENGTH_LONG).show();

            }
        });

//        binding.resultPhoto.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
//            @Override
//            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
//                if(binding.resultPhoto.getDrawable() != null) {
//                    processImage(binding.resultPhoto);
//                }
//            }
//        });

//        binding.resultPhoto.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                if(binding.resultPhoto.getDrawable() != null)
//                   // processImage(((BitmapDrawable)binding.resultPhoto.getDrawable()).getBitmap());
//                    processImage(binding.resultPhoto);
//            }
//        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void processImage(PhotoView photoView ) {
        // PhotoView photoView = binding.getRoot().findViewById(R.id.result_photo);
//        BitmapDrawable bitmapDrawable = (BitmapDrawable)photoView.getDrawable();
//        System.out.println("photo size:" + photoView.getWidth() + "," + photoView.getHeight());
//        float ration = (float)photoView.getHeight()/photoView.getWidth();
//        System.out.println("aspect ratio:" + (float)photoView.getHeight()/photoView.getWidth());
//        float singleRowRatio = 0.03f;

        Bitmap bitmapOrig = ((BitmapDrawable)photoView.getDrawable()).getBitmap();
        int pixelColorHeaderStart = Color.rgb(230, 233,240);
        int pixelColorHeaderEnd = Color.rgb(222, 226,230);
        int pixelColorDivider = Color.rgb(230, 233,240);

        int headerPositionStart = -1;
        int headerPositionEnd = -1;
        List<Integer> dataRowPositionEndList = new ArrayList<Integer>();

        int[] pixels = new int[bitmapOrig.getHeight()];
        bitmapOrig.getPixels(pixels,0, 1,0, 0, 1, bitmapOrig.getHeight());
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

        int[] allPixels = new int[bitmapOrig.getWidth() * bitmapOrig.getHeight()];
        final int headerPositionStartFinal = headerPositionStart;
        final int headerPositionEndFinal = headerPositionEnd;
        List<Integer> destRowNums = new ArrayList<>();
        LinearLayout container = binding.rowListContainer;

        container.removeAllViews();
        for(int i = 0; i < dataRowPositionEndList.size(); i++) {
            CheckBox checkBox = new CheckBox(container.getContext());
            checkBox.setText((i + 1) + "");
            container.addView(checkBox);
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CheckBox checkbox = (CheckBox) view;
                    int row = Integer.parseInt(checkbox.getText().toString()) - 1;
                    if(checkbox.isChecked()) {
                        destRowNums.add(row);
                    } else {
                        destRowNums.remove(Integer.valueOf(row));
                    }

                    System.out.println("destRowNums:" + destRowNums);
                     buildNewImage(destRowNums, bitmapOrig, allPixels, headerPositionStartFinal, headerPositionEndFinal, dataRowPositionEndList);
                }
            });
        }


//        bitmapOrig.getPixels(allPixels,0, bitmapOrig.getWidth(),0, 0, bitmapOrig.getWidth(), bitmapOrig.getHeight());
//        List<Integer> destRowNums = new ArrayList<>();
//        List<Integer> destPixels = new ArrayList<>();
//
//        // test data
//        destRowNums.add(0);
//        destRowNums.add(2);
//
//        int width = bitmapOrig.getWidth();
//        int totalHight = headerPositionEnd - headerPositionStart;
//        for(int i = headerPositionStart * width; i < headerPositionEnd * width; i++) {
//            destPixels.add(allPixels[i]);
//        }
//
//        //    int dataRowHeightTotal = 0;
//        for(int r = 0 ; r < destRowNums.size(); r++) {
//            int destRowNum = destRowNums.get(r);
//            int rowStart = 0;
//            int rowEnd = dataRowPositionEndList.get(destRowNum);
//            if(destRowNum == 0) {
//                rowStart = headerPositionEnd;
//            } else{
//                rowStart = dataRowPositionEndList.get(destRowNum - 1);
//            }
//            totalHight += rowEnd - rowStart;
//
//            for(int i = rowStart * width; i < rowEnd * width; i++) {
//                destPixels.add(allPixels[i]);
//            }
//        }
//
//        Bitmap bitmap = Bitmap.createBitmap(width, totalHight, Bitmap.Config.ARGB_8888);
//        bitmap.setPixels(destPixels.stream().mapToInt(Integer::intValue).toArray(), 0, width, 0, 0, width, totalHight);
//
//        PhotoView photoViewNew = binding.getRoot().findViewById(R.id.result_photo_new);
//
//        photoViewNew.setImageBitmap(bitmap);

    }

    private void buildNewImage( List<Integer> destRowNums, Bitmap bitmapOrig, int[] allPixels, int headerPositionStart, int headerPositionEnd, List<Integer> dataRowPositionEndList) {
        bitmapOrig.getPixels(allPixels,0, bitmapOrig.getWidth(),0, 0, bitmapOrig.getWidth(), bitmapOrig.getHeight());
     //   List<Integer> destRowNums = new ArrayList<>();
        List<Integer> destPixels = new ArrayList<>();

        // test data

        int width = bitmapOrig.getWidth();
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

        Bitmap bitmap = Bitmap.createBitmap(width, totalHight, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(destPixels.stream().mapToInt(Integer::intValue).toArray(), 0, width, 0, 0, width, totalHight);

        PhotoView photoViewNew = binding.getRoot().findViewById(R.id.result_photo_new);

        photoViewNew.setImageBitmap(bitmap);

    }

}