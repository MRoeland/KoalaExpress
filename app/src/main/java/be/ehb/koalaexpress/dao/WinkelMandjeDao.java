package be.ehb.koalaexpress.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import be.ehb.koalaexpress.models.Location;
import be.ehb.koalaexpress.models.Product;
import be.ehb.koalaexpress.models.WinkelMandje;

@Dao
public interface WinkelMandjeDao {
    @Insert
    void insertBasket(WinkelMandje winkelMandje);
    @Query("SELECT * FROM winkelMandje_table")
    List<WinkelMandje> getAllBaskets();
    @Delete
    void deleteBasket(WinkelMandje winkelMandje);

    @Update
    void updateBasket(WinkelMandje winkelMandje);

    @Insert
    void insertMultipleBaskets(List<WinkelMandje> winkelMandjeList);

    @Query("DELETE FROM winkelMandje_table")
    void deleteAllBaskets();
}
