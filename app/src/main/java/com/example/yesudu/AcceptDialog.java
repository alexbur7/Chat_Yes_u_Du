package com.example.yesudu;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class AcceptDialog extends DialogFragment {
    private DatabaseReference reference;
    private ValueEventListener listener;
    private TextView acceptText;
    private String firstKey;
    private String receiverUuid;
    int key;
    int user_type;

    public AcceptDialog(DatabaseReference reference, ValueEventListener listener, int key,String receiverUuid,int user_type){
        this.reference = reference;
        this.listener= listener;
        this.key=key;
        this.receiverUuid= receiverUuid;
        this.user_type=user_type;
    }

    public AcceptDialog(DatabaseReference reference, ValueEventListener listener, int key,String receiverUuid){
        this.reference = reference;
        this.listener= listener;
        this.key=key;
        this.receiverUuid= receiverUuid;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.accept_dialog,null);
        acceptText = v.findViewById(R.id.accept_text);
        if (key==MyAccountFragment.KEY_ACCEPT){
            acceptText.setText(R.string.accept_string);
        }
        else {
            acceptText.setText(R.string.accept_msg_string);
        }
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        return builder
                .setView(v)
                .setPositiveButton(R.string.yes_pos_button_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (key==MyAccountFragment.KEY_ACCEPT)
                        deleteUser();
                        else if(key==EditMessageDialog.KEY_MESSAGE_DELETE_MY){
                            deleteMessage();
                        }
                        else if(key==EditMessageDialog.KEY_MESSAGE_DELETE_EVERYONE){
                            deleteForAllMessage();
                        }
                    }
                })
                .setNegativeButton(R.string.no_neg_button_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create();
    }

    private void deleteUser() {
        reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeEventListener(listener);
        Log.e("FIREBAS EAUTH", String.valueOf(FirebaseAuth.getInstance().getCurrentUser()));
        FirebaseAuth.getInstance().getCurrentUser().delete();
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(getActivity(), "Completed", Toast.LENGTH_SHORT);
        deleteImage(User.getCurrentUser(),0);
        deleteImage(User.getCurrentUser(),1);
        deleteImage(User.getCurrentUser(),2);
        deleteImage(User.getCurrentUser(),3);
        FirebaseDatabase.getInstance().getReference("users").child(User.getCurrentUser().getUuid()).removeValue();
        User.setCurrentUser(null,null,null);
        startActivity(new Intent(getActivity(), LogActivity.class));
        getActivity().finish();
    }

    private void deleteImage(User user,int i) {
        if (!user.getPhoto_url().equals("default") && i==0) {
            StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(user.getPhoto_url());
            photoRef.delete();
        }
        else if (!user.getPhoto_url1().equals("default") && i==1){
            StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(user.getPhoto_url1());
            photoRef.delete();
        }
        else if (!user.getPhoto_url2().equals("default") && i==2){
            StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(user.getPhoto_url2());
            photoRef.delete();
        }
        else if (!user.getPhoto_url3().equals("default") && i==3){
            StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(user.getPhoto_url3());
            photoRef.delete();
        }
    }

    private void deleteMessage(){
        generateKey();
        if (user_type==EditMessageDialog.TYPE_OF_USER_USUAL) {
            if (User.getCurrentUser().getUuid().equals(firstKey)) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("firstDelete", "delete");
                reference.updateChildren(hashMap);
            } else {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("secondDelete", "delete");
                reference.updateChildren(hashMap);
            }
        }
        else {
            if (firstKey.equals("admin")) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("firstDelete", "delete");
                reference.updateChildren(hashMap);
            } else {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("secondDelete", "delete");
                reference.updateChildren(hashMap);
            }
        }
        this.dismiss();
    }

    private void deleteForAllMessage(){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("firstDelete", "delete");
        hashMap.put("secondDelete", "delete");
        reference.updateChildren(hashMap);
        this.dismiss();
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
