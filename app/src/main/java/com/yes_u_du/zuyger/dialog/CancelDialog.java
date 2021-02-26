package com.yes_u_du.zuyger.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.yes_u_du.zuyger.R;
import com.yes_u_du.zuyger.account.User;
import com.yes_u_du.zuyger.chat_list.fragment.BlockListFragment;
import com.yes_u_du.zuyger.chat_list.fragment.FavoriteListFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;

public class CancelDialog extends DialogFragment {

    private DatabaseReference reference;
    private ValueEventListener unblockChatListener;
    private ValueEventListener unFavoriteChatListener;
    private String firstKey;
    private String secondKey;
    private String receiverUuid;
    private int type_dialog;
    private TextView textView;

    public CancelDialog(String uuid, int type_dialog){
        receiverUuid=uuid;
        this.type_dialog=type_dialog;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View v= LayoutInflater.from(getActivity()).inflate(R.layout.unblock_dialog,null);
        textView = v.findViewById(R.id.unblock_text);
        if (type_dialog== BlockListFragment.TYPE_DIALOG){
            textView.setText(R.string.unblock_msg_text);
        }
        else if(type_dialog == FavoriteListFragment.TYPE_DIALOG){
            textView.setText(R.string.unfavorite_msg_text);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        reference = FirebaseDatabase.getInstance().getReference("chats").child(generateKey());
        return builder.setNeutralButton(R.string.yes_pos_button_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (type_dialog==BlockListFragment.TYPE_DIALOG) {
                    unblockChat();
                }
                else if(type_dialog ==FavoriteListFragment.TYPE_DIALOG){
                    unfavoriteChat();
                }
                sendResult(Activity.RESULT_OK);
            }
        }).setPositiveButton(R.string.no_neg_button_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setView(v).create();
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
            if (type_dialog==BlockListFragment.TYPE_DIALOG)
                window.setLayout(850, ViewGroup.LayoutParams.WRAP_CONTENT);
            else
                window.setLayout(750, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        else {
            if (type_dialog==BlockListFragment.TYPE_DIALOG)
                window.setLayout(600, ViewGroup.LayoutParams.WRAP_CONTENT);
            else
                window.setLayout(500, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        window.setGravity(Gravity.CENTER);
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

    private void unfavoriteChat() {
        unFavoriteChatListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1:snapshot.getChildren()){
                    if (snapshot1.getKey().equals("firstFavorites") && User.getCurrentUser().getUuid().equals(firstKey)){
                        snapshot1.getRef().setValue("no").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) sendResult(Activity.RESULT_OK);
                                else sendResult(Activity.RESULT_CANCELED);
                            }
                        });
                    }
                    else if (snapshot1.getKey().equals("secondFavorites") && User.getCurrentUser().getUuid().equals(secondKey)){
                        snapshot1.getRef().setValue("no").addOnCompleteListener(new OnCompleteListener<Void>() {
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
        if (unFavoriteChatListener!=null)
            reference.removeEventListener(unFavoriteChatListener);
    }
}
