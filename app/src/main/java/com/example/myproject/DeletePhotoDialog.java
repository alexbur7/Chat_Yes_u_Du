package com.example.myproject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class DeletePhotoDialog extends DialogFragment {
    private String photo_url;
    private String userId;
    private int i;

    public DeletePhotoDialog(String photo_url, String userId, int i){
        this.photo_url = photo_url;
        this.userId = userId;
        this.i =i;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.delete_photo_dialog,null);
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        return builder
                .setView(view)
                .setPositiveButton(R.string.ok_pos_button_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteImage(photo_url,userId);
                    }
                }).create();
    }

    private void deleteImage(String photo_url, String userId){
        StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(photo_url);
        photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                HashMap<String,Object> hashMap = new HashMap<>();
                hashMap.put("photo_url"+i,"default");
                FirebaseDatabase.getInstance().getReference("users").child(userId).updateChildren(hashMap);
                Toast.makeText(getContext(),getResources().getString(R.string.photo_delete),Toast.LENGTH_SHORT).show();
            }
        });
    }
}
