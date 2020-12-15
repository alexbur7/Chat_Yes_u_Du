package com.example.yesudu.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.yesudu.R;

import java.util.ArrayList;

public class DialogAdapter extends RecyclerView.Adapter<DialogAdapter.DialogHolder> {

    private Dismissable dismissable;
    private ArrayList<Integer> elements;
    private Context context;

    public DialogAdapter(ArrayList<Integer> elements,Dismissable dismissable,Context context){
        this.elements=elements;
        this.dismissable=dismissable;
        this.context=context;
    }


    @NonNull
    @Override
    public DialogHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DialogHolder(LayoutInflater.from(context).inflate(R.layout.dialog_options_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull DialogHolder holder, int position) {
        holder.onBind(elements.get(position));
    }


    @Override
    public int getItemCount() {
        return elements.size();
    }

    public class DialogHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView textView;
        private int complainCode;

        public DialogHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            textView= itemView.findViewById(R.id.text_options_item);
        }

        public void onBind(int t){
            this.complainCode=t;
            textView.setText(dismissable.chooseOption(complainCode));
        }

        @Override
        public void onClick(View v) {
            dismissable.onDismiss(dismissable.chooseOption(complainCode));
            Toast.makeText(context,context.getString(R.string.complain_completed),Toast.LENGTH_SHORT).show();
        }
    }
}
