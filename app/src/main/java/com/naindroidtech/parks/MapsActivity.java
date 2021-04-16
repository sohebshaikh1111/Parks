 package com.naindroidtech.parks;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.naindroidtech.parks.adapter.CustomInfoWindow;
import com.naindroidtech.parks.data.Repository;
import com.naindroidtech.parks.model.Park;
import com.naindroidtech.parks.model.ParkViewModel;
import com.naindroidtech.parks.util.Util;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    private ParkViewModel parkViewModel;
    private List<Park> parkList;
    private CardView cardView;
    private EditText stateCodeEt;
    private ImageButton searchButton;
    private String code = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        parkViewModel = new ViewModelProvider(this)
                .get(ParkViewModel.class);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        cardView = findViewById(R.id.cardview);
        stateCodeEt = findViewById(R.id.floating_state_value_et);
        searchButton = findViewById(R.id.floating_search_button);

        BottomNavigationView bottomNavigationView =
                findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int id = item.getItemId();
            if (id == R.id.maps_nav_button) {
                if (cardView.getVisibility() == View.INVISIBLE ||
                        cardView.getVisibility() == View.GONE) {
                    cardView.setVisibility(View.VISIBLE);
                }
                parkList.clear();
                //show the map view
                mMap.clear();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.map, mapFragment)
                        .addToBackStack(null)
                        .commit();
                mapFragment.getMapAsync(this);
                return true;

            } else if (id == R.id.parks_nav_button) {
                selectedFragment = ParksFragment.newInstance();
                cardView.setVisibility(View.GONE);

            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.map, selectedFragment)
                    .addToBackStack(null)
                    .commit();

            return true;
        });

        searchButton.setOnClickListener(view -> {
            parkList.clear();
            Util.hideSoftKeyboard(view);
            String stateCode = stateCodeEt.getText().toString().trim();
            if (!TextUtils.isEmpty(stateCode)) {
                code = stateCode;
                parkViewModel.selectCode(code);
                onMapReady(mMap); //refresh the map!
                stateCodeEt.setText("");
            }
        });
    }

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
        mMap.clear();// important!  Clears the map!
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
                                        BitmapDescriptorFactory.HUE_AZURE))
                                .snippet(park.getStates());

                Marker marker = mMap.addMarker(markerOptions);
                marker.setTag(park);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 5));
                Log.d("Parks", "onMapReady: " + park.getFullName());
            }
            parkViewModel.setSelectedParks(parkList);
            Log.d("SIZE", "populateMap: " + parkList.size());

        }, code);

    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        cardView.setVisibility(View.GONE);
        //go to detailsFragment
        goToDetailsFragment(marker);


    }

    private void goToDetailsFragment(Marker marker) {
        parkViewModel.setSelectedPark((Park) marker.getTag());
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.map, DetailsFragment.newInstance())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Fragment chooseFragment;
        int id = item.getItemId();
        if (id == R.id.fragment_details) {
            chooseFragment = ParksFragment.newInstance();
            this.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_details, chooseFragment)
                    .commit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}