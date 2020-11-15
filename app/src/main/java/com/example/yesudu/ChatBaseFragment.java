package com.example.yesudu;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Layout;
import android.text.SpannableString;
import android.text.format.DateFormat;
import android.text.style.AlignmentSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
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
import java.util.HashMap;
import static android.app.Activity.RESULT_OK;

public abstract class ChatBaseFragment extends Fragment implements View.OnClickListener {
    public static final String KEY_TO_RECEIVER_UUID="recevierID";
    public static final String KEY_TO_RECEIVER_PHOTO_URL = "recevierPHOTO_URL";
    protected static final int EDIT_MSG_DIALOG_CODE = 0;
    protected String receiverUuid;
    protected String receiverPhotoUrl;
    protected FloatingActionButton fab, send_image;
    protected Toolbar toolbar;
    protected EditText input;
    protected TextView username;
    protected TextView statusText;
    protected ImageView complainView;
    protected FirebaseListAdapter<ChatMessage> adapter;
    protected ListView listView;
    protected ImageView circleImageView;
    protected DatabaseReference reference;
    protected String firstKey, secondKey;
    private ValueEventListener seenListener;
    protected ValueEventListener blockListener;
    private StorageTask uploadTask;
    protected StorageReference storageReference;

    protected ChatFragment.CallBack activity;
    private static  final  int IMAGE_REQUEST=1;
    protected Uri image_rui;
    public boolean isEditing;
    protected String delete_string;
    protected String admin_string;

    protected ImageView imageView;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity=(ChatFragment.CallBack) context;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        activity=null;
    }

    abstract void displayChatMessages();

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.fab){
            sendMessage();
        }
        if (v.getId()==R.id.send_image_button){
            openImage();
        }
    }

    protected void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST);
    }

    abstract void sendMessage();

    private void sendMessage(String key) {
        DatabaseReference referenceDB=reference.child(generateKey()).child("message").child(key);
        HashMap<String,Object> map=new HashMap<>();
        map.put("messageText",input.getText().toString());
        map.put("edited","yes");
        referenceDB.updateChildren(map);

        input.setText("");
        fab.setImageResource(R.drawable.baseline_send_black_24dp);
        fab.setOnClickListener(this);
    }

    abstract void clickMessage(View v, DatabaseReference reference, String messageText,int type);


    protected String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    protected void setClickListenerOnImage(ChatMessage model, ImageView imageView) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment newDetail = PhotoViewPagerItemFragment.newInstance(model.getImage_url(),imageView);
                getFragmentManager().beginTransaction()
                        .addToBackStack(null)
                        .add(R.id.fragment_container,newDetail)
                        .commit();
                imageView.setEnabled(false);
            }
        });
    }

    protected void uploadImage(){
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage(getResources().getString(R.string.uploading));
        pd.show();

        if (image_rui != null){
            storageReference = FirebaseStorage.getInstance().getReference("ChatImage");
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

    protected void setToolbarToAcc() {
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = UserAccountActivity.newIntent(getContext(), receiverUuid);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode==EDIT_MSG_DIALOG_CODE){
            if (!isEditing) toolbar.inflateMenu(R.menu.edit_menu);
           // toolbar.getMenu().getItem(0).setEnabled(false);
            MenuItem item =  toolbar.getMenu().getItem(0);
            item.setEnabled(false);
            toolbar.setBackgroundColor(getActivity().getResources().getColor(R.color.colorToolbar));
            username.setVisibility(View.GONE);
            statusText.setVisibility(View.GONE);
            complainView.setVisibility(View.GONE);
            send_image.setEnabled(false);
            isEditing=true;
            toolbar.setOnClickListener(null);
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId()==R.id.cancel_edit_item) {
                        setupEditCancel();
                        return true;
                    }
                    return false;
                }
            });
            input.setText(data.getStringExtra(EditMessageDialog.KEY_TO_MSG_TEXT));
            fab.setImageResource(R.drawable.edit_msg_icon);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("KEY_TO_REF:",data.getStringExtra(EditMessageDialog.KEY_TO_REF));
                    sendMessage(data.getStringExtra(EditMessageDialog.KEY_TO_REF));
                    setupEditCancel();
                }
            });

        }
        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
                && data!=null && data.getData() !=null
        ){
            image_rui = data.getData();
            uploadImage();
        }
    }

    public void setupEditCancel() {
        username.setVisibility(View.VISIBLE);
        statusText.setVisibility(View.VISIBLE);
        complainView.setVisibility(View.VISIBLE);
        isEditing=false;
        //circleImageView.setVisibility(View.VISIBLE);
        toolbar.setTitle("");
        toolbar.getMenu().clear();
        input.setText("");
        send_image.setEnabled(true);
        setToolbarToAcc();
    }

    abstract String generateKey();

    protected void setStatus(){
        FirebaseDatabase.getInstance().getReference("users").child(receiverUuid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                try {
                    if (user.getStatus().equals(getResources().getString(R.string.label_offline)))
                        statusText.setText(user.getStatus() + ": " + DateFormat.format("dd-MM-yyyy (HH:mm)", user.getOnline_time()));
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

    //TODO
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
