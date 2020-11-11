package com.example.yesudu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class ChatFragment extends ChatBaseFragment{
    public static final String KEY_TO_RECEIVER_UUID="recevierID";
    public static final String KEY_TO_RECEIVER_PHOTO_URL = "recevierPHOTO_URL";
    private Toolbar toolbar;
    private ValueEventListener seenListener;
    private String seenText;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity=(CallBack) context;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        activity=null;
    }




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        delete_string =getResources().getString(R.string.delete_users);
        admin_string=getResources().getString(R.string.admin);
        seenText= getResources().getString(R.string.seen_text);
        View v=inflater.inflate(R.layout.chat_fragment,container,false);
        receiverUuid=getArguments().getString(KEY_TO_RECEIVER_UUID);
        receiverPhotoUrl = getArguments().getString(KEY_TO_RECEIVER_PHOTO_URL);
        toolbar=v.findViewById(R.id.toolbarFr);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = UserAccountActivity.newIntent(getContext(), receiverUuid);
                startActivity(intent);
            }
        });
        complainView =v.findViewById(R.id.complain_button);
        complainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.goToAdmin();
            }
        });
        if (receiverUuid.equals(getResources().getString(R.string.admin_key))){
            complainView.setVisibility(View.GONE);
            toolbar.setEnabled(false);
        }
        statusText = v.findViewById(R.id.online_text_in_chat);
        listView = v.findViewById(R.id.list_of_messages);
        fab= v.findViewById(R.id.fab);
        send_image = v.findViewById(R.id.send_image_button);
        send_image.setOnClickListener(this);
        input = v.findViewById(R.id.input);
        fab.setOnClickListener(this);
        reference = FirebaseDatabase.getInstance().getReference("chats");
        storageReference = FirebaseStorage.getInstance().getReference("ChatImage");
        username=v.findViewById(R.id.username_text);
        circleImageView = v.findViewById(R.id.circle_image_chat);
        if (receiverPhotoUrl.equals("default")){
            circleImageView.setImageResource(R.drawable.unnamed);
        }
        else{
            Glide.with(this).load(receiverPhotoUrl).into(circleImageView);
        }

        blockListener=reference.child(generateKey()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1:snapshot.getChildren()){

                    if (snapshot1.getKey().equals("firstBlock") && User.getCurrentUser().getUuid().equals(secondKey) && snapshot1.getValue().equals("block")){
                        input.setText(getActivity().getString(R.string.blocked_chat));
                        input.setEnabled(false);
                        fab.setEnabled(false);
                        send_image.setEnabled(false);
                    }

                    else if (snapshot1.getKey().equals("secondBlock") && User.getCurrentUser().getUuid().equals(firstKey) && snapshot1.getValue().equals("block")){
                        input.setText(getActivity().getString(R.string.blocked_chat));
                        input.setEnabled(false);
                        fab.setEnabled(false);
                        send_image.setEnabled(false);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        if (User.getCurrentUser().getAdmin_block().equals("block") && !receiverUuid.equals(getActivity().getString(R.string.admin_key))){
            input.setText(getResources().getString(R.string.blocked_by_admin));
            input.setEnabled(false);
            fab.setEnabled(false);
            send_image.setEnabled(false);
        }

        if (receiverUuid.equals(getActivity().getResources().getString(R.string.admin_key))){
            statusText.setText("");
            username.setText(admin_string);
        }
        else setStatus();
        displayChatMessages();
        return v;
    }

    @Override
    void displayChatMessages(){
        adapter = new FirebaseListAdapter<ChatMessage>(getActivity(), ChatMessage.class,
                0, FirebaseDatabase.getInstance().getReference("chats").child(generateKey()).child("message")) {

            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                if (User.getCurrentUser().getUuid().equals(firstKey)) {
                    if (!model.getFirstDelete().equals("delete")) {
                        TextView messageText = v.findViewById(R.id.message_text);
                        TextView messageUser = v.findViewById(R.id.message_user);
                        TextView messageTime = v.findViewById(R.id.message_time);
                        ImageView seenImage = v.findViewById(R.id.seen_image);

                        messageText.setText(model.getMessageText());
                        messageUser.setText(model.getFromUser());

                        messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm)",
                                model.getMessageTime()));
                        if (!model.getSecondKey().equals(getActivity().getString(R.string.not_seen_text))) {
                            try {
                                seenImage.setImageResource(R.drawable.seen_image);
                            } catch (Exception e){}
                        }

                        ImageView imageView = v.findViewById(R.id.image_send);
                        if (model.getImage_url() != null) {
                            Glide.with(getActivity()).load(model.getImage_url()).into(imageView);
                            imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Fragment newDetail = PhotoViewPagerItemFragment.newInstance(model.getImage_url());
                                    getFragmentManager().beginTransaction()
                                            .addToBackStack(null)
                                            .add(R.id.fragment_container,newDetail)
                                            .commit();
                                }
                            });
                        }
                    }
                }
                else {
                    if (!model.getSecondDelete().equals("delete")) {
                        TextView messageText = v.findViewById(R.id.message_text);
                        TextView messageUser = v.findViewById(R.id.message_user);
                        TextView messageTime = v.findViewById(R.id.message_time);
                        ImageView seenImage = v.findViewById(R.id.seen_image);

                        messageText.setText(model.getMessageText());
                        messageUser.setText(model.getFromUser());

                        messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm)",
                                model.getMessageTime()));
                        if (!model.getFirstKey().equals(getActivity().getString(R.string.not_seen_text)))
                            try {
                                seenImage.setImageResource(R.drawable.seen_image);
                            }catch (Exception e){}

                        ImageView imageView = v.findViewById(R.id.image_send);
                        if (model.getImage_url() != null) {
                            Glide.with(getActivity()).load(model.getImage_url()).into(imageView);
                        }
                    }
                }
                if (User.getCurrentUser().getUuid().equals(model.getFromUserUUID()))
                clickMessage(v,getRef(position),model.getMessageText());
                Log.e("MESSAGE", String.valueOf(getRef(position)));
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
                ChatMessage chtm = super.getItem(position);
                if (User.getCurrentUser().getUuid().equals(firstKey)) {
                    if (!chtm.getFirstDelete().equals("delete")) {
                        if (chtm.getFromUserUUID().equals(User.getCurrentUser().getUuid()) && chtm.getImage_url() == null) {
                            mLayout = R.layout.chat_list_item_right;
                        } else if (chtm.getFromUserUUID().equals(User.getCurrentUser().getUuid()) && chtm.getImage_url() != null) {
                            mLayout = R.layout.chat_list_item_right_with_image;
                        } else if (!chtm.getFromUserUUID().equals(User.getCurrentUser().getUuid()) && chtm.getImage_url() != null) {
                            mLayout = R.layout.chat_list_item_left_with_image;
                        } else {
                            mLayout = R.layout.chat_list_item_left;
                        }
                    } else mLayout = R.layout.delete_message;
                }
                else {
                    if (!chtm.getSecondDelete().equals("delete")) {
                        if (chtm.getFromUserUUID().equals(User.getCurrentUser().getUuid()) && chtm.getImage_url() == null) {
                            mLayout = R.layout.chat_list_item_right;
                        } else if (chtm.getFromUserUUID().equals(User.getCurrentUser().getUuid()) && chtm.getImage_url() != null) {
                            mLayout = R.layout.chat_list_item_right_with_image;
                        } else if (!chtm.getFromUserUUID().equals(User.getCurrentUser().getUuid()) && chtm.getImage_url() != null) {
                            mLayout = R.layout.chat_list_item_left_with_image;
                        } else {
                            mLayout = R.layout.chat_list_item_left;
                        }
                    } else mLayout = R.layout.delete_message;
                }
                return chtm;
            }
        };
        listView.setStackFromBottom(true);
        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(adapter);
        if (adapter!=null)
            adapter.notifyDataSetChanged();

        seenMessage();
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

    private void clickMessage(View v, DatabaseReference reference, String messageText){
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditMessageDialog editMessageDialog = new EditMessageDialog(reference,receiverUuid,messageText);
                editMessageDialog.setTargetFragment(ChatFragment.this, EDIT_MSG_DIALOG_CODE);
                editMessageDialog.show(getFragmentManager(),null);
            }
        });
    }
    protected void sendMessage() {
        if (!input.getText().toString().equals("")) {
            HashMap<String,Object> map=new HashMap<>();
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        map.put("firstBlock","no block");
                        map.put("secondBlock","no block");
                        reference.child(generateKey()).updateChildren(map);
                        reference.child(generateKey()).removeEventListener(this);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });

            reference.child(generateKey()).child("message")
                    .push()
                    .setValue(new ChatMessage(input.getText().toString(),
                            User.getCurrentUser().getName(),User.getCurrentUser().getUuid(),receiverUuid,getActivity().getString(R.string.not_seen_text),
                            getActivity().getString(R.string.not_seen_text),(image_rui!=null) ? image_rui.toString(): null,"no delete","no delete"));
        }
        if (image_rui!=null){
            HashMap<String,Object> map=new HashMap<>();
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        map.put("firstBlock","no block");
                        map.put("secondBlock","no block");
                        reference.child(generateKey()).updateChildren(map);
                        reference.child(generateKey()).removeEventListener(this);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            reference.child(generateKey()).child("message")
                    .push()
                    .setValue(new ChatMessage(input.getText().toString(),
                            User.getCurrentUser().getName(),User.getCurrentUser().getUuid(),receiverUuid,getActivity().getString(R.string.not_seen_text),
                            getActivity().getString(R.string.not_seen_text),(image_rui!=null) ? image_rui.toString(): null,"no delete","no delete"));
        }
        image_rui=null;
        input.setText("");
    }

     String generateKey(){
        ArrayList<String> templist=new ArrayList<>();
        templist.add(User.getCurrentUser().getUuid());
        templist.add(receiverUuid);
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
                        if (user.getStatus().equals(getResources().getString(R.string.label_offline)))
                            statusText.setText("был в сети " + DateFormat.format("dd-MM-yyyy (HH:mm)", user.getOnline_time()));
                        else statusText.setText(user.getStatus());
                        username.setText(user.getName());
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
    }

    protected void seenMessage(){
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
                                        hashMap.put("firstKey",seenText);
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

    public interface CallBack{
        void goToAdmin();
    }
}
