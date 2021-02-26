package com.yes_u_du.zuyger.account.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.yes_u_du.zuyger.R;
import com.yes_u_du.zuyger.account.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
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
        referenceChats = FirebaseDatabase.getInstance().getReference("chats").child(generateKey(uuId));
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public void onPause() {
        super.onPause();
        if (favoriteChatListener!=null) referenceChats.removeEventListener(favoriteChatListener);
    }

    @Override
    void setToolbar() {
        toolbar.inflateMenu(R.menu.user_account_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return clickToolbarItems(item);
            }
        });
    }


    @Override
    boolean clickToolbarItems(MenuItem item) {
        switch (item.getItemId()){
            case R.id.favorite_add:{
                favoriteChat();
                return true;
            }
            default:return false;
        }
    }

    @Override
    void setUser() {
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage(getResources().getString(R.string.uploading));
        pd.show();
        imageEventListener= referenceUsers.child(uuId).addValueEventListener(new ValueEventListener() {

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
                setPhotoImageView(user);
                setAllTextView(user);
                setUpGallery(user);
                openGallery(user);
                setVerified(user);
                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    void deleteImage(User user,int i) {

    }

    private void setUserParameter(User user){
        this.user=user;
        this.user.setUuid(uuId);
    }

}
