package be.ehb.koalaexpress.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import be.ehb.koalaexpress.KoalaDataRepository;

@Entity(tableName = "orderLine_table", primaryKeys = {"Order_id", "OrderLine_id"})
public class OrderLine {
    @ColumnInfo(name = "Order_id")
    public int mOrderId;
    @ColumnInfo(name = "OrderLine_id")
    public int mBasketLineId;
    @ColumnInfo(name = "Category_id")
    public int mCategoryId;
    @ColumnInfo(name = "Product_id")
    public int mProductId;
    @ColumnInfo(name = "quantity")
    public int mQuantity;
    @ColumnInfo(name = "unitPrice")
    public float mUnitPrice;

    public Product getProduct(KoalaDataRepository repo){
        return repo.getProductWithName(mProductId, mCategoryId);
    }
}
