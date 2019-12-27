package com.maidenexpress;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionVH> {

    Context context;
    List<Transaction> transactionList;

    public TransactionAdapter (Context context, List<Transaction> transactionList){
        this.context = context;
        this.transactionList = transactionList;
    }


    OnItemClickListener listener;
    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener{
        void OnClick(View v, int position);
    }
    @NonNull
    @Override
    public TransactionVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater =LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.transaction_view,parent,false);
        return new TransactionVH(view);
    }

    private String convertLongToDate(long input){
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(input);
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        return date;
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionVH holder, int position) {
        Transaction transaction = transactionList.get(position);
        holder.tvTransactionDate.setText(convertLongToDate(transaction.getDateCreated()));
        holder.tvTransactionReason.setText(transaction.getReason());
        holder.tvTransactionAmount.setText(transaction.getAmount());
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    class TransactionVH extends RecyclerView.ViewHolder{
        TextView tvTransactionDate, tvTransactionReason, tvTransactionAmount;

        public TransactionVH(@NonNull View itemView) {
            super(itemView);
            tvTransactionDate = itemView.findViewById(R.id.tvTransactionDate);
            tvTransactionReason =itemView.findViewById(R.id.tvTransactionReason);
            tvTransactionAmount =itemView.findViewById(R.id.tvTransactionAmount);
        }
    }
}
