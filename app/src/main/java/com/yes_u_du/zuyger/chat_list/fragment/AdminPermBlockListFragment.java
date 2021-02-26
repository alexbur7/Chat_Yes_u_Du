package com.yes_u_du.zuyger.chat_list.fragment;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;

import com.yes_u_du.zuyger.account.User;
import com.yes_u_du.zuyger.chat_list.ChatRecViewAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminPermBlockListFragment extends ChatListFragment {

    public static final int BLOCK_CODE =-10;
    private DatabaseReference mReference;

    @Override
    void setLayoutManagerForRecView() {
        chatRecView.setLayoutManager(new GridLayoutManager(getActivity(),3));
    }


    //chats
    //setUsersFromChats(usersID);
    @Override
    protected void setChats() {
        FirebaseDatabase.getInstance().getReference("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> usersID = new ArrayList<>();
                usersID.clear();
                for (DataSnapshot snapshot1:snapshot.getChildren()){
                    User user=snapshot1.getValue(User.class);
                    user.setUuid(snapshot1.getKey());
                    Log.d("NULL?", String.valueOf(user.getPerm_block()==null));
                    if (user.getPerm_block().equals("block")) usersID.add(user.getUuid());
                }
                setUsersFromChats(usersID);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
//ну я и ебач
    protected void setUsersFromChats(ArrayList<String> arrayList){
        ArrayList<User> usersList=new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                for (DataSnapshot snapshot1: snapshot.getChildren()){
                    User user=snapshot1.getValue(User.class);
                    user.setUuid(snapshot1.getKey());
                    for (String id:arrayList){
                        if (user.getUuid().equals(id)){
                            if (!usersList.contains(user)) usersList.add(user);
                        }
                    }
                }
                ChatRecViewAdapter adapter = new ChatRecViewAdapter(usersList,getActivity(),getFragmentManager(),ChatRecViewAdapter.AdminBanListHolder.VIEW_TYPE,BLOCK_CODE);
                chatRecView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

    }

    @Override
    public void update() {

    }
}
