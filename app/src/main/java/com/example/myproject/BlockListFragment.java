package com.example.myproject;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class BlockListFragment extends ChatListFragment {

    private DatabaseReference reference;
    private ValueEventListener listener;

    private void setChatsFromMsg(){
        Log.e("ARGUMENTS SEEN","BY USERLIST");
        FirebaseDatabase.getInstance().getReference("chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> usersID = new ArrayList<>();
                usersID.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    String genKey=snapshot1.getKey();
                    for (DataSnapshot snapshot2:snapshot1.getChildren()) {
                        if (snapshot2.getKey().equals("firstBlock") && snapshot2.getValue().equals("block") && genKey.startsWith(User.getCurrentUser().getUuid()) ||
                            snapshot2.getKey().equals("secondBlock") && snapshot2.getValue().equals("block") && !genKey.startsWith(User.getCurrentUser().getUuid())) {
                            for (DataSnapshot snapshot3:snapshot1.getChildren()) {
                                if (!snapshot3.getKey().equals("firstBlock") && !snapshot3.getKey().equals("secondBlock")) {
                                    for (DataSnapshot snapshot4 : snapshot3.getChildren()) {
                                        ChatMessage msg = snapshot4.getValue(ChatMessage.class);
                                        Log.e("MESSAGE", String.valueOf(msg.getFromUserUUID() != null));
                                        if (msg.getFromUserUUID().equals(FirebaseAuth.getInstance().getUid())) {
                                            usersID.add(msg.getToUserUUID());
                                        }
                                        if (msg.getToUserUUID() != null && msg.getToUserUUID().equals(FirebaseAuth.getInstance().getUid())) {
                                            usersID.add(msg.getFromUserUUID());
                                        }
                                    }
                                }
                            }
                        }
                        //}
                    }
                }
                //ref.removeEventListener(this);
                if (usersID.size()!=0) setUsersFromChats(usersID);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }

        });
    }


    private void setUsersFromChats(ArrayList<String> usersWithMsgId) {
        ArrayList<User> usersList=new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                for (DataSnapshot snapshot1: snapshot.getChildren()){
                    User user=snapshot1.getValue(User.class);
                    user.setUuid(snapshot1.getKey());
                    for (String id:usersWithMsgId){
                        if (user.getUuid().equals(id)){
                            if (!usersList.contains(user)) usersList.add(user);
                        }
                    }
                }
                ChatRecViewAdapter adapter = new ChatRecViewAdapter(usersList,getActivity(),getFragmentManager());
                chatRecView.setAdapter(adapter);
                //ref.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }






    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        reference= FirebaseDatabase.getInstance().getReference("chats").child("");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void setChats() {
        setChatsFromMsg();
    }

    @Override
    protected void getToolbarMenu() { }

    @Override
    protected boolean clickToolbarItems(MenuItem item) {
        return false;
    }


}
