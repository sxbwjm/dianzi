package com.example.dianzi.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.dianzi.MainApplication;
import com.example.dianzi.common.PhotoViewCommon;
import com.example.dianzi.R;
import com.example.dianzi.db.DBAsyncTask;
import com.example.dianzi.entity.TransactionData;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TransactionDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TransactionDetailsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TransactionData transaction;

    private  View rootView;

    public TransactionDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TransactionDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TransactionDetailsFragment newInstance(String param1, String param2) {
        TransactionDetailsFragment fragment = new TransactionDetailsFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_transaction_details, container, false);
        transaction = (TransactionData) getArguments().getSerializable("transaction");

        TextView textView = rootView.findViewById(R.id.result_transaction_date);
        textView.setText(transaction.inputDate);

        textView = rootView.findViewById(R.id.result_transaction_sequence);
        textView.setText(transaction.sequence);

        textView = rootView.findViewById(R.id.result_sender_name);
        textView.setText(transaction.senderName);

        textView = rootView.findViewById(R.id.result_plate);
        textView.setText(transaction.plateNumber);

        textView = rootView.findViewById(R.id.result_account_name);
        textView.setText(transaction.accountName);

        textView = rootView.findViewById(R.id.result_weight);
        textView.setText(transaction.weight + "");

        textView = rootView.findViewById(R.id.result_price);
        textView.setText(transaction.price + "");

        textView = rootView.findViewById(R.id.result_total_sales);
        textView.setText(transaction.receivable + "");


        textView = rootView.findViewById(R.id.result_fee);
        textView.setText(transaction.fee + "");

        textView = rootView.findViewById(R.id.result_pay_amount);
        textView.setText(transaction.payable + "");

        textView = rootView.findViewById(R.id.result_pay_date);
      //  textView.setText(transaction.payDate + "");

        textView = rootView.findViewById(R.id.result_receive_date);
      //  textView.setText(transaction.receiveDate + "");


        File file = transaction.getTransactionImageFile();
        if(file.exists()) {
            PhotoViewCommon.showImage(rootView.findViewById(R.id.transaction_photo), Uri.fromFile(file));
            //showImage(Uri.fromFile(file));
        }

        file = transaction.getResultImageFile();
        if(file.exists()) {
            PhotoViewCommon.showImage(rootView.findViewById(R.id.result_photo), Uri.fromFile(file));
            //showImage(Uri.fromFile(file));
        }

        Button deleteButton = rootView.findViewById(R.id.button_delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setCancelable(true).setTitle(R.string.dialog_confirm_title)
                        .setMessage(R.string.dialog_confirm_delete_transaction)
                        .setPositiveButton(R.string.dialog_confirm_button, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteTransaction();
                            }

                        });
                builder.setNegativeButton(R.string.dialog_cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });
        return rootView;

    }

    private  void deleteTransaction() {
        Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if(msg.obj != null) {
                // List<Payment> payments = (List<Payment>)msg.obj;
                NavHostFragment.findNavController(TransactionDetailsFragment.this).navigate(R.id.action_transactionDetailsFragment_to_nav_transaction_list);
            }
        }
    };
        DBAsyncTask dbAsyncTask = new DBAsyncTask(MainApplication.instance.getDB());
        dbAsyncTask.deleteTransaction(transaction, handler);
    }
}