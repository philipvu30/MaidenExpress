package com.maidenexpress;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private ViewPager mainPager;
    private MainPagerAdapter adapter;
    private TabLayout mainTabLayout;
    private String[] titles = {"Home","Payment","Account"};
    private int[] arrTabIcons = {R.drawable.ic_home_white,R.drawable.ic_payment_black_24dp,R.drawable.ic_user};
    LinearLayout llSec;
    ImageView llFirst;
    TextView tvWelcome, tvUser;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainPager = findViewById(R.id.mainPager);
        mainTabLayout = findViewById(R.id.mainTabLayout);
        llFirst = findViewById(R.id.llFirst);
        llSec = findViewById(R.id.llSec);
        tvWelcome = findViewById(R.id.tvWelcome);
        tvUser = findViewById(R.id.tvUser);
        auth = FirebaseAuth.getInstance();
        tvUser.setText(auth.getCurrentUser().getDisplayName());
        adapter = new MainPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mainPager.setAdapter(adapter);
        mainPager.setOffscreenPageLimit(3);
        mainTabLayout.setupWithViewPager(mainPager);
        int j = 0;
        for(int i : arrTabIcons){
            TextView tab = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab,null);
            tab.setText(titles[j]);
            tab.setCompoundDrawablesWithIntrinsicBounds(0,arrTabIcons[j],0,0);
            mainTabLayout.getTabAt(j).setCustomView(tab);
            j++;
        }

        final Animation welcome = AnimationUtils.loadAnimation(this, R.anim.welcome_left_to_right);
        final Animation welcome2 = AnimationUtils.loadAnimation(this, R.anim.welcome_left_to_right);
        final Animation welcome3 = AnimationUtils.loadAnimation(this, R.anim.welcome_right_to_left);
        final Animation welcome4 = AnimationUtils.loadAnimation(this, R.anim.welcome_right_to_left);
        final Animation downToUp = AnimationUtils.loadAnimation(this, R.anim.bg_anim_down_to_up);
        final Animation downToUp2 = AnimationUtils.loadAnimation(this, R.anim.bg_anim_down_to_up_2);

        welcome2.setStartOffset(100);
        welcome3.setStartOffset(1500);
        welcome4.setStartOffset(1600);
        downToUp.setStartOffset(3700);
        downToUp2.setStartOffset(3700);

        SimpleDateFormat formatter = new SimpleDateFormat("HH");
        Date date = new Date();
        int hour = Integer.parseInt(formatter.format(date));

     if(hour >= 6 && hour < 12){
         tvWelcome.setText("Good Morning");
     }
     else if(hour >= 12 && hour < 18)
     {
         tvWelcome.setText("Good Afternoon");
     }
     else
     {
         tvWelcome.setText("Good Evening");
     }
        llSec.setAnimation(downToUp);
        llFirst.setAnimation(downToUp2);
        tvWelcome.setAnimation(welcome);
        tvUser.setAnimation(welcome2);

        welcome2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                tvWelcome.setAnimation(welcome3);
                tvUser.setAnimation(welcome4);


            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        welcome3.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                tvWelcome.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        welcome4.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                tvWelcome.setVisibility(View.GONE);
                tvUser.setVisibility(View.GONE);


            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        downToUp2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                llFirst.setVisibility(View.GONE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
