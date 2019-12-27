package com.maidenexpress;


import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {
    MapView mapView;
    GoogleMap map;
    FirebaseFirestore db;
    String userID;
    EditText etCustomerLocation;
    Button btnFindMaid;
    Customer currentCustomer;
    MarkerOptions customerMarker;
    ProgressBar pbFindMaid;
    Transaction transaction;
    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance(String userID, Transaction transaction){
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString("userID", userID);
        args.putSerializable("transaction",transaction);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = view.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        pbFindMaid = view.findViewById(R.id.pbFindMaid);
        mapView.onResume();
        mapView.getMapAsync(this);
        userID = getArguments().getString("userID");
        transaction = (Transaction) getArguments().getSerializable("transaction");
        db = FirebaseFirestore.getInstance();
        etCustomerLocation = view.findViewById(R.id.etCustomerLocation);
        etCustomerLocation.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {

                    return true;
                }
                return false;
            }
        });
        btnFindMaid = view.findViewById(R.id.btnFindMaid);
        btnFindMaid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("maids").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(final QuerySnapshot queryDocumentSnapshots) {
                        new CountDownTimer(3000, 1000) {
                            public void onFinish() {
                                Location location = new Location("Your location");
                                location.setLatitude(currentCustomer.getGeoPoint().getLatitude());
                                location.setLongitude(currentCustomer.getGeoPoint().getLongitude());

                                for(QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots){
                                    GeoPoint geoPoint = queryDocumentSnapshot.getGeoPoint("geoPoint");

                                    Location maidLocation = new Location("Maid location");
                                    maidLocation.setLatitude(geoPoint.getLatitude());
                                    maidLocation.setLongitude(geoPoint.getLongitude());

                                    if(location.distanceTo(maidLocation) < 1000) {
                                        map.addMarker(new MarkerOptions()
                                                .position(new LatLng(geoPoint.getLatitude(),geoPoint.getLongitude()))
                                                .icon(bitmapDescriptorFromVector(getContext(),R.drawable.ic_worker)));
                                    }
                                }
                                pbFindMaid.setVisibility(View.GONE);
                            }

                            public void onTick(long millisUntilFinished) {
                                pbFindMaid.setVisibility(View.VISIBLE);
                            }
                        }.start();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
        });
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable background = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable.setBounds(40, 20, vectorDrawable.getIntrinsicWidth() + 40, vectorDrawable.getIntrinsicHeight() + 20);
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (googleMap != null) {
            map = googleMap;
            db.collection("customers").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Customer customer = documentSnapshot.toObject(Customer.class);
                    currentCustomer = documentSnapshot.toObject(Customer.class);
                    if(!customer.getAddress().isEmpty())
                    {
                        etCustomerLocation.setText(customer.getAddress());
                    }
                    if(customer.getGeoPoint() != null)
                    {
                        if(customer.getGeoPoint().getLatitude() != 0 || customer.getGeoPoint().getLongitude() != 0 ){
                            LatLng latLng = new LatLng(customer.getGeoPoint().getLatitude(),customer.getGeoPoint().getLongitude());
                            customerMarker = new MarkerOptions()
                                    .position(latLng).icon(BitmapDescriptorFactory.defaultMarker(1))
                                    .title("You are here");
                            map.addMarker(customerMarker);
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                    latLng, 15));
                        }
                    }
                    else
                    {
                        if(!customer.getAddress().isEmpty()){
                            try {
                                Geocoder gc = new Geocoder(getContext());
                                List<Address> location = gc.getFromLocationName(customer.getAddress(),1);
                                Address address = location.get(0);
                                LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());
                                customerMarker = new MarkerOptions()
                                        .position(latLng).icon(BitmapDescriptorFactory.defaultMarker(1))
                                        .title("You are here");
                                map.addMarker(customerMarker);
                                map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                        latLng, 15));

                                Map<String,Object> cus = new HashMap<>();
                                final GeoPoint geoPoint = new GeoPoint(latLng.latitude,latLng.longitude);
                                cus.put("geoPoint",geoPoint);

                                db.collection("customers").document(userID)
                                        .update(cus).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        currentCustomer.setGeoPoint(geoPoint);
                                        Log.d("GeoPointDatabase","Update successful");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("GeoPointDatabase","Update failed");
                                    }
                                });

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });

            map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    final LatLng newLocation = latLng;

                    new AlertDialog.Builder(getContext()).setTitle("Your location has been set!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            customerMarker = new MarkerOptions()
                                    .position(newLocation).icon(BitmapDescriptorFactory.defaultMarker(1))
                                    .title("You are here");
                            map.clear();
                            map.addMarker(customerMarker);
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                    newLocation, 15));

                            Map<String,Object> cus = new HashMap<>();
                            final GeoPoint geoPoint = new GeoPoint(newLocation.latitude,newLocation.longitude);
                            cus.put("geoPoint",geoPoint);

                            db.collection("customers").document(userID)
                                    .update(cus).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    currentCustomer.setGeoPoint(geoPoint);
                                    Log.d("GeoPointDatabase","Update successful");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("GeoPointDatabase","Update failed");
                                }
                            });
                        }
                    }).show();
                }
            });
        }
    }
}
