package com.example.yesudu;

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
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    void setToolbar() {
        toolbar.inflateMenu(R.menu.admin_menu);
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
                HashMap<String, Object> hashMap = new HashMap<>();
                if (user.getAdmin_block().equals("unblock")) {
                    hashMap.put("admin_block", "block");
                    reference.child(user.getUuid()).updateChildren(hashMap);
                    item.setTitle(R.string.unblock_account);
                }
                else {
                    hashMap.put("admin_block", "unblock");
                    reference.child(user.getUuid()).updateChildren(hashMap);
                    item.setTitle(R.string.block_account);
                }

            }
            break;
        }
        return true;
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
                if (user.getAdmin_block().equals("block")) {
                    toolbar.getMenu().getItem(0).setTitle(R.string.unblock_account);
                }
                else {
                    toolbar.getMenu().getItem(0).setTitle(R.string.block_account);
                }
                setPhotoImageView(user);
                setAllTextView(user);
                setUpGallery(user);
                openGallery(user);
                //setEditButton();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        reference.child(user.getUuid()).removeEventListener(imageEventListener);
    }
}
