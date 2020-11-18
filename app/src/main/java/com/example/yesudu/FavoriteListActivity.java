package com.example.yesudu;

import androidx.fragment.app.Fragment;

public class FavoriteListActivity extends BaseActivity {
    @Override
    public Fragment getFragment() {
        return new FavoriteListFragment();
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
