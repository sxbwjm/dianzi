package com.example.dianzi;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dianzi.common.CommonFunc;
import com.example.dianzi.databinding.FragmentBankStatementItemBinding;
import com.example.dianzi.entity.BankflowReceive;

import java.util.List;

/**
 * TODO: Replace the implementation with code for your data type.
 */
public class BankflowReceiveRecyclerViewAdapter extends RecyclerView.Adapter<BankflowReceiveRecyclerViewAdapter.ViewHolder> {

    private final List<BankflowReceive> mValues;

    public BankflowReceiveRecyclerViewAdapter(List<BankflowReceive> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentBankStatementItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.viewDate.setText(mValues.get(position).flowDate);
        holder.viewAccountName.setText(mValues.get(position).name);
        holder.viewAmount.setText(CommonFunc.getAmountText(mValues.get(position).amount));

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView viewDate;
        public final TextView viewAccountName;
        public final TextView viewAmount;
        public BankflowReceive mItem;

        public ViewHolder(FragmentBankStatementItemBinding binding) {
            super(binding.getRoot());
            viewDate = binding.date;
            viewAccountName = binding.accountName;
            viewAmount = binding.amount;
        }

    }
}