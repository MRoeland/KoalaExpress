package be.ehb.koalaexpress.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;


@Entity(tableName = "product_table", foreignKeys = @ForeignKey(entity = ProductCategory.class, parentColumns = "Category_id",
        childColumns = "Category_id", onDelete = ForeignKey.CASCADE), primaryKeys = {"Category_id", "Product_id"})
public class Product {
    @ColumnInfo(name = "Category_id")
    public int mCategoryId;
    @ColumnInfo(name = "Product_id")
    public int mProductId;
    @ColumnInfo(name = "name")
    public String mName;
    @ColumnInfo(name = "description")
    public String mDescription;
    @ColumnInfo(name = "detailedDescription")
    public String mDetailedDescription;
    @ColumnInfo(name = "image")
    public String mImage;
    @ColumnInfo(name = "price")
    public float mPrice;

    public Product() {
        
    }
}
