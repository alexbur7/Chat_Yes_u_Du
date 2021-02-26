package com.yes_u_du.zuyger.account.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
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
import com.yes_u_du.zuyger.chat_list.activity.AdminBlockListActivity;
import com.yes_u_du.zuyger.chat_list.fragment.AdminPermBlockListFragment;
import com.yes_u_du.zuyger.chat_list.fragment.AdminTimeBlockListFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.HashMap;

public class AdminAccountFragment extends AccountFragment {
    public static final String KEY_TO_RECEIVER_UUID="recevierID";
    private User user;
    private String uuId;

    public static Fragment newInstance(String toUserUUID){
        AdminAccountFragment fragment = new AdminAccountFragment();
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
        toolbar.inflateMenu(R.menu.admin_account_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return clickToolbarItems(item);
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    boolean clickToolbarItems(MenuItem item) {
        switch (item.getItemId()){
            case R.id.verified:{
                confirm(item);
            }
            break;
            case R.id.delete_image_menu:{
                deleteImage(user,0);
            }
            break;
            case R.id.delete_image1_menu:{
                if (!user.getPhoto_url1().equals("default"))
                deleteImage(user,1);
            }
            break;
            case R.id.delete_image2_menu:{
                if (!user.getPhoto_url2().equals("default"))
                deleteImage(user,2);
            }
            break;
            case R.id.delete_image3_menu:{
                if (!user.getPhoto_url3().equals("default"))
                deleteImage(user,3);
            }
            break;
            case R.id.block_account:{
                setBlock(item);
            }
            break;
            case R.id.perm_block_account:{
                setPermBlock(item);
            }
            break;
            case R.id.list_block_admin:{
                Intent intent=AdminBlockListActivity.newInstance(getActivity(), AdminTimeBlockListFragment.BLOCK_CODE);
                startActivity(intent);
                getActivity().finish();
            }break;
            case R.id.perm_block_admin:{
                Intent intent=AdminBlockListActivity.newInstance(getActivity(), AdminPermBlockListFragment.BLOCK_CODE);
                startActivity(intent);
                getActivity().finish();
            }break;
            /*case R.id.favorite_add:{
                favoriteChat();
            }
            break;*/
        }
        return true;
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
                setTitleToolbar(user);
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
    protected void deleteImage(User user, int i) {
        StorageReference photoRef;
            if (i==0) photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(user.getPhoto_url());
            else if(i==1) photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(user.getPhoto_url1());
            else if(i==2) photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(user.getPhoto_url2());
            else  photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(user.getPhoto_url3());
            photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    HashMap<String,Object> hashMap = new HashMap<>();
                    if (i==0)
                    hashMap.put("photo_url","default");
                    else if(i==1)
                    hashMap.put("photo_url1","default");
                    else if (i==2)
                    hashMap.put("photo_url2","default");
                    else
                    hashMap.put("photo_url3","default");
                    FirebaseDatabase.getInstance().getReference("users").child(user.getUuid()).updateChildren(hashMap);
                }
            });
    }

    private void setUserParameter(User user){
        this.user=user;
        this.user.setUuid(uuId);
    }

    private void setTitleToolbar(User user) {
        if (user.getVerified().equals("no")){
            toolbar.getMenu().getItem(0).setTitle(R.string.verified);
        }
        else  toolbar.getMenu().getItem(0).setTitle(R.string.cancel_verified);
        if (user.getAdmin_block().equals("block")) {
            toolbar.getMenu().getItem(1).setTitle(R.string.unblock_account);
        }
        else {
            toolbar.getMenu().getItem(1).setTitle(R.string.block_account);
        }
        if (user.getPerm_block().equals("block")) {
            toolbar.getMenu().getItem(2).setTitle(R.string.unblock_account_perm);
        }
        else {
            toolbar.getMenu().getItem(2).setTitle(R.string.perm_block_account);
        }
    }

    private void setPermBlock(MenuItem item) {
        HashMap<String, Object> hashMap = new HashMap<>();
        if (user.getPerm_block().equals("unblock")) {
            hashMap.put("perm_block", "block");
            referenceUsers.child(user.getUuid()).updateChildren(hashMap);
            item.setTitle(R.string.unblock_account_perm);
        }
        else {
            hashMap.put("perm_block", "unblock");
            referenceUsers.child(user.getUuid()).updateChildren(hashMap);
            item.setTitle(R.string.perm_block_account);
        }
    }

    private void setBlock(MenuItem item) {
        HashMap<String, Object> hashMap = new HashMap<>();
        if (user.getAdmin_block().equals("unblock")) {
            hashMap.put("admin_block", "block");
            referenceUsers.child(user.getUuid()).updateChildren(hashMap);
            item.setTitle(R.string.unblock_account);
        }
        else {
            hashMap.put("admin_block", "unblock");
            referenceUsers.child(user.getUuid()).updateChildren(hashMap);
            item.setTitle(R.string.block_account);
        }
    }

    private void confirm(MenuItem item){
        HashMap<String, Object> hashMap = new HashMap<>();
        if (user.getVerified().equals("no")){
            hashMap.put("verified", "yes");
            referenceUsers.child(user.getUuid()).updateChildren(hashMap);
            item.setTitle(R.string.cancel_verified);
        }
        else {
            hashMap.put("verified", "no");
            referenceUsers.child(user.getUuid()).updateChildren(hashMap);
            item.setTitle(R.string.verified);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        referenceUsers.child(user.getUuid()).removeEventListener(imageEventListener);
    }
}
