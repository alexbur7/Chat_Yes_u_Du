package com.yes_u_du.zuyger.chat_list.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.yes_u_du.zuyger.dialog.FilterDialog;
import com.yes_u_du.zuyger.R;
import com.yes_u_du.zuyger.chat_list.Updatable;

import java.util.ArrayList;

public abstract class ChatListFragment extends Fragment implements Updatable {

    protected static final int CODE_TO_FILTER_DIALOG=0;
    public static final int KEY_DELETE_DIAOG=-1;
    protected RecyclerView chatRecView;
    protected Toolbar toolbar;

    protected void getToolbarMenu(){
        toolbar.inflateMenu(R.menu.filter_users_menu);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.chat_users_list,container,false);
        chatRecView = v.findViewById(R.id.chat_recycler_view);
        toolbar=v.findViewById(R.id.toolbarFr);
        getToolbarMenu();
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return clickToolbarItems(item);
            }
        });
        setLayoutManagerForRecView();
        setChats();
        return v;
    }


    abstract void setLayoutManagerForRecView();

    protected boolean clickToolbarItems(MenuItem item){
        if (item.getItemId()==R.id.find_item) {
            FilterDialog dialog = new FilterDialog();
            dialog.setTargetFragment(this, CODE_TO_FILTER_DIALOG);
            dialog.show(getFragmentManager(), null);
        }
        return true;
    }

    protected abstract void setUsersFromChats(ArrayList<String> usersWithMsgId);

    protected abstract void setChats();
}
