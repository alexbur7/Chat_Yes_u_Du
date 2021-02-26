package com.yes_u_du.zuyger.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.yes_u_du.zuyger.R;
import com.yes_u_du.zuyger.account.User;
import com.yes_u_du.zuyger.dialog.AcceptDialog;
import com.yes_u_du.zuyger.dialog.ComplainDialog;
import com.yes_u_du.zuyger.dialog.GoToAdminDialog;
import com.yes_u_du.zuyger.dialog.EditMessageDialog;
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
    public static final String KEY_TO_BLOCK_USER= "block_user";
    public static final int GO_TO_ADMIN_REQUEST = 1010;
    public static final int COMPLAIN_REQUEST = 2020;
    public static final int KEY_ACCEPT_BLOCK_USER=101;
    public static final int KEY_ACCEPT_DELETE_CHAT=102;
    private ValueEventListener setChatListener;
    private String seenText;
    private DatabaseReference referenceWriting;
    private ChatMessageAdapter adapter;
    private ValueEventListener seenListener;
    private boolean setChatListenerConnected;
    private ImageView verifiedImage, blockUserImage, notificationAdminImage;
    private ValueEventListener blockChatListener;
    private ValueEventListener deleteMessageListener;
    private String firstKeyToAdmin, secondKeyToAdmin, block_user;
    private ValueEventListener adminMessagesListener;

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
        block_user = getArguments().getString(KEY_TO_BLOCK_USER);
        toolbar=v.findViewById(R.id.toolbarFr);
        verifiedImage = v.findViewById(R.id.verified_image_chat);
        blockUserImage = v.findViewById(R.id.block_image_chat);
        notificationAdminImage = v.findViewById(R.id.notification_admin);
        complainView =v.findViewById(R.id.complain_button);
        complainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notificationAdminImage.getVisibility() == View.VISIBLE){
                    activity.goToAdmin();
                }
                else {
                    GoToAdminDialog dialog = new GoToAdminDialog();
                    dialog.setTargetFragment(ChatFragment.this, GO_TO_ADMIN_REQUEST);
                    dialog.show(getFragmentManager(), null);
                }
                //activity.goToAdmin();
            }
        });
        if (receiverUuid.equals(getResources().getString(R.string.admin_key))){
            complainView.setVisibility(View.GONE);
            toolbar.setEnabled(false);
        }
        else setToolbarToAcc();
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
                            blockClick();
                        } catch (Exception e) {
                            blockClick();
                        }
                    }

                    else if (snapshot1.getKey().equals("secondBlock") && User.getCurrentUser().getUuid().equals(firstKey) && snapshot1.getValue().equals("block")){
                        try {
                            input.setText(getActivity().getString(R.string.blocked_chat));
                            blockClick();
                        } catch (Exception e) {
                            blockClick();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });


        //TODO доделать
        adminMessagesListener=reference.child(generateKeyToAdminChat()).child("message").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1:snapshot.getChildren()){
                    ChatMessage message=snapshot1.getValue(ChatMessage.class);
                    if (User.getCurrentUser().getUuid().equals(firstKeyToAdmin)){
                        if (message.getFromUserUUID().equals(secondKeyToAdmin)){
                            if (message.getFirstSeen().equals(getActivity().getString(R.string.not_seen_text))){
                                notificationAdminImage.setVisibility(View.VISIBLE);
                            }
                            else notificationAdminImage.setVisibility(View.INVISIBLE);
                        }
                    }
                    else if (User.getCurrentUser().getUuid().equals(secondKeyToAdmin)){
                        if (message.getFromUserUUID().equals(firstKeyToAdmin)){
                            if (message.getSecondSeen().equals(getActivity().getString(R.string.not_seen_text))){
                                notificationAdminImage.setVisibility(View.VISIBLE);
                            }
                            else notificationAdminImage.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        if (User.getCurrentUser().getAdmin_block().equals("block") && !receiverUuid.equals(getActivity().getString(R.string.admin_key))){
            input.setText(getActivity().getString(R.string.blocked_by_admin));
            blockClick();
        }
        else if (block_user.equals("block")){
            input.setText(getActivity().getString(R.string.blocked_by_admin_user));
            blockUserImage.setVisibility(View.VISIBLE);
            blockClick();
        }

        if (receiverUuid.equals(getActivity().getResources().getString(R.string.admin_key))){
            statusText.setText(getActivity().getString(R.string.app_name));
            username.setText(admin_string);
        }
        else setStatus();
        displayChatMessages();
        return v;
    }

    private void blockClick() {
        input.setEnabled(false);
        fab.setEnabled(false);
        send_image.setEnabled(false);
        toolbar.setEnabled(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        seenMessage();
        setChatListener();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        activity=null;
    }


    @Override
    protected void setToolbarToAcc() {
        super.setToolbarToAcc();
        toolbar.inflateMenu(R.menu.chat_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.delete_chat:{
                        AcceptDialog acceptDialog = new AcceptDialog(reference,deleteMessageListener,KEY_ACCEPT_DELETE_CHAT,receiverUuid,username.getText().toString());
                        acceptDialog.show(getFragmentManager(),null);
                        //deleteChat();
                        return true;
                    }
                    case R.id.block_chat:{
                        AcceptDialog acceptDialog = new AcceptDialog(reference,blockChatListener,KEY_ACCEPT_BLOCK_USER,receiverUuid,username.getText().toString());
                        acceptDialog.show(getFragmentManager(),null);
                        //blockChat();
                        return true;
                    }
                }
                return false;
            }
        });
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
                                        hashMap.put("firstSeen", seenText);
                                    else if ((message.getToUserUUID().equals(User.getCurrentUser().getUuid())) && (User.getCurrentUser().getUuid().equals(secondKey)))
                                        hashMap.put("secondSeen", seenText);
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

                if (!setChatListenerConnected) {
                    reference.child(generateKey()).addValueEventListener(setChatListener);
                    setChatListenerConnected = true;
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

    @Override
    protected void setChatListener() {
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
    protected String generateKey(){
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
        firstKeyToAdmin=templist.get(0);
        secondKeyToAdmin = templist.get(1);
        return templist.get(0)+templist.get(1);
    }

    public static ChatFragment newInstance(String toUserUUID, String photo_url, String block_user){
        ChatFragment fragment = new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_TO_RECEIVER_UUID, toUserUUID);
        bundle.putString(KEY_TO_RECEIVER_PHOTO_URL,photo_url);
        bundle.putString(KEY_TO_BLOCK_USER, block_user);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    protected void setStatus(){
        FirebaseDatabase.getInstance().getReference("users").child(receiverUuid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                try {
                    if (user.getTyping().equals(User.getCurrentUser().getUuid()) && user.getAdmin_block().equals("unblock")){
                       statusText.setText(R.string.typing);
                    }
                    else if (user.getStatus().equals(getResources().getString(R.string.label_offline))) {
                        String dateDayMonthYear = (String) DateFormat.format("dd MMMM yyyy", user.getOnline_time());
                        if (dateDayMonthYear.charAt(0) == '0') {
                            dateDayMonthYear = dateDayMonthYear.substring(1);

                        }
                        statusText.setText(getActivity().getString(R.string.was) + " " + dateDayMonthYear + " " +
                                getActivity().getString(R.string.in) + " " + DateFormat.format("HH:mm", user.getOnline_time()));
                    }
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
        this.removeAllListener();
        setWriting("unwriting");
        if (deleteMessageListener !=null) reference.child(generateKey()).child("message").removeEventListener(deleteMessageListener);
        if (blockChatListener != null) reference.child(generateKey()).removeEventListener(blockChatListener);
        if (adminMessagesListener !=null) reference.child(generateKeyToAdminChat()).child("message").removeEventListener(adminMessagesListener);
    }

    @Override
    protected void removeAllListener() {
        super.removeAllListener();
        if (seenListener!=null)
            reference.removeEventListener(seenListener);
        seenListener=null;
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
                        ComplainDialog dialog=new ComplainDialog(ComplainDialog.BASE_COMPLAIN_CODE,getFragmentManager(),ChatFragment.this);
                        dialog.setTargetFragment(ChatFragment.this,COMPLAIN_REQUEST);
                        dialog.show(getFragmentManager(),null);
                    };
                }
            }
            else if (requestCode == COMPLAIN_REQUEST){
                if (!data.getStringExtra(ComplainDialog.COMPLAIN_CODE).equals(getActivity().getString(R.string.another_reason_title))) {
                    String complaint= data.getStringExtra(ComplainDialog.COMPLAIN_CODE);
                    if (complaint.equals(getActivity().getString(R.string.false_name))
                        || complaint.equals(getActivity().getString(R.string.false_age))
                        || complaint.equals(getActivity().getString(R.string.false_city))
                        || complaint.equals(getActivity().getString(R.string.false_country))
                        || complaint.equals(getActivity().getString(R.string.false_country_and_country))){
                        complaint = getActivity().getString(R.string.wrong)+" "+data.getStringExtra(ComplainDialog.COMPLAIN_CODE);
                    }
                    else if(!complaint.equals(getActivity().getString(R.string.advertising_title))
                        && !complaint.equals(getActivity().getString(R.string.fishing_title))
                        && !complaint.equals(getActivity().getString(R.string.illegal_substance_title))
                        && !complaint.equals(getActivity().getString(R.string.obscene_content_title))
                        && !complaint.equals(getActivity().getString(R.string.extrimism_title))
                        && !complaint.equals(getActivity().getString(R.string.pornographic_content_title))
                        && !complaint.equals(getActivity().getString(R.string.threats_title))
                        && !complaint.equals(getActivity().getString(R.string.married))
                    ){
                        complaint = getActivity().getString(R.string.illegal_photos)+" "+data.getStringExtra(ComplainDialog.COMPLAIN_CODE);
                    }
                    String text_complaint = getActivity().getString(R.string.complaint_beginning) + "  " + complaint +
                            getActivity().getString(R.string.complaint_ending) + "  " + username.getText() +
                            getActivity().getString(R.string.complaint_id) + "  " + receiverUuid;

                    sendToAdmin(text_complaint);
                }
                else {
                    activity.goToAdmin();
                }
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
        Toast.makeText(getActivity(), getActivity().getString(R.string.complain_completed), Toast.LENGTH_SHORT).show();
    }

    public interface CallBack{
        void goToAdmin();
    }

    /*private void blockChat(){
            blockChatListener = reference.child(generateKey()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snapshot1:snapshot.getChildren()){
                        if (snapshot1.getKey().equals("firstBlock") && User.getCurrentUser().getUuid().equals(firstKey)){
                            HashMap<String,Object> map = new HashMap<>();
                            map.put("firstBlock","block");
                            snapshot.getRef().updateChildren(map);
                        }
                        else if (snapshot1.getKey().equals("secondBlock") && User.getCurrentUser().getUuid().equals(secondKey)){
                            HashMap<String,Object> map = new HashMap<>();
                            map.put("secondBlock","block");
                            snapshot.getRef().updateChildren(map);
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
    }

    private void deleteChat(){
            deleteMessageListener=reference.child(generateKey()).child("message").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snapshot1:snapshot.getChildren()){
                        if (User.getCurrentUser().getUuid().equals(firstKey)) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("firstDelete", "delete");
                            snapshot1.getRef().updateChildren(hashMap);
                        }
                        else {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("secondDelete", "delete");
                            snapshot1.getRef().updateChildren(hashMap);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
    }*/

}