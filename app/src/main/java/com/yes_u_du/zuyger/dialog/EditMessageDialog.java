package com.yes_u_du.zuyger.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.yes_u_du.zuyger.R;
import com.google.firebase.database.DatabaseReference;


public class EditMessageDialog extends DialogFragment implements View.OnClickListener{
    public static final String KEY_TO_MSG_TEXT = "key_to_msg";
    public static final String KEY_TO_REF = "reference";
    public static final int KEY_MESSAGE_DELETE_MY = 3;
    public static final int KEY_MESSAGE_DELETE_EVERYONE = 4;
    private LinearLayout editMessage;
    private LinearLayout deleteMyMessage;
    private LinearLayout deleteFromMessage;
    private TextView deleteMessageText;
    private DatabaseReference reference;
    private String receiverUuid;
    private String messageText;
    // -1 - ЧУЖОЕ
    // 1 - НАШЕ
    private int TYPE_OF_MSG;
    private int TYPE_OF_USER;
    public static final int TYPE_OF_MSG_MY=1;
    public static final int TYPE_OF_MSG_NOT_MY=-1;
    public static final int TYPE_OF_USER_USUAL=-2;
    public static final int TYPE_OF_USER_ADMIN=2;

    public EditMessageDialog(DatabaseReference reference, String receiverUuid, String messageText,int type_msg,int user_type){
        this.reference = reference;
        this.receiverUuid = receiverUuid;
        this.messageText=messageText;
        this.TYPE_OF_MSG=type_msg;
        this.TYPE_OF_USER=user_type;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View v;
        if (TYPE_OF_MSG==TYPE_OF_MSG_MY) {
            v = LayoutInflater.from(getActivity()).inflate(R.layout.edit_message_dialog, null);
            editMessage = v.findViewById(R.id.edit_message_button);
            editMessage.setOnClickListener(this);
            deleteFromMessage = v.findViewById(R.id.delete_all_message);
            deleteFromMessage.setOnClickListener(this);

        }
        else  v = LayoutInflater.from(getActivity()).inflate(R.layout.delete_not_your_msg_dialog, null);
        deleteMessageText = v.findViewById(R.id.delete_my_message_title);
        deleteMyMessage = v.findViewById(R.id.delete_my_message);
        deleteMyMessage.setOnClickListener(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder
                .setView(v)
                .create();
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        Log.e("width", String.valueOf(width));
        if (width>1000) {
            if (deleteMessageText.getText().toString().equals("Delete for yourself"))
                window.setLayout(905, ViewGroup.LayoutParams.WRAP_CONTENT);
            else
                window.setLayout(820, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        else {
            if (deleteMessageText.getText().toString().equals("Delete for yourself"))
                window.setLayout(605, ViewGroup.LayoutParams.WRAP_CONTENT);
            else
                window.setLayout(520, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        window.setGravity(Gravity.CENTER);
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.edit_message_button){
            editMessage(messageText);
        }
        else if (v.getId()==R.id.delete_my_message){
            AcceptDialog acceptDialog = new AcceptDialog(reference, null, KEY_MESSAGE_DELETE_MY,receiverUuid,TYPE_OF_USER);
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
        intent.putExtra(KEY_TO_REF,reference.getKey());
        intent.putExtra(KEY_TO_MSG_TEXT,msgText);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK,intent);
        this.dismiss();
    }
}

