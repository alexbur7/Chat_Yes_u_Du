package com.example.myproject;

import androidx.fragment.app.Fragment;
import android.content.Context;
import android.content.Intent;
import static com.example.myproject.ChatFragment.KEY_TO_RECEIVER_PHOTO_URL;
import static com.example.myproject.ChatFragment.KEY_TO_RECEIVER_UUID;


public class ChatActivity extends BaseActivity {

    @Override
    public Fragment getFragment() {
        return ChatFragment.newInstance(getIntent().getStringExtra(KEY_TO_RECEIVER_UUID),
                getIntent().getStringExtra(KEY_TO_RECEIVER_PHOTO_URL));
    }

    public static Intent newIntent(Context context, String toUserUUID, String photo_url){
        Intent intent=new Intent(context,ChatActivity.class);
        intent.putExtra(KEY_TO_RECEIVER_UUID,toUserUUID);
        intent.putExtra(KEY_TO_RECEIVER_PHOTO_URL,photo_url);
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