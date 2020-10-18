package com.example.myproject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

public class EditDialog extends DialogFragment {
    private EditText nameText;
    private EditText surnameText;
    private EditText countryText;
    private EditText cityText;
    private EditText regionText;
    private Spinner sexSpinner;
    private EditText ageText;
    private Button deletePhoto;
    private FirebaseStorage storage;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.edit_dialog,null);
        storage = FirebaseStorage.getInstance();
        nameText = view.findViewById(R.id.name);
        nameText.setText(User.getCurrentUser().getName());
        surnameText = view.findViewById(R.id.surname);
        surnameText.setText(User.getCurrentUser().getSurname());
        countryText = view.findViewById(R.id.country);
        countryText.setText(User.getCurrentUser().getCountry());
        regionText = view.findViewById(R.id.region);
        regionText.setText(User.getCurrentUser().getRegion());
        cityText = view.findViewById(R.id.city);
        cityText.setText(User.getCurrentUser().getCity());
        sexSpinner = view.findViewById(R.id.spinner_sex_edit);
        if (User.getCurrentUser().getSex().equals(getResources().getStringArray(R.array.sex_for_spinner)[0]))
            sexSpinner.setSelection(0);
        else sexSpinner.setSelection(1);
        ageText = view.findViewById(R.id.age);
        ageText.setText(User.getCurrentUser().getAge());
        deletePhoto = view.findViewById(R.id.delete_image_button);
        deletePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!User.getCurrentUser().getPhoto_url().equals("default")) {
                    StorageReference photoRef = storage.getReferenceFromUrl(User.getCurrentUser().getPhoto_url());
                    photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            HashMap<String,Object> hashMap = new HashMap<>();
                            hashMap.put("photo_url","default");
                            User.getCurrentUser().setPhoto_url("default");
                            FirebaseDatabase.getInstance().getReference("users").child(User.getCurrentUser().getUuid()).updateChildren(hashMap);
                            Toast.makeText(getContext(),getResources().getString(R.string.photo_delete),Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(),getResources().getString(R.string.photo_cant_delete),Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {
                    Toast.makeText(getContext(),getResources().getString(R.string.have_not_photo),Toast.LENGTH_SHORT).show();
                }
            }
        });

        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        return builder
                .setTitle(getResources().getString(R.string.dialog_title_edit))
                .setView(view)
                .setPositiveButton(R.string.ok_pos_button_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateUser();
                    }
                }).create();
    }

    private void updateUser(){
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("name",nameText.getText().toString());
        User.getCurrentUser().setName(nameText.getText().toString());
        hashMap.put("surname",surnameText.getText().toString());
        User.getCurrentUser().setSurname(surnameText.getText().toString());
        hashMap.put("city",cityText.getText().toString());
        User.getCurrentUser().setCity(cityText.getText().toString());
        hashMap.put("country",countryText.getText().toString());
        User.getCurrentUser().setCountry(countryText.getText().toString());
        hashMap.put("region",regionText.getText().toString());
        User.getCurrentUser().setRegion(regionText.getText().toString());
        hashMap.put("sex",sexSpinner.getSelectedItem().toString());
        User.getCurrentUser().setSex(sexSpinner.getSelectedItem().toString());
        hashMap.put("age",ageText.getText().toString());
        User.getCurrentUser().setAge(ageText.getText().toString());
        FirebaseDatabase.getInstance().getReference("users").child(User.getCurrentUser().getUuid()).updateChildren(hashMap);
    }
}
