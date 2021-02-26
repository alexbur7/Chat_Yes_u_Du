package com.yes_u_du.zuyger.rules_and_policy;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.yes_u_du.zuyger.BaseActivity;
import com.yes_u_du.zuyger.R;

public class InformationActivity extends BaseActivity {
    private static final String KEY_INFORMATION= "informationText";

    @Override
    public Fragment getFragment() {
        return InformationFragment.newInstance(getIntent().getStringExtra(KEY_INFORMATION));
    }

    public static Intent newIntent(Context context,String informationText){
        Intent intent = new Intent(context, InformationActivity.class);
        intent.putExtra(KEY_INFORMATION,informationText);
        return intent;
    }
    @Override
    protected void onResume() {
        super.onResume();
        status(getResources().getString(R.string.label_online));
    }

    @Override
    protected void onPause() {
        super.onPause();
        status(getResources().getString(R.string.label_offline));
    }
}
