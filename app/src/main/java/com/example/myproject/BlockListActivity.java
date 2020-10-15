package com.example.myproject;

import androidx.fragment.app.Fragment;

public class BlockListActivity extends BaseActivity {
    @Override
    public Fragment getFragment() {
        return new BlockListFragment();
    }
}
