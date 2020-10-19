package com.example.myproject;

import androidx.fragment.app.Fragment;

public class ResetPasswordActivity extends BaseActivity {
    @Override
    public Fragment getFragment() {
        return new ResetPasswordFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }
}
