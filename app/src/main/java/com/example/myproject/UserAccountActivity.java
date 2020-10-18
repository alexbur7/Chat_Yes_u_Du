package com.example.myproject;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;



public class UserAccountActivity extends BaseActivity {
    @Override
    public Fragment getFragment() {
        if (User.getCurrentUser().getAdmin().equals("true")) {
            return AdminAccountFragment.newInstance(getIntent().getStringExtra(AdminAccountFragment.KEY_TO_RECEIVER_UUID));
        }
        return null;
    }

    public static Intent newIntent(Context context, String toUserUUID){
        Intent intent=new Intent(context,UserAccountActivity.class);
        intent.putExtra(AdminAccountFragment.KEY_TO_RECEIVER_UUID,toUserUUID);
        return intent;
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
}
