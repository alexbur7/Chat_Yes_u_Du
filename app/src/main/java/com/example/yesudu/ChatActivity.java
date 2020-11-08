package com.example.yesudu;

import androidx.fragment.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static com.example.yesudu.ChatFragment.KEY_TO_RECEIVER_PHOTO_URL;
import static com.example.yesudu.ChatFragment.KEY_TO_RECEIVER_UUID;


public class ChatActivity extends BaseActivity implements ChatFragment.CallBack {

    private static final String KEY_TO_VIEW_TYPE = "type";

    @Override
    public Fragment getFragment() {
        if (getIntent().getExtras().getInt(KEY_TO_VIEW_TYPE)==3){
            return ChatFragment.newInstance(getResources().getString(R.string.admin_key),"default");
        }
        else if (!(getIntent().getExtras().getInt(KEY_TO_VIEW_TYPE) == ChatRecViewAdapter.AdminChatHolder.VIEW_TYPE)) {
            Log.e("NORM FRAGMENT CREATED", String.valueOf(ChatActivity.class));
            return ChatFragment.newInstance(getIntent().getStringExtra(KEY_TO_RECEIVER_UUID), getIntent().getStringExtra(KEY_TO_RECEIVER_PHOTO_URL));
        }
        else {
            Log.e("ADMIN FRAGMENT CREATED", String.valueOf(ChatActivity.class));
            return AdminChatFragment.newInstance(getIntent().getStringExtra(KEY_TO_RECEIVER_UUID), getIntent().getStringExtra(KEY_TO_RECEIVER_PHOTO_URL));
        }
    }

    public static Intent newIntent(Context context, String toUserUUID, String photo_url, int viewType){
        Intent intent=new Intent(context,ChatActivity.class);
        intent.putExtra(KEY_TO_RECEIVER_UUID,toUserUUID);
        intent.putExtra(KEY_TO_RECEIVER_PHOTO_URL,photo_url);
        intent.putExtra(KEY_TO_VIEW_TYPE,viewType);
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

    @Override
    public void goToAdmin() {
        ChatFragment fragment= ChatFragment.newInstance(getResources().getString(R.string.admin_key),"default");
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).addToBackStack(null).commit();
    }
}