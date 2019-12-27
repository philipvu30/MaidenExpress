package com.maidenexpress;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment{

    Button btnSignUpWithInfo;
    EditText etSignUpFullName, etSignUpPassword, etSignUpEmail;
    String fullName, password, email;
    Button btnSignIn, btnSignUp;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;



    public SignUpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnSignUpWithInfo = view.findViewById(R.id.btnSignUpWithInfo);
        etSignUpFullName = view.findViewById(R.id.etSignUpFullName);
        etSignUpPassword = view.findViewById(R.id.etSignUpPassword);
        etSignUpEmail = view.findViewById(R.id.etSignUpEmail);
        btnSignIn = getActivity().findViewById(R.id.btnSignIn);
        btnSignUp = getActivity().findViewById(R.id.btnSignUp);
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        btnSignUpWithInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.blink);
                btnSignUpWithInfo.startAnimation(anim);
                fullName = etSignUpFullName.getText().toString();
                password = etSignUpPassword.getText().toString();
                email = etSignUpEmail.getText().toString();

                if (email.isEmpty() || fullName.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getContext(), "Please fill in your info", Toast.LENGTH_SHORT).show();
                    return;
                }
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Sign up success", Toast.LENGTH_SHORT).show();
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fmLoginContainer, new LoginFragment()).commit();
                            btnSignIn.setBackground(getResources().getDrawable(R.drawable.selected_sign_in_button));
                            btnSignUp.setBackground(getResources().getDrawable(R.drawable.sign_up_button));
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(fullName).build();
                            firebaseUser.updateProfile(profileUpdates);
                            firebaseUser.sendEmailVerification();
                            String uid = firebaseUser.getUid();
                            Customer customer = new Customer(fullName,email,password,"",true,new Date().getTime(),new Date().getTime(),"","",0,null,0);
                            db.collection("customers").document(uid).set(customer);
                            firebaseAuth.signOut();
                            etSignUpEmail.setText("");
                            etSignUpFullName.setText("");
                            etSignUpPassword.setText("");


                        } else {
                            Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }


}
