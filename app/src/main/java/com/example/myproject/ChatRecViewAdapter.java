package com.example.myproject;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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
    private int viewType;

    public ChatRecViewAdapter(List<User> list, Context context, FragmentManager manager,int viewType){
        this.userList=list;
        this.context=context;
        this.fragmentManager=manager;
        this.viewType=viewType;
    }

    @NonNull
    @Override
    public ChatRecViewAdapter.ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.users_list_item, parent, false);
        switch (viewType) {
            case ChatHolder.VIEW_TYPE: return new ChatRecViewAdapter.ChatHolder(v, context, fragmentManager);
            case BlockListHolder.VIEW_TYPE: return new BlockListHolder(v,context,fragmentManager);
            case AdminChatHolder.VIEW_TYPE: return new AdminChatHolder(v,context,fragmentManager);
            default: throw new NullPointerException("HOLDER TYPE IS INVALID");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ChatHolder holder, int position) {
        holder.onBind(userList.get(position));
        if (User.getCurrentUser()!=null) {
            holder.setLastMsg(holder.user.getUuid(), holder.userText);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return viewType;
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }


    public static class ChatHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public static final int VIEW_TYPE=0;

        protected User user;
        private TextView userName;
        private TextView userDate;
        private TextView userText;
        private TextView userStatus;
        private ImageView photoImageView;
        protected Context context;
        protected FragmentManager fragmentManager;

        public ChatHolder(@NonNull View itemView,Context context,FragmentManager manager) {
            super(itemView);
            this.context=context;
            this.fragmentManager=manager;
            userName = itemView.findViewById(R.id.user_name);
            userDate = itemView.findViewById(R.id.user_date);
            userText = itemView.findViewById(R.id.user_text);
            userStatus = itemView.findViewById(R.id.text_online_list);
            photoImageView = itemView.findViewById(R.id.circle_image_user);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        protected String generateKey(String receiverUuid){
            ArrayList<String> templist=new ArrayList<>();
            templist.add(User.getCurrentUser().getUuid());
            templist.add(receiverUuid);
            Collections.sort(templist);
            String firstKey=templist.get(0);
            return firstKey;
        }

        public void setLastMsg(String id, TextView view){
            FirebaseDatabase.getInstance().getReference("chats").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snapshot1:snapshot.getChildren())
                        for (DataSnapshot snapshot2:snapshot1.getChildren()){
                            if (!snapshot2.getKey().equals("firstBlock") && !snapshot2.getKey().equals("secondBlock")) {
                                for (DataSnapshot snapshot3 : snapshot2.getChildren()) {
                                    ChatMessage message = snapshot3.getValue(ChatMessage.class);
                                    //если я первый
                                    if (User.getCurrentUser().getUuid().equals(generateKey(id))) {
                                        if ((message.getToUserUUID().equals(User.getCurrentUser().getUuid()) && message.getFromUserUUID().equals(id) ||
                                                message.getToUserUUID().equals(id) && message.getFromUserUUID().equals(User.getCurrentUser().getUuid()))
                                        ) {
                                            if (message.getFirstDelete().equals("delete")){
                                                view.setText("");
                                            }
                                            else
                                                view.setText(message.getMessageText());
                                        }
                                    } else {
                                        if ((message.getToUserUUID().equals(User.getCurrentUser().getUuid()) && message.getFromUserUUID().equals(id) ||
                                                message.getToUserUUID().equals(id) && message.getFromUserUUID().equals(User.getCurrentUser().getUuid())) ) {
                                            if (message.getSecondDelete().equals("delete")){
                                                view.setText("");
                                            }
                                            else
                                                view.setText(message.getMessageText());
                                        }
                                    }
                                }
                            }
                        }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

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
            if (user.getStatus().equals(context.getResources().getString(R.string.label_online))){
                userStatus.setText(context.getResources().getString(R.string.label_online));
            }
            else userStatus.setText(context.getResources().getString(R.string.label_offline));
        }

        @Override
        public void onClick(View view) {
            Log.e("CHATHOLDER","clicked");
            Intent intent = ChatActivity.newIntent(context, user.getUuid(), user.getPhoto_url(), VIEW_TYPE);
            context.startActivity(intent);
        }

        @Override
        public boolean onLongClick(View v) {
            DeleteChatDialog deleteChatDialog = new DeleteChatDialog(user.getUuid(),userText.getText().toString().isEmpty());
            Fragment fragment= fragmentManager.findFragmentById(R.id.fragment_container);
            deleteChatDialog.setTargetFragment(fragment,ChatListFragment.KEY_DELETE_DIAOG);
            deleteChatDialog.show(fragmentManager,null);
            return true;
        }
    }

    public static class AdminChatHolder extends ChatRecViewAdapter.ChatHolder{

        public static final int VIEW_TYPE=2;

        public AdminChatHolder(@NonNull View itemView, Context context, FragmentManager manager) {
            super(itemView, context, manager);
        }

        @Override
        public void onClick(View view) {
            Log.e("USER UUID",user.getUuid());
            Intent intent = ChatActivity.newIntent(context, user.getUuid(), user.getPhoto_url(),VIEW_TYPE);
            context.startActivity(intent);
        }

        @Override
        public void setLastMsg(String id, TextView view) {
            FirebaseDatabase.getInstance().getReference("chats").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snapshot1:snapshot.getChildren())
                        for (DataSnapshot snapshot2:snapshot1.getChildren()){
                            if (!snapshot2.getKey().equals("firstBlock") && !snapshot2.getKey().equals("secondBlock")) {
                                for (DataSnapshot snapshot3 : snapshot2.getChildren()) {
                                    ChatMessage message = snapshot3.getValue(ChatMessage.class);
                                    //если я первый
                                    if (context.getResources().getString(R.string.admin_key).equals(generateKey(id))) {
                                        if ((message.getToUserUUID().equals(context.getResources().getString(R.string.admin_key)) && message.getFromUserUUID().equals(id) ||
                                                message.getToUserUUID().equals(id) && message.getFromUserUUID().equals(context.getResources().getString(R.string.admin_key)))) {
                                            if (message.getFirstDelete().equals("delete")){
                                                view.setText("");
                                            }
                                            else
                                                view.setText(message.getMessageText());
                                        }
                                    } else {
                                        if ((message.getToUserUUID().equals(context.getResources().getString(R.string.admin_key)) && message.getFromUserUUID().equals(id) ||
                                                message.getToUserUUID().equals(id) && message.getFromUserUUID().equals(context.getResources().getString(R.string.admin_key))) ) {
                                            if (message.getSecondDelete().equals("delete")){
                                                view.setText("");
                                            }
                                            else
                                                view.setText(message.getMessageText());
                                        }
                                    }
                                }
                            }
                        }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

        @Override
        protected String generateKey(String receiverUuid) {
            ArrayList<String> templist=new ArrayList<>();
            templist.add(context.getResources().getString(R.string.admin_key));
            templist.add(receiverUuid);
            Collections.sort(templist);
            String firstKey=templist.get(0);
            return firstKey;
        }
    }

    public static class BlockListHolder extends ChatRecViewAdapter.ChatHolder{
        public static final int VIEW_TYPE=1;

        public BlockListHolder(@NonNull View itemView, Context context, FragmentManager manager) {
            super(itemView, context, manager);
        }

        @Override
        public boolean onLongClick(View v) {
            UnblockDialog dialog=new UnblockDialog(user.getUuid());
            Fragment fragment= fragmentManager.findFragmentById(R.id.fragment_container);
            dialog.setTargetFragment(fragment,BlockListFragment.KEY_TO_UNBLOCK);
            dialog.show(fragmentManager,null);
            return true;
        }
    }
}