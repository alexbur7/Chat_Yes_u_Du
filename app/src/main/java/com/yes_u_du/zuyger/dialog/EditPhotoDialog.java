package com.yes_u_du.zuyger.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.yes_u_du.zuyger.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

public class EditPhotoDialog extends DialogFragment {

    private String photo_url;
    private String userId;
    private int imageNumber;
    private CheckBox deletePhoto;

    public EditPhotoDialog(String photo_url, String userId, int imageNumber){
        this.photo_url = photo_url;
        this.userId = userId;
        this.imageNumber = imageNumber;
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

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        if (width>1000) {
            getDialog().getWindow().setLayout(700, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        else getDialog().getWindow().setLayout(400, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);

    }

    private void deleteImage(String photo_url, String userId){
        StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(photo_url);
        photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                HashMap<String,Object> hashMap = new HashMap<>();
                hashMap.put("photo_url"+ imageNumber,"default");
                FirebaseDatabase.getInstance().getReference("users").child(userId).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        sendResult(RESULT_OK, imageNumber);
                    }
                });
            }
        });
    }

    private void sendResult(int result,int i) {
        Intent intent=new Intent();
        intent.putExtra(String.valueOf(getTargetRequestCode()),i);
        getTargetFragment().onActivityResult(getTargetRequestCode(),result,intent);
    }


}
