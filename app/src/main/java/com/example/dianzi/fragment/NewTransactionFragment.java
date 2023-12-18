package com.example.dianzi.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.dianzi.activity.ImageProcessActivity;
import com.example.dianzi.common.CommonFunc;
import com.example.dianzi.common.Config;
import com.example.dianzi.MainApplication;
import com.example.dianzi.common.OCR;
import com.example.dianzi.common.PhotoViewCommon;
import com.example.dianzi.R;
import com.example.dianzi.activity.MainActivity;
import com.example.dianzi.common.TransactionImageNew;
import com.example.dianzi.common.TransactionImageResult;
import com.example.dianzi.databinding.FragmentNewTransactionBinding;
import com.example.dianzi.db.DBAsyncTask;
import com.example.dianzi.entity.TransactionData;
import com.github.chrisbanes.photoview.PhotoView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NewTransactionFragment extends Fragment {

    private FragmentNewTransactionBinding binding;
    private TransactionImageNew transactionImageNew;
    ActivityResultLauncher imageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK) {
                        Intent resultData = result.getData();
                        if(resultData != null) {
                            Uri uri = resultData.getData();
                            PhotoViewCommon.showImage(binding.getRoot().findViewById(R.id.transaction_photo), uri);
                        }
                    }
                }
            }
    );


    protected void saveTransaction(View view) {
        view = binding.getRoot();
        TransactionData transaction = new TransactionData();

        transaction.inputDate = ((TextView)view.findViewById(R.id.input_date)).getText().toString();
        transaction.sequence = ((TextView)view.findViewById(R.id.input_record_no)).getText().toString();
        transaction.senderName = ((Spinner)view.findViewById(R.id.input_sender_name)).getSelectedItem().toString();
        transaction.plateNumber = ((TextView)view.findViewById(R.id.input_plate)).getText().toString();
        transaction.accountName = ((Spinner)view.findViewById(R.id.input_account_name)).getSelectedItem().toString();
        transaction.weight = Float.parseFloat(((TextView)view.findViewById(R.id.input_weight)).getText().toString());

   //     MainActivity activity = (MainActivity)getActivity();
        DBAsyncTask dbAsyncTask = new DBAsyncTask(MainApplication.instance.getDB());
        dbAsyncTask.insertTransaction(transaction);
       // MainActivity.transactions.add(transaction);
        //AppDatabaseSingleton.getInstance(getActivity()).transactionDao().insert(transaction);

        PhotoView photoView = binding.getRoot().findViewById(R.id.transaction_photo);

        Bitmap newBitmap = Bitmap.createBitmap(photoView.getWidth(), photoView.getHeight(), Bitmap.Config.RGB_565);
        Canvas c = new Canvas(newBitmap);
        photoView.draw(c);
       // Bitmap bitmap = ((BitmapDrawable)photoView.getDrawable()).getBitmap();
        transaction.saveTransactionImage(newBitmap);

    }
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentNewTransactionBinding.inflate(inflater, container, false);


        binding.buttonSaveInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTransaction(view);
                //((MainActivity)getActivity()).resetTransactionImage();
                navigateBack();

            }
        });

        binding.buttonCancelInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //((MainActivity)getActivity()).resetTransactionImage();
               // NavHostFragment.findNavController(NewTransactionFragment.this).navigate(R.id.action_newFragment_to_nav_list);
                navigateBack();
            }
        });

        binding.buttonOpenPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                imageLauncher.launch(intent);
            }
        });

        binding.buttonRotateLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.transactionPhoto.setRotationBy(-90);
            }
        });

        binding.buttonRotateRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.transactionPhoto.setRotationBy(90);
            }
        });

        binding.buttonOcr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PhotoView photoView = binding.transactionPhoto;
                Bitmap newBitmap = Bitmap.createBitmap(photoView.getWidth(), photoView.getHeight(), Bitmap.Config.RGB_565);
                Canvas c = new Canvas(newBitmap);
                photoView.draw(c);

                OCR ocr = new OCR(newBitmap);
                Handler handler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.obj != null) {
                             transactionImageNew  = (TransactionImageNew) ocr.getImage();
                             init(transactionImageNew);
                        }

                    }
              };

                ocr.recognize(handler);
            }
        });

        FragmentActivity activity = getActivity();
        if(activity instanceof ImageProcessActivity) {
            transactionImageNew = (TransactionImageNew)((ImageProcessActivity)getActivity()).getTransactionImage();
        }
        else {
            transactionImageNew = null;
        }



       // View view = binding.getRoot();

        init(transactionImageNew);

        return binding.getRoot();

    }

    private void navigateBack() {
        FragmentActivity activity = getActivity();
        if(activity instanceof ImageProcessActivity) {
            Intent intent = new Intent(activity, MainActivity.class);
            startActivity(intent);
        } else {
            NavHostFragment.findNavController(NewTransactionFragment.this).navigate(R.id.action_newFragment_to_nav_list);
        }
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    protected void init(TransactionImageNew transactionImageNew) {

        ArrayAdapter adapter = new ArrayAdapter(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, Config.SENDER_NAMES);
        binding.inputSenderName.setAdapter(adapter);

        DateFormat df = new SimpleDateFormat(Config.DATE_FORMAT);
        Date date = new Date();
        binding.inputDate.setText(CommonFunc.getSystemDateString());

        adapter = new ArrayAdapter(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, Config.ACCOUNT_NAMES);
        binding.inputAccountName.setAdapter(adapter);

        if(transactionImageNew != null) {
            binding.transactionPhoto.setImageBitmap(transactionImageNew.getBitmap());
            TransactionData transactionData = transactionImageNew.getTransactionData();
            binding.inputDate.setText(transactionData.inputDate);
            binding.inputRecordNo.setText(transactionData.sequence);
            binding.inputPlate.setText(transactionData.plateNumber);
            if(!transactionData.accountName.isEmpty()) {
                binding.inputAccountName.setSelection(adapter.getPosition(transactionData.accountName));
            }
            binding.inputWeight.setText(transactionData.weight + "");
        }
    }

}