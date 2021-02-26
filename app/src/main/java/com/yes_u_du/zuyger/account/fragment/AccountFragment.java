package com.yes_u_du.zuyger.account.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yes_u_du.zuyger.photo_utils.GalleryActivity;
import com.yes_u_du.zuyger.photo_utils.PhotoAdapter;
import com.yes_u_du.zuyger.photo_utils.PhotoViewPagerItemFragment;
import com.yes_u_du.zuyger.R;
import com.yes_u_du.zuyger.account.AccountAdapter;
import com.yes_u_du.zuyger.account.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public abstract class AccountFragment extends Fragment {

    private TextView nameTextView;

    protected Toolbar toolbar;
    protected ImageView photoImageView;
    protected StorageReference storageReference;
    protected ValueEventListener imageEventListener;
    protected DatabaseReference referenceUsers;
    protected DatabaseReference referenceChats;
    protected PhotoAdapter photoAdapter;
    protected RecyclerView photoRecView;
    protected RecyclerView textRecView;
    protected ValueEventListener favoriteChatListener;
    protected String firstKey,secondKey;

    private String[] countryArray;
    private String[] regionArray;

    private Button lookAll;
    private ImageView verifiedImage;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_account,container,false);
        nameTextView=v.findViewById(R.id.my_name_text);
        verifiedImage = v.findViewById(R.id.verified_image_account);
        photoImageView = v.findViewById(R.id.photo_view);
        photoRecView =v.findViewById(R.id.photo_recycler_view);
        textRecView=v.findViewById(R.id.text_recycler_view);
        toolbar=v.findViewById(R.id.toolbarFr);
        lookAll = v.findViewById(R.id.look_all_button);
        referenceUsers = FirebaseDatabase.getInstance().getReference("users");
        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        countryArray=getActivity().getResources().getStringArray(R.array.country_filter);
        setToolbar();
        setUser();
        //setPhotoImageView();
        return v;
    }

    abstract void setToolbar();

    @SuppressLint("ClickableViewAccessibility")
    protected void setPhotoImageView(User user){
        //photoImageView.setOnTouchListener(new ZoomInZoomOut());
        photoImageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Fragment newDetail = PhotoViewPagerItemFragment.newInstance(user.getPhoto_url());
                getFragmentManager().beginTransaction()
                        .addToBackStack(null)
                        .add(R.id.fragment_container,newDetail)
                        .commit();
            }
        });
    }

    abstract boolean clickToolbarItems(MenuItem item);

    abstract void setUser();

    abstract void deleteImage(User user,int i);

    protected void setAllTextView(User user){
        //AccountAdapter adapter=new AccountAdapter(getActivity(),hashMap);
        switch (Integer.parseInt(user.getCountry())){
            case 1:{
                regionArray=getResources().getStringArray(R.array.region_filter_rus);
            }
            break;
            case 2:{
                regionArray=getResources().getStringArray(R.array.region_filter_armenia);
            }
            break;
            case 3:{
                regionArray=getResources().getStringArray(R.array.region_filter_usa);
            }
            break;
            default:{
                regionArray=getResources().getStringArray(R.array.no_region_filter);
            }
        }
        nameTextView.setText(user.getName()+" "+user.getSurname());
        ArrayList<String> tempListUser=new ArrayList<>();
        ArrayList<String> tempListLabels=new ArrayList<>();
        tempListUser.add(countryArray[Integer.parseInt(user.getCountry())]);
        if (!user.getRegion().isEmpty())
        tempListUser.add(regionArray[Integer.parseInt(user.getRegion())]);
        tempListUser.add(user.getCity());
        tempListUser.add(user.getSex());
        tempListUser.add(user.getAge());
        if (!user.getAbout().isEmpty())
        tempListUser.add(user.getAbout());
        tempListLabels.add(getActivity().getString(R.string.country));
        if (!user.getRegion().isEmpty())
        tempListLabels.add(getActivity().getString(R.string.region));
        tempListLabels.add(getActivity().getString(R.string.city));
        tempListLabels.add(getActivity().getString(R.string.sex));
        tempListLabels.add(getActivity().getString(R.string.age));
        if (!user.getAbout().isEmpty())
        tempListLabels.add(getResources().getString(R.string.about));
        textRecView.setAdapter(new AccountAdapter(getActivity(),tempListUser,tempListLabels));
        textRecView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void setGallery(ArrayList<String> urlPhotos, String userId){
        photoAdapter = new PhotoAdapter(getContext(),urlPhotos, userId,getFragmentManager(), PhotoAdapter.PhotoHolder.VIEW_TYPE);
        photoRecView.setAdapter(photoAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(RecyclerView.HORIZONTAL);
        photoRecView.setLayoutManager(manager);
    }

    protected void setUpGallery(User user) {
        ArrayList<String> urlPhotos= new ArrayList<>();
        if (!user.getPhoto_url1().equals("default")){
            urlPhotos.add(user.getPhoto_url1());
        }
        if (!user.getPhoto_url2().equals("default")){
            urlPhotos.add(user.getPhoto_url2());
        }
        if (!user.getPhoto_url3().equals("default")){
            urlPhotos.add(user.getPhoto_url3());
        }
        setGallery(urlPhotos, user.getUuid());
    }

    protected void openGallery(User user){
        lookAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = GalleryActivity.newIntent(getActivity(),user.getUuid(),user.getPhoto_url()
                        ,user.getPhoto_url1(),user.getPhoto_url2(),user.getPhoto_url3());
                startActivity(intent);
            }
        });
    }

    protected void setVerified(User user){
        if(user.getVerified().equals("yes")){
            verifiedImage.setVisibility(View.VISIBLE);
        }
        else {
            verifiedImage.setVisibility(View.INVISIBLE);
        }
    }

    protected void favoriteChat() {
        favoriteChatListener = referenceChats.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String,Object> map = new HashMap<>();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    if (snapshot1.getKey().equals("firstFavorites") && User.getCurrentUser().getUuid().equals(firstKey)) {
                        map.put("firstFavorites","yes");
                        snapshot.getRef().updateChildren(map);
                    } else if (snapshot1.getKey().equals("secondFavorites") && User.getCurrentUser().getUuid().equals(secondKey)) {
                        map.put("secondFavorites","yes");
                        snapshot.getRef().updateChildren(map);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    protected String generateKey(String receiverUuid){
        ArrayList<String> templist=new ArrayList<>();
        templist.add(User.getCurrentUser().getUuid());
        templist.add(receiverUuid);
        Collections.sort(templist);
        firstKey=templist.get(0);
        secondKey = templist.get(1);
        return templist.get(0)+templist.get(1);
    }

}
