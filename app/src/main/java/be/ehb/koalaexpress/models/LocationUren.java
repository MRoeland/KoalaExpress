package be.ehb.koalaexpress.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(tableName = "locationUren_table", foreignKeys = @ForeignKey(entity = Location.class, parentColumns = "Location_id", childColumns = "Location_id",
        onDelete = ForeignKey.CASCADE), primaryKeys = {"Location_id", "weekDay"})
public class LocationUren {
    @ColumnInfo(name = "Location_id")
    public int mLocationId;
    @ColumnInfo(name = "weekDay")
    public int mWeekDay;
    @ColumnInfo(name = "fromTime")
    public String mFromTime;
    @ColumnInfo(name = "toTime")
    public String mToTime;

    public LocationUren() {

    }
}
