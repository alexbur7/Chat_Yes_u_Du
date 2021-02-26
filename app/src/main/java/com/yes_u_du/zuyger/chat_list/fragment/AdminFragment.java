package com.yes_u_du.zuyger.chat_list.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.yes_u_du.zuyger.dialog.FilterDialog;
import com.yes_u_du.zuyger.R;
import com.yes_u_du.zuyger.account.User;
import com.yes_u_du.zuyger.chat.ChatMessage;
import com.yes_u_du.zuyger.chat_list.ChatRecViewAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class AdminFragment extends ChatListFragment {

    private DatabaseReference reference;
    private ValueEventListener userListener;
    private String admin_key_string;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        reference = FirebaseDatabase.getInstance().getReference("users");
        admin_key_string = getResources().getString(R.string.admin_key);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isAdded()) {
            toolbar.setTitle(getActivity().getString(R.string.panel_admin));
            toolbar.setBackground(null);
            toolbar.setBackgroundResource(R.color.colorToolbar);
        }
    }

    @Override
    void setLayoutManagerForRecView() {
        chatRecView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    protected boolean clickToolbarItems(MenuItem item) {
        if (item.getItemId()==R.id.find_item){

        }
        return super.clickToolbarItems(item);
    }

    protected void setChats(){
        userListener=FirebaseDatabase.getInstance().getReference("chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> usersID = new ArrayList<>();
                usersID.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    //String genKey=snapshot1.getKey();
                    // usersID.add(msg.getToUserUUID());
                    for (DataSnapshot snapshot2:snapshot1.getChildren()) {
                        if (!snapshot2.getKey().equals("firstBlock") && !snapshot2.getKey().equals("secondBlock")) {
                            for (DataSnapshot snapshot3 : snapshot2.getChildren()) {
                                ChatMessage msg=snapshot3.getValue(ChatMessage.class);
                                if (msg.getFromUserUUID().equals(admin_key_string)) usersID.add(msg.getToUserUUID());
                                if (msg.getToUserUUID().equals(admin_key_string)) usersID.add(msg.getFromUserUUID());
                            }
                        }
                    }
                }
                setUsersFromChats(usersID);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
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
                ChatRecViewAdapter adapter = new ChatRecViewAdapter(usersList,getActivity(),getFragmentManager(),ChatRecViewAdapter.AdminChatHolder.VIEW_TYPE);
                chatRecView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        reference.removeEventListener(userListener);
    }

    private void getFilterInfoAndFilter(Intent data) {
        String nameFilter=data.getStringExtra(FilterDialog.KEY_TO_NAME_FILTER);
        String sexFilter=data.getStringExtra(FilterDialog.KEY_TO_SEX_FILTER);
        String ageFilter=data.getStringExtra(FilterDialog.KEY_TO_AGE_FILTER);
        String cityFilter=data.getStringExtra(FilterDialog.KEY_TO_CITY_FILTER);
        String onlineFilter=data.getStringExtra(FilterDialog.KEY_TO_ONLINE_FILTER);
        String photoFilter=data.getStringExtra(FilterDialog.KEY_TO_PHOTO_FILTER);
        String countryFilter=data.getStringExtra(FilterDialog.KEY_TO_COUNTRY_FILTER);
        String regionFilter=data.getStringExtra(FilterDialog.KEY_TO_REGION_FILTER);
        filterUsers(nameFilter,sexFilter,ageFilter,cityFilter,onlineFilter,photoFilter,countryFilter,regionFilter);
    }

    private void filterUsers(String nameFilter, String sexFilter, String ageFilter, String cityFilter, String onlineFilter, String photoFilter, String countryFilter, String regionFilter){
        reference = FirebaseDatabase.getInstance().getReference("users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<User> users = new ArrayList<>();
                users.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    User user = snapshot1.getValue(User.class);
                    user.setUuid(snapshot1.getKey());
                    users.add(user);
                    filterUsersByName(users, user);
                    filterUserBySex(users, user);
                    filterUsersByAge(users, user);
                    filterUsersByCity(users, user);
                    filterUsersByOnline(users, user);
                    filterUsersByCountry(users,user);
                    filterUsersByRegion(users,user);
                    //filterUsersByPhoto(users,user);
                }
                ChatRecViewAdapter adapter = new ChatRecViewAdapter(users,getActivity(),getFragmentManager(),ChatRecViewAdapter.AdminChatHolder.VIEW_TYPE);
                chatRecView.setAdapter(adapter);
                reference.removeEventListener(this);
            }

            private void filterUsersByRegion(ArrayList<User> users, User user) {
                if (!regionFilter.equals("0")){
                    if (!user.getRegion().equals(regionFilter)){
                        users.remove(user);
                    }
                }
            }

            private void filterUsersByCountry(ArrayList<User> users, User user) {
                if (!countryFilter.equals("0")){
                    if (!user.getCountry().equals(countryFilter)){
                        users.remove(user);
                    }
                }
            }

            private void filterUsersByPhoto(ArrayList<User> users, User user){
                if (!photoFilter.isEmpty()){
                    if (user.getPhoto_url().equals("default")){
                        users.remove(user);
                    }
                }
            }

            private void filterUsersByOnline(ArrayList<User> users, User user) {
                if (!onlineFilter.isEmpty()) {
                    if (!(user.getStatus().equals(onlineFilter))) {
                        users.remove(user);
                    }
                }
            }

            private void filterUsersByCity(ArrayList<User> users, User user) {
                if (!(cityFilter.isEmpty())){
                    if (!user.getCity().equals(cityFilter)) {
                        users.remove(user);
                    }
                }
            }

            private void filterUsersByAge(ArrayList<User> users, User user) {
                if (ageFilter != null) {
                    if (ageFilter.equals(getResources().getStringArray(R.array.age_for_spinner)[0]) && !(Integer.parseInt(user.getAge()) < 18)) {
                        users.remove(user);
                    } else if (ageFilter.equals(getResources().getStringArray(R.array.age_for_spinner)[1]) && !(Integer.parseInt(user.getAge()) >= 18 && Integer.parseInt(user.getAge()) < 30)) {
                        users.remove(user);
                    } else if (ageFilter.equals(getResources().getStringArray(R.array.age_for_spinner)[2]) && !(Integer.parseInt(user.getAge()) >= 30 && Integer.parseInt(user.getAge()) < 45)) {
                        users.remove(user);
                    } else if (ageFilter.equals(getResources().getStringArray(R.array.age_for_spinner)[3]) && !(Integer.parseInt(user.getAge()) >= 45 && Integer.parseInt(user.getAge()) < 60)) {
                        users.remove(user);
                    } else if (ageFilter.equals(getResources().getStringArray(R.array.age_for_spinner)[4]) && !(Integer.parseInt(user.getAge()) >= 60)) {
                        users.remove(user);
                    }
                }
            }

            private void filterUserBySex(ArrayList<User> users, User user) {
                if (!sexFilter.isEmpty()) {
                    if (!(user.getSex().equals(sexFilter))) {
                        users.remove(user);
                    }
                }
            }

            private void filterUsersByName(ArrayList<User> users, User user) {
                if (!nameFilter.isEmpty()){
                    if (!(user.getName().equals(nameFilter))) {
                        users.remove(user);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }

        });
    }



    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode!= Activity.RESULT_OK) return;
        else{
            if (requestCode == CODE_TO_FILTER_DIALOG) {
                getFilterInfoAndFilter(data);
            }
        }
    }

    @Override
    public void update() {

    }
}