package be.ehb.koalaexpress.ui.Winkel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import be.ehb.koalaexpress.R;
import be.ehb.koalaexpress.models.ProductCategory;
import be.ehb.koalaexpress.models.ProductCategoryList;

public class WinkelCategoriesAdapter extends RecyclerView.Adapter<WinkelCategoriesAdapter.CategoryViewHolder>{

    class CategoryViewHolder extends RecyclerView.ViewHolder{

        final TextView tvCategoryName;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCategoryName = itemView.findViewById(R.id.tv_category_name);
        }
    }

    private ProductCategoryList displayList;
    private WinkelViewModel mWinkelViewModel;

    public void setDisplayList(ProductCategoryList displayList) {
        this.displayList = displayList;
    }

    public WinkelCategoriesAdapter(WinkelViewModel winkelViewModel) {
        displayList = winkelViewModel.mCurrentCategoryList;
        mWinkelViewModel = winkelViewModel;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vNieuweCardCategorie = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_category, parent, false);
        CategoryViewHolder mCategoryViewHolder = new CategoryViewHolder(vNieuweCardCategorie);
        vNieuweCardCategorie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int positieInAdapter = mCategoryViewHolder.getAdapterPosition();
                mWinkelViewModel.mSelectedCategory = displayList.mList.get(positieInAdapter);
                mWinkelViewModel.FilterOnCurrentCategory(mWinkelViewModel.mSelectedCategory);
            }
        });
        return mCategoryViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        ProductCategory c = displayList.mList.get(position);
        holder.tvCategoryName.setText(c.mName);
    }

    @Override
    public int getItemCount() {
        if (displayList == null){
            return 0;
        }
        return displayList.mList.size();
    }


}
