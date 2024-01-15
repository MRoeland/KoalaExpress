package be.ehb.koalaexpress.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import be.ehb.koalaexpress.models.LocationUren;
import be.ehb.koalaexpress.models.OrderLine;

@Dao
public interface OrderLineDao {
    @Insert
    void insertOrderLine(OrderLine orderLine);

    @Query("SELECT * FROM orderLine_table")
    List<OrderLine> getAllOrderLines();

    @Delete
    void deleteOrderLine(OrderLine orderLine);

    @Update
    void updateOrderLine(OrderLine orderLine);

    @Insert
    void insertMultipleOrderLines(List<OrderLine> orderLineList);

    @Query("DELETE FROM orderLine_table")
    void deleteAllOrderLines();

    @Query("SELECT * FROM orderLine_table WHERE Order_id = :PARAM")
    List<OrderLine> getAllOrderLinesForBasket(int PARAM);
}
