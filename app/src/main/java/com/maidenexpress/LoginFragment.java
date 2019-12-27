package com.maidenexpress;


import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
public class LoginFragment extends Fragment implements View.OnClickListener{

    static final int GOOGLE_SIGNIN = 100;
    GoogleSignInClient googleSignInClient;
    Button btnLogin;
    EditText etLoginEmail,etLoginPassword;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore db;
    ImageView loginGoogle,loginFacebook;
    CallbackManager callbackManager;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getContext());
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firebaseAuth = firebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        btnLogin = getActivity().findViewById(R.id.btnLogin);
        etLoginEmail = getActivity().findViewById(R.id.etLoginEmail);
        etLoginPassword = getActivity().findViewById(R.id.etLoginPassword);
        loginGoogle = getActivity().findViewById(R.id.loginGoogle);
        loginFacebook = getActivity().findViewById(R.id.loginFacebook);
        loginGoogle.setOnClickListener(this);
        loginFacebook.setOnClickListener(this);
        btnLogin.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(getActivity(),gso);
        registerFBSignInClient();
    }


    //--------------Send user info to Firestore-----------------


    private void setNewUser(){
        DocumentReference docRef = db.collection("customers").document(firebaseUser.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (!document.exists()) {
                        Customer customer = new Customer(firebaseUser.getDisplayName(),firebaseUser.getEmail(),""
                                ,firebaseUser.getPhoneNumber(),false
                                ,new Date().getTime(),new Date().getTime()
                                ,"","",0,null,0);
                        db.collection("customers").document(firebaseUser.getUid()).set(customer);
                        Log.d("getUserInfo", "Success");
                    }
                } else {
                    Log.e("getUserInfo", task.getException().getMessage());
                }
            }
        });
    }
    //--------------Sign in with Google-----------------

    private void signInGoogle(){
        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent,GOOGLE_SIGNIN);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    firebaseUser = firebaseAuth.getCurrentUser();
                    setNewUser();
                    Toast.makeText(getContext(), "Sign in success", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getContext(),MainActivity.class);
                    startActivity(intent);
                    getActivity().finish();


                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GOOGLE_SIGNIN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if(account != null){
                    firebaseAuthWithGoogle(account);
                }
            } catch (ApiException e) {
                Log.w("GOOGLE_SIGNIN", "Google sign in failed", e);
            }
        }
        callbackManager.onActivityResult(requestCode,resultCode,data);

    }


    //-----------Sign in with Email---------------

    private void signInWithEmail(){
        String email = etLoginEmail.getText().toString();
        String password = etLoginPassword.getText().toString();
        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(getContext(), "Please fulfill information", Toast.LENGTH_SHORT).show();
        }
        else {
            firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        firebaseUser = firebaseAuth.getCurrentUser();
                        if(firebaseUser.isEmailVerified()){
                            Toast.makeText(getContext(), "Sign-in Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getContext(),MainActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                        }
                        else{
                            Toast.makeText(getContext(), "Your email is not verified", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    //----------Click Action--------------
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //Login with email
            case R.id.btnLogin: {
                Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.blink);
                btnLogin.startAnimation(anim);
                signInWithEmail();
                break;
            }
            //Login with google
            case R.id.loginGoogle: {
                signInGoogle();
                break;
            }
            //Login with facebook
            case R.id.loginFacebook: {
                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));
                break;
            }
        }
    }


    private void registerFBSignInClient() {
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d("FBSignin",loginResult.toString());
                        // getFacebookAccountInfo(loginResult.getAccessToken());

                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Log.d("FBSignin","Cancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.d("FBSignin",exception.getMessage());
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("handleFB", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            firebaseUser = firebaseAuth.getCurrentUser();
                            setNewUser();
                            Toast.makeText(getContext(), "Sign in success", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getContext(),MainActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("handleFB", "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }
}
