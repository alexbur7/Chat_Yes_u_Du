package com.example.myproject;

import android.util.Log;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MyAccountActivity extends BaseActivity{

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
            FirebaseDatabase.getInstance().getReference("users").child(User.getCurrentUser().getUuid()).updateChildren(hashMap);
        }
    }
}
