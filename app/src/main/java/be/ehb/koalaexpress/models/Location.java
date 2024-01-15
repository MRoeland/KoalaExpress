package be.ehb.koalaexpress.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.android.gms.maps.model.LatLng;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Entity(tableName = "location_table")
public class Location {
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "Location_id")
    public int mLocationId;
    @ColumnInfo(name = "name")
    public String mName;
    @ColumnInfo(name = "street")
    public String mStreet;
    @ColumnInfo(name = "number")
    public int mNumber;
    @ColumnInfo(name = "additional")
    public String mAdditional;
    @ColumnInfo(name = "postalCode")
    public String mPostalCode;
    @ColumnInfo(name = "city")
    public String mCity;
    @ColumnInfo(name = "country")
    public String mCountry;
    @ColumnInfo(name = "pickUpInStore")
    public boolean mPickUpInStore;
    @ColumnInfo(name = "delivery")
    public boolean mDelivery;
    @ColumnInfo(name = "deliveryMaxRangeKm")
    public int mDeliveryMaxRangeKm;
    @ColumnInfo(name = "fixedCostDelivery")
    public float mFixedCostDelivery;
    @ColumnInfo(name = "costDeliveryPricePerKm")
    public float mCostDeliveryPricePerKm;
    @Ignore
    public ArrayList<LocationUren> mListOpeningHours;

    @Ignore
    @JsonIgnore
    public LatLng mLatLng;

    public Location() {
        mListOpeningHours = new ArrayList<>();
        mLatLng = null;
    }

    public void addOpeningHours(LocationUren hourobj) {
        mListOpeningHours.add(hourobj);
    }

    @JsonIgnore
    public String getFullAddress() {
        return mStreet + " "+
                mNumber + " " +
                mPostalCode + ", " +
                mCity + ", " +
                mCountry;
    }
    @JsonIgnore
    private String getWeekdayName(int daynumber) {
        String dag="";
        switch (daynumber) {
            case 1:
                dag = "Maandag";
                break;
            case 2:
                dag = "Dinsdag";
                break;
            case 3:
                dag = "Woensdag";
                break;
            case 4:
                dag = "Donderdag";
                break;
            case 5:
                dag = "Vrijdag";
                break;
            case 6:
                dag = "Zaterdag";
                break;
            case 7:
                dag = "Zondag";
                break;
            default:
                dag = "Fout dagnummer";
        }
        return dag;
    }
    @JsonIgnore
    public String getOpeningsUren() {
        String openingsuren="";
        String dag="";
        for (LocationUren u : mListOpeningHours) {
            dag = getWeekdayName(u.mWeekDay);
            openingsuren += (dag + " : " + u.mFromTime + " - " + u.mToTime + "\n");
        }
        return openingsuren;
    }
    @JsonIgnore
    public LocationUren getLocationUrenForDay(int day) {
        for (LocationUren u : mListOpeningHours) {
            if(u.mWeekDay == day)
                return u;
        }
        return null;
    }
    public String isOpen(LocalDateTime timetocheck) {
        String returnstr="";
        DayOfWeek d = timetocheck.getDayOfWeek();
        int dayofweek = d.getValue();
        LocationUren openingsuren = getLocationUrenForDay(dayofweek);
        if(openingsuren == null)
            returnstr = "De vestiging is momenteel niet geopend.";
        else {
            returnstr = String.format("Vandaag geopend van %s u tot %s u", openingsuren.mFromTime, openingsuren.mToTime);
        }
        return returnstr;

    }
}
