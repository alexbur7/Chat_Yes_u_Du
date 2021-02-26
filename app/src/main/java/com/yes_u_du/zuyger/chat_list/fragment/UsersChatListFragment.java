package com.yes_u_du.zuyger.chat_list.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.yes_u_du.zuyger.dialog.FilterDialog;
import com.yes_u_du.zuyger.R;
import com.yes_u_du.zuyger.account.User;
import com.yes_u_du.zuyger.chat.ChatMessage;
import com.yes_u_du.zuyger.chat_list.ChatRecViewAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;


public class UsersChatListFragment extends ChatListFragment {

    private Callback activityCallBack;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    void setLayoutManagerForRecView() {
        chatRecView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activityCallBack= (UsersChatListFragment.Callback) context;
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onDetach() {
        super.onDetach();
        activityCallBack=null;
    }

    protected boolean clickToolbarItems(MenuItem item){
        if (item.getItemId()== R.id.find_item) {
            if (User.getCurrentUser().getAdmin_block().equals("unblock")) {
                FilterDialog dialog = new FilterDialog();
                dialog.setTargetFragment(this, CODE_TO_FILTER_DIALOG);
                dialog.show(getFragmentManager(), null);
            }
            else {
                Toast.makeText(getActivity(),R.string.blocked_by_admin,Toast.LENGTH_SHORT).show();
            }
        }
        return true;
    }

    protected void setChats(){
                FirebaseDatabase.getInstance().getReference("chats").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<String> usersID = new ArrayList<>();
                        usersID.clear();
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            String genKey=snapshot1.getKey();
                            for (DataSnapshot snapshot2:snapshot1.getChildren()) {
                                if (!snapshot2.getKey().equals("firstBlock") && !snapshot2.getKey().equals("secondBlock")) {
                                    for (DataSnapshot snapshot3 : snapshot2.getChildren()) {
                                         ChatMessage msg = snapshot3.getValue(ChatMessage.class);
                                        if (msg.getFromUserUUID().equals(FirebaseAuth.getInstance().getUid()) &&!(
                                                    msg.getFirstDelete().equals("delete") && genKey.startsWith(FirebaseAuth.getInstance().getUid()) ||
                                                            (msg.getSecondDelete().equals("delete") && !genKey.startsWith(FirebaseAuth.getInstance().getUid())))) {
                                            usersID.add(msg.getToUserUUID());
                                        }
                                        if (msg.getToUserUUID() != null && msg.getToUserUUID().equals(FirebaseAuth.getInstance().getUid()) &&!(
                                                msg.getFirstDelete().equals("delete") && genKey.startsWith(FirebaseAuth.getInstance().getUid()) ||
                                                        (msg.getSecondDelete().equals("delete") && !genKey.startsWith(FirebaseAuth.getInstance().getUid())))) {
                                            usersID.add(msg.getFromUserUUID());
                                        }
                                    }
                                }
                            }
                        }
                            setUsersFromChats(usersID);
                        }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }

                });
            }

    protected void setUsersFromChats(ArrayList<String> usersWithMsgId) {
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
                ChatRecViewAdapter adapter = new ChatRecViewAdapter(usersList,getActivity(),getFragmentManager(),ChatRecViewAdapter.ChatHolder.VIEW_TYPE);
                chatRecView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode!= Activity.RESULT_OK) return;
        else{
            if (requestCode == CODE_TO_FILTER_DIALOG) {
                activityCallBack.onUsersFilter(data);
            }
        }
    }

    @Override
    public void update() {
        setChats();
    }
    public interface Callback {
        void onUsersFilter(Intent data);
    }
}
