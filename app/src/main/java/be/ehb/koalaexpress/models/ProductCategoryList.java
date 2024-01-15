package be.ehb.koalaexpress.models;

import java.util.ArrayList;

public class ProductCategoryList {
    public ArrayList<ProductCategory> mList;

    public ProductCategoryList(ArrayList<ProductCategory> mList) {
        this.mList = mList;
    }

    public ProductCategoryList() {
        mList = new ArrayList<ProductCategory>();
    }
}
