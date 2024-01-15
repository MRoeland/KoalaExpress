package be.ehb.koalaexpress.models;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
/*
@Database(version = 1, entities = {Product.class})
public abstract class KoalaDB extends RoomDatabase {

    private static KoalaDB INSTANCE;

    public static KoalaDB getINSTANCE(Context context){
        if(INSTANCE == null){
            //Maak database verbinding

            INSTANCE = Room.databaseBuilder(context, KoalaDB.class, "KoalaDB.sqlite").build();
        }
        return INSTANCE;
    }

    public abstract ProductDAO getProductDao();

}
*/