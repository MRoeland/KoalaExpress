package be.ehb.koalaexpress.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import be.ehb.koalaexpress.models.LocationUren;
import be.ehb.koalaexpress.models.Product;

@Dao
public interface LocationUrenDao {
    @Insert
    void insertUren(LocationUren locationUren);

    @Query("SELECT * FROM locationUren_table")
    List<LocationUren> getAllUren();

    @Delete
    void deleteUren(LocationUren locationUren);

    @Update
    void updateUren(LocationUren locationUren);

    @Insert
    void insertMultipleUren(List<LocationUren> urenList);

    @Query("DELETE FROM locationUren_table")
    void deleteAllUren();

    @Query("SELECT * FROM LOCATIONUREN_TABLE WHERE Location_id = :PARAM")
    List<LocationUren> GetAllUrenForLocation(int PARAM);
}
