package com.maidenexpress;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {

    Button btnSignIn,btnSignUp;
    int current_fragment;
    Animation animation;
    private GestureDetector gestureDetector;
    LoginFragment loginFragment = new LoginFragment();
    SignUpFragment signUpFragment = new SignUpFragment();


    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseAuth = firebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        animation = AnimationUtils.loadAnimation(this, R.anim.blink);
        btnSignIn = findViewById(R.id.btnSignIn);
        btnSignUp =  findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSignUp.startAnimation(animation);
                current_fragment = 1;
                setSignUpFragment();
            }
        });
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSignIn.startAnimation(animation);
                current_fragment = 0;
                setSignInFragment();
            }
        });

        if(firebaseUser!=null){
            Intent intent = new Intent(this,MainActivity.class);
            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        }
        else {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().replace(R.id.fmLoginContainer,loginFragment);
            fragmentTransaction.addToBackStack(null).commit();
            current_fragment = 0;
        }

        gestureDetector = new GestureDetector(this);
    }

    public void setSignInFragment()
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_left,R.anim.exit_right)
                .replace(R.id.fmLoginContainer,loginFragment);
        fragmentTransaction.addToBackStack(null).commit();
        btnSignIn.setBackground(getResources().getDrawable(R.drawable.selected_sign_in_button));
        btnSignUp.setBackground(getResources().getDrawable(R.drawable.sign_up_button));
        current_fragment = 0;
    }

    public void setSignUpFragment()
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_right,R.anim.exit_left)
                .replace(R.id.fmLoginContainer,signUpFragment);
        fragmentTransaction.addToBackStack(null).commit();
        btnSignIn.setBackground(getResources().getDrawable(R.drawable.sign_in_button));
        btnSignUp.setBackground(getResources().getDrawable(R.drawable.selected_sign_up_button));
        current_fragment = 1;
    }


    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        float diffY = e2.getY() - e1.getY();
        float diffX = e2.getX() - e1.getX();
        if(Math.abs(diffX) > Math.abs(diffY)){
            if(Math.abs(diffX) > 100 && Math.abs(velocityX) > 100){
                if(diffX > 0){
                    //swipe right
                    setSignInFragment();
                    current_fragment = 0;
                }
                else{
                    //swipe left
                    setSignUpFragment();
                    current_fragment = 1;
                }
            }
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
