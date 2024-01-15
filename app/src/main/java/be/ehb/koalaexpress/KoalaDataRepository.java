package be.ehb.koalaexpress;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import be.ehb.koalaexpress.Tasks.Task_FetchCategories;
import be.ehb.koalaexpress.Tasks.Task_FetchCustomers;
import be.ehb.koalaexpress.Tasks.Task_FetchProducts;
import be.ehb.koalaexpress.Tasks.Task_FetchVestigingen;
import be.ehb.koalaexpress.Tasks.Task_LoadRepoFromDB;
import be.ehb.koalaexpress.Tasks.Task_LoadUnfinishedOrder;
import be.ehb.koalaexpress.Tasks.Task_SaveOrderToRoomDB;
import be.ehb.koalaexpress.models.Customer;
import be.ehb.koalaexpress.models.CustomerList;
import be.ehb.koalaexpress.models.KoalaRoomDB;
import be.ehb.koalaexpress.models.Location;
import be.ehb.koalaexpress.models.LocationList;
import be.ehb.koalaexpress.models.Product;
import be.ehb.koalaexpress.models.ProductCategoryList;
import be.ehb.koalaexpress.models.ProductList;
import be.ehb.koalaexpress.models.WinkelMandje;

public class KoalaDataRepository  {

    public static KoalaDataRepository repo;
    public MutableLiveData<ProductCategoryList> koalaCategorieen;
    public MutableLiveData<ProductList> koalaProducten;
    public LocationList koalaLocations;
    public CustomerList koalaCustomers;
    public MutableLiveData<WinkelMandje> mWinkelMandje;
    public MutableLiveData<Customer> mCustomer;
    public MutableLiveData<Location> mVestiging;
    public MutableLiveData<String> repoInfoMessage;
    public String PaypalAccessToken;
    public Boolean mServerStatusGechecked;
    public Boolean mServerIsOnline;
    public Boolean mReadyInitDB;
    public Boolean mFinishedReadingBasketFromRoomDB;

    public static KoalaDataRepository getInstance(){
        if(repo == null) {
            repo = new KoalaDataRepository();
        }

        return repo;
    }

    public KoalaDataRepository() {
        koalaCategorieen = new MutableLiveData<ProductCategoryList>();
        koalaCategorieen.setValue(new ProductCategoryList());

        koalaProducten = new MutableLiveData<ProductList>();
        koalaProducten.setValue(new ProductList());

        mWinkelMandje = new MutableLiveData<WinkelMandje>();
        mWinkelMandje.setValue(new WinkelMandje());

        koalaCustomers = new CustomerList();

        mCustomer = new MutableLiveData<Customer>();
        mCustomer.setValue(null);

        mVestiging=new MutableLiveData<Location>();
        mVestiging.setValue(null);

        repoInfoMessage = new MutableLiveData<>();
        repoInfoMessage.setValue("");

        koalaLocations = new LocationList();

        PaypalAccessToken = "";

        mServerStatusGechecked = false;
        mServerIsOnline = false;
        mReadyInitDB = false;
        mFinishedReadingBasketFromRoomDB = false;
    }

    public void InitialiseBijStart(){
        //http://localhost:8080/KoalaExpressServer/products?action=listCategories
        //genericWebServiceCall webServiceGetProducts = new genericWebServiceCall("products", "listCategories","", this);
        //webServiceGetProducts.execute();
        initCategories();
        initProducts();
        initLocations();
        initCustomers();
    }

    private void initCategories() {
        Task_FetchCategories taak = new Task_FetchCategories();
        taak.execute(this);
    }

    private void initProducts(){
        Task_FetchProducts taak = new Task_FetchProducts();
        taak.execute(this);
    }

    private void initLocations() {
        Task_FetchVestigingen taak = new Task_FetchVestigingen();
        taak.execute(this);
    }
    private void initCustomers() {
        Task_FetchCustomers taak = new Task_FetchCustomers();
        taak.execute(this);
    }
    public Product getProductWithName(int productId, int categoryId){
        for (Product p :koalaProducten.getValue().mList) {
            if(p.mProductId == productId && p.mCategoryId == categoryId){

                return p;
            }
        }

        return null;
    }

    public void TryLoginUser(String userName, String password){
        mCustomer.setValue(koalaCustomers.LoginCustomer(userName, password));
        if(mCustomer.getValue() == null){
            repoInfoMessage.postValue("Klant login of paswoord is ongeldig");
        }
    }

    public void ClearWinkelMandjeOnCheckout(){
        WinkelMandje w = mWinkelMandje.getValue();
        w.ClearBasket();
        //wis de laatste gekende vestiging
        mVestiging.setValue(null);
        if(mCustomer.getValue() != null) {
            // als nog ingelogd, zet customer
            w.SetCustomer(mCustomer.getValue());
        }
        mWinkelMandje.setValue(w);
    }

    public Boolean IsReady(){
        // klaar als er producten, categorieen en locations zijn
        if (koalaCategorieen.getValue().mList.size() > 0
                && koalaProducten.getValue().mList.size() > 0
                && koalaLocations.mList.size() > 0
                && koalaCustomers.mList.size() > 0){
            return true;
        }
        else {
            return false;
        }
    }

    public void SaveRepoInRoomDB(Context c){
        //c.deleteDatabase("Koala_Database");
        KoalaRoomDB db = KoalaRoomDB.getInstance(c);

        KoalaRoomDB.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                //Eerst alles deleten
                db.CleanDataBase();
                //Erna alles toevoegen
                db.categoryDao().insertMultipleCategories(koalaCategorieen.getValue().mList);
                db.productDao().insertMultipleProducts(koalaProducten.getValue().mList);
                db.locationDao().insertMultipleLocations(koalaLocations.mList);
                for (Location l : koalaLocations.mList) {
                    db.locationUrenDao().insertMultipleUren(l.mListOpeningHours);
                }
                db.customerDao().insertMultipleCustomers(koalaCustomers.mList);
                mReadyInitDB = true;
            }
        });
    }

    public void LoadRepoFromRoomDB(Context c){
        Task_LoadRepoFromDB taskLoadRepoFromDB = new Task_LoadRepoFromDB(c);
        taskLoadRepoFromDB.execute(this);
    }

    public void LoadOrderFromRoomDB(Context c){
        Task_LoadUnfinishedOrder taskLoadUnfinishedOrder = new Task_LoadUnfinishedOrder(c);
        taskLoadUnfinishedOrder.execute(this);
    }

    public void SaveOrderToRoomDB(Context c){
        Task_SaveOrderToRoomDB taskSaveOrderToRoomDB = new Task_SaveOrderToRoomDB(c);
        taskSaveOrderToRoomDB.execute(this);
    }

    public void selectVestiging(Location NewSelectedVestiging, boolean delivery) {
        //nieuwe vestiging gekozen, update vestiging en alle velden in mandje
        mVestiging.setValue(NewSelectedVestiging);
        if(NewSelectedVestiging != null) {
            WinkelMandje m = mWinkelMandje.getValue();
            m.mPickUpInStore = (delivery==false);
            m.mPickUpStoreId = NewSelectedVestiging.mLocationId;
            m.mDelAdressline1 = "";
            m.mDelAdressline2 = "";
            m.mDelCity = "";
            m.mDelPostalCode = "";
            m.mDelProvince = "";
            m.mDelCountryCode = "";
            if(delivery == true) {
                if(mCustomer.getValue() != null) {
                    Customer c = mCustomer.getValue();
                    m.mDelAdressline1 = c.mDelAdressline1;
                    m.mDelAdressline2 = c.mDelAdressline2;
                    m.mDelCity = c.mDelCity;
                    m.mDelPostalCode = c.mDelPostalCode;
                    m.mDelProvince = c.mDelProvince;
                    m.mDelCountryCode = c.mDelCountryCode;
                }
                Calendar calendar = Calendar.getInstance();
                // Add 45 minutes
                calendar.add(Calendar.MINUTE, 45);
                Timestamp updatedTimestamp = new Timestamp(calendar.getTimeInMillis());
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                m.mDeliveryTime = sdf.format(updatedTimestamp);
            }
            else {
                Calendar calendar = Calendar.getInstance();
                // Add 30 minutes
                calendar.add(Calendar.MINUTE, 30);
                Timestamp updatedTimestamp = new Timestamp(calendar.getTimeInMillis());
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                m.mPickupTimeFrom = sdf.format(updatedTimestamp);
            }
            repo.mWinkelMandje.setValue(m);
        }
    }
}
