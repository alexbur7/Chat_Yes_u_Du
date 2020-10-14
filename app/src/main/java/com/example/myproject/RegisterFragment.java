package com.example.myproject;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RegisterFragment extends Fragment {
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
    private Button regButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.register_fragment,container,false);
        nameEditText=v.findViewById(R.id.name_edit_text);
        surnameEditText=v.findViewById(R.id.surname_edit_text);
        countryEditText=v.findViewById(R.id.country_edit_text);
        regionEditText=v.findViewById(R.id.region_reg_edit_text);
        cityEditText=v.findViewById(R.id.city_edit_text);
        emailEditText=v.findViewById(R.id.email_reg_edit_text);
        passwordEditText=v.findViewById(R.id.password_reg_edit_text);
        ageEditText=v.findViewById(R.id.age_reg_edit_text);
        spinner = v.findViewById(R.id.spinner_sex);
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

        if (name.isEmpty() || country.isEmpty() || city.isEmpty() || email.isEmpty() || password.isEmpty() || sex.isEmpty() || Integer.parseInt(age)<0){
            Toast.makeText(getActivity(),R.string.reject_reg,Toast.LENGTH_SHORT).show();
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
                    ref.child("photo_url").setValue("default");
                    ref.child("region").setValue(region);
                    ref.child("sex").setValue(sex);
                    ref.child("age").setValue(age);
                    ref.child("status").setValue("offline");
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
}

