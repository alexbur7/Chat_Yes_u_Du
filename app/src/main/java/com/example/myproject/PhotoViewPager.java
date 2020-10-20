package com.example.myproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

public class PhotoViewPager extends BaseActivity {
    private static final String LIST_PHOTO_KEY = "list_key";
    private static final String POSITION_PHOTO_KEY = "position_image";
    private ViewPager photoViewPager;
    private List<String> photoUrl;
    private int i;


    public static Intent newIntent(Context context, ArrayList<String> photoUrl, int position){
        Intent intent = new Intent(context,PhotoViewPager.class);
        intent.putStringArrayListExtra(LIST_PHOTO_KEY, photoUrl);
        intent.putExtra(POSITION_PHOTO_KEY,position);
        return intent;
    }

    @Override
    public Fragment getFragment() {
        return null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_view_pager);
        photoViewPager = findViewById(R.id.gallery_view_pager);
        photoUrl = getIntent().getStringArrayListExtra(LIST_PHOTO_KEY);
        i=getIntent().getIntExtra(POSITION_PHOTO_KEY,0);
        photoViewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager(),FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                PhotoViewPagerItemFragment fragment = PhotoViewPagerItemFragment.newInstance(photoUrl.get(position));
                return fragment;
            }


            @Override
            public int getCount() {
                return photoUrl.size();
            }
        });
        photoViewPager.setCurrentItem(i);
    }

    @Override
    protected void onPause() {
        super.onPause();
        status(getResources().getString(R.string.label_offline));
    }

    @Override
    protected void onResume() {
        super.onResume();
        status(getResources().getString(R.string.label_online));
    }
}