package com.example.dianzi.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.dianzi.R;
import com.example.dianzi.activity.ImageProcessActivity;
import com.example.dianzi.common.OCR;
import com.example.dianzi.common.PhotoViewCommon;
import com.example.dianzi.common.TransactionImage;
import com.example.dianzi.common.TransactionImageNew;
import com.example.dianzi.common.TransactionImageResult;
import com.example.dianzi.databinding.FragmentTransactionNewImageBinding;
import com.example.dianzi.databinding.FragmentTransactionResultImageBinding;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.ArrayList;
import java.util.List;

public class TransactionResultImageFragment extends Fragment {

    private FragmentTransactionResultImageBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentTransactionResultImageBinding.inflate(inflater, container, false);
        TransactionImageResult transactionImageResult = (TransactionImageResult)((ImageProcessActivity)getActivity()).getTransactionImage();
        if(transactionImageResult!= null) {
            // Inflate the layout for this fragment
            binding.resultPhoto.setImageBitmap(transactionImageResult.getBitmap());
        }
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        binding.buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bitmap = ((BitmapDrawable)binding.resultPhotoNew.getDrawable()).getBitmap();

                MediaStore.Images.Media.insertImage(view.getContext().getContentResolver(),bitmap, "", "" );
                Toast.makeText(getContext(), "saved", Toast.LENGTH_LONG).show();

            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    private void processResultImage(TransactionImageResult transactionImageResult) {
        int rowNum = transactionImageResult.analyzeRows();
        List<Integer> destRowNums = new ArrayList<>();
        LinearLayout container = binding.rowListContainer;

        container.removeAllViews();
        for(int i = 0; i < rowNum; i++) {
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
                    Bitmap bitmapNew = transactionImageResult.buildNewImage(destRowNums);
                    PhotoView photoViewNew = binding.getRoot().findViewById(R.id.result_photo_new);

                    photoViewNew.setImageBitmap(bitmapNew);
                }
            });
        }
    }



}