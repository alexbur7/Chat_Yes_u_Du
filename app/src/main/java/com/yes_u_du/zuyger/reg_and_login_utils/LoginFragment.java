package com.yes_u_du.zuyger.reg_and_login_utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.yes_u_du.zuyger.R;
import com.yes_u_du.zuyger.account.MyAccountActivity;
import com.yes_u_du.zuyger.account.User;
import com.yes_u_du.zuyger.rules_and_policy.InformationActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginFragment extends Fragment {

    private static final String KEY_TO_EMAIL = "KeyEmail";
    private static final String KEY_TO_PASSWORD = "KeyPassword";

    private Callback activityCallback;

    private EditText emailEditText;
    private EditText passEditText;
    private Button regButton;
    private Button logButton;
    private String offline_string;
    private TextView policeRule;
    private TextView resourceRule;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        offline_string =getResources().getString(R.string.label_offline);

        View view = inflater.inflate(R.layout.login_fragment, container, false);
        emailEditText=view.findViewById(R.id.email_log_editText);
        passEditText=view.findViewById(R.id.password_log_editText);
        regButton=view.findViewById(R.id.reg_log_button);
        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityCallback.onRegisterClicked();
            }
        });
        logButton=view.findViewById(R.id.login_log_button);
        logButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    login();
            }
        });
        policeRule = view.findViewById(R.id.rule_and_pol);
        policeRule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String informationText = getResources().getString(R.string.text_rule_policy);
                Intent intent = InformationActivity.newIntent(getActivity(),informationText);
                startActivity(intent);
            }
        });
        resourceRule = view.findViewById(R.id.rules_of_res);
        resourceRule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String informationText = getResources().getString(R.string.rules_resources_text);
                String resourceText = getResources().getString(R.string.resources_text_part);
                Intent intent = InformationActivity.newIntent(getActivity(),informationText+resourceText);
                startActivity(intent);
            }
        });
        if (getArguments()!=null) {
            emailEditText.setText(getArguments().getString(KEY_TO_EMAIL));
            passEditText.setText(getArguments().getString(KEY_TO_PASSWORD));
        }
        return view;
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
                Intent intent = new Intent(getActivity(), MyAccountActivity.class);
                startActivity(intent);
                getActivity().finish();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activityCallback = null;
    }

    public interface Callback {
        void onRegisterClicked();
    }

    private void login() {
        if (emailEditText.getText().toString().equals("")  || passEditText.getText().toString().equals("") ) {
            Toast.makeText(getActivity(),R.string.enter_email_and_password,Toast.LENGTH_SHORT).show();
            return;
        }
            FirebaseAuth.getInstance().
                    signInWithEmailAndPassword(emailEditText.getText().toString(), passEditText.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                if (!FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()){
                                    Toast.makeText(getActivity(),getResources().getString(R.string.you_dont_verification),Toast.LENGTH_SHORT).show();
                                    FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification();
                                    return;
                                }
                                setCurrentUser();
                                Intent intent = new Intent(getActivity(), MyAccountActivity.class);
                                startActivity(intent);
                                logButton.setEnabled(false);
                                getActivity().finish();
                            } else {
                                Toast.makeText(getActivity(), R.string.failed_login, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
    }
    private void setCurrentUser(){
        String uuid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("users").child(uuid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User.setCurrentUser(snapshot.getValue(User.class),uuid,offline_string);
                ref.removeEventListener(this);
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
