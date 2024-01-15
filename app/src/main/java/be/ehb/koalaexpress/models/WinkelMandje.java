package be.ehb.koalaexpress.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "winkelMandje_table")
public class WinkelMandje {
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "Order_id")
    public int mOrderId;
    @ColumnInfo(name = "Customer_id")
    public int mCustomerId;
    @ColumnInfo(name = "contactName")
    public String mContactName;
    @ColumnInfo(name = "contactEmail")
    public String mContactEmail;
    @ColumnInfo(name = "contactPhone")
    public String mContactPhone;
    @ColumnInfo(name = "pickupInStore")
    public Boolean mPickUpInStore;
    @ColumnInfo(name = "pickupStore_id")
    public int mPickUpStoreId;
    @ColumnInfo(name = "pickupTimeFrom")
    public String mPickupTimeFrom;
    @ColumnInfo(name = "pickupTimeUntil")
    public String mPickupTimeUntil;
    @ColumnInfo(name = "delAdressLine1")
    public String mDelAdressline1;
    @ColumnInfo(name = "delAdressLine2")
    public String mDelAdressline2;
    @ColumnInfo(name = "delPostalCode")
    public String mDelPostalCode;
    @ColumnInfo(name = "delCity")
    public String mDelCity;
    @ColumnInfo(name = "delProvince")
    public String mDelProvince;
    @ColumnInfo(name = "delCountryCode")
    public String mDelCountryCode;
    @ColumnInfo(name = "deliveryTime")
    public String mDeliveryTime;
    @ColumnInfo(name = "reductionCode")
    public String mReductionCode;
    @ColumnInfo(name = "reductionPrice")
    public float mReductionPrice;
    @ColumnInfo(name = "productPrice")
    public float mProductPrice;
    @ColumnInfo(name = "taxPrice")
    public float mTaxPrice;
    @ColumnInfo(name = "shipCostPrice")
    public float mShipCostPrice;
    @ColumnInfo(name = "totalPrice")
    public float mTotalPrice;
    @ColumnInfo(name = "payPalPaymentId")
    public String mPayPalPaymentId;
    @ColumnInfo(name = "payedOnDate")
    public Timestamp mPayedOnDate;
    @Ignore
    public List<OrderLine> mOrderLines;

    public WinkelMandje() {
        mOrderLines = new ArrayList<>();
        ClearBasket();
    }

    public void AddProduct(Product p, int aantal){
        OrderLine o = FindLineForProduct(p);
        if(o != null){
            o.mQuantity += aantal;
        }
        else {
            o = new OrderLine();
            o.mQuantity = aantal;
            o.mProductId = p.mProductId;
            o.mCategoryId = p.mCategoryId;
            o.mUnitPrice = p.mPrice;
            o.mBasketLineId = mOrderLines.size()+1;
            o.mOrderId = this.mOrderId;

            mOrderLines.add(o);
        }

        if(o.mQuantity <= 0){
            mOrderLines.remove(o);
        }

        CalculateTotalPrice();
    }

    public OrderLine FindLineForProduct(Product p){
        for (OrderLine l: mOrderLines) {
            if(p.mProductId == l.mProductId && p.mCategoryId == l.mCategoryId){
                return  l;
            }
        }

        return null;
    }
    @JsonIgnore
    public float getItemTotal() {
        float TotalPrice = 0.0f;

        for (OrderLine o : mOrderLines) {
            TotalPrice += (o.mUnitPrice * o.mQuantity);
        }
        return TotalPrice;
    }
    public void CalculateTotalPrice(){
        mTotalPrice = 0.0f;

        mTotalPrice += getItemTotal();
        mReductionPrice = getKorting();
        mTotalPrice -= mReductionPrice;

        mTotalPrice += mShipCostPrice;

        if(mTotalPrice < 0){
            mTotalPrice = 0;
        }
    }

    @JsonIgnore
    private float getKorting() {
        DecimalFormat df = new DecimalFormat("0.00");
        float prijslijnen = getItemTotal();
        float reduction = 0.0f;
        if(mReductionCode.equalsIgnoreCase("k10")) {
            reduction = prijslijnen * 10.0f / 100.0f;
        }
        else if(mReductionCode.equalsIgnoreCase("k5")) {
            reduction = prijslijnen * 5.0f / 100.0f;
        }
        else if(mReductionCode.equalsIgnoreCase("v5")) {
            reduction = 5.0f;
        }
        else
            reduction = 0.0f;
        // round 2 decimals
        reduction = (float)Math.round(reduction * 100.0f) / 100.0f;
        return reduction;
    }
    public void SetCustomer(Customer c){
        if(c != null) {
            mCustomerId = c.mCustomerId;
            mContactName = c.getFullName();
            mContactEmail = c.mEmail;
            mContactPhone = c.mPhone;
            mDelAdressline1 = c.mDelAdressline1;
            mDelAdressline2 = c.mDelAdressline2;
            mDelPostalCode = c.mDelPostalCode;
            mDelCity = c.mDelCity;
            mDelProvince = c.mDelProvince;
            mDelCountryCode = c.mDelCountryCode;
        }
        else {
            mCustomerId = 0;
            mContactName = "";
            mContactEmail = "";
            mContactPhone = "";
            mDelAdressline1 = "";
            mDelAdressline2 = "";
            mDelPostalCode = "";
            mDelCity = "";
            mDelProvince = "";
            mDelCountryCode = "";
        }
    }

    public void ClearBasket(){
        mCustomerId = -1;
        mContactName = "";
        mContactEmail = "";
        mContactPhone = "";
        mDelAdressline1 = "";
        mDelAdressline2 = "";
        mDelPostalCode = "";
        mDelCity = "";
        mDelProvince = "";
        mDelCountryCode = "";
        mPickUpInStore=true;
        mPickUpStoreId= -1;
        mPickupTimeFrom="";
        mPickupTimeUntil="";
        mDeliveryTime="";
        mReductionCode="";
        mReductionPrice=0.0f;
        mProductPrice=0.0f;
        mTaxPrice=0.0f;
        mShipCostPrice=0.0f;
        mTotalPrice=0.0f;
        mPayPalPaymentId="";
        mPayedOnDate=new Timestamp(0);
        mOrderLines.clear();
    }

    public boolean canCheckout() {
        if(mOrderLines.size() == 0)
            return false;
        return true;
    }
    public void SetOrderId(int o_id) {
        mOrderId = o_id;
        for (OrderLine l :mOrderLines) {
            l.mOrderId = o_id;
        }
    }

    public Boolean toepassenKorting(String kortingcode) {
        // controleer of korting code bestaat, indien ja, return true en opslaan in mandje
        // berekening gebeurd in CalculateTotalPrice
        ArrayList<String> kortinglist = new ArrayList<String>();
        kortinglist.add("k5");
        kortinglist.add("k10");
        kortinglist.add("v5");
        Boolean returnvalue = false;
        if (kortinglist.contains(kortingcode.toLowerCase())) {
            mReductionCode = kortingcode.toLowerCase();
            returnvalue = true;
        }
        else
            mReductionCode = "";

        return returnvalue;
    }

    public OrderLine getOrderlijnMetProduct(Product p) {
        for (OrderLine l :mOrderLines) {
            if(l.mProductId == p.mProductId && l.mCategoryId == p.mCategoryId) {
                return l;
            }
        }
        return null;
    }
}
