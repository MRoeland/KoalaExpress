package be.ehb.koalaexpress.ui.Winkel;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import be.ehb.koalaexpress.KoalaDataRepository;
import be.ehb.koalaexpress.R;
import be.ehb.koalaexpress.models.ProductCategoryList;
import be.ehb.koalaexpress.models.ProductList;
import be.ehb.koalaexpress.models.WinkelMandje;

public class WinkelFragment extends Fragment {

    private WinkelViewModel mViewModel;
    private WinkelCategoriesAdapter mCategoryAdapter;
    private WinkelItemsAdapter mWinkelItemsAdapter;

    public SearchView mSearchView;
    public ConstraintLayout mLayoutFragmentWinkel;

    private KoalaDataRepository mRepo;

    public WinkelFragment() {
        mRepo = KoalaDataRepository.getInstance();
    }

    public static WinkelFragment newInstance() {
        return new WinkelFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_winkel, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mLayoutFragmentWinkel = view.findViewById(R.id.fragment_winkel);
        mSearchView = view.findViewById(R.id.sv_FilterTextBox);
        RecyclerView mWinkelCategories = view.findViewById(R.id.rv_WinkelCategories);

        mViewModel = new ViewModelProvider(getActivity()).get(WinkelViewModel.class);
        mCategoryAdapter = new WinkelCategoriesAdapter(mViewModel);
        RecyclerView.LayoutManager mLayoutManagerCategories = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);

        mWinkelCategories.setAdapter(mCategoryAdapter);
        mWinkelCategories.setLayoutManager(mLayoutManagerCategories);

        RecyclerView mWinkelItems = view.findViewById(R.id.rv_WinkelItems);

        mWinkelItemsAdapter = new WinkelItemsAdapter(mViewModel.mCurrentItemsList.getValue(), this);
        RecyclerView.LayoutManager mLayoutManagerItems = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        mWinkelItems.setAdapter(mWinkelItemsAdapter);
        mWinkelItems.setLayoutManager(mLayoutManagerItems);

        if(mViewModel.mSelectedCategory == null) {
            // select first automatically
            mViewModel.mSelectedCategory = mRepo.koalaCategorieen.getValue().mList.get(0);
            mViewModel.FilterOnCurrentCategory(mViewModel.mSelectedCategory);

        }
        mSearchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getContext(),"onclick", Toast.LENGTH_SHORT).show();
                // attach recycler to bottom of searchview

                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(mLayoutFragmentWinkel);
                constraintSet.connect(R.id.rv_WinkelItems, ConstraintSet.TOP, R.id.sv_FilterTextBox, ConstraintSet.BOTTOM);
                constraintSet.applyTo(mLayoutFragmentWinkel);

            }
        });
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                // gebeurd als in searchview op kruisje close klikt terwijl aan het zoeken
                // = afsluiten filteren, hier opnieuw alle items invullen
                //Toast.makeText(getContext(),"onclose", Toast.LENGTH_SHORT).show();
                mWinkelItemsAdapter.setDisplayList(mViewModel.mCurrentItemsList.getValue());
                mWinkelItemsAdapter.notifyDataSetChanged();
                //attach to bottom of rvcategories recycler
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(mLayoutFragmentWinkel);
                constraintSet.connect(R.id.rv_WinkelItems, ConstraintSet.TOP, R.id.rv_WinkelCategories, ConstraintSet.BOTTOM);
                constraintSet.applyTo(mLayoutFragmentWinkel);
                return false;
            }
        });
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            //Wordt niet gebruikt maar moet erbij omdat de functie verplicht is
            @Override
            public boolean onQueryTextSubmit(String query) {
                //enter duwen
                //Toast.makeText(getContext(),"onsubmit", Toast.LENGTH_SHORT).show();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                // gebeurd bij elk nieuw character ingeven
                mViewModel.FilterOnProduct(newText);

                mWinkelItemsAdapter.setDisplayList(mViewModel.mFilteredList);
                mWinkelItemsAdapter.notifyDataSetChanged();

                return true;
            }
        });
        SetObservers();
    }

    private void SetObservers() {
        KoalaDataRepository.getInstance().koalaCategorieen.observe(getViewLifecycleOwner(), new Observer<ProductCategoryList>() {
            @Override
            public void onChanged(ProductCategoryList productCategoryList) {
                mViewModel.mCurrentCategoryList = productCategoryList;
                mCategoryAdapter.setDisplayList(productCategoryList);
                mCategoryAdapter.notifyDataSetChanged();
            }
        });

        mViewModel.mCurrentItemsList.observe(getViewLifecycleOwner(), new Observer<ProductList>() {
            @Override
            public void onChanged(ProductList productList) {
                mWinkelItemsAdapter.setDisplayList(productList);
                mWinkelItemsAdapter.notifyDataSetChanged();
            }
        });

        // observe winkelmandje wijzigingen om aanpassingen in aantallen van een product te krijgen en items te updaten
        mRepo.mWinkelMandje.observe(getViewLifecycleOwner(), new Observer<WinkelMandje>() {
            @Override
            public void onChanged(WinkelMandje mandje) {
                mWinkelItemsAdapter.notifyDataSetChanged();
            }
        });
    }
}