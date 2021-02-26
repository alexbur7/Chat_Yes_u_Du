package com.yes_u_du.zuyger.chat_list.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import com.yes_u_du.zuyger.dialog.FilterDialog;
import com.yes_u_du.zuyger.R;
import com.yes_u_du.zuyger.account.User;
import com.yes_u_du.zuyger.chat_list.ChatRecViewAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class FilteredChatListFragment extends ChatListFragment {

    public static final String KEY_TO_INTENT_DATA="key_to_data";
    public static final String FILTER_VIEW_TYPE = "filter_view";
    private static final String TYPE_HOLDER = "type_holder";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    void setLayoutManagerForRecView() {
        chatRecView.setLayoutManager(new GridLayoutManager(getActivity(),3));
    }

    @Override
    protected void setUsersFromChats(ArrayList<String> usersWithMsgId) { }

    @Override
    protected void setChats() {
        getFilterInfoAndFilter(getArguments().getParcelable(KEY_TO_INTENT_DATA));
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
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        ref.addValueEventListener(new ValueEventListener() {
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

                ChatRecViewAdapter adapter = new ChatRecViewAdapter(users,getActivity(),getFragmentManager(),ChatRecViewAdapter.ChatHolder.VIEW_TYPE,FILTER_VIEW_TYPE);
                chatRecView.setAdapter(adapter);
                ref.removeEventListener(this);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode!= Activity.RESULT_OK) return;
        else{
            switch (requestCode) {
                case CODE_TO_FILTER_DIALOG:getFilterInfoAndFilter(data);
            }
        }
    }


    public static FilteredChatListFragment newInstance(Intent data){
        FilteredChatListFragment fragment=new FilteredChatListFragment();
        Bundle args=new Bundle();
        args.putParcelable(KEY_TO_INTENT_DATA,data);
        //args.putSerializable(TYPE_HOLDER, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void update() {

    }
}