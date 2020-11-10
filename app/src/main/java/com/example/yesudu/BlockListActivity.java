package com.example.yesudu;

import androidx.fragment.app.Fragment;

public class BlockListActivity extends BaseActivity {
    @Override
    public Fragment getFragment() {
        return new BlockListFragment();
    }

    @Override
    protected void onPause() {
        super.onPause();
        status(getResources().getString(R.string.label_offline));
    }

    @Override
    protected void onResume() {
        super.onResume();
        status(getResources().getString(R.string.label_online));
    }
}
