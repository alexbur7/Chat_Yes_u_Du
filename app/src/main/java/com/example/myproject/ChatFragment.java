package com.example.myproject;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

public class ChatFragment extends Fragment implements View.OnClickListener{
    public static final String KEY_TO_RECEIVER_UUID="recevierID";
    public static final String KEY_TO_RECEIVER_PHOTO_URL = "recevierPHOTO_URL";
    private String receiverUuid;
    private String receiverPhotoUrl;
    private FloatingActionButton fab, send_image;
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
    private ValueEventListener blockListener;
    private StorageTask uploadTask;
    private StorageReference storageReference;

    private static  final  int IMAGE_REQUEST=1;
    private Uri image_rui;

    private String offline_string;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        offline_string=getResources().getString(R.string.delete_users);

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
        setStatus();
        displayChatMessages();
        return v;
    }

    private void displayChatMessages(){
        adapter = new FirebaseListAdapter<ChatMessage>(getActivity(), ChatMessage.class,
                0, FirebaseDatabase.getInstance().getReference("chats").child(generateKey()).child("message")) {

            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                if (User.getCurrentUser().getUuid().equals(firstKey)) {
                    if (!model.getFirstDelete().equals("delete")) {
                        TextView messageText = v.findViewById(R.id.message_text);
                        TextView messageUser = v.findViewById(R.id.message_user);
                        TextView messageTime = v.findViewById(R.id.message_time);
                        TextView seenText = v.findViewById(R.id.text_seen);

                        messageText.setText(model.getMessageText());
                        messageUser.setText(model.getFromUser());

                        messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm)",
                                model.getMessageTime()));
                        if (User.getCurrentUser().getUuid().equals(firstKey)) {
                            seenText.setText(model.getSecondKey());
                        } else
                            seenText.setText(model.getFirstKey());

                        ImageView imageView = v.findViewById(R.id.image_send);
                        if (model.getImage_url() != null) {
                            Glide.with(getActivity()).load(model.getImage_url()).into(imageView);
                        }
                    }
                }
                else {
                    if (!model.getSecondDelete().equals("delete")) {
                        TextView messageText = v.findViewById(R.id.message_text);
                        TextView messageUser = v.findViewById(R.id.message_user);
                        TextView messageTime = v.findViewById(R.id.message_time);
                        TextView seenText = v.findViewById(R.id.text_seen);

                        messageText.setText(model.getMessageText());
                        messageUser.setText(model.getFromUser());

                        messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm)",
                                model.getMessageTime()));
                        if (User.getCurrentUser().getUuid().equals(firstKey)) {
                            seenText.setText(model.getSecondKey());
                        } else
                            seenText.setText(model.getFirstKey());

                        ImageView imageView = v.findViewById(R.id.image_send);
                        if (model.getImage_url() != null) {
                            Glide.with(getActivity()).load(model.getImage_url()).into(imageView);
                        }
                    }
                }

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

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST);
    }

    private void sendMessage() {
        if (!input.getText().toString().equals("")) {
            reference = FirebaseDatabase.getInstance().getReference("chats").child(generateKey());
            HashMap<String,Object> map=new HashMap<>();
            reference.addValueEventListener(new ValueEventListener() {
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

            reference.child("message")
                    .push()
                    .setValue(new ChatMessage(input.getText().toString(),
                            User.getCurrentUser().getName(),User.getCurrentUser().getUuid(),receiverUuid,getResources().getString(R.string.not_seen_text),
                            getResources().getString(R.string.not_seen_text),(image_rui!=null) ? image_rui.toString(): null,"no delete","no delete"));
        }
        else if (image_rui!=null){
            reference = FirebaseDatabase.getInstance().getReference("chats").child(generateKey());
            HashMap<String,Object> map=new HashMap<>();
            reference.addValueEventListener(new ValueEventListener() {
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

            reference.child("message")
                    .push()
                    .setValue(new ChatMessage(input.getText().toString(),
                            User.getCurrentUser().getName(),User.getCurrentUser().getUuid(),receiverUuid,getResources().getString(R.string.not_seen_text),
                            getResources().getString(R.string.not_seen_text),(image_rui!=null) ? image_rui.toString(): null,"no delete","no delete"));
        }
        image_rui=null;
        input.setText("");
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage(){
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage(getResources().getString(R.string.uploading));
        pd.show();

        if (image_rui != null){
            final StorageReference fileReference= storageReference.child(System.currentTimeMillis()+
                    "."+getFileExtension(image_rui));
            uploadTask = fileReference.putFile(image_rui);
            uploadTask.continueWithTask((Continuation<UploadTask.TaskSnapshot, Task<Uri>>) task -> {
                if (!task.isSuccessful()){
                    throw  task.getException();
                }
                return fileReference.getDownloadUrl();
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        image_rui = downloadUri;
                        Toast.makeText(getContext(), R.string.image_attach,Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(getContext(),R.string.failed_update_photo,Toast.LENGTH_SHORT).show();
                    }
                    pd.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        }
        else {
            Toast.makeText(getContext(),R.string.no_image_selected,Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
                && data!=null && data.getData() !=null
        ){
            image_rui = data.getData();
            uploadImage();
        }
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
        return fragment;
    }

    private void setStatus(){
        FirebaseDatabase.getInstance().getReference("users").child(receiverUuid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                try {
                    if (user.getStatus().equals(getResources().getString(R.string.label_offline)))
                    statusText.setText(user.getStatus()+": "+DateFormat.format("dd-MM-yyyy (HH:mm)", user.getOnline_time()));
                    else statusText.setText(user.getStatus());
                    username.setText(user.getName());
                }catch (Exception e){
                    statusText.setText(offline_string);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
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
                                        hashMap.put("firstKey", getResources().getString(R.string.seen_text));
                                    else if ((message.getToUserUUID().equals(User.getCurrentUser().getUuid())) && (User.getCurrentUser().getUuid().equals(secondKey)))
                                        hashMap.put("secondKey", getResources().getString(R.string.seen_text));
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
}

