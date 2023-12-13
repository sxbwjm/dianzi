package com.example.dianzi;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dianzi.common.CommonFunc;
import com.example.dianzi.databinding.FragmentFlowItemBinding;
import com.example.dianzi.entity.CashflowReceivable;
import com.example.dianzi.entity.Flow;

import java.util.List;

/**
 * TODO: Replace the implementation with code for your data type.
 */
public class FlowRecyclerViewAdapter extends RecyclerView.Adapter<FlowRecyclerViewAdapter.ViewHolder>{

    private final List<? extends Flow> mValues;

    public FlowRecyclerViewAdapter(List<? extends Flow> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentFlowItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }


    class PaymentDetailsClickListener implements View.OnClickListener {
        int position;
        public PaymentDetailsClickListener(int position) {
            this.position = position;
        }
        @Override
        public void onClick(View view) {
            Flow flow = mValues.get(position);
            Bundle bundle = new Bundle();
          //  bundle.putSerializable("payment", flow);
           // Navigation.findNavController(view).navigate(R.id.action_nav_list_to_transactionDetailsFragment, bundle);
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.itemRow.setOnClickListener(new PaymentDetailsClickListener(position));
        holder.viewPayDate.setText(mValues.get(position).flowDate);
        holder.viewPayee.setText(mValues.get(position).name);
        holder.viewAmount.setText(CommonFunc.getAmountText(mValues.get(position).amount));
        if(holder.mItem instanceof CashflowReceivable) {
            CashflowReceivable c = (CashflowReceivable)holder.mItem;
            if(c.bankflowId != 0) {
                holder.itemRow.setBackgroundColor(Color.GRAY);
            } else {
                holder.itemRow.setBackgroundColor(Color.WHITE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView viewPayDate;
        public final TextView viewPayee;
        public final TextView viewAmount;
        public final ConstraintLayout itemRow;



        public Flow mItem;

        public ViewHolder(FragmentFlowItemBinding binding) {
            super(binding.getRoot());
            viewPayDate = binding.payDate;
            viewPayee = binding.payee;
            viewAmount = binding.amount;
            itemRow = binding.itemRow;
        }

    }
}