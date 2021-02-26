package com.yes_u_du.zuyger.account;

import android.content.Context;
import android.content.Intent;
import androidx.fragment.app.Fragment;

import com.yes_u_du.zuyger.BaseActivity;
import com.yes_u_du.zuyger.R;
import com.yes_u_du.zuyger.account.fragment.AdminAccountFragment;
import com.yes_u_du.zuyger.account.fragment.UserAccountFragment;

public class UserAccountActivity extends BaseActivity {
    @Override
    public Fragment getFragment() {
        if (User.getCurrentUser().getAdmin().equals("true")) {
            return AdminAccountFragment.newInstance(getIntent().getStringExtra(AdminAccountFragment.KEY_TO_RECEIVER_UUID));
        }
        return UserAccountFragment.newInstance(getIntent().getStringExtra(AdminAccountFragment.KEY_TO_RECEIVER_UUID));
    }

    public static Intent newIntent(Context context, String toUserUUID){
        Intent intent=new Intent(context,UserAccountActivity.class);
        intent.putExtra(AdminAccountFragment.KEY_TO_RECEIVER_UUID,toUserUUID);
        return intent;
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
