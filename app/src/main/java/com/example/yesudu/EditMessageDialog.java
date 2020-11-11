package com.example.yesudu;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

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
    private Button editMessage;
    private Button deleteMyMessage;
    private Button deleteFromMessage;
    private DatabaseReference reference;
    private String receiverUuid;
    private String messageText;
    private String firstKey;

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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder
                .setView(v)
                .create();
    }

    @Override
    public void onClick(View v) {
        generateKey();
        if (v.getId()==R.id.edit_message_button){
            editMessage(messageText);
        }
        else if (v.getId()==R.id.delete_my_message){
            deleteMessage();
        }
        else if(v.getId()==R.id.delete_all_message){
            deleteForAllMessage();
        }
    }

    private void editMessage(String msgText) {
        Intent intent = new Intent();
        intent.putExtra(KEY_TO_MSG_TEXT,msgText);
        Log.e("KEY",reference.getKey());
        Log.e("ROOT",reference.getRoot().toString());
        Log.e("PARENT",reference.getParent().toString());
        Log.e("REF",reference.getRef().toString());
        intent.putExtra(KEY_TO_REF,reference.getKey());
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK,intent);
        this.dismiss();
    }

    private void deleteMessage(){
        if (User.getCurrentUser().getUuid().equals(firstKey)) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("firstDelete", "delete");
            reference.updateChildren(hashMap);
        }
        else {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("secondDelete", "delete");
            reference.updateChildren(hashMap);
        }
    }

    private void deleteForAllMessage(){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("firstDelete", "delete");
        hashMap.put("secondDelete", "delete");
        reference.updateChildren(hashMap);
    }

    private String generateKey(){
        ArrayList<String> templist=new ArrayList<>();
        templist.add(User.getCurrentUser().getUuid());
        templist.add(receiverUuid);
        Collections.sort(templist);
        firstKey =templist.get(0);
        return templist.get(0)+templist.get(1);
    }
}

