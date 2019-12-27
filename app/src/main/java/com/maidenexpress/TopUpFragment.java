package com.maidenexpress;


import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.w3c.dom.Text;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class TopUpFragment extends Fragment implements View.OnClickListener{

    EditText etTopUpAmount;
    TextView tvFirstTopUpAmount, tvSecondTopUpAmount
            , tvThirdTopUpAmount, tvChangeTopUpMethod
            , tvCurrentCard, tvPayAmount,tvBalanceAfterPayment;
    Button btnSubmitPayment;
    String userId;
    String cardNumber;
    FirebaseFirestore db;
    long balance;

    public TopUpFragment() {
        // Required empty public constructor
    }

    public static TopUpFragment newInstance(String cardNumber, String userId){
        TopUpFragment fragment = new TopUpFragment();
        Bundle bundle = new Bundle();
        bundle.putString("cardNumber",cardNumber);
        bundle.putString("userId",userId);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_top_up, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etTopUpAmount = view.findViewById(R.id.etTopUpAmount);
        tvFirstTopUpAmount = view.findViewById(R.id.tvFirstTopUpAmount);
        tvSecondTopUpAmount = view.findViewById(R.id.tvSecondTopUpAmount);
        tvThirdTopUpAmount = view.findViewById(R.id.tvThirdTopUpAmount);
        tvCurrentCard = view.findViewById(R.id.tvCurrentCard);
        tvPayAmount = view.findViewById(R.id.tvPayAmount);
        tvBalanceAfterPayment = view.findViewById(R.id.tvBalanceAfterPayment);
        btnSubmitPayment = view.findViewById(R.id.btnSubmitPayment);
        tvFirstTopUpAmount.setOnClickListener(this);
        tvSecondTopUpAmount.setOnClickListener(this);
        tvThirdTopUpAmount.setOnClickListener(this);
        tvChangeTopUpMethod.setOnClickListener(this);
        btnSubmitPayment.setOnClickListener(this);
        Bundle bundle = getArguments();
        userId = bundle.getString("userId");
        cardNumber = bundle.getString("cardNumber");
        db = FirebaseFirestore.getInstance();


        etTopUpAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                long afterPayment = balance + Long.parseLong(s.toString());
                String formatBalance = formatBalance(String.valueOf(afterPayment));
                tvBalanceAfterPayment.setText(formatBalance);
                tvPayAmount.setText(formatBalance(s.toString()));
            }
        });


    }

    private void getBalance(){
        db.collection("customers").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                balance = documentSnapshot.getLong("balance");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    private void setBalance(){
        long afterPayment = balance + Long.parseLong(etTopUpAmount.getText().toString());
        Map<String, Object> map = new HashMap<>();
        map.put("balance", afterPayment);
        db.collection("customers").document(userId).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void setTransaction(){
        Transaction transaction = new Transaction(new Date().getTime(),"Top Up","+"+formatBalance(etTopUpAmount.getText().toString()));
        db.collection("customers").document(userId).collection("transactions").add(transaction);
    }

    private String formatBalance(String balance){
        StringBuilder result = new StringBuilder();
        int spare_length = balance.length()%3;
        if(spare_length != 0){
            for(int i = 0; i < spare_length;i++)
            {
                result.append(balance.charAt(i));
            }
            result.append(".");
            int count = 0;
            for (int i = spare_length; i < balance.length(); i++) {
                if(count == 3){
                    count = 0;
                    result.append(".");
                }
                result.append(balance.charAt(i));
                count++;
            }
        }
        else
        {
            for (int i = 0; i < balance.length(); i++) {
                if (i % 3 == 0 && i != 0) {
                    result.append(".");
                }
                result.append(balance.charAt(i));
            }
        }
        return result.toString();
    }

    @Override
    public void onResume() {
        super.onResume();
        tvCurrentCard.setText(cardNumber);
        getBalance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvFirstTopUpAmount:{
                String amount = tvFirstTopUpAmount.getText().toString().replace(".","");
                etTopUpAmount.setText(amount);
                break;
            }
            case R.id.tvSecondTopUpAmount:{
                String amount = tvSecondTopUpAmount.getText().toString().replace(".","");
                etTopUpAmount.setText(amount);
                break;
            }
            case R.id.tvThirdTopUpAmount:{
                String amount = tvThirdTopUpAmount.getText().toString().replace(".","");
                etTopUpAmount.setText(amount);
                break;
            }
            case R.id.btnSubmitPayment:{
                setBalance();
                setTransaction();
                getActivity().finish();
                break;
            }
            default:break;
        }
    }
}
