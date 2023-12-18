package com.example.dianzi.adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dianzi.R;
import com.example.dianzi.common.CommonFunc;
import com.example.dianzi.databinding.FragmentBankflowPrincipleItemBinding;
import com.example.dianzi.entity.BankflowPrinciple;

import java.util.List;

/**
 * TODO: Replace the implementation with code for your data type.
 */
public class BankflowPrincipleRecyclerViewAdapter extends RecyclerView.Adapter<BankflowPrincipleRecyclerViewAdapter.ViewHolder> {

    private final List<BankflowPrinciple> mValues;

    public BankflowPrincipleRecyclerViewAdapter(List<BankflowPrinciple> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentBankflowPrincipleItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.viewDate.setText(mValues.get(position).flowDate);
        holder.note.setText(mValues.get(position).note);
        holder.viewAmount.setText(CommonFunc.getAmountText(mValues.get(position).amount));
        holder.itemRow.setOnClickListener(new UpdateItemClickListener(position));
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView viewDate;
        public final TextView note;
        public final TextView viewAmount;
        public BankflowPrinciple mItem;
        public final LinearLayout itemRow;

        public ViewHolder(FragmentBankflowPrincipleItemBinding binding) {
            super(binding.getRoot());
            viewDate = binding.date;
            note = binding.note;
            viewAmount = binding.amount;
            itemRow = binding.itemRow;
        }

    }

    class UpdateItemClickListener implements View.OnClickListener {
        int position;
        public UpdateItemClickListener(int position) {
            this.position = position;
        }
        @Override
        public void onClick(View view) {
            BankflowPrinciple bankflowPrinciple = mValues.get(position);
            Bundle bundle = new Bundle();
            bundle.putString("flowId", mValues.get(position).flowId + "");
            bundle.putSerializable("bankflowPrinciple", bankflowPrinciple);
            Navigation.findNavController(view).navigate(R.id.action_Top_to_New, bundle);
        }
    }
}