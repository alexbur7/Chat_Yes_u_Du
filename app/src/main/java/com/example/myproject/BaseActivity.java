package com.example.myproject;

import android.os.Bundle;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.HashMap;

public abstract class BaseActivity extends AppCompatActivity {

    //protected Toolbar toolbar;

    public abstract Fragment getFragment();

    @LayoutRes
    public int getLayoutID(){
        return R.layout.fragment_activity;
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutID());
        //toolbar=findViewById(R.id.toolbarFr);
        //setSupportActionBar(toolbar);
        Fragment fragment;
        if (getSupportFragmentManager().findFragmentById(R.id.fragment_container)==null){
            fragment=getFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container,fragment)
                    .commit();
        }
    }


    protected void status(String status) {
        if (User.getCurrentUser() != null) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("status", status);
            if (status.equals("offline")){
                hashMap.put("online_time",(new Date()).getTime());
            }
            FirebaseDatabase.getInstance().getReference("users").child(User.getCurrentUser().getUuid()).updateChildren(hashMap);
        }
    }

}
