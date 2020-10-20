package com.example.myproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class UserGalleryFragment extends Fragment {
    private RecyclerView galleryRecyclerView;
    private PhotoAdapter photoAdapter;
    private String userId;
    private String photo_url1;
    private String photo_url2;
    private String photo_url3;
    private Toolbar toolbar;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.gallery_fragment,null);
        galleryRecyclerView = v.findViewById(R.id.gallery_recycler_view);
        userId = getArguments().getString(GalleryActivity.USER_ID);
        photo_url1 = getArguments().getString(GalleryActivity.PHOTO_URL1);
        photo_url2 = getArguments().getString(GalleryActivity.PHOTO_URL2);
        photo_url3 = getArguments().getString(GalleryActivity.PHOTO_URL3);
        toolbar = v.findViewById(R.id.toolbarFr);
        toolbar.setTitle(R.string.gallery_text);
        setUpGallery();
        return v;
    }

    private void setGallery(List<String> urlPhotos, String userId){
        photoAdapter = new PhotoAdapter(getContext(),urlPhotos,userId,getFragmentManager(), PhotoAdapter.PhotoHolder.VIEW_TYPE);
        galleryRecyclerView.setAdapter(photoAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(RecyclerView.HORIZONTAL);
        galleryRecyclerView.setLayoutManager(manager);
    }

    private void setUpGallery() {
        List<String> urlPhotos= new ArrayList<>();
        if (!photo_url1.equals("default")){
            urlPhotos.add(photo_url1);
        }
        if (!photo_url2.equals("default")){
            urlPhotos.add(photo_url2);
        }
        if (!photo_url3.equals("default")){
            urlPhotos.add(photo_url3);
        }
        setGallery(urlPhotos, userId);
    }

    public static Fragment newInstance( String photoUrl1, String photoUrl2, String photoUrl3,String userId ){
        UserGalleryFragment fragment = new UserGalleryFragment();
        Bundle bundle = new Bundle();
        bundle.putString(GalleryActivity.PHOTO_URL1,photoUrl1);
        bundle.putString(GalleryActivity.PHOTO_URL2,photoUrl2);
        bundle.putString(GalleryActivity.PHOTO_URL3,photoUrl3);
        bundle.putString(GalleryActivity.USER_ID, userId);
        fragment.setArguments(bundle);
        return fragment;
    }

}