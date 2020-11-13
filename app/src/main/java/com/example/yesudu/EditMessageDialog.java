package com.example.yesudu;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.google.firebase.database.DatabaseReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


public class EditMessageDialog extends DialogFragment implements View.OnClickListener{
    public static final String KEY_TO_MSG_TEXT = "key_to_msg";
    public static final String KEY_TO_REF = "reference";
    public static final int KEY_MESSAGE_DELETE_MY = 3;
    public static final int KEY_MESSAGE_DELETE_EVERYONE = 4;
    private LinearLayout editMessage;
    private LinearLayout deleteMyMessage;
    private LinearLayout deleteFromMessage;
    private TextView editMessageText;
    private DatabaseReference reference;
    private String receiverUuid;
    private String messageText;

    public EditMessageDialog(DatabaseReference reference, String receiverUuid, String messageText){
        this.reference = reference;
        this.receiverUuid = receiverUuid;
        this.messageText=messageText;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.edit_message_dialog,null);
        editMessage = v.findViewById(R.id.edit_message_button);
        editMessage.setOnClickListener(this);
        deleteMyMessage = v.findViewById(R.id.delete_my_message);
        deleteMyMessage.setOnClickListener(this);
        deleteFromMessage = v.findViewById(R.id.delete_all_message);
        deleteFromMessage.setOnClickListener(this);
        editMessageText = v.findViewById(R.id.edit_message_text);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder
                .setView(v)
                .create();
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        //window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if(editMessageText.getText().toString().equals("Edit message"))
        getDialog().getWindow().setLayout(905, ViewGroup.LayoutParams.WRAP_CONTENT);
        else
            getDialog().getWindow().setLayout(820, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);

    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.edit_message_button){
            editMessage(messageText);
        }
        else if (v.getId()==R.id.delete_my_message){
            AcceptDialog acceptDialog = new AcceptDialog(reference, null, KEY_MESSAGE_DELETE_MY,receiverUuid);
            acceptDialog.show(getFragmentManager(),null);
            this.dismiss();
        }
        else if(v.getId()==R.id.delete_all_message){
            AcceptDialog acceptDialog = new AcceptDialog(reference, null, KEY_MESSAGE_DELETE_EVERYONE,receiverUuid);
            acceptDialog.show(getFragmentManager(),null);
            this.dismiss();
        }
    }

    private void editMessage(String msgText) {
        Intent intent = new Intent();
        intent.putExtra(KEY_TO_MSG_TEXT,msgText);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK,intent);
        this.dismiss();
    }


}

