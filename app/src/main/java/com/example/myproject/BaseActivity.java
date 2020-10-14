package com.example.myproject;

import android.os.Bundle;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

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

}
