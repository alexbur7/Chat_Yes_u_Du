package com.example.myproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class AdminFragment extends ChatListFragment{

    private DatabaseReference reference;
    private ValueEventListener userListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        reference=FirebaseDatabase.getInstance().getReference("users");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void setChats() {
        ArrayList<User> usersList=new ArrayList<>();
        userListener=reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                for (DataSnapshot snapshot1:snapshot.getChildren()){
                    User user=snapshot1.getValue(User.class);
                    user.setUuid(snapshot1.getKey());
                    usersList.add(user);
                }
                ChatRecViewAdapter adapter = new ChatRecViewAdapter(usersList,getActivity(),getFragmentManager(),ChatRecViewAdapter.ChatHolder.VIEW_TYPE);
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
        Log.e("ARGUMENTS ON FILTER",data.getStringExtra(FilterDialog.KEY_TO_SEX_FILTER));
        String nameFilter=data.getStringExtra(FilterDialog.KEY_TO_NAME_FILTER);
        String sexFilter=data.getStringExtra(FilterDialog.KEY_TO_SEX_FILTER);
        String ageFilter=data.getStringExtra(FilterDialog.KEY_TO_AGE_FILTER);
        String cityFilter=data.getStringExtra(FilterDialog.KEY_TO_CITY_FILTER);
        String onlineFilter=data.getStringExtra(FilterDialog.KEY_TO_ONLINE_FILTER);
        String photoFilter=data.getStringExtra(FilterDialog.KEY_TO_PHOTO_FILTER);
        filterUsers(nameFilter,sexFilter,ageFilter,cityFilter,onlineFilter,photoFilter);
    }

    private void filterUsers(String nameFilter, String sexFilter, String ageFilter, String cityFilter, String onlineFilter,String photoFilter){
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
                    filterUsersByPhoto(users,user);
                }
                ChatRecViewAdapter adapter = new ChatRecViewAdapter(users,getActivity(),getFragmentManager(),ChatRecViewAdapter.ChatHolder.VIEW_TYPE);
                chatRecView.setAdapter(adapter);
                reference.removeEventListener(this);
            }

            private void filterUsersByPhoto(ArrayList<User> users, User user){
                System.out.println(user.getPhoto_url());
                System.out.println(photoFilter);
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
            switch (requestCode) {
                case CODE_TO_FILTER_DIALOG:getFilterInfoAndFilter(data);
            }
        }
    }

    @Override
    public void update() {}
}