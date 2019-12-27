package com.maidenexpress;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {

    LinearLayout llLogout;
    FirebaseAuth firebaseAuth;
    LinearLayout llBookMaid;
    TextView tvSayHello, tvHomeStatus;
    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        llLogout = view.findViewById(R.id.llLogOut);
        llBookMaid = view.findViewById(R.id.llBookMaid);
        firebaseAuth = FirebaseAuth.getInstance();
        tvSayHello = view.findViewById(R.id.tvSayHello);
        tvHomeStatus = view.findViewById(R.id.tvHomeStatus);
        llLogout.setOnClickListener(this);
        llBookMaid.setOnClickListener(this);

        SimpleDateFormat formatter = new SimpleDateFormat("HH");
        Date date = new Date();
        int hour = Integer.parseInt(formatter.format(date));

        if(hour >= 6 && hour < 12){
            tvSayHello.setText("Good Morning");
        }
        else if(hour >= 12 && hour < 18)
        {
            tvSayHello.setText("Good Afternoon");
        }
        else
        {
            tvSayHello.setText("Good Evening");
        }
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.anim_up_to_down);
        tvSayHello.setAnimation(animation);
        tvHomeStatus.setAnimation(animation
        );

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llBookMaid: {
                Intent intent = new Intent(getActivity(),AddBookingDetailActivity.class);
                intent.putExtra("userId",firebaseAuth.getCurrentUser().getUid());
                startActivity(intent);
                break;
            }
            case R.id.llLogOut: {
                firebaseAuth.signOut();
                Intent intent = new Intent(getContext(),LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                getActivity().finish();
                break;
            }
            case R.id.llCheckBookings:{
                break;
            }
            case R.id.llMessages:{
                break;
            }
            default:
                break;
        }
    }


}
