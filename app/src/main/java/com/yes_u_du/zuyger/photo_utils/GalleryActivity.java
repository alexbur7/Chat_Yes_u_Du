package com.yes_u_du.zuyger.photo_utils;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.yes_u_du.zuyger.BaseActivity;
import com.yes_u_du.zuyger.R;
import com.yes_u_du.zuyger.account.User;

public class GalleryActivity extends BaseActivity {
    public static final  String USER_ID= "user_id";
    public static final String PHOTO_URL = "photo_url";
    public static final  String PHOTO_URL1= "photo_url1";
    public static final  String PHOTO_URL2= "photo_url2";
    public static final  String PHOTO_URL3= "photo_url3";

    @Override
    public Fragment getFragment() {
        if (getIntent().getStringExtra(USER_ID).equals(User.getCurrentUser().getUuid()))
        return MyGalleryFragment.newInstance(getIntent().getStringExtra(PHOTO_URL),getIntent().getStringExtra(PHOTO_URL1),getIntent().getStringExtra(PHOTO_URL2),getIntent().getStringExtra(PHOTO_URL3), getIntent().getStringExtra(USER_ID));
        else return UserGalleryFragment.newInstance(getIntent().getStringExtra(PHOTO_URL),getIntent().getStringExtra(PHOTO_URL1),getIntent().getStringExtra(PHOTO_URL2),getIntent().getStringExtra(PHOTO_URL3), getIntent().getStringExtra(USER_ID));
    }

    public static Intent newIntent(Context context, String userId,String photoUrl,String photoUrl1, String photoUrl2, String photoUrl3){
        Intent intent = new Intent(context, GalleryActivity.class);
        intent.putExtra(USER_ID,userId);
        intent.putExtra(PHOTO_URL, photoUrl);
        intent.putExtra(PHOTO_URL1,photoUrl1);
        intent.putExtra(PHOTO_URL2,photoUrl2);
        intent.putExtra(PHOTO_URL3,photoUrl3);
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
