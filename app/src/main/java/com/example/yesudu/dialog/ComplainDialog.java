package com.example.yesudu.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yesudu.R;

import java.util.ArrayList;

public class ComplainDialog extends DialogFragment implements Dismissable {

    public static final int ADVERTISING_BTN_CODE =-111;
    public static final int FISHING_BTN_CODE =-222;
    public static final int SUBSTANCE_BTN_CODE =-333;
    public static final int OBSCENE_BTN_CODE =-444;
    public static final int SPAM_BTN_CODE =-555;
    public static final int PORNOGRAPHIC_BTN_CODE =-666;
    public static final int PHOTOS_BTN_CODE =-777;
    public static final int FAKE_BTN_CODE =-888;
    public static final int THREATS_BTN_CODE =-999;
    public static final int REASON_BTN_CODE =-1000;
    public static final String COMPLAIN_CODE="complain_code";
    private RecyclerView recView;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View v= LayoutInflater.from(getActivity()).inflate(R.layout.complain_dialog,null);
        //setupRecView;
        initRecyclerView(v);
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        return builder.setView(v).create();
    }

    private void initRecyclerView(View v) {
        recView=v.findViewById(R.id.dialog_rec_view);
        ArrayList<Integer> codes=new ArrayList<>();
        codes.add(ADVERTISING_BTN_CODE);
        codes.add(FISHING_BTN_CODE);
        codes.add(SUBSTANCE_BTN_CODE);
        codes.add(OBSCENE_BTN_CODE);
        codes.add(SPAM_BTN_CODE);
        codes.add(PORNOGRAPHIC_BTN_CODE);
        codes.add(PHOTOS_BTN_CODE);
        codes.add(FAKE_BTN_CODE);
        codes.add(THREATS_BTN_CODE);
        codes.add(REASON_BTN_CODE);
        DialogAdapter adapter=new DialogAdapter(codes,this,getActivity());
        recView.setAdapter(adapter);
        recView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void sendResult(int result, String complainName) {
        Intent intent=new Intent();
        intent.putExtra(COMPLAIN_CODE,complainName);
        getTargetFragment().onActivityResult(getTargetRequestCode(),result,intent);
    }


    @Override
    public void onDismiss(Object complainName) {
        sendResult(Activity.RESULT_OK, (String) complainName);
        dismiss();
    }

    @Override
    public String chooseOption(Object code) {
        switch((Integer) code){
            case ADVERTISING_BTN_CODE:{
                return getActivity().getString(R.string.advertising_title);
            }
            case FISHING_BTN_CODE:{
                return getActivity().getString(R.string.fishing_title);
            }
            case SUBSTANCE_BTN_CODE:{
                return getActivity().getString(R.string.illegal_substance_title);
            }
            case OBSCENE_BTN_CODE:{
                return getActivity().getString(R.string.obscene_content_title);
            }
            case SPAM_BTN_CODE:{
                return getActivity().getString(R.string.spam_title);
            }
            case PORNOGRAPHIC_BTN_CODE:{
                return getActivity().getString(R.string.pornographic_content_title);
            }
            case PHOTOS_BTN_CODE:{
                return getActivity().getString(R.string.illegal_photos_title);
            }
            case FAKE_BTN_CODE:{
                return getActivity().getString(R.string.fake_profile_title);
            }
            case THREATS_BTN_CODE:{
                return getActivity().getString(R.string.threats_title);
            }
            case REASON_BTN_CODE:{
                return getActivity().getString(R.string.another_reason_title);
            }
            default:return null;
        }
    }
}
