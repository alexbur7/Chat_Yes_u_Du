package com.yes_u_du.zuyger.account;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yes_u_du.zuyger.R;

import java.util.ArrayList;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.TextHolder> {

    private Context context;
    private ArrayList<String> values;
    private ArrayList<String> keys;

    public AccountAdapter(Context context,ArrayList<String> values,ArrayList<String> keys) {
        this.context = context;
        this.values=values;
        this.keys=keys;
    }


    @NonNull
    @Override
    public TextHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TextHolder(LayoutInflater.from(context).inflate(R.layout.item_account_text,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull TextHolder holder, int position) {
        holder.textView_label.setText(keys.get(position));
        holder.textView.setText(values.get(position));
    }

    @Override
    public int getItemCount() {
        return keys.size();
    }

    public class TextHolder extends RecyclerView.ViewHolder{

        private TextView textView;
        private TextView textView_label;

        public TextHolder(@NonNull View itemView) {
            super(itemView);
            textView=itemView.findViewById(R.id.textView);
            textView_label=itemView.findViewById(R.id.text_label);
        }
    }
}
