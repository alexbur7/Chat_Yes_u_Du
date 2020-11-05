package com.example.yesudu;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;

public class UnblockDialog extends DialogFragment {

    private DatabaseReference reference;
    private ValueEventListener unblockChatListener;
    private String firstKey;
    private String secondKey;
    private String receiverUuid;

    public UnblockDialog(String uuid){receiverUuid=uuid;}
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View v= LayoutInflater.from(getActivity()).inflate(R.layout.unblock_dialog,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        reference = FirebaseDatabase.getInstance().getReference("chats").child(generateKey());
        return builder.setNeutralButton(R.string.ok_pos_button_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                unblockChat();
                sendResult(Activity.RESULT_OK);
            }
        }).setView(v).create();
    }

    private void sendResult(int result) {
        getTargetFragment().onActivityResult(getTargetRequestCode(),result,null);
    }

    private String generateKey(){
        ArrayList<String> templist=new ArrayList<>();
        templist.add(User.getCurrentUser().getUuid());
        templist.add(receiverUuid);
        Collections.sort(templist);
        firstKey =templist.get(0);
        secondKey =templist.get(1);
        return templist.get(0)+templist.get(1);
    }

    private void unblockChat(){
            unblockChatListener = reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snapshot1:snapshot.getChildren()){
                        if (snapshot1.getKey().equals("firstBlock") && User.getCurrentUser().getUuid().equals(firstKey)){
                            snapshot1.getRef().setValue("no block").addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) sendResult(Activity.RESULT_OK);
                                    else sendResult(Activity.RESULT_CANCELED);
                                }
                            });
                        }
                        else if (snapshot1.getKey().equals("secondBlock") && User.getCurrentUser().getUuid().equals(secondKey)){
                            snapshot1.getRef().setValue("no block").addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) sendResult(Activity.RESULT_OK);
                                    else sendResult(Activity.RESULT_CANCELED);
                                }
                            });
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (unblockChatListener!=null)
        reference.removeEventListener(unblockChatListener);
    }
}
