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

    private TextView nameTextView;

    protected Button editButton;
    protected Toolbar toolbar;
    protected ImageView photoImageView;
    protected StorageReference storageReference;
    protected ValueEventListener imageEventListener;
    protected DatabaseReference reference;
    protected PhotoAdapter photoAdapter;
    protected RecyclerView photoRecView;
    protected RecyclerView textRecView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.account_fragment,container,false);
        nameTextView=v.findViewById(R.id.my_name_text);
        editButton = v.findViewById(R.id.edit_button);
        photoImageView = v.findViewById(R.id.photo_view);
        photoRecView =v.findViewById(R.id.photo_recycler_view);
        textRecView=v.findViewById(R.id.text_recycler_view);
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
        //AccountAdapter adapter=new AccountAdapter(getActivity(),hashMap);
        nameTextView.setText(user.getName()+" "+user.getSurname());
        ArrayList<String> tempListUser=new ArrayList<>();
        ArrayList<String> tempListLabels=new ArrayList<>();
        tempListUser.add(user.getCountry());
        if (!user.getRegion().isEmpty())
        tempListUser.add(user.getRegion());
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

    private void setGallery(List<String> urlPhotos){
        photoAdapter = new PhotoAdapter(getContext(),urlPhotos);
        photoRecView.setAdapter(photoAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(RecyclerView.HORIZONTAL);
        photoRecView.setLayoutManager(manager);
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
