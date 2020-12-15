package com.example.yesudu.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.yesudu.R;
import com.example.yesudu.account.User;
import com.example.yesudu.dialog.ComplainDialog;
import com.example.yesudu.dialog.GoToAdminDialog;
import com.example.yesudu.dialog.EditMessageDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class ChatFragment extends ChatBaseFragment {
    public static final String KEY_TO_RECEIVER_UUID="recevierID";
    public static final String KEY_TO_RECEIVER_PHOTO_URL = "recevierPHOTO_URL";
    public static final int GO_TO_ADMIN_REQUEST = 1010;
    public static final int COMPLAIN_REQUEST = 2020;
    private ValueEventListener setChatListener;
    private String seenText;
    private DatabaseReference referenceWriting;
    private ChatMessageAdapter adapter;
    private ValueEventListener seenListener;
    private boolean setChatListenerConnected;
    private ImageView verifiedImage;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity=(CallBack) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setChatListenerConnected=false;
        delete_string =getResources().getString(R.string.delete_users);
        admin_string=getResources().getString(R.string.admin);
        seenText= getResources().getString(R.string.seen_text);
        View v=inflater.inflate(R.layout.chat_fragment,container,false);
        receiverUuid=getArguments().getString(KEY_TO_RECEIVER_UUID);
        receiverPhotoUrl = getArguments().getString(KEY_TO_RECEIVER_PHOTO_URL);
        toolbar=v.findViewById(R.id.toolbarFr);
        verifiedImage = v.findViewById(R.id.verified_image_chat);
        setToolbarToAcc();
        complainView =v.findViewById(R.id.complain_button);
        complainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GoToAdminDialog dialog=new GoToAdminDialog();
                dialog.setTargetFragment(ChatFragment.this, GO_TO_ADMIN_REQUEST);
                dialog.show(getFragmentManager(),null);
                //activity.goToAdmin();
            }
        });
        if (receiverUuid.equals(getResources().getString(R.string.admin_key))){
            complainView.setVisibility(View.GONE);
            toolbar.setEnabled(false);
        }
        statusText = v.findViewById(R.id.online_text_in_chat);
        recyclerView = v.findViewById(R.id.list_of_messages);
        fab= v.findViewById(R.id.fab);
        send_image = v.findViewById(R.id.send_image_button);
        send_image.setOnClickListener(this);
        input = v.findViewById(R.id.input);
        input.addTextChangedListener(this);
        fab.setOnClickListener(this);
        reference = FirebaseDatabase.getInstance().getReference("chats");
        referenceWriting = FirebaseDatabase.getInstance().getReference("users");
        storageReference = FirebaseStorage.getInstance().getReference("ChatImage");
        username=v.findViewById(R.id.username_text);
        circleImageView = v.findViewById(R.id.circle_image_chat);
        if (receiverPhotoUrl.equals("default")){
            circleImageView.setImageResource(R.drawable.admin_icon);
           // circleImageView.setBackgroundResource(R.color.admin_grey_back);
        }
        else{
            Glide.with(this).load(receiverPhotoUrl).into(circleImageView);
        }

        blockListener=reference.child(generateKey()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1:snapshot.getChildren()){

                    if (snapshot1.getKey().equals("firstBlock") && User.getCurrentUser().getUuid().equals(secondKey) && snapshot1.getValue().equals("block")){
                        try {
                            input.setText(getActivity().getString(R.string.blocked_chat));
                            input.setEnabled(false);
                            fab.setEnabled(false);
                            send_image.setEnabled(false);
                            toolbar.setEnabled(false);
                        } catch (Exception e) {
                            input.setEnabled(false);
                            fab.setEnabled(false);
                            send_image.setEnabled(false);
                            toolbar.setEnabled(false);
                        }
                    }

                    else if (snapshot1.getKey().equals("secondBlock") && User.getCurrentUser().getUuid().equals(firstKey) && snapshot1.getValue().equals("block")){
                        try {
                            input.setText(getActivity().getString(R.string.blocked_chat));
                            input.setEnabled(false);
                            fab.setEnabled(false);
                            send_image.setEnabled(false);
                            toolbar.setEnabled(false);
                        } catch (Exception e) {
                            input.setEnabled(false);
                            fab.setEnabled(false);
                            send_image.setEnabled(false);
                            toolbar.setEnabled(false);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        setChatListener();

        if (User.getCurrentUser().getAdmin_block().equals("block") && !receiverUuid.equals(getActivity().getString(R.string.admin_key))){
            input.setText(getActivity().getString(R.string.blocked_by_admin));
            input.setEnabled(false);
            fab.setEnabled(false);
            send_image.setEnabled(false);
            toolbar.setEnabled(false);
        }

        if (receiverUuid.equals(getActivity().getResources().getString(R.string.admin_key))){
            statusText.setText(getActivity().getString(R.string.app_name));
            username.setText(admin_string);
        }
        else setStatus();
        displayChatMessages();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        seenMessage();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        activity=null;
    }





    @Override
    void displayChatMessages(){
        adapter = new ChatMessageAdapter(ChatMessage.class, R.layout.chat_list_item_right, ChatMessageAdapter.ChatMessageHolder.class,
                FirebaseDatabase.getInstance().getReference("chats").child(generateKey()).child("message"),
                receiverUuid, getActivity(), getFragmentManager(),ChatFragment.this,EditMessageDialog.TYPE_OF_USER_USUAL);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = adapter.getItemCount();
                int lastVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) && lastVisiblePosition == (positionStart - 1))) {
                    recyclerView.scrollToPosition(positionStart);
                }
            }
        });
        recyclerView.setAnimation(null);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        if (adapter!=null)
            adapter.notifyDataSetChanged();

    }

    private void seenMessage(){
        seenListener=reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren())
                    for (DataSnapshot snapshot2 : snapshot1.getChildren()) {
                        if (!snapshot2.getKey().equals("firstBlock") && !snapshot2.getKey().equals("secondBlock")) {
                            for (DataSnapshot snapshot3 : snapshot2.getChildren()) {
                                ChatMessage message = snapshot3.getValue(ChatMessage.class);
                                if ((message.getFromUserUUID().equals(User.getCurrentUser().getUuid()) && message.getToUserUUID().equals(getArguments().getString(KEY_TO_RECEIVER_UUID))) ||
                                        (message.getFromUserUUID().equals(getArguments().getString(KEY_TO_RECEIVER_UUID)) && message.getToUserUUID().equals(User.getCurrentUser().getUuid()))) {
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    if ((message.getToUserUUID().equals(User.getCurrentUser().getUuid())) && (User.getCurrentUser().getUuid().equals(firstKey)))
                                        hashMap.put("firstKey", seenText);
                                    else if ((message.getToUserUUID().equals(User.getCurrentUser().getUuid())) && (User.getCurrentUser().getUuid().equals(secondKey)))
                                        hashMap.put("secondKey", seenText);
                                    snapshot3.getRef().updateChildren(hashMap);
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
    public void onClick(View v) {
        if (v.getId()==R.id.fab){
            sendMessage();
        }
        if (v.getId()==R.id.send_image_button){
            openImage();
        }
    }
    protected void sendMessage() {
        if (image_rui!=null){
            //setChatListener();

            if (!setChatListenerConnected) {
                reference.child(generateKey()).addValueEventListener(setChatListener);
                setChatListenerConnected=true;
            }
            reference.child(generateKey()).child("message")
                    .push()
                    .setValue(new ChatMessage(input.getText().toString(),
                            User.getCurrentUser().getName(),User.getCurrentUser().getUuid(),receiverUuid,getActivity().getString(R.string.not_seen_text),
                            getActivity().getString(R.string.not_seen_text),(image_rui!=null) ? image_rui.toString(): null,"no delete","no delete","no"));
        }
        else if (!input.getText().toString().equals("")) {
            //setChatListener();
            if (!setChatListenerConnected) {
                reference.child(generateKey()).addValueEventListener(setChatListener);
                setChatListenerConnected=true;
            }
            reference.child(generateKey()).child("message")
                    .push()
                    .setValue(new ChatMessage(input.getText().toString(),
                            User.getCurrentUser().getName(),User.getCurrentUser().getUuid(),receiverUuid,getActivity().getString(R.string.not_seen_text),
                            getActivity().getString(R.string.not_seen_text),(image_rui!=null) ? image_rui.toString(): null,"no delete","no delete","no"));
        }
        image_rui=null;
        input.setText("");
    }

    private void setChatListener() {
        HashMap<String, Object> map = new HashMap<>();
        setChatListener= new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    map.put("firstBlock", "no block");
                    map.put("secondBlock", "no block");
                    map.put("firstFavorites", "no");
                    map.put("secondFavorites", "no");
                    reference.child(generateKey()).updateChildren(map);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }


    @Override
    String generateKey(){
        ArrayList<String> templist=new ArrayList<>();
        templist.add(User.getCurrentUser().getUuid());
        templist.add(receiverUuid);
        Collections.sort(templist);
        firstKey=templist.get(0);
        secondKey = templist.get(1);
        return templist.get(0)+templist.get(1);
    }

    private String generateKeyToAdminChat(){
        ArrayList<String> templist=new ArrayList<>();
        templist.add(User.getCurrentUser().getUuid());
        templist.add(getActivity().getString(R.string.admin_key));
        Collections.sort(templist);
        firstKey=templist.get(0);
        secondKey = templist.get(1);
        return templist.get(0)+templist.get(1);
    }

    public static ChatFragment newInstance(String toUserUUID, String photo_url){
        ChatFragment fragment = new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_TO_RECEIVER_UUID, toUserUUID);
        bundle.putString(KEY_TO_RECEIVER_PHOTO_URL,photo_url);
        fragment.setArguments(bundle);
        return fragment;
    }

    protected void setStatus(){
        FirebaseDatabase.getInstance().getReference("users").child(receiverUuid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                try {
                    if (user.getTyping().equals(User.getCurrentUser().getUuid()) && user.getAdmin_block().equals("unblock")){
                       statusText.setText(R.string.typing);
                    }
                    else if (user.getStatus().equals(getResources().getString(R.string.label_offline)))
                        statusText.setText("был в сети " + DateFormat.format("dd-MM-yyyy (HH:mm)", user.getOnline_time()));
                    else statusText.setText(user.getStatus());
                    username.setText(user.getName());
                    if (user.getVerified().equals("yes")){
                        verifiedImage.setVisibility(View.VISIBLE);
                    }
                    else verifiedImage.setVisibility(View.INVISIBLE);
                } catch (Exception e) {
                    statusText.setText(delete_string);
                    username.setText("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void onPause() {
        super.onPause();
        if (seenListener!=null)
            reference.removeEventListener(seenListener);
        seenListener=null;
        if (setChatListener!=null) reference.child(generateKey()).removeEventListener(setChatListener);
        setChatListener=null;
        if (blockListener!=null) reference.child(generateKey()).removeEventListener(blockListener);
        blockListener=null;
        setWriting("unwriting");
    }



    @Override
    protected void setWriting(String writing) {
        HashMap<String,Object> map=new HashMap<>();
        map.put("typing", writing);
        referenceWriting.child(User.getCurrentUser().getUuid()).updateChildren(map);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.toString().trim().length() ==0){
            setWriting("unwriting");
        }else if (!s.toString().equals(getActivity().getString(R.string.blocked_chat)) && !s.toString().equals(getActivity().getString(R.string.blocked_by_admin))){
            setWriting(receiverUuid);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode== GO_TO_ADMIN_REQUEST){
                switch (data.getIntExtra(GoToAdminDialog.BTN_CODE,-1)){
                    case GoToAdminDialog.CHAT_BTN_CODE:activity.goToAdmin();
                    break;
                    case GoToAdminDialog.COMPLAIN_BTN_CODE:{
                        ComplainDialog dialog=new ComplainDialog();
                        dialog.setTargetFragment(ChatFragment.this,COMPLAIN_REQUEST);
                        dialog.show(getFragmentManager(),null);
                    };
                }
            }

            if (requestCode == COMPLAIN_REQUEST){
                String complaint=getActivity().getString(R.string.complaint_beginning)+data.getStringExtra(ComplainDialog.COMPLAIN_CODE)+
                        getActivity().getString(R.string.complaint_ending)+username.getText()+
                        getActivity().getString(R.string.complaint_id)+receiverUuid;

                sendToAdmin(complaint);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void sendToAdmin(String str){
        reference.child(generateKeyToAdminChat()).child("message")
                .push()
                .setValue(new ChatMessage(str,
                        User.getCurrentUser().getName(),User.getCurrentUser().getUuid(),getActivity().getString(R.string.admin_key),getActivity().getString(R.string.not_seen_text),
                        getActivity().getString(R.string.not_seen_text),receiverPhotoUrl,"no delete","no delete","no"));
    }

    public interface CallBack{
        void goToAdmin();
    }

}