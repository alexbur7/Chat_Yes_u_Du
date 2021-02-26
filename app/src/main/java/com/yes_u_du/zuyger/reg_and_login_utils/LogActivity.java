package com.yes_u_du.zuyger.reg_and_login_utils;

import androidx.fragment.app.Fragment;

import com.yes_u_du.zuyger.BaseActivity;
import com.yes_u_du.zuyger.R;


public class LogActivity extends BaseActivity implements LoginFragment.Callback, RegisterFragment.Callbacks {

    @Override
    public Fragment getFragment() {
        return LoginFragment.newFragment(null,null);
    }
    

    @Override
    public void onRegisterClicked() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container,new RegisterFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void returnLoginFragment(String email,String password) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container,LoginFragment.newFragment(email,password))
                .commit();
    }
}
