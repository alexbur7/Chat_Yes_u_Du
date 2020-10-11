package com.example.myproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String KEY_TO_RECEIVER_UUID="recevierID";
    public static final String KEY_TO_RECEIVER_NAME="recevierNAME";
    private static final String KEY_TO_RECEIVER_PHOTO_URL = "recevierPHOTO_URL";
    private String receiverUuid;
    private String receiverName;
    private String receiverPhotoUrl;
    private FloatingActionButton fab;
    private Toolbar toolbar;
    private EditText input;
    private TextView username;
    private FirebaseListAdapter<ChatMessage> adapter;
    private ListView listView;
    private ImageView circleImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        receiverUuid=getIntent().getStringExtra(KEY_TO_RECEIVER_UUID);
        receiverName=getIntent().getStringExtra(KEY_TO_RECEIVER_NAME);
        receiverPhotoUrl = getIntent().getStringExtra(KEY_TO_RECEIVER_PHOTO_URL);

        listView = findViewById(R.id.list_of_messages);
        fab= findViewById(R.id.fab);
        input = findViewById(R.id.input);
        fab.setOnClickListener(this);
        toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        username=findViewById(R.id.username_text);
        username.setText(receiverName);
        circleImageView = findViewById(R.id.circle_image_chat);
        if (receiverPhotoUrl.equals("default")){
            circleImageView.setImageResource(R.drawable.unnamed);
        }
        else{
            Glide.with(this).load(receiverPhotoUrl).into(circleImageView);
        }
        displayChatMessages();
    }

    private void displayChatMessages(){
        adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class,
                0, FirebaseDatabase.getInstance().getReference("message").child(generateKey())) {

            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                TextView messageText = v.findViewById(R.id.message_text);
                TextView messageUser = v.findViewById(R.id.message_user);
                TextView messageTime = v.findViewById(R.id.message_time);

                messageText.setText(model.getMessageText());
                messageUser.setText(model.getFromUser());

                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                        model.getMessageTime()));
            }

            @Override
            public View getView(int position, View view, ViewGroup viewGroup) {

                ChatMessage model = getItem(position);
                View view2 = mActivity.getLayoutInflater().inflate(mLayout, viewGroup, false);
                // Call out to subclass to marshall this model into the provided view
                populateView(view2, model, position);
                return view2;
            }

            @Override
            public ChatMessage getItem(int position) {
                ChatMessage chtm=super.getItem(position);
                if (chtm.getFromUserUUID().equals(User.getCurrentUser().getUuid())){
                    mLayout=R.layout.chat_list_item_right;
                    Log.e(chtm.getMessageText().toUpperCase(), String.valueOf(mLayout));
                }
                else {
                    mLayout=R.layout.chat_list_item_left;
                    Log.e(chtm.getMessageText().toUpperCase(), String.valueOf(mLayout));
                }
                return chtm;
            }
        };
        listView.setAdapter(adapter);
        listView.setStackFromBottom(true);
        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        if (adapter!=null)
            adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.fab){
            sendMessage();
        }
    }

    private void sendMessage() {
        FirebaseDatabase.getInstance()
                .getReference("message")
                .child(generateKey())
                .push()
                .setValue(new ChatMessage(input.getText().toString(),
                        User.getCurrentUser().getName(),User.getCurrentUser().getUuid(),receiverUuid));
        input.setText("");
    }

    private String generateKey(){
        ArrayList<String> templist=new ArrayList<>();
        templist.add(User.getCurrentUser().getUuid());
        templist.add(receiverUuid);
        Collections.sort(templist);
        return templist.get(0)+templist.get(1);
    }

    public static Intent newIntent(Context context, String toUserUUID,String toUserName, String photo_url){
        Intent intent=new Intent(context,ChatActivity.class);
        intent.putExtra(KEY_TO_RECEIVER_UUID,toUserUUID);
        intent.putExtra(KEY_TO_RECEIVER_NAME,toUserName);
        intent.putExtra(KEY_TO_RECEIVER_PHOTO_URL,photo_url);
        return intent;
    }
}
