package com.maidenexpress;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.security.keystore.KeyGenParameterSpec;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import id.zelory.compressor.Compressor;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment implements View.OnClickListener {

    Button btnAccountSave;
    ImageView imgAccountAvatar;
    EditText etAccountPhone, etAccountEmail, etAccountBirthday, etAccountAddress, etAccountZip;
    TextView tvAccountName;
    Spinner spinnerCountry;
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    FirebaseStorage firebaseStorage;
    FirebaseUser firebaseUser;
    Context context;
    ArrayAdapter<String> countryAdapter;
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnAccountSave = view.findViewById(R.id.btnAccountSave);
        imgAccountAvatar = view.findViewById(R.id.imgAccountAvatar);
        etAccountPhone = view.findViewById(R.id.etAccountPhone);
        etAccountEmail = view.findViewById(R.id.etAccountEmail);
        etAccountBirthday = view.findViewById(R.id.etAccountBirthday);
        etAccountAddress = view.findViewById(R.id.etAccountAddress);
        etAccountZip = view.findViewById(R.id.etAccountZip);
        spinnerCountry = view.findViewById(R.id.spinnerCountry);
        tvAccountName = view.findViewById(R.id.tvAccountName);
        imgAccountAvatar.setOnClickListener(this);
        btnAccountSave.setOnClickListener(this);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        context = getActivity();
        loadCountry();
        loadAvatar();
        loadInfo();


    }

    public void loadCountry(){
        Locale[] locales = Locale.getAvailableLocales();
        ArrayList<String> countries = new ArrayList<String>();
        for (Locale locale : locales) {
            String country = locale.getDisplayCountry();
            if (country.trim().length() > 0 && !countries.contains(country)) {
                countries.add(country);
            }
        }

        Collections.sort(countries);
        for (String country : countries) {
            System.out.println(country);
        }

        countryAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, countries);

        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the your spinner
        spinnerCountry.setAdapter(countryAdapter);
    }

    public void loadInfo(){
        String uid = firebaseUser.getUid();
        final DocumentReference docRef = db.collection("customers").document(uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Customer customer = document.toObject(Customer.class);
                        if(customer.getDisplayName().isEmpty() || customer.getDisplayName() == null)
                        {
                            tvAccountName.setText(firebaseUser.getDisplayName());
                        }
                        else{
                            tvAccountName.setText(customer.getDisplayName());
                        }
                        etAccountEmail.setText(customer.getEmail());
                        etAccountAddress.setText(customer.getAddress());
                        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                        cal.setTimeInMillis(customer.getBirthDay());
                        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
                        etAccountBirthday.setText(date);
                        etAccountPhone.setText(customer.getPhone());
                        etAccountZip.setText(String.valueOf(customer.getZipCode()));
                        if(!customer.getCountry().isEmpty() || customer.getCountry() != null){
                            spinnerCountry.setSelection(countryAdapter.getPosition(customer.getCountry()));
                        }


                        Log.d("loadName", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("loadName", "No such document");
                    }
                } else {
                    Log.d("loadName", "get failed with ", task.getException());
                }
            }
        });
    }

    public void loadAvatar() {

        StorageReference storageReference = firebaseStorage.getReference().child(firebaseUser.getUid()+"/avatar/avatar.jpg");

        storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()){
                    Glide.with(context).load(task.getResult()).apply(RequestOptions.circleCropTransform()).into(imgAccountAvatar);
                }
                else
                {
                    Log.d("loadAvatar",task.getException().getMessage());
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAccountSave: {
                updateCustomer();
                break;
            }
            case R.id.imgAccountAvatar: {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permissions, PERMISSION_CODE);
                    } else {
                        pickImageFromGallery();
                    }
                } else {
                    pickImageFromGallery();
                }
                break;
            }
            default:
                break;
        }
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    pickImageFromGallery();
                } else {

                }
            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            StorageReference storageRef = firebaseStorage.getReference();
            StorageReference avatarRef = storageRef.child(firebaseUser.getUid() + "/avatar/avatar.jpg");
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), data.getData());
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 30, bos);
                byte[] bytes = bos.toByteArray();
                avatarRef.putBytes(bytes);
                Glide.with(context).load(bitmap).apply(RequestOptions.circleCropTransform()).into(imgAccountAvatar);
                Toast.makeText(context, "Avatar Uploaded", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private long convertStringDateToLong(String date) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date resultDate;
        resultDate = dateFormat.parse(date);
        return resultDate.getTime();
    }

    public void updateCustomer(){

        Map<String,Object> cus = new HashMap<>();
        cus.put("phone",etAccountPhone.getText().toString());
        cus.put("email",etAccountEmail.getText().toString());
        cus.put("address",etAccountAddress.getText().toString());
        cus.put("country",spinnerCountry.getSelectedItem().toString());
        cus.put("zipCode",Long.parseLong(etAccountZip.getText().toString()));
        try {
            cus.put("birthDay",convertStringDateToLong(etAccountBirthday.getText().toString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        db.collection("customers").document(firebaseUser.getUid())
                .update(cus).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


        //custUpdate.put("zipcode",etAccountZip.getText());
        //custUpdate.put("")
    }
}
