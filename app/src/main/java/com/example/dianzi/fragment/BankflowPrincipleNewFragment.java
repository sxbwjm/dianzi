package com.example.dianzi.fragment;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.dianzi.MainApplication;
import com.example.dianzi.R;
import com.example.dianzi.activity.MainActivity;
import com.example.dianzi.common.CommonFunc;
import com.example.dianzi.common.Config;
import com.example.dianzi.databinding.FragmentBankflowPrincipleNewBinding;
import com.example.dianzi.db.DBAsyncTask;
import com.example.dianzi.entity.BankflowPrinciple;
import com.example.dianzi.entity.TransactionData;
import com.github.chrisbanes.photoview.PhotoView;

public class BankflowPrincipleNewFragment extends Fragment {

    private FragmentBankflowPrincipleNewBinding binding;
    private BankflowPrinciple bankflowPrinciple = null;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentBankflowPrincipleNewBinding.inflate(inflater, container, false);
        binding.buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveBankflow();
                NavHostFragment.findNavController(BankflowPrincipleNewFragment.this).navigate(R.id.action_New_to_Top);
            }
        });
        binding.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(BankflowPrincipleNewFragment.this).navigate(R.id.action_New_to_Top);
            }
        });

        Bundle bundle = getArguments();
        if(bundle != null) {
            bankflowPrinciple = (BankflowPrinciple) bundle.getSerializable("bankflowPrinciple");
        }

        if(bankflowPrinciple == null) {
            binding.date.setText(CommonFunc.getSystemDateString());
        }
        else {
            binding.date.setText(bankflowPrinciple.flowDate);
            binding.amount.setText(bankflowPrinciple.amount + "");
            binding.note.setText(bankflowPrinciple.note);
        }
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    protected void saveBankflow() {

        boolean isNew = false;
        if(bankflowPrinciple == null) {
            bankflowPrinciple = new BankflowPrinciple();
            isNew = true;
        }

        bankflowPrinciple.flowDate = binding.date.getText().toString();
        bankflowPrinciple.note = binding.note.getText().toString();
        bankflowPrinciple.amount = Float.parseFloat(binding.amount.getText().toString());

        DBAsyncTask dbAsyncTask = new DBAsyncTask(MainApplication.instance.getDB());
        if(isNew) {
            dbAsyncTask.insertBankflowPrinciple(bankflowPrinciple);
        }
        else{
            dbAsyncTask.updateBankflowPrinciple(bankflowPrinciple);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}