package com.example.myproject;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

public class AccountFragment extends Fragment {
    private ImageView photoImageView;
    private TextView countryTextView;
    private TextView regionTextView;
    private TextView cityTextView;
    private TextView nameTextView;
    private TextView ageTextView;
    private TextView sexTextView;

    StorageReference storageReference;
    private static  final  int IMAGE_REQUEST=1;
    private Uri imageUri;
    private StorageTask uploadTask;
    private DatabaseReference reference;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.account_fragment,container,false);
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
        setCurrentUser();
        ButterKnife.bind(this,v);
        return v;
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
        ageTextView.setText(String.valueOf(User.getCurrentUser().getAge()));
        sexTextView.setText(User.getCurrentUser().getSex());
        nameTextView.setText(User.getCurrentUser().getName()+" "+User.getCurrentUser().getSurname());
    }

    private void setCurrentUser() {
        String uuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.e("USER CHECK IN", uuid);
        FirebaseDatabase.getInstance().getReference("users").child(uuid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                User.setCurrentUser(user, uuid);
                if(user.getPhoto_url().equals("default")){
                    photoImageView.setImageResource(R.drawable.unnamed);
                }
                else {
                    Glide.with(getContext()).load(user.getPhoto_url()).into(photoImageView);
                }
                setAllTextView();
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
        pd.setMessage("Uploading");
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
            if (uploadTask !=null){
             Toast
             .makeText(getContext(),R.string.upload_progress,Toast.LENGTH_SHORT).show();
            }
            else {
                uploadImage();
            }
        }
    }
}
