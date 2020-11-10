package com.example.yesudu;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
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
    private Button editMessage;
    private Button deleteMyMessage;
    private Button deleteFromMessage;
    private DatabaseReference reference;
    private String receiverUuid;
    private String firstKey;

    public EditMessageDialog(DatabaseReference reference, String receiverUuid){
        this.reference = reference;
        this.receiverUuid = receiverUuid;
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

        }
        else if (v.getId()==R.id.delete_my_message){
            deleteMessage();
        }
        else if(v.getId()==R.id.delete_all_message){
            deleteForAllMessage();
        }
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

