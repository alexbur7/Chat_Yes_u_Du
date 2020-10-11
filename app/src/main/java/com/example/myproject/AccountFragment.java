package com.example.myproject;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AccountFragment extends Fragment {
    private ImageView photoImageView;
    private TextView countryTextView;
    private TextView regionTextView;
    private TextView cityTextView;
    private TextView nameTextView;
    private TextView ageTextView;
    private TextView sexTextView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.account_fragment,container,false);
        photoImageView = v.findViewById(R.id.photo_view);
        countryTextView = v.findViewById(R.id.country_textView);
        regionTextView = v.findViewById(R.id.region_textView);
        cityTextView = v.findViewById(R.id.city_textView);
        nameTextView = v.findViewById(R.id.my_name_text);
        ageTextView = v.findViewById(R.id.age_textView);
        sexTextView = v.findViewById(R.id.sex_textView);
        setCurrentUser();
        ButterKnife.bind(this,v);
        return v;
    }

    private void setAllTextView(){
        countryTextView.setText(User.getCurrentUser().getCountry());
        regionTextView.setText(User.getCurrentUser().getRegion());
        cityTextView.setText(User.getCurrentUser().getCity());
        ageTextView.setText(String.valueOf(User.getCurrentUser().getAge()));
        sexTextView.setText(User.getCurrentUser().getSex());
        nameTextView.setText(User.getCurrentUser().getName()+" "+User.getCurrentUser().getSurname());
    }

    private void setCurrentUser() {
        String uuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.e("USER CHECK IN", uuid);
        FirebaseDatabase.getInstance().getReference("users").child(uuid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User.setCurrentUser(snapshot.getValue(User.class), uuid);
                setAllTextView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }

        });
    }
}
