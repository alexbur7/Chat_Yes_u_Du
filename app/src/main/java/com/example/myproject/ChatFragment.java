package com.example.myproject;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class ChatFragment extends Fragment implements View.OnClickListener{
    public static final String KEY_TO_RECEIVER_UUID="recevierID";
    public static final String KEY_TO_RECEIVER_PHOTO_URL = "recevierPHOTO_URL";
    private String receiverUuid;
    private String receiverPhotoUrl;
    private FloatingActionButton fab;
    private Toolbar toolbar;
    private EditText input;
    private TextView username;
    private TextView statusText;
    private FirebaseListAdapter<ChatMessage> adapter;
    private ListView listView;
    private ImageView circleImageView;
    private DatabaseReference reference;
    private String firstKey, secondKey;
    private ValueEventListener seenListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.chat_fragment,container,false);
        //setContentView(R.layout.chat_fragment);

        receiverUuid=getArguments().getString(KEY_TO_RECEIVER_UUID);
        receiverPhotoUrl = getArguments().getString(KEY_TO_RECEIVER_PHOTO_URL);
        toolbar=v.findViewById(R.id.toolbarFr);
        statusText = v.findViewById(R.id.online_text_in_chat);
        listView = v.findViewById(R.id.list_of_messages);
        fab= v.findViewById(R.id.fab);
        input = v.findViewById(R.id.input);
        fab.setOnClickListener(this);
        reference = FirebaseDatabase.getInstance().getReference("message");
        //reference.getDatabase().goOnline();
        username=v.findViewById(R.id.username_text);
        circleImageView = v.findViewById(R.id.circle_image_chat);
        if (receiverPhotoUrl.equals("default")){
            circleImageView.setImageResource(R.drawable.unnamed);
        }
        else{
            Glide.with(this).load(receiverPhotoUrl).into(circleImageView);
        }
        status("online");
        setStatus();
        displayChatMessages();
        return v;
    }

    private void displayChatMessages(){
        adapter = new FirebaseListAdapter<ChatMessage>(getActivity(), ChatMessage.class,
                0, FirebaseDatabase.getInstance().getReference("message").child(generateKey())) {

            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                TextView messageText = v.findViewById(R.id.message_text);
                TextView messageUser = v.findViewById(R.id.message_user);
                TextView messageTime = v.findViewById(R.id.message_time);
                TextView seenText =    v.findViewById(R.id.text_seen);

                messageText.setText(model.getMessageText());
                messageUser.setText(model.getFromUser());

                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm)",
                        model.getMessageTime()));
                if (User.getCurrentUser().getUuid().equals(firstKey)){
                    seenText.setText(model.getSecondKey());
                }
                else
                    seenText.setText(model.getFirstKey());
            }

            @Override
            public View getView(int position, View view, ViewGroup viewGroup) {

                ChatMessage model = getItem(position);
                View view2 = mActivity.getLayoutInflater().inflate(mLayout, viewGroup, false);
                populateView(view2, model, position);
                return view2;
            }

            @Override
            public ChatMessage getItem(int position) {
                ChatMessage chtm=super.getItem(position);
                if (chtm.getFromUserUUID().equals(User.getCurrentUser().getUuid())){
                    mLayout=R.layout.chat_list_item_right;
                }
                else {
                    mLayout=R.layout.chat_list_item_left;
                }
                return chtm;
            }
        };
        listView.setAdapter(adapter);
        listView.setStackFromBottom(true);
        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        if (adapter!=null)
            adapter.notifyDataSetChanged();

        seenMessage();
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.fab){
            sendMessage();
        }
    }


    private void sendMessage() {
     FirebaseDatabase.getInstance().getReference("message").child(generateKey())
                .push()
                .setValue(new ChatMessage(input.getText().toString(),
                        User.getCurrentUser().getName(),User.getCurrentUser().getUuid(),receiverUuid,"no seen",
                        "no seen"));
        input.setText("");
    }


    private String generateKey(){
        ArrayList<String> templist=new ArrayList<>();
        templist.add(User.getCurrentUser().getUuid());
        templist.add(receiverUuid);
        Collections.sort(templist);
        firstKey=templist.get(0);
        secondKey = templist.get(1);
        return templist.get(0)+templist.get(1);
    }

    public static Fragment newInstance(String toUserUUID, String photo_url){
        ChatFragment fragment = new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_TO_RECEIVER_UUID, toUserUUID);
        bundle.putString(KEY_TO_RECEIVER_PHOTO_URL,photo_url);
        fragment.setArguments(bundle);
        //Intent intent=new Intent(context,ChatActivity.class);
        //intent.putExtra(KEY_TO_RECEIVER_UUID,toUserUUID);
        //intent.putExtra(KEY_TO_RECEIVER_PHOTO_URL,photo_url);
        return fragment;
    }

    private void setStatus(){
        FirebaseDatabase.getInstance().getReference("users").child(receiverUuid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                statusText.setText(user.getStatus());
                username.setText(user.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void status(String status){
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        User.getCurrentUser().setStatus(status);
        FirebaseDatabase.getInstance().getReference("users").child(User.getCurrentUser().getUuid()).updateChildren(hashMap);
    }

    @Override
    public void onPause() {
        super.onPause();
        reference.removeEventListener(seenListener);
        //reference.getDatabase().goOffline();
        System.out.println(reference.orderByValue());
        seenListener=null;
        reference=null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

     /*for (DataSnapshot snapshot1 : snapshot.getChildren())
            for (DataSnapshot snapshot2 : snapshot1.getChildren()) {
        ChatMessage message=snapshot2.getValue(ChatMessage.class);
        Log.e("MESSAGE FROM ME", String.valueOf((message.getFromUserUUID().equals(User.getCurrentUser().getUuid()))));
        if (User.getCurrentUser().getUuid().equals(firstKey)) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("secondKey", "seen");
            snapshot1.getRef().updateChildren(hashMap);
        } else {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("firstKey", "seen");
            snapshot1.getRef().updateChildren(hashMap);
        }
    }*/

     /* if (User.getCurrentUser().getUuid().equals(firstKey)) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("secondKey", "seen");
                            snapshot1.getRef().updateChildren(hashMap);
                        } else {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("firstKey", "seen");
                            snapshot1.getRef().updateChildren(hashMap);
                        }*/

    private void seenMessage(){
        //reference=FirebaseDatabase.getInstance().getReference("message").child(generateKey());
        seenListener=reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren())
                    for (DataSnapshot snapshot2 : snapshot1.getChildren()) {
                        ChatMessage message=snapshot2.getValue(ChatMessage.class);
                        if ((message.getFromUserUUID().equals(User.getCurrentUser().getUuid()) && message.getToUserUUID().equals(getArguments().getString(KEY_TO_RECEIVER_UUID))) ||
                                (message.getFromUserUUID().equals(getArguments().getString(KEY_TO_RECEIVER_UUID)) && message.getToUserUUID().equals(User.getCurrentUser().getUuid()))){

                            //Если из нашей переписки
                            Log.e("MESSAGE TO ME", String.valueOf((message.getToUserUUID().equals(User.getCurrentUser().getUuid()))));

                            HashMap<String, Object> hashMap = new HashMap<>();
                            if ((message.getToUserUUID().equals(User.getCurrentUser().getUuid())) && (User.getCurrentUser().getUuid().equals(firstKey))) hashMap.put("firstKey", "seen");
                            else if ((message.getToUserUUID().equals(User.getCurrentUser().getUuid())) && (User.getCurrentUser().getUuid().equals(secondKey))) hashMap.put("secondKey", "seen");
                            snapshot2.getRef().updateChildren(hashMap);

                        }

                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}

