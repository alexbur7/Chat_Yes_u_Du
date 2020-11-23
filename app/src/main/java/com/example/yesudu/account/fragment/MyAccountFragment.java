package com.example.yesudu.account.fragment;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import com.bumptech.glide.Glide;
import com.example.yesudu.dialog.AcceptDialog;
import com.example.yesudu.dialog.EditAccountDialog;
import com.example.yesudu.account.User;
import com.example.yesudu.chat_list.activity.AdminActivity;
import com.example.yesudu.chat_list.activity.BlockListActivity;
import com.example.yesudu.chat_list.activity.FavoriteListActivity;
import com.example.yesudu.reg_and_login_utils.LogActivity;
import com.example.yesudu.R;
import com.example.yesudu.reg_and_login_utils.ResetPasswordActivity;
import com.example.yesudu.chat.ChatActivity;
import com.example.yesudu.rules_and_policy.InformationListActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.Date;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

public class MyAccountFragment extends AccountFragment {

    private static  final  int IMAGE_REQUEST=1;
    public static  final  int KEY_ACCEPT=2;
    private Uri imageUri;
    private StorageTask uploadTask;

    private String status_online;
    private String status_offline;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        status_online=getResources().getString(R.string.label_online);
        status_offline=getResources().getString(R.string.label_offline);
        return super.onCreateView(inflater,container,savedInstanceState);
    }

    @Override
    void setToolbar() {
        toolbar.inflateMenu(R.menu.account_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return clickToolbarItems(item);
            }
        });
    }

    /*void setEditButton() {
        editButton.setText(getResources().getString(R.string.edit_account));
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditDialog editDialog = new EditDialog();
                editDialog.show(getFragmentManager(),null);
            }
        });
    }*/


    @Override
    protected void setPhotoImageView(User user) {
        super.setPhotoImageView(user);
        photoImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                openImage();
                return true;
            }
        });
    }

    @Override
    boolean clickToolbarItems(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout: {
                User.getCurrentUser().setStatus(getResources().getString(R.string.label_offline));
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), LogActivity.class));
                getActivity().finish();
            }
            break;
            case R.id.delete_account:{
                AcceptDialog dialog = new AcceptDialog(reference,imageEventListener,KEY_ACCEPT,null);
                dialog.show(getFragmentManager(),null);
            }
            break;
            case R.id.blocklist:{
                Intent intent=new Intent(getActivity(), BlockListActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.panel_admin:{
                Intent intent=new Intent(getActivity(), AdminActivity.class);
                startActivity(intent);
            }
            break;
            case  R.id.reset_menu_password:{
                Intent intent = new Intent(getActivity(), ResetPasswordActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.information_menu:{
                Intent intent = new Intent(getActivity(), InformationListActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.custmoize:{
                EditAccountDialog editAccountDialog = new EditAccountDialog();
                editAccountDialog.show(getFragmentManager(),null);
            }
            break;
            case R.id.chat_administrator:{
                Intent intent = ChatActivity.newIntent(getActivity(),User.getCurrentUser().getUuid(),User.getCurrentUser().getPhoto_url(),3);
                startActivity(intent);
            }
            break;
            case R.id.favoritelist:{
                Intent intent = new Intent(getActivity(), FavoriteListActivity.class);
                startActivity(intent);
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
        String uuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        imageEventListener=reference.child(uuid).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                User.setCurrentUser(user, uuid,status_offline);
                    if (user.getAdmin().equals("true")) {
                        toolbar.getMenu().getItem(8).setVisible(true);
                    }
                if(user.getPhoto_url().equals("default")){
                    photoImageView.setImageResource(R.drawable.unnamed);
                }
                else {
                    if (isAdded()) Glide.with(getContext()).load(user.getPhoto_url()).into(photoImageView);
                }
                setPhotoImageView(user);
                setUpGallery(user);
                setAllTextView(user);
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
    void deleteImage(User user,int i) {
        if (!user.getPhoto_url().equals("default") && i==0) {
            StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(user.getPhoto_url());
            photoRef.delete();
        }
        else if (!user.getPhoto_url1().equals("default") && i==1){
            StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(user.getPhoto_url1());
            photoRef.delete();
        }
        else if (!user.getPhoto_url2().equals("default") && i==2){
            StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(user.getPhoto_url2());
            photoRef.delete();
        }
        else if (!user.getPhoto_url3().equals("default") && i==3){
            StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(user.getPhoto_url3());
            photoRef.delete();
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        status(status_online);
    }


    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST);
    }



    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage(){
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage(getResources().getString(R.string.uploading));
        pd.show();

        if (imageUri != null){
            final StorageReference fileReference= storageReference.child(System.currentTimeMillis()+
                    "."+getFileExtension(imageUri));
            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask((Continuation<UploadTask.TaskSnapshot, Task<Uri>>) task -> {
                if (!task.isSuccessful()){
                    throw  task.getException();
                }
                return fileReference.getDownloadUrl();
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();

                        reference = FirebaseDatabase.getInstance().getReference("users").child(User.getCurrentUser().getUuid());
                        HashMap<String,Object> map = new HashMap<>();
                        map.put("photo_url",mUri);
                        deleteImage(User.getCurrentUser(),0);
                        reference.updateChildren(map);
                        User.getCurrentUser().setPhoto_url(mUri);
                    }
                    else {
                        Toast.makeText(getContext(),R.string.failed_update_photo,Toast.LENGTH_SHORT).show();
                    }
                    pd.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        }
        else {
            Toast.makeText(getContext(),R.string.no_image_selected,Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
                && data!=null && data.getData() !=null
        ){
            imageUri = data.getData();
            uploadImage();
        }
    }


    private void status(String status){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        if (status.equals(status_offline)){
            hashMap.put("online_time",(new Date()).getTime());
        }
        FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getUid()).updateChildren(hashMap);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (User.getCurrentUser()!=null)
        reference.child(User.getCurrentUser().getUuid()).removeEventListener(imageEventListener);
    }
}
