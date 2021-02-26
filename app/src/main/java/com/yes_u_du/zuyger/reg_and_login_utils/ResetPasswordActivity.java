package com.yes_u_du.zuyger.reg_and_login_utils;

import androidx.fragment.app.Fragment;

import com.yes_u_du.zuyger.BaseActivity;
import com.yes_u_du.zuyger.R;

public class ResetPasswordActivity extends BaseActivity {
    @Override
    public Fragment getFragment() {
        return new ResetPasswordFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
        status(getResources().getString(R.string.label_online));
    }

    @Override
    protected void onPause() {
        super.onPause();
        status(getResources().getString(R.string.label_offline));
    }
}
