package com.yes_u_du.zuyger.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.yes_u_du.zuyger.R;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class CalendarDialog extends DialogFragment {
    public static final String EXTRA_DATE ="com.example.criminalintent.date" ;
    private DatePicker dialogDate;
    private static final String ARG_GATE = "date";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Date date = (Date) getArguments().getSerializable(ARG_GATE);
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date,null);
        dialogDate = v.findViewById(R.id.dialog_date_picker);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        dialogDate.init(year,month,day,null);
        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int year = dialogDate.getYear();
                        int month = dialogDate.getMonth();
                        int day = dialogDate.getDayOfMonth();
                        Date date = new GregorianCalendar(year,month,day).getTime();
                        sendResult(Activity.RESULT_OK,date);
                    }
                })
                .create();
    }

    private void sendResult(int resultCode, Date date){
        if (getTargetFragment()==null){
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE,date);
        getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode,intent);
    }

    public static CalendarDialog newInstance(Date date){
        Bundle arg = new Bundle();
        arg.putSerializable(ARG_GATE,date);
        CalendarDialog datePickerFragment = new CalendarDialog();
        datePickerFragment.setArguments(arg);
        return datePickerFragment;
    }
}
