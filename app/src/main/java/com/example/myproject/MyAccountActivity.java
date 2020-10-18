package com.example.myproject;

import android.content.Intent;
import android.util.Log;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.HashMap;

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
        status("offline");
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    private void status(String status) {
        if (User.getCurrentUser() != null) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("status", status);
            if (status.equals("offline")){
                hashMap.put("online_time",(new Date()).getTime());
            }
            FirebaseDatabase.getInstance().getReference("users").child(User.getCurrentUser().getUuid()).updateChildren(hashMap);
        }
    }

    @Override
    public void onUsersFilter(Intent data) {
        Log.e("FILTER FRAGMENT","INCOMING");
        ChatAndAccPager fragment= (ChatAndAccPager) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        fragment.checkUsersFragment(CODE_FILTER,data);
    }

    @Override
    public void onBackPressed() {
        ChatAndAccPager fragment= (ChatAndAccPager) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment.getViewPager().getCurrentItem()==1 && fragment.TYPE_OF_LIST.equals("F"))
            fragment.checkUsersFragment(CODE_NO_FILTER,null);
        else super.onBackPressed();
    }
}
