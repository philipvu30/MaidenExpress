package com.maidenexpress;


import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.timessquare.CalendarPickerView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class BookingFragment extends Fragment implements View.OnClickListener{

    Button btnAddRoom, btnAddKitchen, btnAddBath, btnAddLivRoom, btnSubRoom, btnSubKitchen, btnSubBath, btnSubLivRoom, btnBookComplete;
    ImageView imgClose;
    TextView etRoom, etKitchen, etBath, etLivRoom, tvTime, tvDate;
    CheckBox chkRoom, chkKitchen, chkBath, chkLivRoom, chkCleanRoom, chkLaundry, chkCleanToilet, chkCleanDishes;
    String[] selectedDate = {""};
    String userId;
    FirebaseFirestore db;
    String address;
    public BookingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        userId = this.getArguments().getString("userId");
        db = FirebaseFirestore.getInstance();
        db.collection("customers").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Customer customer = documentSnapshot.toObject(Customer.class);
                address = customer.getAddress();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_booking, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpUI(view);
    }

    private void setUpUI(View view) {
        btnAddRoom = view.findViewById(R.id.btnAddRoom);
        btnAddKitchen = view.findViewById(R.id.btnAddKitchen);
        btnAddBath = view.findViewById(R.id.btnAddBath);
        btnAddLivRoom = view.findViewById(R.id.btnAddLivRoom);
        btnSubRoom = view.findViewById(R.id.btnSubRoom);
        btnSubKitchen = view.findViewById(R.id.btnSubKitchen);
        btnSubBath = view.findViewById(R.id.btnSubBath);
        btnSubLivRoom = view.findViewById(R.id.btnSubLivRoom);
        btnBookComplete = view.findViewById(R.id.btnBookComplete);
        imgClose = view.findViewById(R.id.imgClose);
        etRoom = view.findViewById(R.id.etRoom);
        etKitchen = view.findViewById(R.id.etKitchen);
        etBath = view.findViewById(R.id.etBath);
        etLivRoom = view.findViewById(R.id.etLivRoom);
        chkRoom = view.findViewById(R.id.chkRoom);
        chkKitchen = view.findViewById(R.id.chkKitchen);
        chkBath = view.findViewById(R.id.chkBath);
        chkLivRoom = view.findViewById(R.id.chkLivRoom);
        chkCleanRoom = view.findViewById(R.id.chkCleanRoom);
        chkCleanToilet = view.findViewById(R.id.chkCleanToilet);
        chkCleanDishes = view.findViewById(R.id.chkCleanDishes);
        chkLaundry = view.findViewById(R.id.chkLaundry);
        tvTime = view.findViewById(R.id.tvBookTime);
        tvDate = view.findViewById(R.id.tvBookDate);

        btnAddRoom.setOnClickListener(this);
        btnAddKitchen.setOnClickListener(this);
        btnAddBath.setOnClickListener(this);
        btnAddLivRoom.setOnClickListener(this);
        btnSubRoom.setOnClickListener(this);
        btnSubKitchen.setOnClickListener(this);
        btnSubBath.setOnClickListener(this);
        btnSubLivRoom.setOnClickListener(this);
        btnBookComplete.setOnClickListener(this);
        imgClose.setOnClickListener(this);
        tvDate.setOnClickListener(this);
        tvTime.setOnClickListener(this);

        tvDate.setText(DateFormat.getDateInstance(DateFormat.DATE_FIELD).format(new Date()));
        SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm a");
        tvTime.setText(outputFormat.format(new Date()));



    }

    public static BookingFragment newInstance(String userID){
        BookingFragment fragment = new BookingFragment();
        Bundle bundle = new Bundle();
        bundle.putString("userId",userID);
        fragment.setArguments(bundle);
        return fragment;
    }
    private void openTimePicker(){
        Date date = new Date();
        int hour = date.getHours();
        int minute = date.getMinutes();
        TimePickerDialog dialog = new TimePickerDialog(getContext(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String time = "";
                        if(hourOfDay == 12){
                            time=12 + ":" + minute+" ";
                        }
                        else{
                            time =hourOfDay%12 + ":" + minute+" ";
                        }

                        if (hourOfDay >= 12) {
                            time += "PM";
                        } else {
                            time += "AM";
                        }
                        tvTime.setText(time);
                    }
                }, hour, minute, false);
        dialog.show();
    }



    private void openDatePicker() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_calendar_picker);

        Date today = new Date();
        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);
        CalendarPickerView calendar = dialog.findViewById(R.id.calendar_view);
        Button btnDateChooseDone = dialog.findViewById(R.id.btnDateChooseDone);
        calendar.init(today, nextYear.getTime())
                .withSelectedDate(today);


        calendar.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
                selectedDate[0] = DateFormat.getDateInstance(DateFormat.DATE_FIELD).format(date);
            }

            @Override
            public void onDateUnselected(Date date) {

            }
        });

        btnDateChooseDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvDate.setText(selectedDate[0]);
                dialog.dismiss();
            }
        });

        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }


    private void openMap() throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("mm/DD/yyyy HH:mm a");
        String dateTime = tvDate.getText() + " "+tvTime.getText();
        Date date = formatter.parse(dateTime);
        StringBuilder builder = new StringBuilder();
        int roomCount = Integer.parseInt(etRoom.getText().toString());
        int kitchenCount = Integer.parseInt(etKitchen.getText().toString());
        int livRoomCount = Integer.parseInt(etLivRoom.getText().toString());
        int bathRoomCount = Integer.parseInt(etBath.getText().toString());

        if(chkCleanRoom.isChecked()){

            builder.append(chkCleanRoom.getText()+"\n");
        }
        if(chkLaundry.isChecked()){

            builder.append(chkLaundry.getText()+"\n");
        }
        if(chkCleanToilet.isChecked()){

            builder.append(chkCleanToilet.getText()+"\n");
        }
        if(chkCleanDishes.isChecked()){
            builder.append(chkCleanDishes.getText()+"\n");
        }
        Transaction transaction = new Transaction(new Date().getTime(),"Hire Maid",""
                ,roomCount,kitchenCount,livRoomCount,bathRoomCount
                ,date.getTime(),builder.toString()
                );
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().replace(R.id.fmAddBooking,MapFragment.newInstance(userId,transaction));
        fragmentTransaction.addToBackStack(null).commit();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnAddRoom:{
                if(chkRoom.isChecked()){
                    String number = etRoom.getText().toString();
                    int current_number = Integer.parseInt(number);
                    current_number++;
                    etRoom.setText(String.valueOf(current_number));
                }
                break;
            }
            case R.id.btnAddKitchen:{
                if(chkKitchen.isChecked()){
                    String number = etKitchen.getText().toString();
                    int current_number = Integer.parseInt(number);
                    current_number++;
                    etKitchen.setText(String.valueOf(current_number));
                }
                break;
            }
            case R.id.btnAddBath:{
                if(chkBath.isChecked()){
                    String number = etBath.getText().toString();
                    int current_number = Integer.parseInt(number);
                    current_number++;
                    etBath.setText(String.valueOf(current_number));
                }
                break;
            }
            case R.id.btnAddLivRoom:{
                if(chkLivRoom.isChecked()){
                    String number = etLivRoom.getText().toString();
                    int current_number = Integer.parseInt(number);
                    current_number++;
                    etLivRoom.setText(String.valueOf(current_number));
                }
                break;
            }
            case R.id.btnSubRoom:{
                if(chkRoom.isChecked()){
                    String number = etRoom.getText().toString();
                    int current_number = Integer.parseInt(number);
                    if(current_number > 0)
                        current_number--;
                    etRoom.setText(String.valueOf(current_number));
                }
                break;
            }
            case R.id.btnSubKitchen:{
                if(chkKitchen.isChecked()){
                    String number = etKitchen.getText().toString();
                    int current_number = Integer.parseInt(number);
                    if(current_number > 0)
                        current_number--;
                    etKitchen.setText(String.valueOf(current_number));
                }
                break;
            }
            case R.id.btnSubBath:{
                if(chkBath.isChecked()){
                    String number = etBath.getText().toString();
                    int current_number = Integer.parseInt(number);
                    if(current_number > 0)
                        current_number--;
                    etBath.setText(String.valueOf(current_number));
                }
                break;
            }
            case R.id.btnSubLivRoom:{
                if(chkLivRoom.isChecked()){
                    String number = etLivRoom.getText().toString();
                    int current_number = Integer.parseInt(number);
                    if(current_number > 0)
                        current_number--;
                    etLivRoom.setText(String.valueOf(current_number));
                }
                break;
            }
            case R.id.tvBookDate:{
                openDatePicker();
                break;
            }
            case R.id.tvBookTime:{
                openTimePicker();
                break;
            }
            case R.id.imgClose:{
                getActivity().finish();
                break;
            }
            case R.id.btnBookComplete:{
                if(address.isEmpty() || address == null){
                    new AlertDialog.Builder(getContext()).setTitle("Please update your information to continue!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    getActivity().finish();
                                }
                            }).show();
                }
                else{
                    if(!chkBath.isChecked() && !chkKitchen.isChecked()
                    && !chkLivRoom.isChecked() && !chkRoom.isChecked())
                    {
                        new AlertDialog.Builder(getContext()).setTitle("Need to choose at least 1 service")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).show();
                    }
                    else{
                        try {
                            openMap();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            }
            default:break;
        }
    }
}
