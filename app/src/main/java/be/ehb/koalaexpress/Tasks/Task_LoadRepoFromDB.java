package be.ehb.koalaexpress.Tasks;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;

import be.ehb.koalaexpress.KoalaDataRepository;
import be.ehb.koalaexpress.models.Customer;
import be.ehb.koalaexpress.models.CustomerList;
import be.ehb.koalaexpress.models.KoalaRoomDB;
import be.ehb.koalaexpress.models.Location;
import be.ehb.koalaexpress.models.LocationList;
import be.ehb.koalaexpress.models.LocationUren;
import be.ehb.koalaexpress.models.Product;
import be.ehb.koalaexpress.models.ProductCategory;
import be.ehb.koalaexpress.models.ProductCategoryList;
import be.ehb.koalaexpress.models.ProductList;

public class Task_LoadRepoFromDB extends AsyncTask<KoalaDataRepository, Object, Object> {

    public KoalaDataRepository repo;
    public Context context;

    ProductCategoryList productCategoryList;
    ProductList productList;
    LocationList locationList;
    CustomerList customerList;

    public Task_LoadRepoFromDB(Context c) {
        context = c;
    }

    @Override
    protected Object doInBackground(KoalaDataRepository... koalaDataRepositories) {
        repo = koalaDataRepositories[0];

        KoalaRoomDB db = KoalaRoomDB.getInstance(context);

        //Eerst alles inlezen
        productCategoryList = new ProductCategoryList();
        productCategoryList.mList = (ArrayList<ProductCategory>) db.categoryDao().getAllProductCategories();

        productList = new ProductList();
        productList.mList = (ArrayList<Product>) db.productDao().getAllProducts();

        locationList = new LocationList();
        locationList.mList = (ArrayList<Location>) db.locationDao().getAllLocations();

        for (Location l :locationList.mList) {
            l.mListOpeningHours = (ArrayList<LocationUren>) db.locationUrenDao().GetAllUrenForLocation(l.mLocationId);
        }

        customerList = new CustomerList();
        customerList.mList = (ArrayList<Customer>) db.customerDao().getAllCustomers();

        return null;
    }

    @Override
    protected void onPostExecute(Object o){
        super.onPostExecute(o);
        repo.koalaCategorieen.setValue(productCategoryList);
        repo.koalaProducten.setValue(productList);
        repo.koalaLocations = locationList;
        repo.koalaCustomers = customerList;
    }
}
