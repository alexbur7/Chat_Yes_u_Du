package com.yes_u_du.zuyger.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.yes_u_du.zuyger.R;

public class GoToAdminDialog extends DialogFragment {

    public static final int CHAT_BTN_CODE=-11;
    public static final int COMPLAIN_BTN_CODE=-22;
    public static final String BTN_CODE="btn_code";


    private Button chatBtn;
    private Button complainBtn;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View v= LayoutInflater.from(getActivity()).inflate(R.layout.go_to_admin_dialog,null);
        chatBtn=v.findViewById(R.id.go_chat_btn);
        complainBtn=v.findViewById(R.id.go_complain_btn);
        setupListeners();
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        return builder.setView(v)
                .create();
    }

    protected void setupListeners() {
        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendResult(Activity.RESULT_OK,CHAT_BTN_CODE);
                dismiss();
            }
        });
        complainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendResult(Activity.RESULT_OK,COMPLAIN_BTN_CODE);
                dismiss();
            }
        });
    }

    private void sendResult(int result, int btnCode) {
        Intent intent=new Intent();
        intent.putExtra(BTN_CODE,btnCode);
        getTargetFragment().onActivityResult(getTargetRequestCode(),result,intent);
    }
}
