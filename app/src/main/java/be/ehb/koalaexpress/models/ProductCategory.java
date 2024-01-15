package be.ehb.koalaexpress.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "category_table")
public class ProductCategory {
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "Category_id")
    public int mCategoryId;
    @ColumnInfo(name = "name")
    public String mName;
    @ColumnInfo(name = "description")
    public String mDescription;
    @ColumnInfo(name = "order")
    public int mSortOrder;

    public String toString() {
        return mName;
    }
}
