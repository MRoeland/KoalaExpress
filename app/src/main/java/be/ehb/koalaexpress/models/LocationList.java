package be.ehb.koalaexpress.models;

import java.util.ArrayList;

public class LocationList {
    public ArrayList<Location> mList;

    public LocationList() {

        mList = new ArrayList<Location>();
    }

    public Location getLocationWithId(int LocationId) {
        for (Location l: mList) {
            if(l.mLocationId == LocationId)
                return l;
        }
        // niet gevonden
        return null;
    }
}
