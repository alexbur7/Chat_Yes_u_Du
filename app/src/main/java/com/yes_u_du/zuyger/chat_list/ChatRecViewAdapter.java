package com.yes_u_du.zuyger.chat_list;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.yes_u_du.zuyger.R;
import com.yes_u_du.zuyger.account.User;
import com.yes_u_du.zuyger.account.UserAccountActivity;
import com.yes_u_du.zuyger.chat.ChatActivity;
import com.yes_u_du.zuyger.chat.ChatMessage;
import com.yes_u_du.zuyger.chat_list.fragment.BlockListFragment;
import com.yes_u_du.zuyger.chat_list.fragment.FilteredChatListFragment;
import com.yes_u_du.zuyger.dialog.CancelDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatRecViewAdapter extends RecyclerView.Adapter<ChatRecViewAdapter.ChatHolder>{

    private List<User> userList;
    private Context context;
    private FragmentManager fragmentManager;
    private int viewType;
    public static String filtered;
    private int type_dialog;

    public ChatRecViewAdapter(List<User> list, Context context, FragmentManager manager,int viewType){
        this.userList=list;
        this.context=context;
        this.fragmentManager=manager;
        this.viewType=viewType;
        this.filtered="none";
    }

    public ChatRecViewAdapter(List<User> list, Context context, FragmentManager manager,int viewType,String filtered){
        this.userList=list;
        this.context=context;
        this.fragmentManager=manager;
        this.viewType=viewType;
        this.filtered=filtered;
    }

    public ChatRecViewAdapter(List<User> list, Context context, FragmentManager manager,int viewType, int type_dialog){
        this.userList=list;
        this.context=context;
        this.fragmentManager=manager;
        this.viewType=viewType;
        this.type_dialog = type_dialog;
        this.filtered="none";
    }

    @NonNull
    @Override
    public ChatRecViewAdapter.ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        if (filtered.equals("none") && viewType!=AdminBanListHolder.VIEW_TYPE){
            v = LayoutInflater.from(context).inflate(R.layout.users_list_item, parent, false);
        }
        else {
            v = LayoutInflater.from(context).inflate(R.layout.users_filtered_list_item, parent, false);
        }

        switch (viewType) {
            case ChatHolder.VIEW_TYPE: return new ChatRecViewAdapter.ChatHolder(v, context, fragmentManager);
            case BlockListHolder.VIEW_TYPE: return new BlockListHolder(v,context,fragmentManager,type_dialog);
            case AdminChatHolder.VIEW_TYPE: return new AdminChatHolder(v,context,fragmentManager);
            case FavoriteListHolder.VIEW_TYPE:return new FavoriteListHolder(v,context,fragmentManager,type_dialog);
            case AdminBanListHolder.VIEW_TYPE:return new AdminBanListHolder(v,context,fragmentManager,type_dialog);
            default: throw new NullPointerException("HOLDER TYPE IS INVALID");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ChatHolder holder, int position) {
        Log.e("users_inAdapter_id",userList.get(position).getUuid());
        holder.onBind(userList.get(position));
        if (User.getCurrentUser()!=null && filtered.equals("none") && viewType!=AdminBanListHolder.VIEW_TYPE) {
            Log.d("if_bind","here");
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
        private ImageView verifiedImage;
        private ImageView blockedUserImage;
        private CircleImageView photoImageView;
        protected Context context;
        protected FragmentManager fragmentManager;
        private LinearLayout linearLayout;

        public ChatHolder(@NonNull View itemView, Context context, FragmentManager manager) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.chat_list_item_layout);
            this.context=context;
            this.fragmentManager=manager;
            userName = itemView.findViewById(R.id.user_name);
            userDate = itemView.findViewById(R.id.user_date);
            userText = itemView.findViewById(R.id.user_text);
            userStatus = itemView.findViewById(R.id.text_online_list);
            photoImageView = itemView.findViewById(R.id.circle_image_user);
            verifiedImage = itemView.findViewById(R.id.verified_image_item);
            blockedUserImage=itemView.findViewById(R.id.blocked_image_item);
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
                                            else {
                                                view.setText(message.getMessageText());
                                            }
                                            if(message.getSecondSeen().equals(context.getString(R.string.not_seen_text))){
                                                linearLayout.setBackgroundResource(R.color.no_seen);
                                            }
                                            if(message.getSecondSeen().equals(context.getString(R.string.seen_text)) || message.getFirstSeen().equals(context.getString(R.string.seen_text))) linearLayout.setBackgroundColor(Color.WHITE);
                                        }
                                    } else {
                                        if ((message.getToUserUUID().equals(User.getCurrentUser().getUuid()) && message.getFromUserUUID().equals(id) ||
                                                message.getToUserUUID().equals(id) && message.getFromUserUUID().equals(User.getCurrentUser().getUuid())) ) {
                                            if (message.getSecondDelete().equals("delete")){
                                                view.setText("");
                                            }
                                            else
                                                view.setText(message.getMessageText());
                                            if(message.getFirstSeen().equals(context.getString(R.string.not_seen_text))){
                                                //TODO здесь красится лейаут
                                                linearLayout.setBackgroundResource(R.color.no_seen);
                                            }
                                            if(message.getSecondSeen().equals(context.getString(R.string.seen_text)) || message.getFirstSeen().equals(context.getString(R.string.seen_text))) linearLayout.setBackgroundColor(Color.WHITE);
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
            if (filtered.equals(FilteredChatListFragment.FILTER_VIEW_TYPE)) {
                if (user.getPerm_block().equals("block")) {
                    setBlockedListeners(context.getString(R.string.perm_blocked_by_admin_on_chatlist_title));
                    blockedUserImage.setVisibility(View.VISIBLE);
                } else if (user.getAdmin_block().equals("block")) {
                    setBlockedListeners(context.getString(R.string.blocked_by_admin_on_chatlist_title));
                    blockedUserImage.setVisibility(View.VISIBLE);
                }
            }
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
            if (user.getVerified().equals("yes")){
                verifiedImage.setVisibility(View.VISIBLE);
            }
            else verifiedImage.setVisibility(View.INVISIBLE);
        }

        private void setBlockedListeners(String text){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context,text,Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onClick(View view) {
            Intent intent = ChatActivity.newIntent(context, user.getUuid(), user.getPhoto_url(),user.getAdmin_block(), VIEW_TYPE);
            context.startActivity(intent);
        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }

        /*@Override
        public boolean onLongClick(View v) {
            if (userText!=null) {
                DeleteChatDialog deleteChatDialog = new DeleteChatDialog(user.getUuid(), userText.getText().toString().isEmpty());
                Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);
                deleteChatDialog.setTargetFragment(fragment, ChatListFragment.KEY_DELETE_DIAOG);
                deleteChatDialog.show(fragmentManager, null);
            }
            return true;
        }*/
    }

    public static class AdminChatHolder extends ChatRecViewAdapter.ChatHolder{

        public static final int VIEW_TYPE=2;

        public AdminChatHolder(@NonNull View itemView, Context context, FragmentManager manager) {
            super(itemView, context, manager);
        }

        @Override
        public void onClick(View view) {
            Intent intent = ChatActivity.newIntent(context, user.getUuid(), user.getPhoto_url(),user.getAdmin_block(),VIEW_TYPE);
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
        private int type_dialog;

        public BlockListHolder(@NonNull View itemView, Context context, FragmentManager manager, int type_dialog) {
            super(itemView, context, manager);
            this.type_dialog = type_dialog;
        }

        @Override
        public boolean onLongClick(View v) {
            CancelDialog dialog=new CancelDialog(user.getUuid(),type_dialog);
            Fragment fragment= fragmentManager.findFragmentById(R.id.fragment_container);
            dialog.setTargetFragment(fragment, BlockListFragment.KEY_TO_UNBLOCK);
            dialog.show(fragmentManager,null);
            return true;
        }
    }

    public static class FavoriteListHolder extends ChatRecViewAdapter.ChatHolder{
        public static final int VIEW_TYPE=3;
        private int type_dialog;

        public FavoriteListHolder(@NonNull View itemView, Context context, FragmentManager manager, int type_dialog) {
            super(itemView, context, manager);
            this.type_dialog = type_dialog;
        }

        @Override
        public boolean onLongClick(View v) {
            CancelDialog dialog=new CancelDialog(user.getUuid(),type_dialog);
            Fragment fragment= fragmentManager.findFragmentById(R.id.fragment_container);
            dialog.setTargetFragment(fragment,BlockListFragment.KEY_TO_UNBLOCK);
            dialog.show(fragmentManager,null);
            return true;
        }
    }

    public static class AdminBanListHolder extends ChatRecViewAdapter.ChatHolder {
        public static final int VIEW_TYPE=4;
        private int list_type;
        //private DatabaseReference mReference;

        public AdminBanListHolder(@NonNull View itemView, Context context, FragmentManager manager,int list_type) {
            super(itemView, context, manager);
            this.list_type=list_type;
            //Log.d("holder_userID",user.getUuid());
            //mReference=FirebaseDatabase.getInstance().getReference("users").child(user.getUuid());
        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }

        @Override
        public void onClick(View view) {
            Intent intent= UserAccountActivity.newIntent(context,user.getUuid());
            FragmentActivity activity= (FragmentActivity) context;
            context.startActivity(intent);
            activity.finish();
        }
    }
}