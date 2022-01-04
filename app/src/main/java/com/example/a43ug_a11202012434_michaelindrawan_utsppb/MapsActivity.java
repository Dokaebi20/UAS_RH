package com.example.a43ug_a11202012434_michaelindrawan_utsppb;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.a43ug_a11202012434_michaelindrawan_utsppb.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.w3c.dom.Text;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    FusedLocationProviderClient fusedLocationProviderClient;
    Button proceedBtn;
    String tPrice;
    TextView tCostFee, tRange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        proceedBtn = (Button) findViewById(R.id.proceedLocationButton);
        tCostFee = (TextView) findViewById(R.id.tCostFee);
        tRange = (TextView) findViewById(R.id.tRange);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if (location!=null){
                        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                        float results[] = new float[10];
                        Location.distanceBetween(location.getLatitude(), location.getLongitude(), -6.316033963611316, 106.89218427311948, results);
                        double addingCostFee = (Math.ceil(((double) results[0])*0.005*0.001));
                        DecimalFormat numberFormat = new DecimalFormat("#.00");
                        tCostFee.setText(numberFormat.format(addingCostFee));
                        tRange.setText(numberFormat.format(results[0]/1000) + " Km");

                            LatLng destination = new LatLng(location.getLatitude(), location.getLongitude());
                            float zoomLevel = 17.0f;
                            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                            mMap.addMarker(new MarkerOptions().position(destination).title("Universitas Dian Nuswantoro"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destination, zoomLevel));

                            proceedBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    List<Address> addresses = null;
                                    try {
                                        addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                        Intent i = new Intent(MapsActivity.this, checkout_form.class);
                                        if(getIntent().hasExtra("tPrice")){
                                            tPrice = getIntent().getStringExtra("tPrice");
                                        }
                                        i.putExtra("yourLoc", (addresses.get(0).getAddressLine(0)).toString().trim());
                                        double intAddingCostFee = addingCostFee+Double.parseDouble(tPrice);
                                        i.putExtra("tPrice", numberFormat.format(intAddingCostFee));
                                        i.putExtra("subtractionFee", addingCostFee);
                                        startActivity(i);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                    }
                }
            });
        }
        else {
            Toast.makeText(getApplicationContext(), "Location Error!", Toast.LENGTH_SHORT).show();
        }
        // Add a marker in Sydney and move the camera

    }
}