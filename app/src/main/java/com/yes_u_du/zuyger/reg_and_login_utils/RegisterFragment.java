package com.yes_u_du.zuyger.reg_and_login_utils;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.yes_u_du.zuyger.R;
import com.yes_u_du.zuyger.dialog.CalendarDialog;
import com.yes_u_du.zuyger.rules_and_policy.InformationActivity;
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

import java.util.Calendar;
import java.util.Date;

import static android.app.Activity.RESULT_OK;


public class RegisterFragment extends Fragment{
    private static  final  int IMAGE_REQUEST=1;
    private static final int CALENDAR_REQUEST=11;


    private FirebaseAuth auth;
    private FirebaseDatabase db;
    private DatabaseReference ref;
    private Callbacks callbacks;
    private Spinner sexSpinner;
    private EditText nameEditText;
    private EditText surnameEditText;
    private Spinner countrySpinner;
    private Spinner regionSpinner;
    private EditText cityEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    //private EditText ageEditText;
    private EditText aboutEditText;
    private Button regButton;
    private TextView photoDemands;
    private ImageView photoImageView, photoImageView1;
    private Uri imageUri;
    private String uri1,uri2;
    private int imageNumber;
    private StorageTask uploadTask;
    private TextView calendarTextView;
    //private TextView ruleText;
    private Date dateOfBirth;

    private String status_offline;

    private ArrayAdapter<CharSequence> countryAdapter;
    private ArrayAdapter<CharSequence> regionAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.register_fragment,container,false);
        status_offline=getResources().getString(R.string.label_offline);
        nameEditText=view.findViewById(R.id.name_edit_text);
        surnameEditText=view.findViewById(R.id.surname_edit_text);

        regionSpinner=view.findViewById(R.id.region_reg_spinner);
        countrySpinner=view.findViewById(R.id.country_spinner);
        regionAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.region_filter_rus, android.R.layout.simple_spinner_item);
        regionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        regionSpinner.setAdapter(regionAdapter);
        countryAdapter =ArrayAdapter.createFromResource(getActivity(), R.array.country_filter, android.R.layout.simple_spinner_item);
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(countryAdapter);
        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1: {
                        regionAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.region_filter_rus, android.R.layout.simple_spinner_item);
                        regionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        regionSpinner.setAdapter(regionAdapter);
                    }
                    break;
                    case 2: {
                        regionAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.region_filter_armenia, android.R.layout.simple_spinner_item);
                        regionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        regionSpinner.setAdapter(regionAdapter);
                    }
                    break;
                    case 4:{
                        regionAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.region_filter_usa, android.R.layout.simple_spinner_item);
                        regionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        regionSpinner.setAdapter(regionAdapter);
                    }
                    break;
                    default:{
                        regionAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.no_region_filter, android.R.layout.simple_spinner_item);
                        regionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        regionSpinner.setAdapter(regionAdapter);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        cityEditText=view.findViewById(R.id.city_edit_text);
        emailEditText=view.findViewById(R.id.email_reg_edit_text);
        passwordEditText=view.findViewById(R.id.password_reg_edit_text);
        photoDemands = view.findViewById(R.id.photo_demands);
        photoDemands.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String informationText = getResources().getString(R.string.photo_demands);
                Intent intent = InformationActivity.newIntent(getActivity(),informationText);
                startActivity(intent);
            }
        });
        //ageEditText=view.findViewById(R.id.age_reg_edit_text);
        aboutEditText=view.findViewById(R.id.about_edit_text);
        sexSpinner = view.findViewById(R.id.spinner_sex);
        photoImageView = view.findViewById(R.id.photo1);
        photoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage(1);
            }
        });
        photoImageView1 = view.findViewById(R.id.photo2);
        photoImageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage(2);
            }
        });
        regButton=view.findViewById(R.id.registration_button);
        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRegistration();
            }
        });
        calendarTextView = view.findViewById(R.id.call_calendar_text_view);
        calendarTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarDialog dialog = CalendarDialog.newInstance(new Date());
                dialog.setTargetFragment(RegisterFragment.this,CALENDAR_REQUEST);
                dialog.show(getFragmentManager(),null);
            }
        });
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        return view;
    }


    private void setRegistration(){
        final  String name = nameEditText.getText().toString();
        final  String surname = surnameEditText.getText().toString();
        final  String country = String.valueOf(countrySpinner.getSelectedItemPosition());
        final  String city = cityEditText.getText().toString();
        final  String email = emailEditText.getText().toString();
        final  String password = passwordEditText.getText().toString();
        final  String sex = sexSpinner.getSelectedItem().toString();
        final  String region = String.valueOf(regionSpinner.getSelectedItemPosition());
        final  String about=aboutEditText.getText().toString();
        if (uri1 ==null || uri2 == null){
            Toast.makeText(getActivity(),R.string.required_photos,Toast.LENGTH_SHORT).show();
            return;
        }

        if (name.isEmpty() || city.isEmpty() || email.isEmpty() || password.isEmpty() || sex.isEmpty() || country.equals("0") ||(country.equals("1") && region.equals("0")
                 || (country.equals("2") && region.equals("0"))|| (country.equals("3") && region.equals("0")))){
            Toast.makeText(getActivity(),R.string.reject_reg,Toast.LENGTH_SHORT).show();
            return;
        }

        if (dateOfBirth == null){
            Toast.makeText(getActivity(), R.string.enter_date_birthday, Toast.LENGTH_SHORT).show();
            return;
        }

        Calendar calendar =Calendar.getInstance();
        calendar.setTime(dateOfBirth);
        AgeCalculation ageCalculation = new AgeCalculation ();
        ageCalculation.getCurrentDate();
        ageCalculation.setDateOfBirth(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        //final  String age = ageEditText.getText().toString();
        final String age = String.valueOf(ageCalculation.calculateYear());

        if (ageCalculation.calculateYear()<0){
            Toast.makeText(getActivity(), R.string.enter_true_age,Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length()<6){
            Toast.makeText(getActivity(),R.string.password_length_short,Toast.LENGTH_SHORT).show();
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
                    ref.child(getActivity().getResources().getString(R.string.admin_key)).setValue("false");
                    ref.child("online_time").setValue((new Date()).getTime());
                    ref.child("admin_block").setValue("unblock");
                    ref.child("perm_block").setValue("unblock");
                    ref.child("verified").setValue("no");
                    //changed
                    ref.child("about").setValue(about);
                    ref.child("typing").setValue("unwriting");
                    ref.child("dateBirthday").setValue(dateOfBirth.getTime());

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
        this.imageNumber =i;
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
            uploadImage(imageNumber);
        }
        else if (requestCode == CALENDAR_REQUEST && resultCode == RESULT_OK){
            dateOfBirth = (Date) data.getSerializableExtra(CalendarDialog.EXTRA_DATE);
            Toast.makeText(getActivity(),R.string.age_was_add, Toast.LENGTH_SHORT).show();
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

    public static class AgeCalculation{
        private int startYear;
        private int startMonth;
        private int startDay;
        private int endYear;
        private int endMonth;
        private int endDay;
        private int resYear;
        private int resMonth;
        private int resDay;
        private Calendar start;
        private Calendar end;


        public void getCurrentDate() {
            end = Calendar.getInstance();
            endYear = end.get(Calendar.YEAR);
            endMonth = end.get(Calendar.MONTH);
            endMonth++;
            endDay = end.get(Calendar.DAY_OF_MONTH);
        }

        public void setDateOfBirth(int sYear, int sMonth, int sDay) {
            startYear = sYear;
            startMonth = sMonth;
            startDay = sDay;
        }

        public int calculateYear() {
            resYear = endYear - startYear;
            if (endMonth-1 < startMonth){ resYear --; }
            if (endMonth-1==startMonth && startDay> endDay){ resYear--; }
            return resYear;
        }

    }
}

