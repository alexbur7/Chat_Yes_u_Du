package com.yes_u_du.zuyger.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import com.yes_u_du.zuyger.R;

import java.util.ArrayList;

import static com.yes_u_du.zuyger.chat.ChatFragment.COMPLAIN_REQUEST;

public class DialogAdapter extends RecyclerView.Adapter<DialogAdapter.DialogHolder> {

    private Dismissable dismissable;
    private ArrayList<Integer> elements;
    private Context context;
    private FragmentManager manager;
    private Fragment fragment;
    private ComplainDialog dialog;

    public DialogAdapter(ArrayList<Integer> elements, Dismissable dismissable, Context context, FragmentManager manager, Fragment fragment, ComplainDialog dialog){
        this.elements=elements;
        this.dismissable=dismissable;
        this.context=context;
        this.manager = manager;
        this.fragment = fragment;
        this.dialog = dialog;
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

        public void onBind(int complainCode){
            this.complainCode=complainCode;
            textView.setText(dismissable.chooseOption(this.complainCode));
        }

        @Override
        public void onClick(View v) {
            if (complainCode==ComplainDialog.FAKE_BTN_CODE){
                dialog.dismiss();
                ComplainDialog fakeDataDialog = new ComplainDialog(ComplainDialog.FAKE_COMPLAIN_CODE);
                fakeDataDialog.setTargetFragment(fragment,COMPLAIN_REQUEST);
                fakeDataDialog.show(manager,null);
            }
            else if (complainCode==ComplainDialog.PHOTOS_BTN_CODE){
                dialog.dismiss();
                ComplainDialog fakeLocationDialog = new ComplainDialog(ComplainDialog.FAKE_PHOTO_COMPLAIN_CODE);
                fakeLocationDialog.setTargetFragment(fragment,COMPLAIN_REQUEST);
                fakeLocationDialog.show(manager,null);
            }
            else {
                dismissable.onDismiss(dismissable.chooseOption(complainCode));
               // Toast.makeText(context, context.getString(R.string.complain_completed), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
