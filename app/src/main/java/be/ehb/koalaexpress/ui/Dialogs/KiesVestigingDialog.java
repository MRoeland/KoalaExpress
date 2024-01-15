package be.ehb.koalaexpress.ui.Dialogs;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

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

import java.time.LocalDateTime;
import java.util.ArrayList;

import be.ehb.koalaexpress.KoalaDataRepository;
import be.ehb.koalaexpress.R;
import be.ehb.koalaexpress.Tasks.Task_GeolocationZoeken;

public class KiesVestigingDialog extends DialogFragment  implements OnMapReadyCallback  {
    private static final String TAG = "KiesVestigingDialog";
    public Button btnOK;

    private boolean mLocationPermissionsGranted = false;
    private final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private final float STANDARD_ZOOM_FACTOR = 12f;

    private KoalaDataRepository mrepo;

    private GoogleMap mGoogleMap;

    private Location mMyCurrentLocation;
    public be.ehb.koalaexpress.models.Location m_SelectedVestiging;
    public Switch swKiesVestigingButton;
    public TextView tvTitle;
    public TextView tvAdres;
    public TextView tvOpen;


    public ArrayList<Marker> mListGoogleMapMarkers;

    public KiesVestigingDialog(KoalaDataRepository repo) {
        mrepo=repo;
        mListGoogleMapMarkers = new ArrayList<>();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_googlemaps_kiesvestiging, container, false);

        mrepo = KoalaDataRepository.getInstance();

        tvTitle  =view.findViewById(R.id.tvTitel);
        tvAdres  =view.findViewById(R.id.tvAdres);
        tvOpen  =view.findViewById(R.id.tvOpen);

        swKiesVestigingButton =view.findViewById(R.id.dialog_btnvestigingswitch);
        swKiesVestigingButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
                // means bezorgen
                removeCurrentMarkers();
                voegLocatiesToeAlsMarkers(true);
            }
            else {
                // means afhalen
                removeCurrentMarkers();
                voegLocatiesToeAlsMarkers(false);
            }
        });

        btnOK = view.findViewById(R.id.dialog_btn_dlg_ok);
        btnOK.setOnClickListener(v -> {
            dismiss();
        });
        mGoogleMap = null;
        mLocationPermissionsGranted = false;
        m_SelectedVestiging = null;

        m_SelectedVestiging = mrepo.mVestiging.getValue();


        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mGoogleMap = null;
        mLocationPermissionsGranted = false;
        mrepo = KoalaDataRepository.getInstance();

        getLocationPermission();
        if (mLocationPermissionsGranted) {
            initGoogleMaps();
        }
    }
    public void vulGegevensVestiging() {
        if(m_SelectedVestiging == null) {
            tvTitle.setText("Geen vestiging gekozen, klik op een marker.");
            tvAdres.setText("");
            tvOpen.setText("");
        }
        else {
            tvTitle.setText(m_SelectedVestiging.mName);
            tvAdres.setText(m_SelectedVestiging.getFullAddress());
            LocalDateTime now = LocalDateTime.now();
            String opennustring = m_SelectedVestiging.isOpen(now);
            tvOpen.setText(opennustring);
            if(m_SelectedVestiging.mLatLng != null)
                moveCameraOnMap(m_SelectedVestiging.mLatLng, STANDARD_ZOOM_FACTOR);
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
            //mGoogleMap.setMyLocationEnabled(true);
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
        voegLocatiesToeAlsMarkers(swKiesVestigingButton.isChecked());

        vulGegevensVestiging();

        mGoogleMap.setOnMarkerClickListener(marker -> {
            //clicked on a marker, tag has location
            m_SelectedVestiging = (be.ehb.koalaexpress.models.Location)marker.getTag();
            vulGegevensVestiging();
            boolean delivery = swKiesVestigingButton.isChecked();
            mrepo.selectVestiging(m_SelectedVestiging, delivery);
            return false;
        });
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
                title.setTextSize(10.0f);
                adres.setTextSize(10.0f);
                omschrijving.setTextSize(8.0f);
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

    public void removeCurrentMarkers() {
        for (Marker m: mListGoogleMapMarkers) {
            m.remove();
        }
        mListGoogleMapMarkers.clear();
    }

    public void voegLocatiesToeAlsMarkers(Boolean bezorgen) {
        // toevoegen Koala locations
        for (be.ehb.koalaexpress.models.Location loc : mrepo.koalaLocations.mList) {
            // Replace this with your desired address
            mListGoogleMapMarkers.clear();
            String Locatie_adres = loc.getFullAddress();
            if(bezorgen == true && loc.mDelivery==true || bezorgen == false){
                // gebruik geocoding in a achtergrond thread om adres om te zetten in een LatLong coordinaat
                Task_GeolocationZoeken geotask = new Task_GeolocationZoeken(mGoogleMap, getActivity(),mListGoogleMapMarkers);
                geotask.execute(loc);
            }
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
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.dlg_frame_googlemap);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void getDeviceLocation() {
        Log.i(TAG, "getDeviceLocation: getting device coordinates");
        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.getContext());
        try {
            if(mLocationPermissionsGranted) {
                @SuppressLint("MissingPermission")
                Task<Location> location = mFusedLocationProviderClient.getLastLocation();
                location.addOnSuccessListener(this.getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        mMyCurrentLocation = location;
                        Log.i(TAG, "onComplete: found location of device");
                        if(mMyCurrentLocation != null)
                            moveCameraOnMap(new LatLng(mMyCurrentLocation.getLatitude(), mMyCurrentLocation.getLongitude()), STANDARD_ZOOM_FACTOR);
                    }
                });
            }
        } catch(SecurityException e) {
            Log.d(TAG, "getDeviceLocation: security exception occured : " + e.getMessage());
        }
    }
    private void moveCameraOnMap(LatLng location, float zoomfactor) {
        if(mGoogleMap == null)
            return;
        Log.i(TAG, "moveComeraOnMap: camera locatie verplatsen naar: " + location.toString() );
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,zoomfactor));
    }
}
