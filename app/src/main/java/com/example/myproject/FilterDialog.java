package com.example.myproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class FilterDialog extends DialogFragment {

    private EditText nameEditText;
    private EditText cityEditText;
    private CheckBox maleCheckBox;
    private CheckBox femaleCheckBox;
    private CheckBox onlineCheckBox;
    private Spinner ageSpinner;
    //private CheckBox photoCheckBox;

    public static final String KEY_TO_NAME_FILTER="name_filter";
    public static final String KEY_TO_SEX_FILTER="sex_filter";
    public static final String KEY_TO_CITY_FILTER="city_filter";
    public static final String KEY_TO_AGE_FILTER="age_filter";
    public static final String KEY_TO_ONLINE_FILTER="online_filter";
    public static final String KEY_TO_PHOTO_FILTER="photo_filter";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        View view= LayoutInflater.from(getActivity()).inflate(R.layout.filter_dialog,null);

        nameEditText=view.findViewById(R.id.name_filter_edit_text);
        cityEditText=view.findViewById(R.id.city_filter_edit_text);
        maleCheckBox=view.findViewById(R.id.genderMaleCheckBox);
        femaleCheckBox=view.findViewById(R.id.genderFemaleCheckBox);
        //photoCheckBox=view.findViewById(R.id.photoCheckBox);
        ageSpinner=view.findViewById(R.id.spinner_age_filter);
        onlineCheckBox=view.findViewById(R.id.onlineCheckBox);
        ageSpinner.setSelection(5);

        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        return builder
                .setTitle(getResources().getString(R.string.dialog_title))
                .setView(view)
                .setPositiveButton(R.string.search_pos_button_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if ((femaleCheckBox.isChecked() && maleCheckBox.isChecked()) || (!femaleCheckBox.isChecked() && !maleCheckBox.isChecked()) )
                        sendResult(nameEditText.getText().toString(),"",(ageSpinner.getSelectedItem().equals(getResources().getStringArray(R.array.age_for_spinner)[5]) ? null :ageSpinner.getSelectedItem().toString()),cityEditText.getText().toString(),(onlineCheckBox.isChecked() ? getResources().getString(R.string.label_online):""),(true ? "default" : ""),Activity.RESULT_OK);

                        else  sendResult(nameEditText.getText().toString(),(maleCheckBox.isChecked() ? getResources().getString(R.string.label_male) : getResources().getString(R.string.label_female)),
                                (ageSpinner.getSelectedItem().equals(getResources().getStringArray(R.array.age_for_spinner)[5]) ? null :ageSpinner.getSelectedItem().toString()),cityEditText.getText().toString(),(onlineCheckBox.isChecked() ? getResources().getString(R.string.label_online):""),(true ? "default" : ""),Activity.RESULT_OK);
                    }
                }).create();
    }

    private void sendResult(String name,String sex,String age,String city,String online,String photo,int result){
        Intent intent=new Intent();
        intent.putExtra(KEY_TO_NAME_FILTER,name);
        intent.putExtra(KEY_TO_SEX_FILTER,sex);
        intent.putExtra(KEY_TO_AGE_FILTER,age);
        intent.putExtra(KEY_TO_CITY_FILTER,city);
        intent.putExtra(KEY_TO_ONLINE_FILTER,online);
        intent.putExtra(KEY_TO_PHOTO_FILTER,photo);
        getTargetFragment().onActivityResult(getTargetRequestCode(),result,intent);
    }
}

