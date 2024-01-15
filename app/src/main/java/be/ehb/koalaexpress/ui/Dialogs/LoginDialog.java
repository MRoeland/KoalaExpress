package be.ehb.koalaexpress.ui.Dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import be.ehb.koalaexpress.KoalaDataRepository;
import be.ehb.koalaexpress.R;
import be.ehb.koalaexpress.models.Customer;

public class LoginDialog extends DialogFragment {

    public EditText etLoginName;
    public EditText etLoginPassword;
    public Button btnLogin;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_login_user, container, false);

        etLoginName = view.findViewById(R.id.etLoginName);
        etLoginPassword = view.findViewById(R.id.etLoginPassword);
        btnLogin = view.findViewById(R.id.btnLoginDialog);

        btnLogin.setOnClickListener(v -> {
            KoalaDataRepository.getInstance().TryLoginUser(etLoginName.getText().toString(), etLoginPassword.getText().toString());

            dismiss();
        });

        return view;
    }

}
