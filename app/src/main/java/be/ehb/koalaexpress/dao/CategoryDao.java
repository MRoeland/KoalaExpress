package be.ehb.koalaexpress.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import be.ehb.koalaexpress.models.ProductCategory;

@Dao
public interface CategoryDao {
    @Insert
    void insertCategory(ProductCategory category);

    @Query("SELECT * FROM category_table")
    List<ProductCategory> getAllProductCategories();

    @Delete
    void deleteCategory(ProductCategory category);

    @Update
    void updateCategory(ProductCategory category);

    @Insert
    void insertMultipleCategories(List<ProductCategory> categoryList);

    @Query("DELETE FROM category_table")
    void deleteAllCategories();
}
