package be.ehb.koalaexpress.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import be.ehb.koalaexpress.models.Location;
import be.ehb.koalaexpress.models.Product;

@Dao
public interface LocationDao {
    @Insert
    void insertLocation(Location location);

    @Query("SELECT * FROM location_table")
    List<Location> getAllLocations();

    @Delete
    void deleteLocation(Location location);

    @Update
    void updateLocation(Location location);

    @Insert
    void insertMultipleLocations(List<Location> locationList);

    @Query("DELETE FROM location_table")
    void deleteAllLocations();
}
