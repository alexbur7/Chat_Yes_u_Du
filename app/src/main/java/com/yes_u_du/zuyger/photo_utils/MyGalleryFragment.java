package com.yes_u_du.zuyger.photo_utils;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yes_u_du.zuyger.R;
import com.yes_u_du.zuyger.account.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

public class MyGalleryFragment extends Fragment {
    private static  final  int IMAGE_REQUEST=1;
    public static  final  int DELETE_IMAGE_REQUEST=1;

    private Uri imageUri;
    private StorageTask uploadTask;
    private DatabaseReference reference;
    private RecyclerView galleryRecyclerView;
    private PhotoAdapter photoAdapter;
    private String userID;
    private String photo_url;
    private String photo_url1;
    private String photo_url2;
    private String photo_url3;
    private Toolbar toolbar;
    private StorageReference storageReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.gallery_fragment,null);
        galleryRecyclerView = v.findViewById(R.id.gallery_recycler_view);
        userID = getArguments().getString(GalleryActivity.USER_ID);
        photo_url = getArguments().getString(GalleryActivity.PHOTO_URL);
        photo_url1 = getArguments().getString(GalleryActivity.PHOTO_URL1);
        photo_url2 = getArguments().getString(GalleryActivity.PHOTO_URL2);
        photo_url3 = getArguments().getString(GalleryActivity.PHOTO_URL3);
        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        toolbar = v.findViewById(R.id.toolbarFr);
       // toolbar.setTitle(R.string.gallery_text);
        setUpGallery();
        setToolbar();
        return v;
    }


    private void setGallery(ArrayList<String> urlPhotos, String userId){
        photoAdapter = new PhotoAdapter(getContext(),urlPhotos, userId,getFragmentManager(), PhotoAdapter.GalleryHolder.VIEW_TYPE);
        galleryRecyclerView.setAdapter(photoAdapter);
        galleryRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));
    }

    private void setUpGallery() {
        ArrayList<String> urlPhotos= new ArrayList<>();
        urlPhotos.add(photo_url);
        if (!photo_url1.equals("default")){
            urlPhotos.add(photo_url1);
        }
        if (!photo_url2.equals("default")){
            urlPhotos.add(photo_url2);
        }
        if (!photo_url3.equals("default")){
            urlPhotos.add(photo_url3);
        }
        setGallery(urlPhotos,userID);
    }

    private void updateGallery() {
        photoAdapter.getUrlPhotos().clear();
        photoAdapter.getUrlPhotos().add(photo_url);
        if (!photo_url1.equals("default")){
            photoAdapter.getUrlPhotos().add(photo_url1);
        }
        if (!photo_url2.equals("default")){
            photoAdapter.getUrlPhotos().add(photo_url2);
        }
        if (!photo_url3.equals("default")){
            photoAdapter.getUrlPhotos().add(photo_url3);
        }
        photoAdapter.notifyDataSetChanged();
        //setGallery(urlPhotos,userID);
    }

    private void updateGallery(int num){
        photoAdapter.getUrlPhotos().listIterator(num).set("default");
        photoAdapter.notifyDataSetChanged();
    }


    public static Fragment newInstance(String photoUrl,String photoUrl1, String photoUrl2, String photoUrl3, String userId ){
        MyGalleryFragment fragment = new MyGalleryFragment();
        Bundle bundle = new Bundle();
        bundle.putString(GalleryActivity.PHOTO_URL, photoUrl);
        bundle.putString(GalleryActivity.PHOTO_URL1,photoUrl1);
        bundle.putString(GalleryActivity.PHOTO_URL2,photoUrl2);
        bundle.putString(GalleryActivity.PHOTO_URL3,photoUrl3);
        bundle.putString(GalleryActivity.USER_ID, userId);
        fragment.setArguments(bundle);
        return fragment;
    }

    private void setToolbar() {
        toolbar.inflateMenu(R.menu.gallery_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return clickToolbarItems(item);
            }
        });
    }

    private boolean clickToolbarItems(MenuItem item) {
        switch (item.getItemId()){
            case R.id.add_image:{
                openImage();
            }
            break;
        }
        return true;
    }

    private void openImage() {
        if (!photo_url3.equals("default") && !photo_url2.equals("default") && !photo_url1.equals("default"))
        {
            Toast.makeText(getContext(),getActivity().getResources().getString(R.string.you_cant_add_4_photo),Toast.LENGTH_SHORT).show();
            return;
        }
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
                        if (User.getCurrentUser().getPhoto_url1().equals("default")){
                            map.put("photo_url1", mUri);
                            photo_url1=mUri;
                            User.getCurrentUser().setPhoto_url1(mUri);
                        }
                        else if (User.getCurrentUser().getPhoto_url2().equals("default")) {
                            map.put("photo_url2", mUri);
                            photo_url2=mUri;
                            User.getCurrentUser().setPhoto_url2(mUri);
                        }
                        else if (User.getCurrentUser().getPhoto_url3().equals("default")){
                            map.put("photo_url3", mUri);
                            photo_url3=mUri;
                            User.getCurrentUser().setPhoto_url3(mUri);
                        }
                        else {
                        }
                        //setUpGallery();
                        updateGallery();
                        reference.updateChildren(map);
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
                && data!=null && data.getData() !=null){
            imageUri = data.getData();
            uploadImage();
        }
        else if (requestCode == DELETE_IMAGE_REQUEST && resultCode == RESULT_OK){
            Log.d("tut_photo_delete", " зашли в onActivityResult");
            switch (data.getExtras().getInt(String.valueOf(DELETE_IMAGE_REQUEST))){
                case 1:{
                    photo_url1="default";
                    break;
                }
                case 2:{
                    photo_url2="default";
                    break;
                }
                case 3:{
                    photo_url3="default";
                    break;
                }
            }
            updateGallery();
        }
    }
}
