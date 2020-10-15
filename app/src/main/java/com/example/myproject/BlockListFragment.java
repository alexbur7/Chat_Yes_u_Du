package com.example.myproject;

import android.view.MenuItem;

public class BlockListFragment extends ChatListFragment {
    @Override
    protected void setChats() {

    }

    @Override
    protected void getToolbarMenu() {}

    @Override
    protected boolean clickToolbarItems(MenuItem item) {
        return false;
    }


}
