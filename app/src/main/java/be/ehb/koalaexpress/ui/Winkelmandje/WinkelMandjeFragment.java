package be.ehb.koalaexpress.ui.Winkelmandje;

import static androidx.fragment.app.FragmentManager.TAG;

import android.app.Dialog;
import android.content.Intent;
import android.icu.text.IDNA;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Base64;

import be.ehb.koalaexpress.CheckoutActivity;
import be.ehb.koalaexpress.KoalaDataRepository;
import be.ehb.koalaexpress.MainActivity;
import be.ehb.koalaexpress.PayPalConfig;
import be.ehb.koalaexpress.R;
import be.ehb.koalaexpress.Tasks.Task_SendOrderToDB;
import be.ehb.koalaexpress.models.Customer;
import be.ehb.koalaexpress.models.Location;
import be.ehb.koalaexpress.models.OrderLine;
import be.ehb.koalaexpress.models.WinkelMandje;
import be.ehb.koalaexpress.ui.Dialogs.InfoDialog;
import be.ehb.koalaexpress.ui.Dialogs.KiesVestigingDialog;
import be.ehb.koalaexpress.ui.Dialogs.LoginDialog;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.StringEntity;

public class WinkelMandjeFragment extends Fragment {

    public WinkelMandjeViewModel mViewModel;
    public WinkelMandjeAdapter mWinkelMandjeAdapter;
    public KoalaDataRepository repo;
    public TextView tvTotalPrice;
    public TextView tvTotalKorting;
    public Button btnLogin;
    public Button btnKiesVestiging;
    public Button btnCheckout;
    public Button btnKortingClear;
    public RelativeLayout relCustomerData;
    public RecyclerView rvBasketItems;
    public TextView tvNogNietsBesteld;
    public EditText etUserName;
    public EditText etUserEmail;
    public EditText etUserGsm;
    public EditText etCouponText;
    public TextView tvtvVestigingTitel;
    public TextView tvBezorgenInfo;
    public TextView tvLeveringsAdres;
    public TextView tvVerwachttijdstip;
    public TextView tvAfhalenInfo;
    public TextView tvVestigingNaam;
    public TextView tvVestigingAdres;

    public RelativeLayout layoutBezorgen;
    public RelativeLayout layoutAfhalen;

    public String tag = "WinkelMandjeFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_winkel_mandje, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvTotalPrice = view.findViewById(R.id.tvTotalPrice);
        btnLogin = view.findViewById(R.id.btnLogin);
        relCustomerData = view.findViewById(R.id.userData);
        etUserName = view.findViewById(R.id.etUserName);
        etUserEmail = view.findViewById(R.id.etUserEmail);
        etUserGsm = view.findViewById(R.id.etUserGsm);
        btnCheckout = view.findViewById(R.id.btnAfrekenen);
        btnKiesVestiging = view.findViewById(R.id.btnKiesVestiging);
        rvBasketItems = view.findViewById(R.id.rvBasketItems);
        tvNogNietsBesteld= view.findViewById(R.id.tv_NogNietsBesteld);
        etCouponText =  view.findViewById(R.id.etCouponText);
        tvTotalKorting = view.findViewById(R.id.tvTotalKorting);
        btnKortingClear= view.findViewById(R.id.btnKortingClear);
        layoutBezorgen = view.findViewById(R.id.layoutLevering);
        layoutAfhalen = view.findViewById(R.id.layoutAfhalen);
        tvtvVestigingTitel = view.findViewById(R.id.tvVestigingTitel);
        tvBezorgenInfo=  view.findViewById(R.id.tvBezorgenInfo);
        tvVerwachttijdstip=  view.findViewById(R.id.tvVerwachttijdstip);
        tvLeveringsAdres=  view.findViewById(R.id.tvLeveringsAdres);
        tvVestigingNaam=  view.findViewById(R.id.tvVestigingNaam);
        tvAfhalenInfo=  view.findViewById(R.id.tvAfhalenInfo);
        tvVestigingAdres=  view.findViewById(R.id.tvVestigingAdres);

        repo = KoalaDataRepository.getInstance();
        if(repo.mWinkelMandje.getValue().mReductionCode.equals("")==false) {
            btnKortingClear.setVisibility(View.VISIBLE);
            tvTotalKorting.setVisibility(View.VISIBLE);
            etCouponText.setVisibility(View.INVISIBLE);
        }
        else {
            btnKortingClear.setVisibility(View.INVISIBLE);
            tvTotalKorting.setVisibility(View.INVISIBLE);
            etCouponText.setVisibility(View.VISIBLE);
        }

        btnLogin.setOnClickListener(v -> {
            LoginDialog dialog = new LoginDialog();
            dialog.show(getChildFragmentManager(), "Login Dialog");
        });
        btnKiesVestiging.setOnClickListener(v -> {
            KiesVestigingDialog dlg = new KiesVestigingDialog(repo);
            dlg.show(getFragmentManager(), "KiestVestigingDialog");
        });

        btnCheckout.setOnClickListener(v -> {
            WinkelMandje mandje = repo.mWinkelMandje.getValue();
            if(mandje.canCheckout() == false){
                InfoDialog dlg = new InfoDialog("Winkelmandje bevat nog geen items. \n\nWe helpen je graag verder maar kunnen pas aan de slag als je ons verteld wat je wilt bestellen. \n\nGelieve eerst producten te kiezen in onze productenlijst.", R.color.KoalaRedFailure, R.color.white);
                dlg.show(getChildFragmentManager(),"InfoDialog");
                return;
            }
            if(repo.mCustomer.getValue() == null){
                InfoDialog dlg = new InfoDialog("Gelieve eerst in te loggen. \n\nWe hebben Uw klantgegevens nodig om het order te kunnen doorgeven aan de vestiging.", R.color.KoalaRedFailure, R.color.white);
                dlg.show(getChildFragmentManager(),"InfoDialog");
                return;
            }
            if(repo.mVestiging.getValue() == null){
                InfoDialog dlg = new InfoDialog("Je hebt geen vestiging gekozen.\n\nGeef aan welke vestiging jouw order mag afhandelen.", R.color.KoalaRedFailure, R.color.white);
                dlg.show(getChildFragmentManager(),"InfoDialog");
                return;
            }
            if(repo.mServerIsOnline == false) {
                InfoDialog dlg = new InfoDialog("Je bent momenteel niet online. \n\nGeen zorgen, we slaan je bestelling lokaal op zodat je ze door kan sturen zodra je online bent.", R.color.KoalaRedFailure, R.color.white);
                dlg.show(getChildFragmentManager(),"InfoDialog");
                return;
            }
            //laatste gegevens uit edit in mandje copieren
            mandje.mContactName = etUserName.getText().toString();
            mandje.mContactEmail = etUserEmail.getText().toString();
            mandje.mContactPhone = etUserGsm.getText().toString();

            CreatePaypalOrder();
        });

        btnKortingClear.setOnClickListener(v -> {
            WinkelMandje m = repo.mWinkelMandje.getValue();
            m.mReductionCode = "";
            repo.mWinkelMandje.setValue(m);
        });
        etCouponText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                WinkelMandje m = repo.mWinkelMandje.getValue();
                Boolean gelukt = m.toepassenKorting(s.toString());
                repo.mWinkelMandje.setValue(m);
                if(gelukt == true) {
                    InfoDialog dlg = new InfoDialog("Kortingscode ("+ s.toString() + ") is toegepast !", R.color.KoalaDarkGreen, R.color.white);
                    dlg.show(getChildFragmentManager(),"InfoDlg");
                }
            }
        });


        RecyclerView mBasketItems = view.findViewById(R.id.rvBasketItems);

        mViewModel = new ViewModelProvider(getActivity()).get(WinkelMandjeViewModel.class);

        mWinkelMandjeAdapter = new WinkelMandjeAdapter(repo.mWinkelMandje.getValue());
        RecyclerView.LayoutManager mLayoutManagerBasket = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);

        mBasketItems.setAdapter(mWinkelMandjeAdapter);
        mBasketItems.setLayoutManager(mLayoutManagerBasket);

        SetObservers();

        if(repo.PaypalAccessToken == "" && repo.mServerIsOnline == true)
            GetPaypalAccessToken();
    }

    private void SetObservers(){
        repo.mWinkelMandje.observe(getViewLifecycleOwner(), new Observer<WinkelMandje>() {
            @Override
            public void onChanged(WinkelMandje mandje) {
                mWinkelMandjeAdapter.SetWinkelMandje(mandje);
                mWinkelMandjeAdapter.notifyDataSetChanged();
                mandje.CalculateTotalPrice();
                etUserName.setText(mandje.mContactName);
                etUserEmail.setText(mandje.mContactEmail);
                etUserGsm.setText(mandje.mContactPhone);
                tvTotalKorting.setText("€ " + String.format("%.02f", mandje.mReductionPrice));
                tvTotalPrice.setText("€ " + String.format("%.02f", mandje.mTotalPrice));
                if (mandje.mOrderLines.size() > 0) {
                    rvBasketItems.setVisibility(View.VISIBLE);
                    tvNogNietsBesteld.setVisibility(View.INVISIBLE);
                }
                else {
                    rvBasketItems.setVisibility(View.INVISIBLE);
                    tvNogNietsBesteld.setVisibility(View.VISIBLE);
                }
                if(mandje.mReductionCode.equals("") == false) {
                    btnKortingClear.setVisibility(View.VISIBLE);
                    tvTotalKorting.setVisibility(View.VISIBLE);
                    etCouponText.setVisibility(View.INVISIBLE);
                }
                else {
                    btnKortingClear.setVisibility(View.INVISIBLE);
                    tvTotalKorting.setVisibility(View.INVISIBLE);
                    etCouponText.setVisibility(View.VISIBLE);
                }
                updateLocationGekozen();
            }
        });

        repo.mCustomer.observe(getViewLifecycleOwner(), new Observer<Customer>() {
            @Override
            public void onChanged(Customer customer) {
                if(customer != null){
                    btnLogin.setVisibility(View.GONE);
                    relCustomerData.setVisibility(View.VISIBLE);

                    WinkelMandje currentMandje = repo.mWinkelMandje.getValue();
                    currentMandje.SetCustomer(customer);
                    repo.mWinkelMandje.postValue(currentMandje);
                }
                else{
                    btnLogin.setVisibility(View.VISIBLE);
                    relCustomerData.setVisibility(View.GONE);
                }
            }
        });

        repo.repoInfoMessage.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                // gebruikt om resultaat van login customer weer te geven (bij fout)
                if(!s.equals("")){
                    InfoDialog infoDialog = new InfoDialog(s, R.color.KoalaRedFailure, R.color.white);
                    infoDialog.show(getChildFragmentManager(), "InfoDialog");
                    repo.repoInfoMessage.postValue("");

                }
            }
        });
        repo.mVestiging.observe(getViewLifecycleOwner(), new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                //vult of wist locatie velden in card
                updateLocationGekozen();
            }
        });
    }

    public static String GetPayPalClientID(){
        //String input = "<CLIENT_ID>:<CLIENT_SECRET//Password>";
        String input = PayPalConfig.PAYPAL_CLIENT_ID + ":" + PayPalConfig.PAYPAL_SECRET_KEY1;
        String encodedString = Base64.getEncoder().encodeToString(input.getBytes());
        return encodedString;
    }

    public void GetPaypalAccessToken(){
        // een accesstoken is een soort van sessie
        String AUTH = GetPayPalClientID();
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Accept", "application/json");
        client.addHeader("Content-type", "application/x-www-form-urlencoded");
        client.addHeader("Authorization", "Basic "+ AUTH);
        String jsonString = "grant_type=client_credentials";
        HttpEntity entity = new StringEntity(jsonString, "utf-8");
        client.post(getContext(), "https://api-m.sandbox.paypal.com/v1/oauth2/token", entity, "application/x-www-form-urlencoded",new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                Log.e("RESPONSE", response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) {
                try {
                    JSONObject jobj = new JSONObject(response);
                    KoalaDataRepository.getInstance().PaypalAccessToken = jobj.getString("access_token");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private JSONArray createPayPalOrderItemsArray() throws JSONException {
        // toevoegen van winkel items uit mandje in de request order van paypal
        JSONArray itemsArray = new JSONArray();
        for (OrderLine orderlijn : repo.mWinkelMandje.getValue().mOrderLines) {
            JSONObject itemObject = new JSONObject()
                    .put("name", orderlijn.getProduct(repo).mName)
                    .put("unit_amount", new JSONObject()
                            .put("currency_code", "EUR")
                            .put("value", String.format("%.02f", orderlijn.mUnitPrice))
                    )
                    .put("quantity", orderlijn.mQuantity);
            itemsArray.put(itemObject);
            Log.d("Tag", String.format("%.02f", orderlijn.mUnitPrice));
        }
        return itemsArray;
    }
    private JSONObject getPurchaseUnitsBlock(WinkelMandje mandje) throws JSONException {
        String paymentAmount = String.format("%.02f",mandje.mTotalPrice);
        String item_total = String.format("%.02f",mandje.getItemTotal());
        Log.d("Tag", paymentAmount);
        DecimalFormat df = new DecimalFormat("0.00");
        JSONObject json = new JSONObject();
        json.put("amount", new JSONObject()
                    .put("currency_code", "EUR")
                    .put("value", paymentAmount)
                    .put("breakdown", new JSONObject()
                            .put("item_total", new JSONObject()
                                    .put("currency_code", "EUR")
                                    .put("value", item_total)
                            )
                            .put("discount", new JSONObject() // Add discount section
                                    .put("currency_code", "EUR")
                                    .put("value", df.format(mandje.mReductionPrice)) // round 2 dig
                            )
                    )
            );
        if(mandje.mPickUpInStore == false) {
            // alleen shipping toevoegen als "bezorgen" gekozen is
            json.put("shipping", new JSONObject()
                    .put("name", new JSONObject()
                            .put("full_name", mandje.mContactName)
                    )
                    .put("address", new JSONObject()
                            .put("address_line_1", mandje.mDelAdressline1)
                            .put("admin_area_2", mandje.mDelCity)
                            .put("admin_area_1", mandje.mDelProvince)
                            .put("postal_code", mandje.mDelPostalCode)
                            .put("country_code", mandje.mDelCountryCode)
                    )
            );
        }
        json.put("items", createPayPalOrderItemsArray());
        return json;
    }
    private void CreatePaypalOrder() {

        //https://lo-victoria.com/the-complete-guide-to-integrate-paypal-in-mobile-apps
        //Stappenplan!

        WinkelMandje mandje = repo.mWinkelMandje.getValue();
        String order = "";

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Accept", "application/json");
        client.addHeader("Content-type", "application/json");
        client.addHeader("Authorization", "Bearer " + repo.getInstance().PaypalAccessToken);

        try {
            // build using https://developer.paypal.com/docs/api/orders/v2/#definition-purchase_unit    rest api doc
            JSONObject requestObject = new JSONObject()
                    .put("intent", "CAPTURE")
                    .put("purchase_units", new JSONArray()
                            .put(getPurchaseUnitsBlock(mandje)
                            )
                    )
                    .put("payee", new JSONObject()
                            .put("email_address","mroeland03@gmail.com")
                            .put("merchant_id","KoalaExpress & Co")
                    )
                    .put("application_context", new JSONObject()
                            .put("brand_name","KoalaExpress & Co")
                            .put("return_url","koalaexpress://www.koalaexpress.com/succes")
                            .put("cancel_url","koalaexpress://www.koalaexpress.com/failed")
                    );
            order = requestObject.toString();
            Log.d("Tag", order);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(tag, order);
        HttpEntity entity = new StringEntity(order, "utf-8");

        String urlNaarPaypal = "https://api-m.sandbox.paypal.com/"; //of "https://api.sandbox.paypal.com/"
        client.post(this.getActivity(), urlNaarPaypal+"/v2/checkout/orders", entity, "application/json",new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                Log.e("RESPONSE", response);
                Toast.makeText( getContext(), response,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) {
                try {
                    JSONArray links = new JSONObject(response).getJSONArray("links");

                    //iterate the array to get the approval link
                    for (int i = 0; i < links.length(); ++i) {
                        JSONObject jsonobj = links.getJSONObject(i);
                        String rel = jsonobj.getString("rel");
                        String hreflink = jsonobj.getString("href"); //de href is de link die je moet oproepen om die order te bevestigen


                        if (rel.equals("approve")){
                            String link = hreflink;
                            //redirect in a web browser
                            /*Intent in = new Intent(Intent.ACTION_VIEW);
                            in.setData(Uri.parse(link));
                            startActivity(in);*/
                            //redirect to this link via CCT
                            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                            CustomTabsIntent customTabsIntent = builder.build();
                            customTabsIntent.launchUrl(getActivity(), Uri.parse(link));

                            //Ga naar de HREF link gegeven door Paypal in de response met rel = approve
                            //Zie voorbeeld stap 5 in stappenplan
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
    }
    public void updateLocationGekozen() {
        WinkelMandje m = repo.mWinkelMandje.getValue();
        Location vestiging = repo.mVestiging.getValue();
        if(vestiging == null) {
            layoutBezorgen.setVisibility(View.GONE);
            layoutAfhalen.setVisibility(View.GONE);
            tvtvVestigingTitel.setText("Nog niet gekozen");
        }
        else {
            if(m.mPickUpInStore == true) {
                //AFHALEN
                layoutBezorgen.setVisibility(View.GONE);
                layoutAfhalen.setVisibility(View.VISIBLE);
                tvtvVestigingTitel.setText("Gekozen voor Afhalen");
                tvAfhalenInfo.setText("U kan de bestelling afhalen vanaf  " + m.mPickupTimeFrom);
                tvVestigingAdres.setText(vestiging.getFullAddress());
                tvVestigingNaam.setText(vestiging.mName);

            } else {
                //bezorgen
                layoutBezorgen.setVisibility(View.VISIBLE);
                layoutAfhalen.setVisibility(View.GONE);
                tvtvVestigingTitel.setText("Gekozen voor Bezorgen");
                tvBezorgenInfo.setText("Uitgevoerd door "+ vestiging.mName);
                if(repo.mCustomer.getValue()!=null)
                    tvLeveringsAdres.setText(m.mDelAdressline1 + " " + m.mDelAdressline2 + ", " + m.mDelPostalCode + " " + m.mDelCity);
                else
                    tvLeveringsAdres.setText("Log in voor leveradres.");
                tvVerwachttijdstip.setText("De verwachte levertijd is " + m.mDeliveryTime);

            }
        }
    }
}