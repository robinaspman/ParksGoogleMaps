package com.example.parks;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.parks.adapter.CustomInfoWindow;
import com.example.parks.data.Repository;
import com.example.parks.model.Park;
import com.example.parks.model.ParkViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.parks.databinding.ActivityMapsBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private ParkViewModel parkViewModel;
    private List<Park> parkList;
    private CardView cardView;
    private EditText stateCodeEt;
    private ImageButton searchButton;
    private String code = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parkViewModel = new ViewModelProvider(this)
                .get(ParkViewModel.class);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        cardView = findViewById(R.id.cardview);
        //Error: android.widget.LinearLayout cannot be cast to
        // androidx.cardview.widget.CardView
        stateCodeEt = findViewById(R.id.floating_state_value_et);
        searchButton = findViewById(R.id.floating_search_button);

        BottomNavigationView bottomNavigationView =
                findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int id = item.getItemId();
            if (id == R.id.maps_nav_button) {
                if (cardView.getVisibility() == View.VISIBLE ||
                        cardView.getVisibility() == View.GONE) {
                    cardView.setVisibility(View.VISIBLE);
                }
                parkList.clear();

                mMap.clear();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.map, mapFragment)
                        .commit();
                mapFragment.getMapAsync(this);
                return true;

            } else if (id == R.id.parks_nav_button) {
                selectedFragment = new ParksFragment();
                cardView.setVisibility(View.GONE);
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.map, selectedFragment)
                    .commit();

            return true;
        });

        searchButton.setOnClickListener(view -> {
           parkList.clear();
            String stateCode = stateCodeEt.getText().toString().trim();
            if (!TextUtils.isEmpty(stateCode)) {
                code = stateCode;
                parkViewModel.selectCode(code);
                onMapReady(mMap);
                stateCodeEt.setText("");
            }
        });
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
        mMap.setInfoWindowAdapter(new CustomInfoWindow(getApplicationContext()));
        mMap.setOnInfoWindowClickListener(this);
        parkList = new ArrayList<>();
        parkList.clear();

        populateMap();
    }

    private void populateMap() {
        mMap.clear(); //Important! Clears the map!
        Repository.getParks(parks -> {
            parkList = parks;
            for (Park park : parks) {
                LatLng location = new LatLng(Double.parseDouble(park.getLatitude()),
                        Double.parseDouble(park.getLongitude()));

                MarkerOptions markerOptions =
                        new MarkerOptions()
                                .position(location)
                                .title(park.getName())
                                .icon(BitmapDescriptorFactory.defaultMarker(
                                        BitmapDescriptorFactory.HUE_VIOLET))
                                .snippet(park.getStates());

                Marker marker = mMap.addMarker(markerOptions);
                marker.setTag(park);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 5));
                Log.d("Parks", "onMapReady: " + park.getFullName());

            }
            parkViewModel.setSelectedParks(parkList);

        }, code);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        cardView.setVisibility(View.GONE);

        goToDetailsFragment(marker);
    }

    private void goToDetailsFragment(Marker marker) {
        parkViewModel.setSelectedParks((Park) marker.getTag());
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.map, DetailsFragment.newInstance())
                .commit();
    }
}