package com.example.yesudu.chat;

import androidx.fragment.app.Fragment;
import android.content.Context;
import android.content.Intent;

import com.example.yesudu.BaseActivity;
import com.example.yesudu.chat_list.ChatRecViewAdapter;
import com.example.yesudu.photo_utils.PhotoViewPagerItemFragment;
import com.example.yesudu.R;

import static com.example.yesudu.chat.ChatFragment.KEY_TO_RECEIVER_PHOTO_URL;
import static com.example.yesudu.chat.ChatFragment.KEY_TO_RECEIVER_UUID;


public class ChatActivity extends BaseActivity implements ChatFragment.CallBack {

    private static final String KEY_TO_VIEW_TYPE = "type";

    @Override
    public Fragment getFragment() {
        if (getIntent().getExtras().getInt(KEY_TO_VIEW_TYPE)==3){
            return ChatFragment.newInstance(getResources().getString(R.string.admin_key),"default");
        }
        else if (!(getIntent().getExtras().getInt(KEY_TO_VIEW_TYPE) == ChatRecViewAdapter.AdminChatHolder.VIEW_TYPE)) {
            return ChatFragment.newInstance(getIntent().getStringExtra(KEY_TO_RECEIVER_UUID), getIntent().getStringExtra(KEY_TO_RECEIVER_PHOTO_URL));
        }
        else {
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
    public void onBackPressed() {
        if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof ChatBaseFragment && ((ChatBaseFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container)).isEditing) {
            ChatBaseFragment baseFragment = (ChatBaseFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            baseFragment.setupEditCancel();
        }
        else if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof PhotoViewPagerItemFragment) {
            ((PhotoViewPagerItemFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container)).getImageViewFromChat().setEnabled(true);
            super.onBackPressed();
        }
        else super.onBackPressed();
    }

    @Override
    public void goToAdmin() {
        ChatFragment fragment= ChatFragment.newInstance(getResources().getString(R.string.admin_key),"default");
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).addToBackStack(null).commit();
    }
}