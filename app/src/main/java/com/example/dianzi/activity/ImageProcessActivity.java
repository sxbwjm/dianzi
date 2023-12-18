package com.example.dianzi.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import com.example.dianzi.common.CommonFunc;
import com.example.dianzi.common.OCR;
import com.example.dianzi.common.PhotoViewCommon;
import com.example.dianzi.common.TransactionImage;
import com.example.dianzi.common.TransactionImageNew;
import com.example.dianzi.common.TransactionImageResult;
import com.example.dianzi.fragment.TransactionResultImageFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.dianzi.databinding.ActivityImageProcessBinding;

import com.example.dianzi.R;

public class ImageProcessActivity extends AppCompatActivity {

    private TransactionImage transactionImage = null;
   // private AppBarConfiguration appBarConfiguration;
    private ActivityImageProcessBinding binding;
    ActivityResultLauncher imageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK) {
                        Intent resultData = result.getData();
                        if(resultData != null) {
                            Uri uri = resultData.getData();
                            processImage(uri);
                        }
                    }
                }
            }
    );

    private void processImage(Uri uri) {
        Bitmap bitmap = CommonFunc.getBitmap(uri);
        OCR ocr = new OCR(bitmap);
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if(msg.obj != null) {
                    transactionImage = ocr.getImage();
                    if(transactionImage == null) {
                        Toast.makeText(ImageProcessActivity.this, "image type wrong", Toast.LENGTH_LONG).show();
                    } else {
                        NavController navController = Navigation.findNavController(ImageProcessActivity.this, R.id.nav_host_fragment_content_image_process);
                        if (transactionImage instanceof TransactionImageNew) {
                            navController.navigate(R.id.fragment_new_transaction);
                        } else if(transactionImage instanceof TransactionImageResult) {
                            navController.navigate(R.id.fragment_transaction_result_image);
                        }
                    }

                }
            }
        };
        ocr.recognize(handler);
        // processResultImage(bitmap);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityImageProcessBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_image_process);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
       binding.buttonReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ImageProcessActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


        openImage();

      //  NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_image_process);
     //   AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
      //  NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

      //  NavigationView navigationView = findViewById(R.id.nav_graph_image_process);
     //   NavigationUI.setupWithNavController(navigationView, navController);

    }

    private void openImage() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        imageLauncher.launch(intent);
    }

    public TransactionImage getTransactionImage() {
        return transactionImage;
    }
}