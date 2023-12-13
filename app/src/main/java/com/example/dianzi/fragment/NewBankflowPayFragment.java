package com.example.dianzi.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.dianzi.common.CommonFunc;
import com.example.dianzi.common.Config;
import com.example.dianzi.MainApplication;
import com.example.dianzi.R;
import com.example.dianzi.activity.MainActivity;
import com.example.dianzi.db.DBAsyncTask;
import com.example.dianzi.entity.BankflowPay;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewBankflowPayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewBankflowPayFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View rootView;

    public NewBankflowPayFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewPaymentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewBankflowPayFragment newInstance(String param1, String param2) {
        NewBankflowPayFragment fragment = new NewBankflowPayFragment();
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
        rootView = inflater.inflate(R.layout.fragment_new_payment, container, false);

        rootView.findViewById(R.id.button_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePrePayment(view);
                NavHostFragment.findNavController(NewBankflowPayFragment.this).navigate(R.id.action_newBankflowPayFragment_to_nav_payment);
            }
        });

        rootView.findViewById(R.id.button_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(NewBankflowPayFragment.this).navigate(R.id.action_newBankflowPayFragment_to_nav_payment);
            }
        });

        init(rootView);
        return rootView;
    }

    protected void init(View view) {
        EditText text = view.findViewById(R.id.date);
        text.setText(CommonFunc.getSystemDateString());

        ArrayAdapter adapter = new ArrayAdapter(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, Config.SENDER_NAMES);
        Spinner spinner = (Spinner) view.findViewById(R.id.sender_name);
        spinner.setAdapter(adapter);

    }

    protected void savePrePayment(View view) {
        BankflowPay bankflowPay = new BankflowPay();

        bankflowPay.flowDate = ((TextView)rootView.findViewById(R.id.date)).getText().toString();
        bankflowPay.name = ((Spinner)rootView.findViewById(R.id.sender_name)).getSelectedItem().toString();
        bankflowPay.amount = Float.parseFloat(((TextView)rootView.findViewById(R.id.amount)).getText().toString());
        bankflowPay.payType = BankflowPay.TYPE_PREPAY;

        MainActivity activity = (MainActivity)getActivity();
        DBAsyncTask dbAsyncTask = new DBAsyncTask(MainApplication.instance.getDB());
        dbAsyncTask.insertPrePayment(bankflowPay);
    }
}