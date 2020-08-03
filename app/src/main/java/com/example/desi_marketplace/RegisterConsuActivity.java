package com.example.desi_marketplace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;

public class RegisterConsuActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private static final int GPS_REQUEST_CODE = 9003;
    public static final int PERMISSION_REQUEST_CODE = 9001;
    private boolean mLocationPermissionGranted, locationSelected;
    String phone;
    EditText editText_phone;
    Button button_next;

    FirebaseFirestore firestore;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    private GoogleMap mGoogleMap;

    LatLng myLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);

        setContentView(R.layout.activity_register_consu);
        getSupportActionBar().hide();

        preferences = getSharedPreferences("LOGIN_INFO", MODE_PRIVATE);
        editor = preferences.edit();

        button_next = findViewById(R.id.button_next);
        editText_phone = findViewById(R.id.editText_phone);

        firestore = FirebaseFirestore.getInstance();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(RegisterConsuActivity.this);
        if (isGpsEnabled()) {
        } else
            Toast.makeText(this, "Enable GPS", Toast.LENGTH_SHORT).show();
        if (mLocationPermissionGranted) {
            mGoogleMap.getUiSettings().setZoomControlsEnabled(true);

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
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
            mGoogleMap.setMyLocationEnabled(true);
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
                }

            }
        }
    }

    private boolean isGpsEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (providerEnabled)
            return true;
        else {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("GPS Permission")
                    .setMessage("GPS is required. Please turn on GPS")
                    .setPositiveButton("Yes", (new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            RegisterConsuActivity.this.startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), GPS_REQUEST_CODE);
                        }
                    }))
                    .setCancelable(false)
                    .show();
        }
        return false;
    }

    public void onClickNext(View v) {
        phone = editText_phone.getText().toString();
        if (phone.length() < 10)
            Toast.makeText(this, "Enter valid phone number by which you can contact others", Toast.LENGTH_SHORT).show();
        else if (!locationSelected)
            Toast.makeText(this, "Tap on the map to select location", Toast.LENGTH_SHORT).show();
        else {
            editor.putString("Uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
            editor.commit();
            Intent intent=getIntent();
            String firstname=intent.getStringExtra("Firstname");//preferences.getString("Firstname","");
            String lastname=intent.getStringExtra("Lastname");//preferences.getString("Lastname","");
            String email=intent.getStringExtra("email");//preferences.getString("email","");
            HashMap<String, Object> map = new HashMap<>();
            map.put("Phone", phone);
            map.put("Firstname", firstname);
            map.put("Lastname", lastname);
            map.put("email", email);
            map.put("Latitude", myLoc.latitude);
            map.put("Longitude", myLoc.longitude);

            editor.remove("Firstname");
            editor.remove("Lastname");

            HashMap<String, Object> map3 = new HashMap<>();
            map3.put(FirebaseAuth.getInstance().getCurrentUser().getUid(), "Consumer");
            firestore.collection("Users").document("UserType").set(map3, SetOptions.merge());

            firestore.collection("Consumer").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(map, SetOptions.merge())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                                Toast.makeText(RegisterConsuActivity.this, "Welcome!!!", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(RegisterConsuActivity.this, "Unable to link to database", Toast.LENGTH_SHORT).show();
                        }
                    });
            firestore.collection("Consumer").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update("Favourites", FieldValue.arrayUnion("Sample"));
            firestore.collection("Consumer").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update("Chats", FieldValue.arrayUnion("Sample"));
            editor.putBoolean("LoggedIn",true);
            editor.commit();
            startActivity(new Intent(RegisterConsuActivity.this,HomePage.class));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        goToLocation(27, 79, 5);
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
