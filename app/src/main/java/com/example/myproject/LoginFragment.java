package com.example.myproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class LoginFragment extends Fragment {

    private static final String KEY_TO_EMAIL = "KeyEmail";
    private static final String KEY_TO_PASSWORD = "KeyPassword";

    private Callback activityCallback;
    private Unbinder unbinder;

    @BindView(R.id.email_log_editText)
    EditText emailEditText;
    @BindView(R.id.password_log_editText)
    EditText passEditText;
    @BindView(R.id.reg_log_button)
    Button regButton;
    @BindView(R.id.login_log_button)
    Button logButton;

    @OnClick(R.id.reg_log_button)
    public void onClickReg() {
        activityCallback.onRegisterClicked();
    }

    @OnClick(R.id.login_log_button)
    public void onClickLog(){
        login();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activityCallback = (Callback) context;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser()!=null){
            Intent intent=new Intent(getActivity(),MyAccountActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activityCallback = null;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        emailEditText.setText(getArguments().getString(KEY_TO_EMAIL));
        passEditText.setText(getArguments().getString(KEY_TO_PASSWORD));
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public interface Callback {
        void onRegisterClicked();
    }

    private void login() {
        if (emailEditText.getText().toString() == null || passEditText.getText().toString() == null)
            return;
        FirebaseAuth.getInstance().
                signInWithEmailAndPassword(emailEditText.getText().toString(), passEditText.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            setCurrentUser();

                            Intent intent = new Intent(getActivity(), MyAccountActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                        }
                        else{
                            Toast.makeText(getActivity(),R.string.failed_login,Toast.LENGTH_SHORT);
                        }
                    }
                });
    }
    private void setCurrentUser(){
        String uuid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference("users").child(uuid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User.setCurrentUser(snapshot.getValue(User.class),uuid);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}

        });

    }

    public static Fragment newFragment(String email,String pass){
        LoginFragment fragment=new LoginFragment();
        Bundle bundle=new Bundle();
        bundle.putString(KEY_TO_EMAIL,email);
        bundle.putString(KEY_TO_PASSWORD,pass);
        fragment.setArguments(bundle);
        return fragment;
    }
}
