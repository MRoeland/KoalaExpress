package be.ehb.koalaexpress.ui.Winkel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Locale;

import be.ehb.koalaexpress.KoalaDataRepository;
import be.ehb.koalaexpress.models.Product;
import be.ehb.koalaexpress.models.ProductCategory;
import be.ehb.koalaexpress.models.ProductCategoryList;
import be.ehb.koalaexpress.models.ProductList;

public class WinkelViewModel extends ViewModel {

    public static WinkelViewModel mViewModel;
    public ProductCategory mCurrentCategory;
    public ProductCategoryList mCurrentCategoryList;
    public MutableLiveData<ProductList> mCurrentItemsList;
    public ProductCategory mSelectedCategory;
    public ProductList mFilteredList;


    public WinkelViewModel() {
        mViewModel = null;
        mCurrentCategory = null;
        mCurrentCategoryList = null;
        mCurrentItemsList = new MutableLiveData<>();
        mCurrentItemsList.setValue(null);
        mSelectedCategory = null;
        mFilteredList = new ProductList();
    }

    public void FilterOnCurrentCategory(ProductCategory newCurrent){
        if(newCurrent != mCurrentCategory){
            mCurrentCategory = newCurrent;

            ProductList itemList = mCurrentItemsList.getValue();

            if(itemList == null){
                itemList = new ProductList();
            }

            if(mCurrentCategory == null){
                itemList.mList.clear();
            }
            else{
                itemList.mList.clear();

                for (Product p : KoalaDataRepository.getInstance().koalaProducten.getValue().mList) {
                    if (p.mCategoryId == mCurrentCategory.mCategoryId){
                        itemList.mList.add(p);
                    }
                }
            }

            mCurrentItemsList.setValue(itemList);
        }
    }

    public void FilterOnProduct(String searchString){
        mFilteredList.mList.clear();
        searchString = searchString.toLowerCase();
        if(searchString.equals("")){
          mFilteredList.mList.addAll(mCurrentItemsList.getValue().mList);
        }
        else{
            for (Product p : mCurrentItemsList.getValue().mList) {
                if(p.mName.toLowerCase().contains(searchString) || p.mDescription.toLowerCase().contains(searchString)){
                    mFilteredList.mList.add(p);
                }
            }
        }
    }
}