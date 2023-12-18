package com.example.dianzi.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PatternMatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.dianzi.adapter.TransactionRecyclerViewAdapter;
import com.example.dianzi.common.Config;
import com.example.dianzi.MainApplication;
import com.example.dianzi.common.PhotoViewCommon;
import com.example.dianzi.R;
import com.example.dianzi.databinding.FragmentResultInputBinding;
import com.example.dianzi.db.DBAsyncTask;
import com.example.dianzi.entity.TransactionData;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ResultInputFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResultInputFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FragmentResultInputBinding binding;


    ActivityResultLauncher imageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK) {
                        Intent resultData = result.getData();
                        if(resultData != null) {
                            Uri uri = resultData.getData();
                            PhotoView photoView = binding.getRoot().findViewById(R.id.result_photo);
                            PhotoViewCommon.showImage(photoView, uri);
                            Bitmap bitmap = ((BitmapDrawable)photoView.getDrawable()).getBitmap();
                            ExecutorService executor = Executors.newSingleThreadExecutor();
                            executor.submit(new Runnable() {
                                 @Override
                                 public void run() {
                                     ocr(bitmap);
                                 }
                             }
                            );



                        }
                    }
                }
            }
    );

    public ResultInputFragment() {
        // Required empty public constructor
    }

    private void ocr(Bitmap bitmap) {
        TextRecognizer recognizer =
                TextRecognition.getClient(new ChineseTextRecognizerOptions.Builder().build());
        InputImage inputImage = InputImage.fromBitmap(bitmap, 0);
        recognizer.process(inputImage).addOnSuccessListener(new OnSuccessListener<Text>() {
            @Override
            public void onSuccess(Text text) {
                String result = text.getText();
              //  System.out.println(result);
                Matcher m = null;

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

            }

        });
    }

    Handler ocrHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if(msg.obj != null) {
                Text ocrText = (Text)msg.obj;

            }
        }
    };


    private void testOCR(Text text) {
            String resultText = text.getText();
            System.out.println("full text:" + resultText);
            for(Text.TextBlock block : text.getTextBlocks()) {
                String blockText = block.getText();

                Point[] blockCornerPoints = block.getCornerPoints();

                Rect blockFrame = block.getBoundingBox();
                System.out.print("\tblock points:");
                for(Point p : blockCornerPoints) {
                    System.out.println(p);
                }
                System.out.println();
                System.out.println("\tblock frame:" + blockFrame);
                System.out.println("\tblock text:" + blockText);
                for(Text.Line line : block.getLines()) {
                    String lineText = line.getText();
                    System.out.println("\t\tline text:" + lineText);
                    Point[] lineCornerPoints = line.getCornerPoints();
                    Rect lineFrame = line.getBoundingBox();
                    for(Text.Element element : line.getElements()) {
                        String elementText = element.getText();
                        System.out.println("\t\t\telement text:" + elementText);
                        Point[] elementCornerPoints = element.getCornerPoints();
                        Rect elementFrame = element.getBoundingBox();
                        for(Text.Symbol symbol : element.getSymbols()) {
                            String symbolText = symbol.getText();
                            System.out.println("\t\t\t\tsymbol text:" + symbolText);
                            Point[] symbolCornerPoints = symbol.getCornerPoints();
                            Rect symbolFrame = symbol.getBoundingBox();
                        }

                    }
                }


            }

    }



    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ResultFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ResultInputFragment newInstance(String param1, String param2) {
        ResultInputFragment fragment = new ResultInputFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    protected void saveResult(TransactionData transactionData) {
        View view = binding.getRoot();

        transactionData.inputDate = ((EditText)view.findViewById(R.id.result_transaction_date)).getText().toString();
        transactionData.sequence = ((EditText)view.findViewById(R.id.result_transaction_sequence)).getText().toString();
        transactionData.senderName = ((Spinner)view.findViewById(R.id.result_sender_name)).getSelectedItem().toString();
        transactionData.plateNumber = ((EditText)view.findViewById(R.id.result_plate)).getText().toString();
        transactionData.accountName = ((Spinner)view.findViewById(R.id.result_account_name)).getSelectedItem().toString();
        transactionData.weight = Float.parseFloat(((EditText)view.findViewById(R.id.result_weight)).getText().toString());

        transactionData.price = Float.parseFloat(((TextView)view.findViewById(R.id.result_price)).getText().toString());
        transactionData.receivable = Float.parseFloat(((TextView)view.findViewById(R.id.result_total_sales)).getText().toString());
        transactionData.updateFeePayable();

        //MainActivity activity = (MainActivity)getActivity();
        DBAsyncTask dbAsyncTask = new DBAsyncTask(MainApplication.instance.getDB());
        dbAsyncTask.updateTransaction(transactionData);

        PhotoView photoView = binding.getRoot().findViewById(R.id.result_photo);
        Bitmap bitmap = ((BitmapDrawable)photoView.getDrawable()).getBitmap();
        transactionData.saveResultImage(bitmap);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentResultInputBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
//        View view = inflater.inflate(R.layout.fragment_result, container, false);
        TransactionData transactionData = (TransactionData) getArguments().getSerializable("transaction");

        EditText textView = view.findViewById(R.id.result_transaction_date);
        textView.setText(transactionData.inputDate);

        textView = view.findViewById(R.id.result_transaction_sequence);
        textView.setText(transactionData.sequence);

        Spinner spinner = view.findViewById(R.id.result_sender_name);
        ArrayAdapter adapter = new ArrayAdapter(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, Config.SENDER_NAMES);
        spinner.setAdapter(adapter);

        spinner.setSelection(adapter.getPosition(transactionData.senderName));

        textView = view.findViewById(R.id.result_plate);
        textView.setText(transactionData.plateNumber);

        adapter = new ArrayAdapter(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, Config.ACCOUNT_NAMES);
        spinner = view.findViewById(R.id.result_account_name);
        spinner.setAdapter(adapter);
        spinner.setSelection(adapter.getPosition(transactionData.accountName));

        TextView weightTextView = view.findViewById(R.id.result_weight);
        weightTextView.setText(transactionData.weight + "");

        EditText priceEditText = view.findViewById(R.id.result_price);
        priceEditText.setText(transactionData.price == 0 ? "" : transactionData.price + "");



        EditText totalSalesEditText = view.findViewById(R.id.result_total_sales);
        totalSalesEditText.setText(transactionData.receivable == 0 ? "" : transactionData.receivable + "");

        totalSalesEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length() > 0) {
                    float totalSales = Float.parseFloat(editable.toString());
                    float weight = Float.parseFloat(weightTextView.getText().toString());
                    float price = (float)Math.round(totalSales * 10000 / weight) / 10;
                    priceEditText.setText(price + "");
                } else {
                    priceEditText.setText("");
                }

            }
        });


        File file = transactionData.getResultImageFile();
        if(file.exists()) {
            PhotoViewCommon.showImage(binding.resultPhoto, Uri.fromFile(file));
            //showImage(Uri.fromFile(file));
        }
        binding.buttonSaveResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveResult(transactionData);
                NavHostFragment.findNavController(ResultInputFragment.this).navigate(R.id.action_resultFragment_to_nav_list);
            }
        });

        binding.buttonCancelResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(ResultInputFragment.this).navigate(R.id.action_resultFragment_to_nav_list);
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

               return view;
    }
}