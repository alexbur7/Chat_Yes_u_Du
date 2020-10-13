package com.example.myproject;

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
    protected void onDestroy() {
        super.onDestroy();
        status("offline");
    }

    private void status(String status){
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        FirebaseDatabase.getInstance().getReference("users").child(User.getCurrentUser().getUuid()).updateChildren(hashMap);
    }
}
