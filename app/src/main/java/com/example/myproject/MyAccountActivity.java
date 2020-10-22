package com.example.myproject;

import android.content.Intent;
import android.util.Log;
import androidx.fragment.app.Fragment;


public class MyAccountActivity extends BaseActivity implements UsersChatListFragment.Callback{

    public static final int CODE_NO_FILTER=0;
    public static final int CODE_FILTER=1;


    @Override
    public Fragment getFragment() {
        return new ChatAndAccPager();
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

    @Override
    public void onUsersFilter(Intent data) {
        ChatAndAccPager fragment= (ChatAndAccPager) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        fragment.checkUsersFragment(CODE_FILTER,data);
    }

    @Override
    public void onBackPressed() {
            ChatAndAccPager fragment = (ChatAndAccPager) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (fragment.getViewPager().getCurrentItem() == 1 && fragment.TYPE_OF_LIST.equals("F"))
                fragment.checkUsersFragment(CODE_NO_FILTER, null);
            else super.onBackPressed();
        }
}
