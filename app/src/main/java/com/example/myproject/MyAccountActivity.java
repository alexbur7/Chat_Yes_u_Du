package com.example.myproject;

import androidx.fragment.app.Fragment;

public class MyAccountActivity extends BaseActivity implements AccountFragment.Callbacks{

    @Override
    public Fragment getFragment() {
        return new ChatAndAccPager();
    }

    @Override
    public void setEditFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new EditFragment()).commit();
    }
}
