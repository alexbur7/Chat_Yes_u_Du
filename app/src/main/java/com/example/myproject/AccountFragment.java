package com.example.myproject;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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

public class AccountFragment extends Fragment {
    private ImageView photoImageView;
    private TextView countryTextView;
    private TextView regionTextView;
    private TextView cityTextView;
    private TextView nameTextView;
    private TextView ageTextView;
    private TextView sexTextView;
    private Button editButton;

    private StorageReference storageReference;
    private static  final  int IMAGE_REQUEST=1;
    private Uri imageUri;
    private StorageTask uploadTask;
    private DatabaseReference reference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.account_fragment,container,false);
        editButton = v.findViewById(R.id.edit_button);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditDialog editDialog = new EditDialog();
                editDialog.show(getFragmentManager(),null);
            }
        });
        photoImageView = v.findViewById(R.id.photo_view);
        countryTextView = v.findViewById(R.id.country_textView);
        regionTextView = v.findViewById(R.id.region_textView);
        cityTextView = v.findViewById(R.id.city_textView);
        nameTextView = v.findViewById(R.id.my_name_text);
        ageTextView = v.findViewById(R.id.age_textView);
        sexTextView = v.findViewById(R.id.sex_textView);
        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        photoImageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                openImage();
            }
        });
        status("online");
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.account_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout: {
                status("offline");
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), LogActivity.class));
                getActivity().finish();
            }
            case R.id.delete_account:{
                FirebaseAuth.getInstance().getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getActivity(),"Completed",Toast.LENGTH_SHORT);
                        startActivity(new Intent(getActivity(), LogActivity.class));
                        getActivity().finish();
                    }
                });
            }
            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setCurrentUser();
    }

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST);
    }

    private void setAllTextView(){
        countryTextView.setText(User.getCurrentUser().getCountry());
        regionTextView.setText(User.getCurrentUser().getRegion());
        cityTextView.setText(User.getCurrentUser().getCity());
        ageTextView.setText(User.getCurrentUser().getAge());
        sexTextView.setText(User.getCurrentUser().getSex());
        if (User.getCurrentUser().getSurname().equals(""))
        nameTextView.setText(User.getCurrentUser().getName());
        else
            nameTextView.setText(User.getCurrentUser().getName() +" "+User.getCurrentUser().getSurname());
    }

    private void setCurrentUser() {
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage(getResources().getString(R.string.uploading));
        pd.show();
        String uuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference("users").child(uuid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                User.setCurrentUser(user, uuid);
                if(user.getPhoto_url().equals("default")){
                    photoImageView.setImageResource(R.drawable.unnamed);
                }
                else {
                    if (isAdded()) Glide.with(getContext()).load(user.getPhoto_url()).into(photoImageView);
                }
                setAllTextView();
                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }

        });
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

        if (imageUri != null){
            final StorageReference fileReference= storageReference.child(System.currentTimeMillis()+
                    "."+getFileExtension(imageUri));
            uploadTask = fileReference.putFile(imageUri);
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
                        String mUri = downloadUri.toString();

                        reference = FirebaseDatabase.getInstance().getReference("users").child(User.getCurrentUser().getUuid());
                        HashMap<String,Object> map = new HashMap<>();
                        map.put("photo_url",mUri);

                        reference.updateChildren(map);
                        pd.dismiss();
                    }
                    else {
                        Toast.makeText(getContext(),R.string.failed_update_photo,Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
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
            imageUri = data.getData();
            uploadImage();
        }
    }

    private void status(String status){
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getUid()).updateChildren(hashMap);
    }
}
