package be.ehb.koalaexpress.models;

import androidx.room.TypeConverter;
import java.sql.Timestamp;

public class TimestampConverter {

    @TypeConverter
    public static Timestamp toTimestamp(Long value) {
        return value == null ? null : new Timestamp(value);
    }

    @TypeConverter
    public static Long fromTimestamp(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.getTime();
    }
}