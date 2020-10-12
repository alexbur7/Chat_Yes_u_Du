package com.example.myproject;

import androidx.fragment.app.Fragment;

public class MyAccountActivity extends BaseActivity{

    @Override
    public Fragment getFragment() {
        return new ChatAndAccPager();
    }

}
