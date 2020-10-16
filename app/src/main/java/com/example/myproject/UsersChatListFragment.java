package com.example.myproject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class UsersChatListFragment extends ChatListFragment{

    private Callback activityCallBack;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e("Fragment created:","USERCHATLIST");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activityCallBack= (UsersChatListFragment.Callback) context;
        Log.e("ON ATTACH", "SET CURRENT USER");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("ON RESUME", "SET CURRENT USER");
    }


    @Override
    public void onDetach() {
        super.onDetach();
        activityCallBack=null;
    }

    protected boolean clickToolbarItems(MenuItem item){
        if (item.getItemId()==R.id.find_item) {
            FilterDialog dialog = new FilterDialog();
            dialog.setTargetFragment(this, CODE_TO_FILTER_DIALOG);
            dialog.show(getFragmentManager(), null);
        }
        return true;
    }

    protected void setChats(){
        Log.e("ARGUMENTS SEEN","BY USERLIST");
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
                                        Log.e("MESSAGE", String.valueOf(msg.getFromUserUUID()!=null));
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
                        //ref.removeEventListener(this);
                       // if (usersID.size()!=0)
                            setUsersFromChats(usersID);
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
                ChatRecViewAdapter adapter = new ChatRecViewAdapter(usersList,getActivity(),getFragmentManager(),ChatRecViewAdapter.ChatHolder.VIEW_TYPE);
                chatRecView.setAdapter(adapter);
                //ref.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode!= Activity.RESULT_OK) return;
        else{
            switch (requestCode){
                case CODE_TO_FILTER_DIALOG:activityCallBack.onUsersFilter(data);
                //case KEY_DELETE_DIAOG:setChats();
            }
        }
    }

    @Override
    public void update() {
        setChats();
    }

    /*private void filterUsers(Intent data) {
        String nameFilter=data.getStringExtra(FilterDialog.KEY_TO_NAME_FILTER);
        String sexFilter=data.getStringExtra(FilterDialog.KEY_TO_SEX_FILTER);
        String ageFilter=data.getStringExtra(FilterDialog.KEY_TO_AGE_FILTER);
        String cityFilter=data.getStringExtra(FilterDialog.KEY_TO_CITY_FILTER);
        String onlineFilter=data.getStringExtra(FilterDialog.KEY_TO_ONLINE_FILTER);
        String photoFilter=data.getStringExtra(FilterDialog.KEY_TO_PHOTO_FILTER);
        filterUsers(nameFilter,sexFilter,ageFilter,cityFilter,onlineFilter,photoFilter);
    }*/
    public interface Callback {
        void onUsersFilter(Intent data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
