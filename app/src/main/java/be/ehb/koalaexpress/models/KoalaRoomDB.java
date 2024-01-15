package be.ehb.koalaexpress.models;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import be.ehb.koalaexpress.dao.CategoryDao;
import be.ehb.koalaexpress.dao.CustomerDao;
import be.ehb.koalaexpress.dao.LocationDao;
import be.ehb.koalaexpress.dao.LocationUrenDao;
import be.ehb.koalaexpress.dao.OrderLineDao;
import be.ehb.koalaexpress.dao.ProductDao;
import be.ehb.koalaexpress.dao.WinkelMandjeDao;


@Database(entities = {ProductCategory.class, Product.class, Location.class, LocationUren.class, Customer.class, WinkelMandje.class, OrderLine.class}, version = 1)
@TypeConverters({TimestampConverter.class})
public abstract class KoalaRoomDB extends RoomDatabase {
    public abstract CategoryDao categoryDao();
    public abstract ProductDao productDao();
    public abstract LocationDao locationDao();
    public abstract LocationUrenDao locationUrenDao();
    public abstract CustomerDao customerDao();
    public abstract WinkelMandjeDao winkelMandjeDao();
    public abstract OrderLineDao orderLineDao();

    private static volatile KoalaRoomDB INSTANCE;

    private static final int numberOfThreads = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(numberOfThreads);

    public static KoalaRoomDB getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (KoalaRoomDB.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    KoalaRoomDB.class, "Koala_Database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public void CleanDataBase(){
        categoryDao().deleteAllCategories();
        productDao().deleteAllProducts();
        locationUrenDao().deleteAllUren();
        locationDao().deleteAllLocations();
        customerDao().deleteAllCustomers();
    }
}
