package be.ehb.koalaexpress.ui.Dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import be.ehb.koalaexpress.KoalaDataRepository;
import be.ehb.koalaexpress.R;
import be.ehb.koalaexpress.Tasks.Task_ImageLoad;
import be.ehb.koalaexpress.models.Product;

public class ProductDetailDialog extends DialogFragment {
    public TextView tvNameProduct;
    public TextView tvDescription;
    public TextView tvDetail;
    public Button btnOK;
    public ImageView imgProductPicture;
    public Product mProduct;

    public ProductDetailDialog(Product selectedProduct) {
        mProduct = selectedProduct;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // zie styles.xml, om frame doorzichtig te maken
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.TransparentDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_detail_winkelitem, container, false);

        tvNameProduct = view.findViewById(R.id.txt_nameItem);
        tvDescription = view.findViewById(R.id.txt_itemDescription);
        tvDetail = view.findViewById(R.id.txt_itemDetail);
        imgProductPicture = view.findViewById(R.id.imageItem);
        btnOK  = view.findViewById(R.id.btn_detaildlg_ok);
        btnOK.setOnClickListener(v -> {
            dismiss();
        });
        if(mProduct != null){
            tvNameProduct.setText(mProduct.mName);
            tvDescription.setText(mProduct.mDescription);
            tvDetail.setText(mProduct.mDetailedDescription);
            if(mProduct.mImage != ""){
                Task_ImageLoad loader = new Task_ImageLoad(imgProductPicture);
                loader.execute(mProduct.mImage);
            }
        }
        return view;
    }
}
