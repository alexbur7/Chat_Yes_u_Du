package com.example.myproject;

import androidx.fragment.app.Fragment;



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
