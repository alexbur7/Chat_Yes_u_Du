package com.example.myproject;

import androidx.fragment.app.Fragment;

public class UserAccountActivity extends BaseActivity {
    @Override
    public Fragment getFragment() {
        return new MyAccountFragment();
    }
}
