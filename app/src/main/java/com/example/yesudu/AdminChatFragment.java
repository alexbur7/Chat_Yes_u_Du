package com.example.yesudu;

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
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class AdminChatFragment extends ChatBaseFragment {


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        delete_string =getResources().getString(R.string.delete_users);
        admin_string=getResources().getString(R.string.admin);

        View v=inflater.inflate(R.layout.chat_fragment,container,false);
        receiverUuid=getArguments().getString(KEY_TO_RECEIVER_UUID);
        receiverPhotoUrl = getArguments().getString(KEY_TO_RECEIVER_PHOTO_URL);
        toolbar=v.findViewById(R.id.toolbarFr);
        complainView =v.findViewById(R.id.complain_button);
        complainView.setVisibility(View.GONE);
        statusText = v.findViewById(R.id.online_text_in_chat);
        listView = v.findViewById(R.id.list_of_messages);
        fab= v.findViewById(R.id.fab);
        send_image = v.findViewById(R.id.send_image_button);
        send_image.setOnClickListener(this);
        input = v.findViewById(R.id.input);
        fab.setOnClickListener(this);
        reference = FirebaseDatabase.getInstance().getReference("chats");
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
                        input.setText(getResources().getString(R.string.blocked_chat));
                        input.setEnabled(false);
                        fab.setEnabled(false);
                        send_image.setEnabled(false);
                    }

                    else if (snapshot1.getKey().equals("secondBlock") && User.getCurrentUser().getUuid().equals(firstKey) && snapshot1.getValue().equals("block")){
                        input.setText(getResources().getString(R.string.blocked_chat));
                        input.setEnabled(false);
                        fab.setEnabled(false);
                        send_image.setEnabled(false);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        if (User.getCurrentUser().getAdmin_block().equals("block")){
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
    protected void sendMessage() {
        if (!input.getText().toString().equals("")) {
           // reference = FirebaseDatabase.getInstance().getReference("chats").child(generateKey());
            HashMap<String,Object> map=new HashMap<>();
            reference.child(generateKey()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        map.put("firstBlock","no block");
                        map.put("secondBlock","no block");
                        reference.updateChildren(map);
                        reference.removeEventListener(this);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });

            reference.child(generateKey()).child("message")
                    .push()
                    .setValue(new ChatMessage(input.getText().toString(),
                            getActivity().getResources().getString(R.string.admin),getActivity().getString(R.string.admin_key),receiverUuid,getResources().getString(R.string.not_seen_text),
                            getResources().getString(R.string.not_seen_text),(image_rui!=null) ? image_rui.toString(): null,"no delete","no delete","no"));
        }
        else if (image_rui!=null){
            //reference = FirebaseDatabase.getInstance().getReference("chats").child(generateKey());
            HashMap<String,Object> map=new HashMap<>();
            reference.child(generateKey()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        map.put("firstBlock","no block");
                        map.put("secondBlock","no block");
                        reference.updateChildren(map);
                        reference.removeEventListener(this);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            reference.child(generateKey()).child("message")
                    .push()
                    .setValue(new ChatMessage(input.getText().toString(),
                            getActivity().getResources().getString(R.string.admin),getActivity().getString(R.string.admin_key),receiverUuid,getResources().getString(R.string.not_seen_text),
                            getResources().getString(R.string.not_seen_text),(image_rui!=null) ? image_rui.toString(): null,"no delete","no delete","no"));
        }
        image_rui=null;
        input.setText("");
    }

    @Override
    void clickMessage(View v, DatabaseReference reference, String messageText, int type) {
        v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                EditMessageDialog editMessageDialog = new EditMessageDialog(reference,receiverUuid,messageText,type,EditMessageDialog.TYPE_OF_USER_ADMIN);
                editMessageDialog.setTargetFragment(AdminChatFragment.this, EDIT_MSG_DIALOG_CODE);
                editMessageDialog.show(getFragmentManager(),null);
                return true;
            }
        });
    }

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
                        if (model.getEdited().equals("yes")){
                            ImageView editImage = v.findViewById(R.id.edit_image);
                            editImage.setVisibility(View.VISIBLE);
                        }

                        messageText.setText(model.getMessageText());
                        messageUser.setText(model.getFromUser());

                        messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm)",
                                model.getMessageTime()));
                        imageView = v.findViewById(R.id.image_send);
                        if (model.getImage_url() != null) {
                            Log.e("GLIDE","CLICKED");
                            Glide.with(getActivity()).load(model.getImage_url()).into(imageView);
                            setClickListenerOnImage(model,imageView);
                        }
                    }
                }
                else {
                    if (!model.getSecondDelete().equals("delete")) {
                        TextView messageText = v.findViewById(R.id.message_text);
                        TextView messageUser = v.findViewById(R.id.message_user);
                        TextView messageTime = v.findViewById(R.id.message_time);

                        messageText.setText(model.getMessageText());
                        messageUser.setText(model.getFromUser());

                        messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm)",
                                model.getMessageTime()));

                        imageView = v.findViewById(R.id.image_send);
                        if (model.getImage_url() != null) {
                            Log.e("GLIDE","CLICKED");
                            Glide.with(getActivity()).load(model.getImage_url()).into(imageView);
                            setClickListenerOnImage(model,imageView);
                        }
                        if (model.getEdited().equals("yes")){
                            ImageView editImage = v.findViewById(R.id.edit_image);
                            editImage.setVisibility(View.VISIBLE);
                        }
                    }
                }
                if (model.getFromUserUUID().equals("admin"))
                    clickMessage(v,getRef(position),model.getMessageText(),EditMessageDialog.TYPE_OF_MSG_MY);
                else clickMessage(v,getRef(position),model.getMessageText(),EditMessageDialog.TYPE_OF_MSG_NOT_MY);
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
                        if (chtm.getFromUserUUID().equals(getResources().getString(R.string.admin_key)) && chtm.getImage_url() == null) {
                            mLayout = R.layout.chat_list_item_right;
                        } else if (chtm.getFromUserUUID().equals(getResources().getString(R.string.admin_key)) && chtm.getImage_url() != null) {
                            mLayout = R.layout.chat_list_item_right_with_image;
                        } else if (!chtm.getFromUserUUID().equals(getResources().getString(R.string.admin_key)) && chtm.getImage_url() != null) {
                            mLayout = R.layout.chat_list_item_left_with_image;
                        } else {
                            mLayout = R.layout.chat_list_item_left;
                        }
                    } else mLayout = R.layout.delete_message;
                }
                else {
                    if (!chtm.getSecondDelete().equals("delete")) {
                        if (chtm.getFromUserUUID().equals(getResources().getString(R.string.admin_key)) && chtm.getImage_url() == null) {
                            mLayout = R.layout.chat_list_item_right;
                        } else if (chtm.getFromUserUUID().equals(getResources().getString(R.string.admin_key)) && chtm.getImage_url() != null) {
                            mLayout = R.layout.chat_list_item_right_with_image;
                        } else if (!chtm.getFromUserUUID().equals(getResources().getString(R.string.admin_key)) && chtm.getImage_url() != null) {
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

    String generateKey() {
        ArrayList<String> templist=new ArrayList<>();
        templist.add(getActivity().getResources().getString(R.string.admin_key));
        templist.add(receiverUuid);
        Collections.sort(templist);
        firstKey=templist.get(0);
        secondKey = templist.get(1);
        return templist.get(0)+templist.get(1);
    }

    public static AdminChatFragment newInstance(String toUserUUID,String photo_url){
        AdminChatFragment fragment = new AdminChatFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_TO_RECEIVER_UUID, toUserUUID);
        bundle.putString(KEY_TO_RECEIVER_PHOTO_URL,photo_url);
        fragment.setArguments(bundle);
        return fragment;
    }
}
