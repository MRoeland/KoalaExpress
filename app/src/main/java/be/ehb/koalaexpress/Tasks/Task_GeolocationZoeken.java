package be.ehb.koalaexpress.Tasks;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import be.ehb.koalaexpress.ConnectionInfo;
import be.ehb.koalaexpress.JSONhelper;
import be.ehb.koalaexpress.KoalaDataRepository;
import be.ehb.koalaexpress.R;
import be.ehb.koalaexpress.models.Customer;
import be.ehb.koalaexpress.models.Location;
import be.ehb.koalaexpress.returnMessage;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Task_GeolocationZoeken extends AsyncTask<Location, Object, Address> {
    private String TAG = "Task_GeolocationZoeken";
    private GoogleMap mGoogleMap;
    private Activity mActivity;
    public ArrayList<Marker> mMarkerListToFill;
    private Location mLocation;
    public Task_GeolocationZoeken(GoogleMap deMap, Activity vanuitActivity, ArrayList<Marker> markerlisttofill) {
        mGoogleMap = deMap;
        mActivity = vanuitActivity;
        mLocation = null;
        mMarkerListToFill = markerlisttofill;

    }

    @Override
    protected Address doInBackground(Location... loclijst) {
        mLocation = loclijst[0];
        if (mLocation==null)
            return null;

        Address returnAdres = null;
        if(mLocation.mLatLng == null) {
            String Locatie_adres = mLocation.getFullAddress();
            Geocoder geocoder = new Geocoder(mActivity, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocationName(Locatie_adres, 1);
                if (addresses != null && addresses.size() > 0) {
                    // adressen gevonden, neem eerste
                    returnAdres = addresses.get(0);
                } else {
                    Log.e(TAG, "Adres locatie " + mLocation.mName + " (" + mLocation.getFullAddress() + ") kan niet gevonden worden in geocoding van googlemaps");
                }
            } catch (IOException e) {
                Log.e(TAG, "Adres exception locatie " + mLocation.mName + " (" + mLocation.getFullAddress() + ") kan niet gevonden worden in geocoding van googlemaps");
                Log.e(TAG, "Geocoding error: " + e.getLocalizedMessage());
            }
        }
        return returnAdres;
    }

    @Override
    protected void onPostExecute(Address googlemapsadres){
        super.onPostExecute(googlemapsadres);
        //wanneer klaar is in background komt hier en dit wordt uitgevoerd in de main ui thread, mag dus googlemap aanpassen
        //maak marker en hang locatie als tag aan marker
        if(mLocation != null){
            LatLng geoCoordinaat = null;
            if(googlemapsadres != null){
                //net opgezocht
                double latitude = googlemapsadres.getLatitude();
                double longitude = googlemapsadres.getLongitude();
                geoCoordinaat = new LatLng(latitude, longitude);
                //hou latlng bij (1 keer zoeken)
                mLocation.mLatLng = geoCoordinaat;
            }
            else {
                // is een vorige keer al opgezocht met geocoder
                geoCoordinaat = mLocation.mLatLng;
            }
            if(geoCoordinaat == null) {
                Log.e(TAG, "Geocoordinaat voor " + mLocation.mName + " is NULL !");
                return;
            }
            MarkerOptions mo = new MarkerOptions().position(geoCoordinaat);
            mo.title(getAddressAsString(googlemapsadres));
            mo.draggable(false);
            if (mLocation.mDelivery == true) {
                mo.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_koala_logo_t50));
            } else {
                mo.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_koala_logo_or_t50));
            }
            mo.anchor(0.5f, 0.5f);
            Marker newMarker = mGoogleMap.addMarker(mo);
            // voeg object toe als tag aan marker zodat we het later kunnen opnieuw gebruiken vanuit marker
            newMarker.setTag(mLocation);
            mMarkerListToFill.add(newMarker);
        }
    }
    public static String getAddressAsString(Address address) {
        if (address == null) {
            return "";
        }
        StringBuilder fullAddress = new StringBuilder();
        int maxLines = address.getMaxAddressLineIndex();
        for (int i = 0; i <= maxLines; i++) {
            fullAddress.append(address.getAddressLine(i));
            if (i < maxLines) {
                fullAddress.append(", "); // Add a comma and space between address lines
            }
        }
        return fullAddress.toString();
    }
}
