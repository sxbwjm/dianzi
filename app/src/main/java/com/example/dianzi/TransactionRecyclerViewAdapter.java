package com.example.dianzi;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.dianzi.common.CommonFunc;
import com.example.dianzi.entity.TransactionData;
import com.example.dianzi.databinding.FragmentTransactionItemBinding;

import java.util.List;

/**
 * TODO: Replace the implementation with code for your data type.
 */
public class TransactionRecyclerViewAdapter extends RecyclerView.Adapter<TransactionRecyclerViewAdapter.ViewHolder>{

    private final List<TransactionData> mValues;

    public TransactionRecyclerViewAdapter(List<TransactionData> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentTransactionItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    class AddResultClickListener implements View.OnClickListener {
        int position;
        public AddResultClickListener(int position) {
            this.position = position;
        }
        @Override
        public void onClick(View view) {
            TransactionData transaction = mValues.get(position);
            System.out.println(mValues.get(position).inputDate + mValues.get(position).sequence);
            Bundle bundle = new Bundle();
           // bundle.putString("inputDate", mValues.get(position).inputDate);
            bundle.putSerializable("transaction", transaction);
            Navigation.findNavController(view).navigate(R.id.action_nav_list_to_resultFragment, bundle);
        }
    }

    class TransactionDetailsClickListener implements View.OnClickListener {
        int position;
        public TransactionDetailsClickListener(int position) {
            this.position = position;
        }
        @Override
        public void onClick(View view) {
            TransactionData transaction = mValues.get(position);
            Bundle bundle = new Bundle();
            bundle.putSerializable("transaction", transaction);
            Navigation.findNavController(view).navigate(R.id.action_nav_list_to_transactionDetailsFragment, bundle);
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.buttonAddResult.setOnClickListener(new AddResultClickListener(position));
        holder.itemRow.setOnClickListener(new TransactionDetailsClickListener(position));
        holder.viewTransactionNo.setText(mValues.get(position).inputDate + mValues.get(position).sequence);
        holder.viewSenderName.setText(mValues.get(position).senderName);
        holder.viewPlateNumber.setText(mValues.get(position).plateNumber);
        holder.viewAccountName.setText(mValues.get(position).accountName);
        holder.viewWeight.setText((int)mValues.get(position).weight + "");

        holder.viewPrice.setText(CommonFunc.getAmountText(mValues.get(position).price));
        holder.viewTotalSale.setText(CommonFunc.getAmountText(mValues.get(position).receivable));
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final Button buttonAddResult;
        public final TextView viewTransactionNo;
        public final TextView viewSenderName;
        public final TextView viewPlateNumber;
        public final TextView viewAccountName;
        public final TextView viewWeight;

        public final TextView viewPrice;
        public final TextView viewTotalSale;
        public final ConstraintLayout itemRow;



        public TransactionData mItem;

        public ViewHolder(FragmentTransactionItemBinding binding) {
            super(binding.getRoot());
            buttonAddResult = binding.addResult;
            viewTransactionNo = binding.transactionNo;
            viewSenderName = binding.senderName;
            viewPlateNumber = binding.plateNumber;
            viewAccountName = binding.accountName;
            viewWeight = binding.weight;
            viewPrice = binding.resultPrice;
            viewTotalSale = binding.resultTotalSales;
            itemRow = binding.itemRow;
        }

    }
}