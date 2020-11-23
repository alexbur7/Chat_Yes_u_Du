package com.example.yesudu.chat_list.activity;

import androidx.fragment.app.Fragment;

import com.example.yesudu.BaseActivity;
import com.example.yesudu.R;
import com.example.yesudu.chat_list.fragment.BlockListFragment;

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
