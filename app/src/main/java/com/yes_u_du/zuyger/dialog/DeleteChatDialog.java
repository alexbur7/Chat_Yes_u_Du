package com.yes_u_du.zuyger.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.yes_u_du.zuyger.R;
import com.yes_u_du.zuyger.account.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;

public class DeleteChatDialog extends DialogFragment {
    private RadioButton deleteBox;
    //private RadioButton blockBox;
    //private RadioButton favoriteBox;
    private DatabaseReference reference;
    private String receiverUuid;
    private boolean empty;
    //private ValueEventListener deleteMessageListener;
    //private ValueEventListener blockChatListener;
    //private ValueEventListener favoriteChatListener;
    private String firstKey;
    private String secondKey;

    public DeleteChatDialog(String receiverUuid, boolean empty){
        this.receiverUuid = receiverUuid;
        this.empty=empty;
    }
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.delete_chat_dialog,null);
        reference = FirebaseDatabase.getInstance().getReference("chats").child(generateKey());
       // deleteBox = view.findViewById(R.id.check_delete_box);
        //blockBox = view.findViewById(R.id.check_blocklist_box);
       // favoriteBox = view.findViewById(R.id.check_favorite_list_box);

       // if (empty) blockBox.setEnabled(false);

        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        return builder
                .setView(view)
                .setPositiveButton(R.string.ok_pos_button_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      // deleteChat(deleteBox.isChecked());
                      // blockChat(blockBox.isChecked());
                     //  favoriteChat(favoriteBox.isChecked());
                    }
                }).create();
    }

   /* private void deleteChat(boolean check){
        if (check){
            deleteMessageListener=reference.child("message").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snapshot1:snapshot.getChildren()){
                        if (User.getCurrentUser().getUuid().equals(firstKey)) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("firstDelete", "delete");
                            snapshot1.getRef().updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    sendResult(Activity.RESULT_OK);
                                }
                            });
                        }
                        else {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("secondDelete", "delete");
                            snapshot1.getRef().updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    sendResult(Activity.RESULT_OK);
                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
       sendResult(Activity.RESULT_CANCELED);
    }*/

    /*private void blockChat(boolean block){
        if (block) {
            blockChatListener = reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snapshot1:snapshot.getChildren()){
                        if (snapshot1.getKey().equals("firstBlock") && User.getCurrentUser().getUuid().equals(firstKey)){
                            HashMap<String,Object> map = new HashMap<>();
                            map.put("firstBlock","block");
                            reference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    sendResult(Activity.RESULT_OK);
                                }
                            });
                        }
                        else if (snapshot1.getKey().equals("secondBlock") && User.getCurrentUser().getUuid().equals(secondKey)){
                            HashMap<String,Object> map = new HashMap<>();
                            map.put("secondBlock","block");
                            reference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    sendResult(Activity.RESULT_OK);
                                }
                            });
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }
        sendResult(Activity.RESULT_CANCELED);
    }*/

  /*  private void favoriteChat(boolean favorite) {
        if (favorite) {
            favoriteChatListener = reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        if (snapshot1.getKey().equals("firstFavorites") && User.getCurrentUser().getUuid().equals(firstKey)) {
                            HashMap<String,Object> map = new HashMap<>();
                            map.put("firstFavorites","yes");
                            reference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    sendResult(Activity.RESULT_OK);
                                }
                            });
                        } else if (snapshot1.getKey().equals("secondFavorites") && User.getCurrentUser().getUuid().equals(secondKey)) {
                            HashMap<String,Object> map = new HashMap<>();
                            map.put("secondFavorites","yes");
                            reference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    sendResult(Activity.RESULT_OK);
                                }
                            });
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
        sendResult(Activity.RESULT_CANCELED);
    }*/

    private void sendResult(int result) {
        getTargetFragment().onActivityResult(getTargetRequestCode(),result,null);
    }

    @Override
    public void onPause() {
        super.onPause();
        //if (deleteMessageListener !=null) reference.child("message").removeEventListener(deleteMessageListener);
        //if (blockChatListener != null) reference.removeEventListener(blockChatListener);
        //if (favoriteChatListener!=null) reference.removeEventListener(favoriteChatListener);
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
}
