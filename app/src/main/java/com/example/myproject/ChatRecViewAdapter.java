package com.example.myproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatRecViewAdapter extends RecyclerView.Adapter<ChatRecViewAdapter.ChatHolder>{
    private List<User> userList;

    private Context context;
    private FragmentManager fragmentManager;

    public ChatRecViewAdapter(List<User> list, Context context, FragmentManager manager){
        this.userList=list;
        this.context=context;
        this.fragmentManager=manager;
    }

    private void setLastMsg(String id, TextView view){
        FirebaseDatabase.getInstance().getReference("message").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1:snapshot.getChildren())
                    for (DataSnapshot snapshot2:snapshot1.getChildren()){
                        ChatMessage message=snapshot2.getValue(ChatMessage.class);
                        if (User.getCurrentUser().getUuid().equals(generateKey(id))) {
                            if ((message.getToUserUUID().equals(User.getCurrentUser().getUuid()) && message.getFromUserUUID().equals(id) ||
                                    message.getToUserUUID().equals(id) && message.getFromUserUUID().equals(User.getCurrentUser().getUuid()))
                                    && !message.getFirstDelete().equals("delete")) {
                                view.setText(message.getMessageText());
                            }
                        }
                        else {
                            if ((message.getToUserUUID().equals(User.getCurrentUser().getUuid()) && message.getFromUserUUID().equals(id) ||
                                    message.getToUserUUID().equals(id) && message.getFromUserUUID().equals(User.getCurrentUser().getUuid())) && !message.getSecondDelete().equals("delete")) {
                                view.setText(message.getMessageText());
                            }
                        }
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private String generateKey(String receiverUuid){
        ArrayList<String> templist=new ArrayList<>();
        templist.add(User.getCurrentUser().getUuid());
        templist.add(receiverUuid);
        Collections.sort(templist);
        String firstKey=templist.get(0);
        return firstKey;
    }

    @NonNull
    @Override
    public ChatRecViewAdapter.ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.users_list_item,parent,false);
        return new ChatRecViewAdapter.ChatHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatHolder holder, int position) {
        holder.onBind(userList.get(position));
        if (User.getCurrentUser()!=null) {
            setLastMsg(holder.user.getUuid(), holder.userText);
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }


    ///////////////////////////////////////////////////
    public class ChatHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private User user;
        TextView userName;
        TextView userDate;
        TextView userText;
        TextView userStatus;
        ImageView photoImageView;

        public ChatHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_name);
            userDate = itemView.findViewById(R.id.user_date);
            userText = itemView.findViewById(R.id.user_text);
            userStatus = itemView.findViewById(R.id.text_online_list);
            photoImageView = itemView.findViewById(R.id.circle_image_user);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        void onBind(User user){
            this.user=user;
            userName.setText(user.getName());
            if (user.getPhoto_url().equals("default")){
                photoImageView.setImageResource(R.drawable.unnamed);
            }
            else{
                Glide.with(context).load(user.getPhoto_url()).into(photoImageView);
            }
            if (user.getStatus().equals("online")){
                userStatus.setText("online");
            }
            else userStatus.setText("offline");
        }

        @Override
        public void onClick(View view) {
            Intent intent = ChatActivity.newIntent(context, user.getUuid(), user.getPhoto_url());
            context.startActivity(intent);
        }

        @Override
        public boolean onLongClick(View v) {
            Log.e("LONG TOUCH", "TOOOOOUCh");
            DeleteChatDialog deleteChatDialog = new DeleteChatDialog(user.getUuid());
            deleteChatDialog.show(fragmentManager,null);
            return true;
        }
    }

}