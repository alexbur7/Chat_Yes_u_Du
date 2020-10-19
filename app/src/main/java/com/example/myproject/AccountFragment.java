package com.example.myproject;

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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public abstract class AccountFragment extends Fragment {

    private TextView countryTextView;
    private TextView regionTextView;
    private TextView cityTextView;
    private TextView nameTextView;
    private TextView ageTextView;
    private TextView sexTextView;
    private TextView aboutTextView;
    protected Button editButton;
    protected Toolbar toolbar;
    protected ImageView photoImageView;
    protected StorageReference storageReference;
    protected ValueEventListener imageEventListener;
    protected DatabaseReference reference;
    protected PhotoAdapter photoAdapter;
    protected RecyclerView recyclerView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.account_fragment,container,false);
        editButton = v.findViewById(R.id.edit_button);
        photoImageView = v.findViewById(R.id.photo_view);
        countryTextView = v.findViewById(R.id.country_textView);
        regionTextView = v.findViewById(R.id.region_textView);
        cityTextView = v.findViewById(R.id.city_textView);
        nameTextView = v.findViewById(R.id.my_name_text);
        ageTextView = v.findViewById(R.id.age_textView);
        sexTextView = v.findViewById(R.id.sex_textView);
        aboutTextView=v.findViewById(R.id.about_textView);
        recyclerView =v.findViewById(R.id.photo_recycler_view);
        toolbar=v.findViewById(R.id.toolbarFr);
        reference = FirebaseDatabase.getInstance().getReference("users");
        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        setToolbar();
        setUser();
        setPhotoImageView();
        return v;
    }

    abstract void setToolbar();

    abstract void setEditButton();

    abstract void setPhotoImageView();

    abstract boolean clickToolbarItems(MenuItem item);

    abstract void setUser();

    abstract void deleteImage(User user);

    protected void setAllTextView(User user){
        countryTextView.setText(user.getCountry());
        regionTextView.setText(user.getRegion());
        cityTextView.setText(user.getCity());
        ageTextView.setText(user.getAge());
        sexTextView.setText(user.getSex());
        aboutTextView.setText(user.getAbout());
        if (user.getSurname().equals("")) nameTextView.setText(user.getName());
        else nameTextView.setText(user.getName() +" "+user.getSurname());
    }

    private void setGallery(List<String> urlPhotos){
        photoAdapter = new PhotoAdapter(getContext(),urlPhotos);
        recyclerView.setAdapter(photoAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(RecyclerView.HORIZONTAL);
        recyclerView.setLayoutManager(manager);
    }

    protected void setUpGallery(User user) {
        List<String> urlPhotos= new ArrayList<>();
        if (!user.getPhoto_url1().equals("default")){
            urlPhotos.add(user.getPhoto_url1());
        }
        if (!user.getPhoto_url2().equals("default")){
            urlPhotos.add(user.getPhoto_url2());
        }
        if (!user.getPhoto_url3().equals("default")){
            urlPhotos.add(user.getPhoto_url3());
        }
        setGallery(urlPhotos);
    }
}
