package com.example.myproject;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class UserAccountFragment extends AccountFragment {
    public static final String KEY_TO_RECEIVER_UUID="recevierID";
    private User user;
    private String uuId;

    public static Fragment newInstance(String toUserUUID){
        UserAccountFragment fragment = new UserAccountFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_TO_RECEIVER_UUID, toUserUUID);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        uuId = getArguments().getString(KEY_TO_RECEIVER_UUID);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    void setToolbar() {

    }

    @Override
    void setEditButton() {
        editButton.setVisibility(View.INVISIBLE);
    }

    @Override
    void setPhotoImageView() {

    }

    @Override
    boolean clickToolbarItems(MenuItem item) {
        return false;
    }

    @Override
    void setUser() {
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage(getResources().getString(R.string.uploading));
        pd.show();
        imageEventListener=reference.child(uuId).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                setUserParameter(user);
                if(user.getPhoto_url().equals("default")){
                    photoImageView.setImageResource(R.drawable.unnamed);
                }
                else {
                    if (isAdded()) Glide.with(getContext()).load(user.getPhoto_url()).into(photoImageView);
                }
                setAllTextView(user);
                setUpGallery(user);
                openGallery(user);
                setEditButton();
                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    void deleteImage(User user) {

    }

    private void setUserParameter(User user){
        this.user=user;
        this.user.setUuid(uuId);
    }
}
