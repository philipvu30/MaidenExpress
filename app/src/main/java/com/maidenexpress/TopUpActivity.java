package com.maidenexpress;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

public class TopUpActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up);
        Intent intent = getIntent();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_left,R.anim.exit_right)
                .replace(R.id.fmTopUp,TopUpFragment.newInstance(intent.getStringExtra("cardNumber"),intent.getStringExtra("userId")));
        fragmentTransaction.addToBackStack(null).commit();
    }


    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
