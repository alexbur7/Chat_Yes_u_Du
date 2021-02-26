package com.yes_u_du.zuyger.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yes_u_du.zuyger.R;

import java.util.ArrayList;

public class ComplainDialog extends DialogFragment implements Dismissable {

    public static final int BASE_COMPLAIN_CODE = 10;
    public static final int FAKE_COMPLAIN_CODE = 20;
    public static final int FAKE_PHOTO_COMPLAIN_CODE = 30;

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
    public static final int WRONG_AGE= -1001;
    //public static final int WRONG_LOCATION= -1002;
    public static final int MARRIED= -1003;
    public static final int WRONG_COUNTRY= -1004;
    public static final int WRONG_CITY= -1005;
    public static final int WRONG_COUNTRY_AND_CITY= -1006;
    public static final int WRONG_NAME = -1007;
    public static final int UNDERAGE_USER = -1008;

    public static final int FAR_PHOTO = -1009;
    public static final int SUBSTANDARD = -1010;
    public static final int ALREADY_EXISTING = -1011;
    public static final int OTHER_PEOPLE = -1012;
    public static final int NOT_VISIBLE_USER = -1013;
    public static final int POORLY_VISIBLE = -1014;
    public static final int ANY_THINGS = -1015;
    public static final int PORNO_PHOTO = -1016;
    public static final int GROUP_PHOTO = -1017;
    public static final int BABY_PHOTO = -1018;
    public static final int IRRELEVANT = -1019;
    public static final int IN_SUNGLASSES = -1020;
    public static final int TILTED_IN_PROFILE = -1021;
    public static final int PARTS_OF_USER = -1022;
    public static final int CONTAINING_SCENES = -1023;
    public static final int NON_ORIGINAL = -1024;
    public static final int BLACK_AND_WHITE_PHOTOS = -1025;
    public static final int WRONG_SEX = -1026;
    public static final String COMPLAIN_CODE="complain_code";
    private RecyclerView recView;
    private int codeTypeRecView;
    private FragmentManager manager;
    private Fragment fragment;
    private TextView wrongText;

    public ComplainDialog(int code){
        this.codeTypeRecView =code;
        manager=null;
        fragment=null;
    }

    public ComplainDialog(int code, FragmentManager manager, Fragment fragment){
        this.codeTypeRecView =code;
        this.manager = manager;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View v= LayoutInflater.from(getActivity()).inflate(R.layout.complain_dialog,null);
        //setupRecView;
        wrongText = v.findViewById(R.id.wrong_textView);
        if (codeTypeRecView==FAKE_COMPLAIN_CODE){
            wrongText.setVisibility(View.VISIBLE);
            wrongText.setText(R.string.wrong);
        }
        else if (codeTypeRecView == FAKE_PHOTO_COMPLAIN_CODE){
            wrongText.setVisibility(View.VISIBLE);
            wrongText.setText(R.string.illegal_photos);
        }
        initRecyclerView(v, codeTypeRecView);
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        return builder.setView(v).create();
    }

    private void initRecyclerView(View v,int codeTypeRecView) {
        recView=v.findViewById(R.id.dialog_rec_view);
        ArrayList<Integer> codes=new ArrayList<>();
        fillingArrayListWithCodes(codeTypeRecView, codes);
        DialogAdapter adapter=new DialogAdapter(codes,this,getActivity(), manager,fragment,this);
        recView.setAdapter(adapter);
        recView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void fillingArrayListWithCodes(int codeTypeRecView, ArrayList<Integer> codes) {
        if (codeTypeRecView==BASE_COMPLAIN_CODE) {
            codes.add(ADVERTISING_BTN_CODE);
            codes.add(FISHING_BTN_CODE);
            codes.add(SUBSTANCE_BTN_CODE);
            codes.add(OBSCENE_BTN_CODE);
            codes.add(SPAM_BTN_CODE);
            codes.add(PORNOGRAPHIC_BTN_CODE);
            //codes.add(PHOTOS_BTN_CODE);
            codes.add(PHOTOS_BTN_CODE);
            codes.add(FAKE_BTN_CODE);
            codes.add(THREATS_BTN_CODE);
            codes.add(MARRIED);
            codes.add(UNDERAGE_USER);
            codes.add(REASON_BTN_CODE);
        }
        else if (codeTypeRecView ==FAKE_COMPLAIN_CODE){
            codes.add(WRONG_NAME);
            codes.add(WRONG_AGE);
            codes.add(WRONG_SEX);
            codes.add(WRONG_COUNTRY);
            codes.add(WRONG_CITY);
            codes.add(WRONG_COUNTRY_AND_CITY);
            //codes.add(WRONG_LOCATION);
        }
        else  if (codeTypeRecView == FAKE_PHOTO_COMPLAIN_CODE){
            codes.add(FAR_PHOTO);
            codes.add(SUBSTANDARD);
            codes.add(ALREADY_EXISTING);
            codes.add(OTHER_PEOPLE);
            codes.add(NOT_VISIBLE_USER);
            codes.add(POORLY_VISIBLE);
            codes.add(ANY_THINGS);
            codes.add(PORNO_PHOTO);
            codes.add(GROUP_PHOTO);
            codes.add(BABY_PHOTO);
            codes.add(IRRELEVANT);
            codes.add(IN_SUNGLASSES);
            codes.add(TILTED_IN_PROFILE);
            codes.add(PARTS_OF_USER);
            codes.add(CONTAINING_SCENES);
            codes.add(NON_ORIGINAL);
            codes.add(BLACK_AND_WHITE_PHOTOS);
        }
       /* else if (codeTypeRecView == FAKE_LOCATION_COMPLAIN_CODE){
            codes.add(WRONG_COUNTRY);
            codes.add(WRONG_CITY);
            codes.add(WRONG_COUNTRY_AND_CITY);
        }*/
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
                return getActivity().getString(R.string.extrimism_title);
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
            case WRONG_AGE:{
                return getActivity().getString(R.string.false_age);
            }
            /*case WRONG_LOCATION:{
                return getActivity().getString(R.string.false_location);
            }*/
            case MARRIED:{
                return getActivity().getString(R.string.married);
            }
            case WRONG_COUNTRY:{
                return getActivity().getString(R.string.false_country);
            }case WRONG_CITY:{
                return getActivity().getString(R.string.false_city);
            }case WRONG_COUNTRY_AND_CITY:{
                return getActivity().getString(R.string.false_country_and_country);
            }
            case WRONG_NAME:{
                return getActivity().getString(R.string.false_name);
            }
            case UNDERAGE_USER:{
                return getActivity().getString(R.string.underage);
            }
            case FAR_PHOTO:{
                return getActivity().getString(R.string.far_photo);
            }
            case SUBSTANDARD:{
                return getActivity().getString(R.string.substandard);
            }
            case ALREADY_EXISTING:{
                return getActivity().getString(R.string.already_existing);
            }
            case OTHER_PEOPLE:{
                return getActivity().getString(R.string.other_people);
            }
            case NOT_VISIBLE_USER:{
                return getActivity().getString(R.string.not_visible_user);
            }
            case POORLY_VISIBLE:{
                return getActivity().getString(R.string.poorly_visible);
            }
            case ANY_THINGS:{
                return getActivity().getString(R.string.any_things);
            }
            case PORNO_PHOTO:{
                return getActivity().getString(R.string.porno_photo);
            }
            case GROUP_PHOTO:{
                return getActivity().getString(R.string.group_photos);
            }
            case BABY_PHOTO:{
                return getActivity().getString(R.string.baby_photos);
            }
            case IRRELEVANT:{
                return getActivity().getString(R.string.irrelevant);
            }
            case IN_SUNGLASSES:{
                return getActivity().getString(R.string.in_sunglasses);
            }
            case TILTED_IN_PROFILE:{
                return getActivity().getString(R.string.tilted_in_profile);
            }
            case PARTS_OF_USER:{
                return getActivity().getString(R.string.parts_of_user);
            }
            case CONTAINING_SCENES:{
                return getActivity().getString(R.string.containing_scenes);
            }
            case NON_ORIGINAL:{
                return getActivity().getString(R.string.non_original);
            }
            case BLACK_AND_WHITE_PHOTOS:{
                return getActivity().getString(R.string.black_and_white_photos);
            }
            case WRONG_SEX:{
                return getActivity().getString(R.string.label_sex);
            }
            default:return null;
        }
    }
}
