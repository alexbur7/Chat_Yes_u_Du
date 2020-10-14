package com.example.myproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

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
}