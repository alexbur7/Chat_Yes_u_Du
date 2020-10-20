package com.example.myproject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

public class EditPhotoDialog extends DialogFragment {

    private String photo_url;
    private String userId;
    private int i;
    private RadioButton deletePhoto;

    public EditPhotoDialog(String photo_url, String userId, int i){
        this.photo_url = photo_url;
        this.userId = userId;
        this.i =i;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.edit_photo_dialog,null);
        deletePhoto = view.findViewById(R.id.check_delete_photo);
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        return builder
                .setView(view)
                .setPositiveButton(R.string.ok_pos_button_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (deletePhoto.isChecked())
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
            }
        });
    }


}
