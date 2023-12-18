package com.example.dianzi.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.dianzi.MainApplication;
import com.example.dianzi.R;
import com.example.dianzi.common.CommonFunc;
import com.example.dianzi.databinding.FragmentPayableBatchItemBinding;
import com.example.dianzi.db.DBAsyncTask;
import com.example.dianzi.entity.BankflowPay;
import com.example.dianzi.entity.CashflowPayable;
import com.example.dianzi.entity.PayableBatch;
import com.example.dianzi.entity.PayableBatchWithBreakdown;

import java.util.List;

/**
 * TODO: Replace the implementation with code for your data type.
 */
public class PaymentRecyclerViewAdapter extends RecyclerView.Adapter<PaymentRecyclerViewAdapter.ViewHolder>{

    private final List<PayableBatchWithBreakdown> mValues;

    public PaymentRecyclerViewAdapter(List<PayableBatchWithBreakdown> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ViewHolder viewHolder = new ViewHolder(FragmentPayableBatchItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

        return viewHolder;

    }


    class PaymentDetailsClickListener implements View.OnClickListener {
        int position;
        public PaymentDetailsClickListener(int position) {
            this.position = position;
        }
        @Override
        public void onClick(View view) {
            PayableBatchWithBreakdown payableBatchWithBreakdown = mValues.get(position);
            Bundle bundle = new Bundle();
          //  bundle.putSerializable("payment", flow);
           // Navigation.findNavController(view).navigate(R.id.action_nav_list_to_transactionDetailsFragment, bundle);
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        PayableBatchWithBreakdown payableBatchWithBreakdown = mValues.get(position);
        holder.mItem = payableBatchWithBreakdown;
     //   holder.itemRow.setOnClickListener(new PaymentDetailsClickListener(position));
     //   holder.viewPayDate.setText(mValues.get(position).flowDate);
        float settleAmount = showPayableBreakdown(holder, payableBatchWithBreakdown);
        holder.viewPayee.setText(payableBatchWithBreakdown.payableBatch.payee);

        if(payableBatchWithBreakdown.payableBatch.payDate == null) {
            holder.viewAmount.setText(settleAmount + "");
            showSettleButton(holder, payableBatchWithBreakdown.payableBatch, settleAmount);
        }
       else {
            holder.viewAmount.setText("已结算");
            holder.settleButton.setVisibility(Button.INVISIBLE);
            holder.itemRow.setBackgroundColor(Color.GRAY);
        }


    }

    private void showSettleButton(final ViewHolder holder, PayableBatch payableBatch, float settleAmount) {
            Button settleButton = holder.settleButton;
             settleButton.setText(R.string.settle);
            if(settleAmount < 0) {
                settleButton.setEnabled(false);
            } else {
                settleButton.setEnabled(true);
                settleButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText settleDateView = new EditText(view.getContext());
                        settleDateView.setText(CommonFunc.getSystemDateString());

                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                        builder.setCancelable(true).setTitle(R.string.dialog_confirm_title)
                                .setMessage(R.string.dialog_confirm_create_payment)
                                .setView(settleDateView)
                                .setPositiveButton(R.string.dialog_confirm_button, new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        String settleDate = settleDateView.getText().toString();
                                        createSettlePayment(payableBatch, settleAmount, settleDate);
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
            }
            //  row.addView(payButton);

        }




    protected void createSettlePayment(PayableBatch payableBatch, float settleAmount, String settleDate) {
        BankflowPay bankflowPay = new BankflowPay();
       // DateFormat df = new SimpleDateFormat(Config.DATE_FORMAT);
        bankflowPay.flowDate = settleDate; //df.format(new Date());
        bankflowPay.name = payableBatch.payee;
        bankflowPay.amount = settleAmount;
        bankflowPay.payableBatchId = payableBatch.payableBatchId;
        bankflowPay.payType = BankflowPay.TYPE_SETTLE;

        payableBatch.payDate  = settleDate;

      //  DataSet.getInstance().bankflowPayList.add(bankflowPay);

        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if(msg.obj != null) {
                    // TODO: refresh fragement
                }
            }
        };

        DBAsyncTask dbAsyncTask = new DBAsyncTask(MainApplication.instance.getDB());
        dbAsyncTask.insertSettlePayment(bankflowPay, payableBatch, handler);
    }

    private float showPayableBreakdown(ViewHolder holder, PayableBatchWithBreakdown payableBatchWithBreakdown) {
        List<CashflowPayable> cashflowPayableList = payableBatchWithBreakdown.cashflowPayableList;
        List<BankflowPay> bankflowPayList = payableBatchWithBreakdown.bankflowPayList;
        Context context = holder.breakdownTable.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        int totalRowNum = Math.max(cashflowPayableList.size(), bankflowPayList.size());
        int totalPayableAmount = 0;
        int totalPaidAmount = 0;
        for(int r = 0; r < totalRowNum; r++) {
            TableRow row = new TableRow(context);
            inflater.inflate(R.layout.fragement_payable_breakdown_item, row, true);
            holder.breakdownTable.addView(row);
            if(r < cashflowPayableList.size()) {
                CashflowPayable cashflowPayable = cashflowPayableList.get(r);
                ((TextView) row.findViewById(R.id.cashflow_payable_date)).setText(cashflowPayable.flowDate);
                ((TextView) row.findViewById(R.id.cashflow_payable_type)).setText(cashflowPayable.payableType);
                ((TextView) row.findViewById(R.id.cashflow_payable_amount)).setText(cashflowPayable.amount + "");
                totalPayableAmount += cashflowPayable.amount;
            } else  {
                ((TextView) row.findViewById(R.id.cashflow_payable_date)).setText("");
                ((TextView) row.findViewById(R.id.cashflow_payable_type)).setText("");
                ((TextView) row.findViewById(R.id.cashflow_payable_amount)).setText("");
            }

            if(r < bankflowPayList.size()) {
                BankflowPay bankflowPay = bankflowPayList.get(r);
                ((TextView) row.findViewById(R.id.bankflow_pay_date)).setText(bankflowPay.flowDate);
                ((TextView) row.findViewById(R.id.bankflow_pay_type)).setText(bankflowPay.payType);
                ((TextView) row.findViewById(R.id.bankflow_pay_amount)).setText(bankflowPay.amount + "");
                totalPaidAmount += bankflowPay.amount;
            } else  {
                ((TextView) row.findViewById(R.id.bankflow_pay_date)).setText("");
                ((TextView) row.findViewById(R.id.bankflow_pay_type)).setText("");
                ((TextView) row.findViewById(R.id.bankflow_pay_amount)).setText("");
            }
        }

        return totalPayableAmount - totalPaidAmount;
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
      //  public final TextView viewPayDate;
        public final TextView viewPayee;
        public final TextView viewAmount;
        public final Button settleButton;
        public final TableLayout breakdownTable;
       // public final ConstraintLayout itemRow;

        View itemRow;


        public PayableBatchWithBreakdown mItem;

        public ViewHolder(FragmentPayableBatchItemBinding binding) {
            super(binding.getRoot());
            itemRow = binding.payableBatchItemRow;
           // viewPayDate = binding.;
            viewPayee = binding.payablePayee;
            viewAmount = binding.payableAmount;
            settleButton = binding.payableButtonPay;
           breakdownTable = binding.payableBatchBreakdownTable;

        }

    }
}