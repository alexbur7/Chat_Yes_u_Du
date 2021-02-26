package com.yes_u_du.zuyger.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.yes_u_du.zuyger.R;

public class FilterDialog extends DialogFragment {

    private EditText nameEditText;
    private EditText cityEditText;
    private CheckBox maleCheckBox;
    private CheckBox femaleCheckBox;
    private CheckBox onlineCheckBox;
    private Spinner ageSpinner;
    private Spinner regionSpinner;
    private Spinner countrySpinner;

    private ArrayAdapter<CharSequence> countryAdapter;
    private ArrayAdapter<CharSequence> regionAdapter;

    public static final String KEY_TO_NAME_FILTER="name_filter";
    public static final String KEY_TO_SEX_FILTER="sex_filter";
    public static final String KEY_TO_CITY_FILTER="city_filter";
    public static final String KEY_TO_AGE_FILTER="age_filter";
    public static final String KEY_TO_ONLINE_FILTER="online_filter";
    public static final String KEY_TO_PHOTO_FILTER="photo_filter";
    public static final String KEY_TO_COUNTRY_FILTER="country_filter";
    public static final String KEY_TO_REGION_FILTER="region_filter";


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        View view= LayoutInflater.from(getActivity()).inflate(R.layout.filter_dialog,null);

        nameEditText=view.findViewById(R.id.name_filter_edit_text);
        cityEditText=view.findViewById(R.id.city_filter_edit_text);
        maleCheckBox=view.findViewById(R.id.genderMaleCheckBox);
        femaleCheckBox=view.findViewById(R.id.genderFemaleCheckBox);
        regionSpinner=view.findViewById(R.id.spinner_region_filter);
        countrySpinner=view.findViewById(R.id.spinner_country_filter);
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
                    case 1: {
                        regionAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.region_filter_rus, android.R.layout.simple_spinner_item);
                        regionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        regionSpinner.setAdapter(regionAdapter);
                    }
                    break;
                    case 2: {
                        regionAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.region_filter_armenia, android.R.layout.simple_spinner_item);
                        regionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        regionSpinner.setAdapter(regionAdapter);
                    }
                    break;
                    case 4: {
                        regionAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.region_filter_usa, android.R.layout.simple_spinner_item);
                        regionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        regionSpinner.setAdapter(regionAdapter);
                    }
                    break;
                    default:{
                        regionAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.no_region_filter, android.R.layout.simple_spinner_item);
                        regionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        regionSpinner.setAdapter(regionAdapter);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //photoCheckBox=view.findViewById(R.id.photoCheckBox);
        ageSpinner=view.findViewById(R.id.spinner_age_filter);
        ageSpinner.setSelection(5);
        onlineCheckBox=view.findViewById(R.id.onlineCheckBox);
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        return builder
                .setTitle(getResources().getString(R.string.dialog_title))
                .setView(view)
                .setPositiveButton(R.string.search_pos_button_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if ((femaleCheckBox.isChecked() && maleCheckBox.isChecked()) || (!femaleCheckBox.isChecked() && !maleCheckBox.isChecked()) )
                        sendResult(nameEditText.getText().toString(),"",(ageSpinner.getSelectedItem().equals(getResources().getStringArray(R.array.age_for_spinner)[5]) ? null :ageSpinner.getSelectedItem().toString()),cityEditText.getText().toString(),(onlineCheckBox.isChecked() ? getResources().getString(R.string.label_online):""),(true ? "default" : ""), String.valueOf(countrySpinner.getSelectedItemPosition()), String.valueOf(regionSpinner.getSelectedItemPosition()),Activity.RESULT_OK);

                        else  sendResult(nameEditText.getText().toString(),(maleCheckBox.isChecked() ? getResources().getString(R.string.label_male) : getResources().getString(R.string.label_female)),
                                (ageSpinner.getSelectedItem().equals(getResources().getStringArray(R.array.age_for_spinner)[5]) ? null :ageSpinner.getSelectedItem().toString()),cityEditText.getText().toString(),(onlineCheckBox.isChecked() ? getResources().getString(R.string.label_online):""),(true ? "default" : ""), String.valueOf(countrySpinner.getSelectedItemPosition()), String.valueOf(regionSpinner.getSelectedItemPosition()),Activity.RESULT_OK);
                    }
                }).create();
    }

    private void sendResult(String name,String sex,String age,String city,String online,String photo,String country,String region,int result){
        Intent intent=new Intent();
        intent.putExtra(KEY_TO_NAME_FILTER,name);
        intent.putExtra(KEY_TO_SEX_FILTER,sex);
        intent.putExtra(KEY_TO_AGE_FILTER,age);
        intent.putExtra(KEY_TO_CITY_FILTER,city);
        intent.putExtra(KEY_TO_ONLINE_FILTER,online);
        intent.putExtra(KEY_TO_PHOTO_FILTER,photo);
        intent.putExtra(KEY_TO_COUNTRY_FILTER,country);
        intent.putExtra(KEY_TO_REGION_FILTER,region);
        getTargetFragment().onActivityResult(getTargetRequestCode(),result,intent);
    }
}

