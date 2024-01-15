package be.ehb.koalaexpress.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity(tableName = "customer_table")
public class Customer {
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "Customer_id")
    public int mCustomerId;
    @ColumnInfo(name = "firstName")
    public String mFirst_Name;
    @ColumnInfo(name = "lastName")
    public String mLast_Name;
    @ColumnInfo(name = "login")
    public String mLogin;
    @ColumnInfo(name = "password")
    public String mPassword;
    @ColumnInfo(name = "invAdressLine1")
    public String mInvAdressline1;
    @ColumnInfo(name = "invAdressLine2")
    public String mInvAdressline2;
    @ColumnInfo(name = "invPostalCode")
    public String mInvPostalCode;
    @ColumnInfo(name = "invCity")
    public String mInvCity;
    @ColumnInfo(name = "invProvince")
    public String mInvProvince;
    @ColumnInfo(name = "invCountryCode")
    public String mInvCountryCode;
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
    @ColumnInfo(name = "email")
    public String mEmail;
    @ColumnInfo(name = "phone")
    public String mPhone;

    public Customer() {
    }

    @JsonIgnore
    public String getFullName() {
        return mFirst_Name + " " + mLast_Name;
    }
}
