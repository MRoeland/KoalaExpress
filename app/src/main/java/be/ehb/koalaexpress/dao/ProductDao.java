package be.ehb.koalaexpress.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import be.ehb.koalaexpress.models.Product;
import be.ehb.koalaexpress.models.ProductCategory;

@Dao
public interface ProductDao {
    @Insert
    void insertProduct(Product product);

    @Query("SELECT * FROM product_table")
    List<Product> getAllProducts();

    @Delete
    void deleteProduct(Product product);

    @Update
    void updateProduct(Product product);

    @Insert
    void insertMultipleProducts(List<Product> productList);

    @Query("DELETE FROM product_table")
    void deleteAllProducts();
}
