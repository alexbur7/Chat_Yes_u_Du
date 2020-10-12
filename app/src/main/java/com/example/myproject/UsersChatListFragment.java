package com.example.myproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UsersChatListFragment extends Fragment {

    private int CODE_TO_FILTER_DIALOG=0;

    @BindView(R.id.chat_recycler_view)
    RecyclerView chatRecView;
    ChatRecViewAdapter adapter;
    //private Toolbar toolbar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.chat_users_list,container,false);
        ButterKnife.bind(this,v);
        chatRecView = v.findViewById(R.id.chat_recycler_view);
        //createUsers();
        //toolbar=v.findViewById(R.id.toolbarFr);
        //toolbar.inflateMenu(R.menu.filter_users_menu);
        chatRecView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        FilterDialog dialog=new FilterDialog();
        dialog.setTargetFragment(this,CODE_TO_FILTER_DIALOG);
        dialog.show(getFragmentManager(),null);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.filter_users_menu,menu);
    }

    /*private void createUsers(String name){
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<User> users = new ArrayList<>();
                        users.clear();
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            User user = snapshot1.getValue(User.class);
                            if (name!=null && user.getName().equals(name)) {
                                user.setUuid(snapshot1.getKey());
                                users.add(user);
                            }
                        }
                        ChatRecViewAdapter adapter = new ChatRecViewAdapter(users);
                        chatRecView.setAdapter(adapter);
                        //ref.removeEventListener(this);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }*/
    private void filterUsersByName(String name){
        System.out.println(name);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").orderByChild("name").getRef();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<User> users = new ArrayList<>();
                users.clear();
                //Log.e("NAME IS",name);
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    User user = snapshot1.getValue(User.class);
                    System.out.println(user.getName());
                    if (name!=null && user.getName().equals(name)) {
                        user.setUuid(snapshot1.getKey());
                        users.add(user);
                        System.out.println("GANVINA");
                    }
                }
                ChatRecViewAdapter adapter = new ChatRecViewAdapter(users);
                chatRecView.setAdapter(adapter);
                //ref.removeEventListener(this);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public class ChatHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private User user;
        TextView userName;
        TextView userDate;
        TextView userText;
        ImageView photoImageView;

        public ChatHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_name);
            userDate = itemView.findViewById(R.id.user_date);
            userText = itemView.findViewById(R.id.user_text);
            photoImageView = itemView.findViewById(R.id.circle_image_user);
            itemView.setOnClickListener(this);
        }

        void onBind(User user){
            this.user=user;
            userName.setText(user.getName());
            if (user.getPhoto_url().equals("default")){
                photoImageView.setImageResource(R.drawable.unnamed);
            }
            else{
                Glide.with(getContext()).load(user.getPhoto_url()).into(photoImageView);
            }
        }

        @Override
        public void onClick(View view) {
            Intent intent=ChatActivity.newIntent(getActivity(),user.getUuid(),user.getName(),user.getPhoto_url());
            startActivity(intent);
        }
    }

    public class ChatRecViewAdapter extends RecyclerView.Adapter<ChatHolder>{
        private List<User> userList;
        public ChatRecViewAdapter(List<User> list){
            this.userList=list;
        }

        @NonNull
        @Override
        public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v=LayoutInflater.from(getActivity()).inflate(R.layout.users_list_item,parent,false);
            return new ChatHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ChatHolder holder, int position) {
            holder.onBind(userList.get(position));
        }

        @Override
        public int getItemCount() {
            return userList.size();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode!= Activity.RESULT_OK) return;
        else{
            if (requestCode==CODE_TO_FILTER_DIALOG){
                String nameFilter=data.getStringExtra(FilterDialog.KEY_TO_NAME_FILTER);
                filterUsersByName(nameFilter);
            }
        }
    }
}