package com.example.myproject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class DeleteChatDialog extends DialogFragment {
    private CheckBox deleteBox;
    private DatabaseReference reference;
    private String receiverUuid;
    private ValueEventListener deleteMessage;
    private String firstDelete, secondDelete;

    public DeleteChatDialog(String receiverUuid){
        this.receiverUuid = receiverUuid;
    }
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.delete_chat_dialog,null);
        reference = FirebaseDatabase.getInstance().getReference("message").child(generateKey());
        deleteBox = view.findViewById(R.id.check_delete_box);

        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        return builder
                .setTitle(getResources().getString(R.string.dialog_title_delete))
                .setView(view)
                .setPositiveButton(R.string.ok_pos_button_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       deleteChat(deleteBox.isChecked());
                    }
                }).create();
    }

    private void deleteChat(boolean check){
        if (check){
            deleteMessage=reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snapshot1:snapshot.getChildren()){
                        if (User.getCurrentUser().getUuid().equals(firstDelete)) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("firstDelete", "delete");
                            snapshot1.getRef().updateChildren(hashMap);
                        }
                        else {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("secondDelete", "delete");
                            snapshot1.getRef().updateChildren(hashMap);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        reference.removeEventListener(deleteMessage);
    }

    private String generateKey(){
        ArrayList<String> templist=new ArrayList<>();
        templist.add(User.getCurrentUser().getUuid());
        templist.add(receiverUuid);
        Collections.sort(templist);
        firstDelete=templist.get(0);
        secondDelete = templist.get(1);
        return templist.get(0)+templist.get(1);
    }
}
