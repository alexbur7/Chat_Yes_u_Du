package com.example.myproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class FilterDialog extends DialogFragment {

    private EditText nameEditText;

    public static final String KEY_TO_NAME_FILTER="name_filter";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        View view= LayoutInflater.from(getActivity()).inflate(R.layout.filter_dialog,null);

        nameEditText=view.findViewById(R.id.name_filter_edit_text);

        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        return builder
                .setTitle(getResources().getString(R.string.dialog_title))
                .setView(view)
                .setPositiveButton(R.string.search_pos_button_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(nameEditText.getText().toString(),Activity.RESULT_OK);
                    }
                }).create();
    }

    private void sendResult(String name,int result){
        Intent intent=new Intent();
        intent.putExtra(KEY_TO_NAME_FILTER,name);
        getTargetFragment().onActivityResult(getTargetRequestCode(),result,intent);
    }
}

