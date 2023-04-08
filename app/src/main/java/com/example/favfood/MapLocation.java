package com.example.favfood;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MapLocation extends AppCompatActivity implements OnMapReadyCallback{

    Intent intent;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private GoogleMap mMap;
    FloatingActionButton floatingActionButton;
    String currentAddress = "";
    LocationManager manager;
    Button search,setLocation;
    EditText editText;
    Marker marker;

    private static final int REQUEST_CODE = 101;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom);

        manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        floatingActionButton = findViewById(R.id.floatingActionButton);
        editText = findViewById(R.id.LocationCurrent);
        search = findViewById(R.id.button4);
        setLocation = findViewById(R.id.button);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLocation();
        floatingActionButton.setOnClickListener(v -> fetchLocation());

        search.setOnClickListener(v -> searchLocation());
        setLocation.setOnClickListener(v -> setAsLocation());

        Intent in =  getIntent();
        if(in.getExtras()!= null){
            String loc = in.getStringExtra("Location");
            editText.setText(loc);
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        LatLng latLng = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).draggable(true).title(currentAddress);
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
        if(marker!=null){
            marker.remove();
        }
        marker = googleMap.addMarker(markerOptions);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.setOnMapClickListener(latLng1 -> {
            Objects.requireNonNull(marker).remove();
            currentAddress = getCompleteAddressString(latLng1.latitude, latLng1.longitude);
            marker = googleMap.addMarker(new MarkerOptions().position(latLng1).draggable(true).title(currentAddress));
//                Toast.makeText(getApplicationContext(), "Lat : " + latLng.latitude + " , " + "Long : " + latLng.longitude, Toast.LENGTH_LONG).show();
        });

        googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(@NonNull Marker marker) { }
            @Override
            public void onMarkerDrag(@NonNull Marker marker) { }
            @Override
            public void onMarkerDragEnd(@NonNull Marker marker) {
                LatLng latLng = marker.getPosition();
                getCompleteAddressString(latLng.latitude, latLng.longitude);
            }
        });
    }
    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
            }
            return;
        }
        if (!manager.isProviderEnabled( LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null) {
                currentLocation = location;
//                    Toast.makeText(getApplicationContext(), currentLocation.getLatitude() + "" + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                currentAddress = getCompleteAddressString(currentLocation.getLatitude(),currentLocation.getLongitude());
                SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                if (supportMapFragment != null) {
                    supportMapFragment.getMapAsync(MapLocation.this);
                }
//                    setAsLocation();
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocation();
            }
        }
    }
    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder();

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("CurrentLocation", strReturnedAddress.toString());
            } else {
                strAdd = "";
                Log.w("CurrentLocation", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("CurrentLocation", "Can't get Address!");
        }
        editText.setText(strAdd);
        return strAdd;
    }
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS to get your current location automatically")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                .setNegativeButton("No", (dialog, id) -> {
                    Toast.makeText(MapLocation.this, "Please Enter your location manually", Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
    public void searchLocation() {
        String location = editText.getText().toString();
        List<Address> addressList = null;

        Geocoder geocoder = new Geocoder(this);
        try {
            addressList = geocoder.getFromLocationName(location, 1);

        } catch (IOException e) {
            e.printStackTrace();
        }

        Address address = Objects.requireNonNull(addressList).get(0);
        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
        if(marker!=null){
            marker.remove();
        }
        marker = mMap.addMarker(new MarkerOptions().position(latLng).title(location));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        Toast.makeText(getApplicationContext(),address.getLatitude()+" "+address.getLongitude(),Toast.LENGTH_LONG).show();
    }
    public void setAsLocation(){
        intent = new Intent(getApplicationContext(),MainActivity.class);
        intent.putExtra("Address",currentAddress);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}