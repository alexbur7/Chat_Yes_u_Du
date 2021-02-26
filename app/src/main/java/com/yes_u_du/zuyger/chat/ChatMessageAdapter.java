package com.yes_u_du.zuyger.chat;

import android.content.Context;
import android.text.format.DateFormat;
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
import com.yes_u_du.zuyger.R;
import com.yes_u_du.zuyger.account.User;
import com.yes_u_du.zuyger.dialog.EditMessageDialog;
import com.yes_u_du.zuyger.photo_utils.PhotoViewPagerItemFragment;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Collections;

import static com.yes_u_du.zuyger.chat.ChatBaseFragment.EDIT_MSG_DIALOG_CODE;

public class ChatMessageAdapter extends FirebaseRecyclerAdapter<ChatMessage, ChatMessageAdapter.ChatMessageHolder> implements SetFunctionalAdapter{

    private String receiverUuid;
    private Context context;
    private FragmentManager manager;
    private String firstKey;
    private Fragment fragment;
    private int typeUser;

    public ChatMessageAdapter(Class<ChatMessage> modelClass, int modelLayout, Class<ChatMessageHolder> viewHolderClass, DatabaseReference ref, String receiverUuid, Context context, FragmentManager manager, Fragment fragment, int typeUser) {
        super(modelClass, modelLayout, viewHolderClass, ref);

        this.receiverUuid= receiverUuid;
        this.context= context;
        this.manager=manager;
        this.fragment= fragment;
        this.typeUser=typeUser;
        generateKey();
    }

    @Override
    protected void populateViewHolder(ChatMessageHolder chatMessageHolder, ChatMessage chatMessage, int i) {
        chatMessageHolder.onBind(chatMessage,i);
    }

    @Override
    public ChatMessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view2 = LayoutInflater.from(context).inflate(viewType, parent, false);
        return new ChatMessageHolder(view2);
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage chatMessage = super.getItem(position);
        if (User.getCurrentUser().getUuid().equals(firstKey)) {
            if (!chatMessage.getFirstDelete().equals("delete")) {
                setMessageLayout(chatMessage);
            } else mModelLayout = R.layout.delete_message;
        } else {
            if (!chatMessage.getSecondDelete().equals("delete")) {
                setMessageLayout(chatMessage);
            } else mModelLayout = R.layout.delete_message;
        }
        return mModelLayout;
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }


    @Override
    public void setMessageLayout(ChatMessage chatMessage) {
        if (chatMessage.getFromUserUUID().equals(User.getCurrentUser().getUuid()) && chatMessage.getImage_url() == null) {
            mModelLayout = R.layout.chat_list_item_right;
        } else if (chatMessage.getFromUserUUID().equals(User.getCurrentUser().getUuid()) && chatMessage.getImage_url() != null) {
            mModelLayout = R.layout.chat_list_item_right_with_image;
        } else if (!chatMessage.getFromUserUUID().equals(User.getCurrentUser().getUuid()) && chatMessage.getImage_url() != null) {
            mModelLayout = R.layout.chat_list_item_left_with_image;
        } else {
            mModelLayout = R.layout.chat_list_item_left;
        }
    }

    @Override
    public String generateKey() {
        ArrayList<String> templist=new ArrayList<>();
        templist.add(User.getCurrentUser().getUuid());
        templist.add(receiverUuid);
        Collections.sort(templist);
        firstKey=templist.get(0);
        return templist.get(0)+templist.get(1);
    }

    public class ChatMessageHolder extends RecyclerView.ViewHolder{
        private TextView messageText, messageUser, messageTime;
        private ImageView imageSend;
        private ImageView editImage;
        private View v;
        public ChatMessageHolder(@NonNull View itemView) {
            super(itemView);
            v=itemView;
            messageText = itemView.findViewById(R.id.message_text);
            messageUser = itemView.findViewById(R.id.message_user);
            messageTime = itemView.findViewById(R.id.message_time);
            imageSend = itemView.findViewById(R.id.image_send);
            editImage = itemView.findViewById(R.id.edit_image);
        }

        public void onBind(ChatMessage model, int position){
            if (User.getCurrentUser().getUuid().equals(firstKey)) {
                if (!model.getFirstDelete().equals("delete")) {
                    createMessage(model);

                    ImageView seenImage = v.findViewById(R.id.seen_image);
                    if (!model.getSecondSeen().equals(context.getString(R.string.not_seen_text)) && !model.getToUserUUID().equals(context.getString(R.string.admin_key))) {
                        try {
                            seenImage.setImageResource(R.drawable.seen_image);
                            seenImage.setVisibility(View.VISIBLE);
                        } catch (Exception e) {
                            seenImage.setVisibility(View.INVISIBLE);
                        }
                    }
                    else {
                        seenImage.setVisibility(View.INVISIBLE);
                    }
                }
            } else {
                if (!model.getSecondDelete().equals("delete")) {
                    createMessage(model);

                    ImageView seenImage = v.findViewById(R.id.seen_image);
                    if (!model.getFirstSeen().equals(context.getString(R.string.not_seen_text))) {
                        try {
                            seenImage.setImageResource(R.drawable.seen_image);
                            seenImage.setVisibility(View.VISIBLE);
                        } catch (Exception e) {
                            seenImage.setVisibility(View.INVISIBLE);
                        }
                    }
                    else {
                        seenImage.setVisibility(View.INVISIBLE);
                    }
                }
            }
            if (User.getCurrentUser().getUuid().equals(model.getFromUserUUID()))
                clickMessage(v, getRef(position), model.getMessageText(), EditMessageDialog.TYPE_OF_MSG_MY);
            else
                clickMessage(v, getRef(position), model.getMessageText(), EditMessageDialog.TYPE_OF_MSG_NOT_MY);
        }

        private void createMessage(ChatMessage model) {
            messageText.setText(model.getMessageText());
            messageUser.setText(model.getFromUser());

            String dateDayMonthYear = (String) DateFormat.format("dd MMMM yyyy HH:mm", model.getMessageTime());
            if (dateDayMonthYear.charAt(0) == '0') {
                dateDayMonthYear = dateDayMonthYear.substring(1);
            }
            messageTime.setText(dateDayMonthYear);

            if (model.getImage_url() != null) {
                Glide.with(context).load(model.getImage_url()).into(imageSend);
                setClickListenerOnImage(model, imageSend);
            }
            if (model.getEdited().equals("yes")) {
                editImage.setVisibility(View.VISIBLE);
            } else editImage.setVisibility(View.GONE);
        }

        private void setClickListenerOnImage(ChatMessage model, ImageView imageView) {
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment newDetail = PhotoViewPagerItemFragment.newInstance(model.getImage_url(),imageView);
                    manager.beginTransaction()
                            .addToBackStack(null)
                            .add(R.id.fragment_container,newDetail)
                            .commit();
                    imageView.setEnabled(false);
                }
            });
        }

        private void clickMessage(View v, DatabaseReference reference, String messageText, int type) {
            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    EditMessageDialog editMessageDialog = new EditMessageDialog(reference,receiverUuid,messageText,type,typeUser);
                    editMessageDialog.setTargetFragment(fragment, EDIT_MSG_DIALOG_CODE);
                    editMessageDialog.show(manager,null);
                    return true;
                }
            });
        }

    }

}
