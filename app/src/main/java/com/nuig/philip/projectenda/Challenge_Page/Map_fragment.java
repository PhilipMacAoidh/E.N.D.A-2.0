package com.nuig.philip.projectenda.Challenge_Page;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nuig.philip.projectenda.R;
import com.nuig.philip.projectenda.Tasks.Toasts;

public class Map_fragment extends Fragment {

    MapView mMapView;
    private GoogleMap googleMap;
    private LatLng currentLocation, destLocation;
    private View rootView = null;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        initializeMap();
        rootView = inflater.inflate(R.layout.fragment_map, container, false);
        checkLocationPermission();

        mMapView = (MapView) rootView.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)  == PackageManager.PERMISSION_GRANTED) {
                    googleMap.setMyLocationEnabled(true);
                    // Check if we were successful in obtaining the map.
                    if (googleMap != null) {
                        googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                            @Override
                            public void onMyLocationChange (Location arg0){
                                if(currentLocation==null)
                                {
                                    setCurrentLocation(new LatLng(arg0.getLatitude(), arg0.getLongitude()));
                                    googleMap.addCircle(new CircleOptions()
                                            .center(new LatLng(arg0.getLatitude(), arg0.getLongitude()))
                                            .radius(1 * 1000) //measures in meters, *1000 of whatever user set
                                            .strokeColor(Color.argb(50, 194, 94, 0))
                                            .fillColor(Color.argb(50, 251, 140, 0))
                                    );

                                    // For zooming automatically to the location of the marker
                                    CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(arg0.getLatitude(), arg0.getLongitude())).zoom(13).build();
                                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                }
                            }
                        });
                    }
                }

                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    public void onMapClick(LatLng point) {
                        // Drawing marker on the map
                        MarkerOptions markerObject = new MarkerOptions().position(point);
                        setDestination(point);
                        googleMap.clear();
                        googleMap.animateCamera(CameraUpdateFactory.newLatLng(point));
                        googleMap.addMarker(markerObject);
                        if(currentLocation!=null) {
                            googleMap.addCircle(new CircleOptions()
                                    .center(currentLocation)
                                    .radius(1 * 1000) //measures in meters, *1000 of whatever user set
                                    .strokeColor(Color.argb(100, 194, 94, 0))
                                    .fillColor(Color.argb(80, 251, 140, 0))
                            );
                        }
                        // Create location object
//                        Location location = new Location(point.latitude, point.longitude);
                        // add location to SQLite database
//                        locationsDB.insert(location);
                    }
                });
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            return false;
        } else {
            return true;
        }
    }

    public void setDestination(LatLng point) {
        destLocation = point;

        Button getDirectionsBtn = rootView.findViewById(R.id.getDirectionsBtn);
        getDirectionsBtn.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        getDirectionsBtn.setTextColor(getResources().getColor(R.color.black));
        getDirectionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/dir/?api=1&origin=" + currentLocation.latitude + "," + currentLocation.longitude + "&destination=" + destLocation.latitude + "," + destLocation.longitude + "&travelmode=walking"));
                startActivity(browserIntent);
            }
        });
    }

    public void setCurrentLocation(LatLng point) {
       currentLocation = point;
    }
}