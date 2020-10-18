package com.example.myproject;

import androidx.fragment.app.Fragment;

public class AdminActivity extends BaseActivity {
    @Override
    public Fragment getFragment() {
        return new AdminFragment();
    }
}
