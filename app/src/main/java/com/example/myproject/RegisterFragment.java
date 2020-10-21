package com.example.myproject;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
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
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.Date;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;


public class RegisterFragment extends Fragment {
    private static  final  int IMAGE_REQUEST=1;
    private FirebaseAuth auth;
    private FirebaseDatabase db;
    private DatabaseReference ref;
    private Callbacks callbacks;
    private Spinner spinner;
    private EditText nameEditText;
    private EditText surnameEditText;
    private EditText countryEditText;
    private EditText regionEditText;
    private EditText cityEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText ageEditText;
    private EditText aboutEditText;
    private Button regButton;
    private ImageView photoImageView, photoImageView1;
    private Uri imageUri;
    private String uri1,uri2;
    private int i;
    private StorageTask uploadTask;

    private String status_offline;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.register_fragment,container,false);
        status_offline=getResources().getString(R.string.label_offline);
        nameEditText=v.findViewById(R.id.name_edit_text);
        surnameEditText=v.findViewById(R.id.surname_edit_text);
        countryEditText=v.findViewById(R.id.country_edit_text);
        regionEditText=v.findViewById(R.id.region_reg_edit_text);
        cityEditText=v.findViewById(R.id.city_edit_text);
        emailEditText=v.findViewById(R.id.email_reg_edit_text);
        passwordEditText=v.findViewById(R.id.password_reg_edit_text);
        ageEditText=v.findViewById(R.id.age_reg_edit_text);
        aboutEditText=v.findViewById(R.id.about_edit_text);
        spinner = v.findViewById(R.id.spinner_sex);
        photoImageView = v.findViewById(R.id.photo1);
        photoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage(1);
            }
        });
        photoImageView1 = v.findViewById(R.id.photo2);
        photoImageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage(2);
            }
        });
        regButton=v.findViewById(R.id.registration_button);
        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRegistration();
            }
        });
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        return v;
    }

    private void setRegistration(){
        final  String name = nameEditText.getText().toString();
        final  String surname = surnameEditText.getText().toString();
        final  String country = countryEditText.getText().toString();
        final  String city = cityEditText.getText().toString();
        final  String email = emailEditText.getText().toString();
        final  String password = passwordEditText.getText().toString();
        final  String sex = spinner.getSelectedItem().toString();
        final  String region = regionEditText.getText().toString();
        final  String age = ageEditText.getText().toString();
        final  String about=aboutEditText.getText().toString();

        if (name.isEmpty() || country.isEmpty() || city.isEmpty() || email.isEmpty() || password.isEmpty() || sex.isEmpty() || Integer.parseInt(age)<0){
            Toast.makeText(getActivity(),R.string.reject_reg,Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length()<6){
            Toast.makeText(getActivity(),R.string.password_length_short,Toast.LENGTH_SHORT).show();
            return;
        }

        if (uri1 ==null || uri2 == null){
            Toast.makeText(getActivity(),R.string.required_photos,Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getActivity(),R.string.is_successful,Toast.LENGTH_SHORT).show();
                    ref = db.getReference("users").child(auth.getCurrentUser().getUid());
                    ref.child("name").setValue(name);
                    ref.child("surname").setValue(surname);
                    ref.child("country").setValue(country);
                    ref.child("city").setValue(city);
                    ref.child("photo_url").setValue(uri1);
                    ref.child("photo_url1").setValue(uri2);
                    ref.child("photo_url2").setValue("default");
                    ref.child("photo_url3").setValue("default");
                    ref.child("region").setValue(region);
                    ref.child("sex").setValue(sex);
                    ref.child("age").setValue(age);
                    ref.child("status").setValue(status_offline);
                    //TODO admin gavnina
                    ref.child(getActivity().getResources().getString(R.string.admin_key)).setValue("false");
                    ref.child("online_time").setValue((new Date()).getTime());
                    ref.child("admin_block").setValue("unblock");
                    //changed
                    ref.child("about").setValue(about);
                    auth.getCurrentUser().sendEmailVerification();
                    auth.signOut();
                    callbacks.returnLoginFragment(email,password);
                }
                else {
                    Toast.makeText(getActivity(), R.string.is_not_successful, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        callbacks = (Callbacks) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callbacks = null;
    }

    public interface Callbacks{
        void returnLoginFragment(String email,String pass);
    }

    private void openImage(int i) {
        this.i=i;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage(int i){
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage(getResources().getString(R.string.uploading));
        pd.show();

        if (imageUri != null){
            final StorageReference fileReference= FirebaseStorage.getInstance().getReference("uploads").child(System.currentTimeMillis()+
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
                        downloadUri(downloadUri, i);
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
            imageUri = data.getData();
            uploadImage(i);
        }
    }

    private void downloadUri(Uri uri,int i) {
        if (i == 1) {
            uri1 = uri.toString();
            Glide.with(getContext()).load(uri).into(photoImageView);
        } else {
            uri2 = uri.toString();
            Glide.with(getContext()).load(uri).into(photoImageView1);
        }
    }
}

