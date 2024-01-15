package be.ehb.koalaexpress.models;

import java.util.ArrayList;

public class ProductList
{
    public ArrayList<Product> mList;

    public ProductList() {

        mList = new ArrayList<Product>();
    }
    public Product findProductById(int CategoryId, int ProductId) {

        for(Product d : mList) {
            if (d.mCategoryId == CategoryId && d.mProductId == ProductId)
                return d;
        }
        return null;
    }
}
