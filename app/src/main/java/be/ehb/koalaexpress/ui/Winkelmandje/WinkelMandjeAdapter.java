package be.ehb.koalaexpress.ui.Winkelmandje;

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
import be.ehb.koalaexpress.ui.Winkel.WinkelItemsAdapter;

public class WinkelMandjeAdapter extends RecyclerView.Adapter<WinkelMandjeAdapter.ItemViewHolder>{
    class ItemViewHolder extends RecyclerView.ViewHolder{
        public TextView tvProductName;
        public Button btnAdd;
        public Button btnRemove;
        public TextView etProductAmount;
        public ImageView imgItemImage;
        public TextView tvPrice;


        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            tvProductName = itemView.findViewById(R.id.tvNameProduct);
            btnAdd = itemView.findViewById(R.id.btnAdd1);
            btnRemove = itemView.findViewById(R.id.btnRemove1);
            etProductAmount = itemView.findViewById(R.id.etAmount);
            imgItemImage = itemView.findViewById(R.id.imageItem);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }
    }

    public WinkelMandje hetWinkelMandje;
    public KoalaDataRepository repo;

    public void SetWinkelMandje(WinkelMandje mandje) {
        hetWinkelMandje = mandje;
    }
    public WinkelMandjeAdapter(WinkelMandje mandje) {
        hetWinkelMandje = mandje;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vNieuweCardItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item_winkel_mandje, parent, false);
        WinkelMandjeAdapter.ItemViewHolder mItemViewHolder = new WinkelMandjeAdapter.ItemViewHolder(vNieuweCardItem);
        repo = KoalaDataRepository.getInstance();
        return mItemViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        OrderLine o = hetWinkelMandje.mOrderLines.get(position);
        Product p = o.getProduct(repo);
        holder.tvProductName.setText(p.mName);

        String price = Float.toString(p.mPrice);
        holder.etProductAmount.setText(String.format("%d", o.mQuantity));
        holder.tvPrice.setText("€ " + price);
        if(p.mImage != ""){
            Task_ImageLoad loader = new Task_ImageLoad(holder.imgItemImage);
            loader.execute(p.mImage);
        }

        holder.btnAdd.setOnClickListener(v -> {
            hetWinkelMandje.AddProduct(p, 1);
            repo.mWinkelMandje.postValue(hetWinkelMandje);
        });

        holder.btnRemove.setOnClickListener(v -> {
            hetWinkelMandje.AddProduct(p, -1);
            repo.mWinkelMandje.postValue(hetWinkelMandje);
        });
    }

    @Override
    public int getItemCount() {
        if (hetWinkelMandje == null){
            return 0;
        }

        return hetWinkelMandje.mOrderLines.size();
    }

}
