package be.ehb.koalaexpress.ui.Dialogs;

import android.graphics.Color;
import android.icu.text.IDNA;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;

import be.ehb.koalaexpress.KoalaDataRepository;
import be.ehb.koalaexpress.R;

public class InfoDialog extends DialogFragment {

    public TextView tvInfoText;
    public Button btnOK;
    public CardView cvBackgroundCardView;


    public String mInfoText;
    public int mBackground;
    public int  mTextColor;
    public InfoDialog(String InfoText, int background, int textcolor) {
        mBackground = background;
        mTextColor = textcolor;
        mInfoText = InfoText;
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
        View view = inflater.inflate(R.layout.dailog_info_text, container, false);
        tvInfoText = view.findViewById(R.id.dlg_info_text);
        cvBackgroundCardView= view.findViewById(R.id.dlg_infodlg_cardview);
        btnOK = view.findViewById(R.id.btn_dlg_ok);

        tvInfoText.setText(mInfoText);


        int backcolor= getResources().getColor(mBackground);
        int color= getResources().getColor(mTextColor);
        cvBackgroundCardView.setCardBackgroundColor(backcolor);
        tvInfoText.setTextColor(color);
        tvInfoText.setBackgroundColor(backcolor);

        btnOK.setOnClickListener(v -> {
            dismiss();
        });

        return view;
    }

}