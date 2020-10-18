package com.example.myproject;

import androidx.fragment.app.Fragment;

public class AdminActivity extends BaseActivity {
    @Override
    public Fragment getFragment() {
        return new AdminFragment();
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }
}
