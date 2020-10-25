package com.example.myproject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;

public class EditDialog extends DialogFragment {
    private EditText nameText;
    private EditText surnameText;
    private Spinner countrySpinner;
    private EditText cityText;
    private Spinner regionSpinner;
    private EditText ageText;
    private EditText aboutText;
    private Spinner sexSpinner;

    private ArrayAdapter<CharSequence> countryAdapter;
    private ArrayAdapter<CharSequence> regionAdapter;

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
        cityText = view.findViewById(R.id.city);
        cityText.setText(User.getCurrentUser().getCity());
        aboutText=view.findViewById(R.id.about_edit_text);
        aboutText.setText(User.getCurrentUser().getAbout());
        sexSpinner = view.findViewById(R.id.spinner_sex_edit);
        countrySpinner = view.findViewById(R.id.country);
        regionSpinner = view.findViewById(R.id.region);
        Log.e("COUNTRY",User.getCurrentUser().getCountry());
        Log.e("REGION",User.getCurrentUser().getRegion());
        regionAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.region_filter_rus, android.R.layout.simple_spinner_item);
        regionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        regionSpinner.setAdapter(regionAdapter);
        countryAdapter =ArrayAdapter.createFromResource(getActivity(), R.array.country_filter, android.R.layout.simple_spinner_item);
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(countryAdapter);
        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: {
                        regionAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.region_filter_rus, android.R.layout.simple_spinner_item);
                        regionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        regionSpinner.setAdapter(regionAdapter);
                    }
                    break;
                    case 1: {
                        regionAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.region_filter_arm, android.R.layout.simple_spinner_item);
                        regionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        regionSpinner.setAdapter(regionAdapter);
                    }
                    break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        countrySpinner.setSelection(Integer.parseInt(User.getCurrentUser().getCountry()));
        regionSpinner.setSelection(Integer.parseInt(User.getCurrentUser().getRegion()));

        if (User.getCurrentUser().getSex().equals(getResources().getStringArray(R.array.sex_for_spinner)[0]))
            sexSpinner.setSelection(0);
        else sexSpinner.setSelection(1);
        ageText = view.findViewById(R.id.age);
        ageText.setText(User.getCurrentUser().getAge());


        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        return builder
                .setTitle(getResources().getString(R.string.dialog_title_edit))
                .setView(view)
                .setPositiveButton(R.string.ok_pos_button_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateUser();
                    }
                })
                .create();
    }

    private void updateUser(){
        if (nameText.getText().toString().isEmpty() || countrySpinner.getSelectedItem().toString().isEmpty() || cityText.getText().toString().isEmpty()
                || Integer.parseInt(ageText.getText().toString())<0){
            Toast.makeText(getActivity(),R.string.reject_update,Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("name", nameText.getText().toString());
            User.getCurrentUser().setName(nameText.getText().toString());
            hashMap.put("surname", surnameText.getText().toString());
            User.getCurrentUser().setSurname(surnameText.getText().toString());
            hashMap.put("city", cityText.getText().toString());
            User.getCurrentUser().setCity(cityText.getText().toString());
            hashMap.put("country",  String.valueOf(countrySpinner.getSelectedItemPosition()));
            User.getCurrentUser().setCountry(String.valueOf(countrySpinner.getSelectedItemPosition()));
            hashMap.put("region", String.valueOf(regionSpinner.getSelectedItemPosition()));
            User.getCurrentUser().setRegion(String.valueOf(regionSpinner.getSelectedItemPosition()));
            hashMap.put("sex", sexSpinner.getSelectedItem().toString());
            User.getCurrentUser().setSex(sexSpinner.getSelectedItem().toString());
            hashMap.put("age", ageText.getText().toString());
            User.getCurrentUser().setAge(ageText.getText().toString());
            hashMap.put("about",aboutText.getText().toString());
            User.getCurrentUser().setAbout(aboutText.getText().toString());
            FirebaseDatabase.getInstance().getReference("users").child(User.getCurrentUser().getUuid()).updateChildren(hashMap);
        }
    }
}
