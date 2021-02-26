package com.yes_u_du.zuyger.chat_list.activity;

import androidx.fragment.app.Fragment;

import com.yes_u_du.zuyger.BaseActivity;
import com.yes_u_du.zuyger.R;
import com.yes_u_du.zuyger.chat_list.fragment.FavoriteListFragment;

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
