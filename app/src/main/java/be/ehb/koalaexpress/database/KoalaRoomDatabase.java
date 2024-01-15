package be.ehb.koalaexpress.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import be.ehb.koalaexpress.models.ProductCategory;

@Database(entities = {ProductCategory.class}, version = 1)
public abstract class KoalaRoomDatabase extends RoomDatabase {

    //public abstract TodoDao todoDao();

    private static volatile KoalaRoomDatabase INSTANCE;

    static KoalaRoomDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (KoalaRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    KoalaRoomDatabase.class, "Koala_Database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}


