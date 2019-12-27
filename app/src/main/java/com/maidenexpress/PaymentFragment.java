package com.maidenexpress;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class PaymentFragment extends Fragment {

    LinearLayout llAddCard;
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    ArrayList<String> lstCards;
    ArrayAdapter adapter;
    ListView lvCards;
    TextView tvBalance;
    RecyclerView rvTransactions;
    TransactionAdapter transactionAdapter;
    List<Transaction> transactionList;

    public PaymentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("CheckingStatus","onCreateView");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_payment, container, false);
    }

    public static PaymentFragment newInstance(String userId){
        PaymentFragment fragment = new PaymentFragment();
        Bundle bundle = new Bundle();
        bundle.putString("userId",userId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d("CheckingStatus","onViewCreated");

        llAddCard = view.findViewById(R.id.llAddCard);
        lvCards = view.findViewById(R.id.lvCards);
        tvBalance = view.findViewById(R.id.tvBalance);
        rvTransactions = view.findViewById(R.id.rvTransaction);
        llAddCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),AddCardActivity.class);
                startActivity(intent);
            }
        });
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();



        lstCards = new ArrayList<>();
        transactionList = new ArrayList<>();
        getCards();
        getBalance();
        getTransactions();
        adapter = new CardAdapter(getContext(),lstCards);
        transactionAdapter = new TransactionAdapter(getContext(),transactionList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false);
        rvTransactions.setAdapter(transactionAdapter);

        rvTransactions.setLayoutManager(layoutManager);
        lvCards.setAdapter(adapter);

        lvCards.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(),TopUpActivity.class);
                intent.putExtra("cardNumber",lstCards.get(position));
                intent.putExtra("userId",firebaseAuth.getCurrentUser().getUid());
                startActivity(intent);
            }
        });


    }


    private void getBalance(){
        db.collection("customers").document(firebaseAuth.getCurrentUser()
                .getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                long balance = documentSnapshot.getLong("balance");
                tvBalance.setText(formatBalance(String.valueOf(balance))+" VND");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

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


    private String formatCard(String cardnumber){

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < cardnumber.length(); i++) {
            if (i % 4 == 0 && i != 0) {
                result.append("-");
            }
            result.append(cardnumber.charAt(i));
        }
        return result.toString();
    }

    private void getCards(){
        db.collection("customers").document(firebaseAuth.getCurrentUser()
                .getUid()).collection("cards").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        lstCards.clear();
                        for(QueryDocumentSnapshot document : queryDocumentSnapshots){
                            String cardnumber = formatCard(document.get("card").toString());
                            lstCards.add(cardnumber);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void getTransactions(){

        db.collection("customers").document(firebaseAuth.getCurrentUser()
                .getUid()).collection("transactions").orderBy("dateCreated", Query.Direction.DESCENDING).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        transactionList.clear();
                        for(QueryDocumentSnapshot document : queryDocumentSnapshots){
                            Transaction transaction = document.toObject(Transaction.class);
                            transactionList.add(transaction);
                        }
                        transactionAdapter.notifyItemInserted(0);
                        transactionAdapter.notifyDataSetChanged();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("RefreshTransaction",e.getMessage());
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("CheckingStatus","onStart");

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("CheckingStatus","onResume");
        getCards();
        adapter.notifyDataSetChanged();
        getTransactions();



        getBalance();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("CheckingStatus","onStop");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("CheckingStatus","onPause");
    }
}
