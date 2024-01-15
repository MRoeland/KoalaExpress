package be.ehb.koalaexpress.ui.Winkel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import be.ehb.koalaexpress.KoalaDataRepository;
import be.ehb.koalaexpress.R;
import be.ehb.koalaexpress.Tasks.Task_ImageLoad;
import be.ehb.koalaexpress.models.OrderLine;
import be.ehb.koalaexpress.models.Product;
import be.ehb.koalaexpress.models.ProductList;
import be.ehb.koalaexpress.models.WinkelMandje;
import be.ehb.koalaexpress.ui.Dialogs.ProductDetailDialog;

public class WinkelItemsAdapter extends RecyclerView.Adapter<WinkelItemsAdapter.ItemViewHolder>{

    class ItemViewHolder extends RecyclerView.ViewHolder{
        final TextView tvItemName;
        final TextView tvItemDescription;
        final TextView tvItemPrice;
        final ImageView imgItemImage;
        final Button btnItemAdd;
        final TextView etAmount;


        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            tvItemName = itemView.findViewById(R.id.txt_nameItem);
            tvItemDescription = itemView.findViewById(R.id.txt_itemDescription);
            tvItemPrice = itemView.findViewById(R.id.txtPrice);
            imgItemImage = itemView.findViewById(R.id.imageItem);
            btnItemAdd = itemView.findViewById(R.id.btn_addToBasket);
            etAmount = itemView.findViewById(R.id.etAmount);
            etAmount.setVisibility(View.INVISIBLE);
        }
    }

    private ProductList displayList;
    private WinkelFragment mInFragment;

    private KoalaDataRepository repo;

    public void setDisplayList(ProductList displayList) {
        this.displayList = displayList;
    }

    public WinkelItemsAdapter(ProductList displayList, WinkelFragment frag) {
        displayList = displayList;
        mInFragment = frag;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vNieuweCardItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_winkel_item, parent, false);
        ItemViewHolder mItemViewHolder = new ItemViewHolder(vNieuweCardItem);
        mItemViewHolder.etAmount.setText("1");
        mItemViewHolder.imgItemImage.setImageResource(R.mipmap.ic_no_image);
        repo = KoalaDataRepository.getInstance();
        return mItemViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Product p = displayList.mList.get(position);
        holder.tvItemName.setText(p.mName);
        holder.tvItemDescription.setText(p.mDescription);
        String price = Float.toString(p.mPrice);
        holder.tvItemPrice.setText("€ " + price);
        String value = "Kaas en Hesp";

        if(p.mImage != ""){
            Task_ImageLoad loader = new Task_ImageLoad(holder.imgItemImage);
            loader.execute(p.mImage);
        }
        else
            holder.imgItemImage.setImageResource(R.mipmap.ic_no_image);

        holder.btnItemAdd.setOnClickListener(v -> {
            WinkelMandje currentMandje = repo.mWinkelMandje.getValue();
            currentMandje.AddProduct(p, 1);
            repo.mWinkelMandje.postValue(currentMandje);
        });
        holder.imgItemImage.setOnClickListener(v -> {
            ProductDetailDialog dlg = new ProductDetailDialog(p);
            dlg.show(mInFragment.getChildFragmentManager(),"ProductDetailDialog");
        });
        // laat het aantal in winkelmandje zien indien product gevonden in winkelmandje
        WinkelMandje mandje = repo.mWinkelMandje.getValue();
        OrderLine lijnInMandje = mandje.getOrderlijnMetProduct(p);
        if(lijnInMandje !=null) {
            holder.etAmount.setText(String.valueOf(lijnInMandje.mQuantity));
            holder.etAmount.setVisibility(View.VISIBLE);
        }
        else {
            holder.etAmount.setText("");
            holder.etAmount.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        if (displayList == null){

            return 0;
        }

        return displayList.mList.size();
    }
}
