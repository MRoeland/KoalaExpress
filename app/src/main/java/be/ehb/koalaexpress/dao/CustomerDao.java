package be.ehb.koalaexpress.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import be.ehb.koalaexpress.models.Customer;
import be.ehb.koalaexpress.models.Product;

@Dao
public interface CustomerDao {
    @Insert
    void insertCustomer(Customer customer);

    @Query("SELECT * FROM customer_table")
    List<Customer> getAllCustomers();

    @Delete
    void deleteCustomer(Customer customer);

    @Update
    void updateCustomer(Customer customer);

    @Insert
    void insertMultipleCustomers(List<Customer> customerList);

    @Query("DELETE FROM customer_table")
    void deleteAllCustomers();
}
