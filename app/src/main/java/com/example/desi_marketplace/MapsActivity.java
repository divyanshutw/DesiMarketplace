package com.example.desi_marketplace;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.collect.Maps;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import java.util.HashMap;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private static final int GPS_REQUEST_CODE = 9003;
    public static final int PERMISSION_REQUEST_CODE = 9001;
    private boolean mLocationPermissionGranted, locationSelected;

    Button button_next;

    FirebaseFirestore firestore;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    private GoogleMap mGoogleMap;

    LatLng myLoc;

    String UserType,type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);

        setContentView(R.layout.activity_maps);

        //getSupportActionBar().hide();

        preferences = getSharedPreferences("LOGIN_INFO", MODE_PRIVATE);
        editor = preferences.edit();

        UserType=preferences.getString("UserType","");

        Intent intent=getIntent();
        type=intent.getStringExtra("Type");

        button_next = findViewById(R.id.button_next);

        firestore = FirebaseFirestore.getInstance();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(MapsActivity.this);

        if (isGpsEnabled()) {
        } else {
            //Toast.makeText(this, "Enable GPS", Toast.LENGTH_SHORT).show();
        }
        if (mLocationPermissionGranted) {
            mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mGoogleMap.setMyLocationEnabled(true);
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
                }

            }
        }
    }

    private boolean isGpsEnabled()
    {
        LocationManager locationManager=(LocationManager)getSystemService(LOCATION_SERVICE);
        boolean providerEnabled=locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(providerEnabled)
            return true;
        else
        {
            AlertDialog alertDialog=new AlertDialog.Builder(this)
                    .setTitle("GPS Permission")
                    .setMessage("GPS is required. Please turn on GPS")
                    .setPositiveButton("Yes",(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                           MapsActivity.this.startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), GPS_REQUEST_CODE);
                        }
                    }))
                    .setCancelable(false)
                    .show();
        }
        return false;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap=googleMap;
        goToLocation(27,79,5);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                locationSelected=true;
                mGoogleMap.clear();
                goToLocation(latLng.latitude,latLng.longitude,9);
                mGoogleMap.addMarker(new MarkerOptions().position(latLng).title("Your position"));
                myLoc=latLng;
            }
        });
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);
    }

    public void onClickNext(View view) {
        if(!locationSelected)
            Toast.makeText(this, "Tap on the map to select location", Toast.LENGTH_SHORT).show();
        else
        {

            HashMap<String,Object> map=new HashMap<>();
            map.put("Latitude",myLoc.latitude);
            map.put("Longitude",myLoc.longitude);

            if(UserType!=null) {
                firestore.collection(UserType).document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(map, SetOptions.merge())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                {
                                    Toast.makeText(MapsActivity.this, "Welcome!!!", Toast.LENGTH_SHORT).show();
                                    editor.remove("UserType");
                                    Intent goToHome=new Intent();
                                    goToHome.setClass(MapsActivity.this,HomePage.class);
                                    editor.putBoolean("LoggedIn",true);
                                    editor.commit();
                                    startActivity(goToHome);
                                    finish();
                                }
                                else
                                    Toast.makeText(MapsActivity.this, "Unable to fetch from database", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
            else
                Toast.makeText(this, "Unable to link to database", Toast.LENGTH_SHORT).show();
            //startActivity(new Intent(MapsActivity.this,HomePage.class));

            if(UserType!=null)
            {
                firestore.collection("EnterpriseType").document("Type").collection(type).document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(map,SetOptions.merge());
            }

        }
    }
    private void goToLocation(double lat,double lng, int cam)
    {
        LatLng latlng=new LatLng(lat,lng);

        CameraUpdate cameraUpdate= CameraUpdateFactory.newLatLngZoom(latlng,cam);

        mGoogleMap.animateCamera(cameraUpdate);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==PERMISSION_REQUEST_CODE && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            mLocationPermissionGranted=true;

        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        locationSelected=true;
        goToLocation(latLng.latitude,latLng.longitude,9);
        mGoogleMap.clear();
        mGoogleMap.addMarker(new MarkerOptions().position(latLng).title("Your position"));
        myLoc=latLng;
    }
}

