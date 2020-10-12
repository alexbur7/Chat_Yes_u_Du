package com.example.myproject;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;



public class RegisterFragment extends Fragment {
    private FirebaseAuth auth;
    private FirebaseDatabase db;
    private DatabaseReference ref;
    private Callbacks callbacks;
    private Spinner spinner;

    @BindView(R.id.name_edit_text)
    EditText nameEditText;
    @BindView(R.id.surname_edit_text)
    EditText surnameEditText;
    @BindView(R.id.country_edit_text)
    EditText countryEditText;
    @BindView(R.id.region_reg_edit_text)
    EditText regionEditText;
    @BindView(R.id.city_edit_text)
    EditText cityEditText;
    @BindView(R.id.email_reg_edit_text)
    EditText emailEditText;
    @BindView(R.id.password_reg_edit_text)
    EditText passwordEditText;
    @BindView(R.id.age_reg_edit_text)
    EditText ageEditText;
    @BindView(R.id.registration_button)
    Button regButton;

    @OnClick(R.id.registration_button)
    public void onClick(){
        setRegistration();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.register_fragment,container,false);
        ButterKnife.bind(this,v);
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        spinner = v.findViewById(R.id.spinner_sex);
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

        if (name.isEmpty() || country.isEmpty() || city.isEmpty() || email.isEmpty() || password.isEmpty() || sex.isEmpty() || region.isEmpty() || Integer.parseInt(age)<0){
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

