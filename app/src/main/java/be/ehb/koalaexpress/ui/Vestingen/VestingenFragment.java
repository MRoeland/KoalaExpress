package be.ehb.koalaexpress.ui.Vestingen;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

import be.ehb.koalaexpress.KoalaDataRepository;
import be.ehb.koalaexpress.R;
import be.ehb.koalaexpress.Tasks.Task_GeolocationZoeken;

public class VestingenFragment extends Fragment implements OnMapReadyCallback {
    final String tag = "VestingenFragment";
    private boolean mLocationPermissionsGranted = false;
    private final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private final float STANDARD_ZOOM_FACTOR = 12f;

    private KoalaDataRepository mrepo;

    private GoogleMap mGoogleMap;

    private Location mMyCurrentLocation;
    public ArrayList<Marker> mMarkerList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_vestingen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMarkerList = new ArrayList<Marker>();
        mGoogleMap = null;
        mLocationPermissionsGranted = false;
        mrepo = KoalaDataRepository.getInstance();

        getLocationPermission();
        if (mLocationPermissionsGranted) {
            initGoogleMaps();
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mGoogleMap = googleMap;
        //Toast.makeText(getContext(), "Map is ready", Toast.LENGTH_SHORT).show();mGoogleMap = googleMap;
        if (mLocationPermissionsGranted) {
            getDeviceLocation();
            // volgende zet de blauwe dot met pijltje op de map
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mGoogleMap.setMyLocationEnabled(true);
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
        voegLocatiesToeAlsMarkers();

        //custom info window voor markers voor de adress info en openingsuren
        // Set a custom info window adapter for the marker
        mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null; // Use default info window background
            }
            @Override
            public View getInfoContents(Marker marker) {
                // Inflate custom layout for the info window
                View v = getLayoutInflater().inflate(R.layout.ic_custom_googlemaps_markerwindow, null);

                be.ehb.koalaexpress.models.Location loc = (be.ehb.koalaexpress.models.Location) marker.getTag();
                // Find views in the custom layout
                TextView title = v.findViewById(R.id.titleTextView);
                TextView omschrijving = v.findViewById(R.id.openingsurenTextView);
                TextView adres = v.findViewById(R.id.tvaddress);
                ImageView imghuislevering = v.findViewById(R.id.imageViewdeliver);
                ImageView imgafhaling = v.findViewById(R.id.imageViewtakeaway);

                // Set the data into the custom layout
                title.setText(loc.mName);
                adres.setText(marker.getTitle());
                String openingsurenstring = loc.getOpeningsUren();
                omschrijving.setText("Openingsuren : \n" + openingsurenstring);

                if(loc.mDelivery) {
                    imghuislevering.setVisibility(View.VISIBLE);
                }
                else {
                    imghuislevering.setVisibility(View.INVISIBLE);
                }
                if(loc.mPickUpInStore) {
                    imgafhaling.setVisibility(View.VISIBLE);
                }
                else {
                    imgafhaling.setVisibility(View.INVISIBLE);
                }

                return v;
            }
        });
    }

    public void voegLocatiesToeAlsMarkers() {
        // toevoegen Koala locations
        for (be.ehb.koalaexpress.models.Location loc : mrepo.koalaLocations.mList) {
            // Replace this with your desired address
            String Locatie_adres = loc.getFullAddress();
            // gebruik geocoding in a achtergrond thread om adres om te zetten in een LatLong coordinaat
            Task_GeolocationZoeken geotask = new Task_GeolocationZoeken(mGoogleMap, getActivity(),mMarkerList);
            geotask.execute(loc);
        }
    }
    public void getLocationPermission() {
        // toestemming vragen om locatie te kennen
        String[] permission = {ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionsGranted = true;
        }
        else
        {
            //ask permission
            ActivityCompat.requestPermissions(getActivity(), permission, LOCATION_PERMISSION_REQUEST_CODE);
            if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
            }
        }
    }
    private void initGoogleMaps() {
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.vestigingen_frame_googlemap, mapFragment)
                .commit();

//        SupportMapFragment mapFragment = (SupportMapFragment)getParentFragment().findFragmentById(R.id.vestigingen_frame_googlemap);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);  // -> roept onmapready op als klaar met initialiseren
        }
    }

    private void getDeviceLocation() {
        Log.i(tag, "getDeviceLocation: getting device coordinates");
        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.getContext());
        try {
            if(mLocationPermissionsGranted) {
                @SuppressLint("MissingPermission")
                Task<Location> location = mFusedLocationProviderClient.getLastLocation();
                location.addOnSuccessListener(this.getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        mMyCurrentLocation = location;
                        Log.i(tag, "onComplete: found location of device");
                        if(mMyCurrentLocation != null)
                            moveComeraOnMap(new LatLng(mMyCurrentLocation.getLatitude(), mMyCurrentLocation.getLongitude()), STANDARD_ZOOM_FACTOR);
                    }
                });
            }
        } catch(SecurityException e) {
            Log.d(tag, "getDeviceLocation: security exception occured : " + e.getMessage());
        }
    }
    private void moveComeraOnMap(LatLng location, float zoomfactor) {
        if(mGoogleMap == null)
            return;
        Log.i(tag, "moveComeraOnMap: camera locatie verplatsen naar: " + location.toString() );
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,zoomfactor));
    }
}