package be.ehb.koalaexpress;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import be.ehb.koalaexpress.Tasks.Task_SendOrderToDB;
import be.ehb.koalaexpress.models.WinkelMandje;
import cz.msebera.android.httpclient.Header;

public class CheckoutActivity extends AppCompatActivity {

    TextView orderID_label;
    TextView payerID_label;
    TextView paymentAmount_label;
    Button confirm_btn;
    Button annuleren_btn;
    ProgressBar progressbar;
    public String orderID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hiding ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_checkout);

        //get the orderID from the query parameter
        Uri redirectUri = getIntent().getData();
        List<String> segmentsInUrl = redirectUri.getPathSegments();
        //hier kan je succes of failure halen uit de segmenstInURL

        orderID = redirectUri.getQueryParameter("token");
        String payerID = redirectUri.getQueryParameter("PayerID");

        progressbar = findViewById(R.id.progressbar);
        progressbar.setVisibility(View.INVISIBLE);

        //set the orderID string to the UI
        orderID_label = (TextView) findViewById(R.id.orderID);
        orderID_label.setText("Checkout ID: " +orderID);

        payerID_label = (TextView) findViewById(R.id.payerid);
        payerID_label.setText("Je Betaler Id is: " +payerID);

        paymentAmount_label = (TextView) findViewById(R.id.amt);
        paymentAmount_label.setText(String.format("Te betalen: € %.02f", KoalaDataRepository.getInstance().mWinkelMandje.getValue().mTotalPrice));

        //add an onClick listener to the confirm button
        confirm_btn = findViewById(R.id.confirm_btn);
        confirm_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                captureOrder(orderID); //function to finalize the payment
            }
        });
        annuleren_btn= findViewById(R.id.annuleren_btn);
        annuleren_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                returnToOrderZonderConfirm();
            }
        });
    }

    void captureOrder(String orderID){
        //get the accessToken from MainActivity
        progressbar.setVisibility(View.VISIBLE);

        String accessToken = KoalaDataRepository.getInstance().PaypalAccessToken;

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Accept", "application/json");
        client.addHeader("Content-type", "application/json");
        client.addHeader("Authorization", "Bearer " + accessToken);

        client.post("https://api-m.sandbox.paypal.com/v2/checkout/orders/"+orderID+"/capture", new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                Log.i("RESPONSE", response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) {
                // eerst het resultaat van call verwerken om paymentid op te halen
                String paymentId = "";
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    String orderId = jsonResponse.getString("id"); // This is the order ID
                    JSONArray purchaseUnits = jsonResponse.getJSONArray("purchase_units");
                    if (purchaseUnits.length() > 0) {
                        JSONObject purchaseUnit = purchaseUnits.getJSONObject(0);
                        JSONArray payments = purchaseUnit.getJSONObject("payments").getJSONArray("captures");
                        if (payments.length() > 0) {
                            JSONObject payment = payments.getJSONObject(0);
                            paymentId = payment.getString("id"); // dit is de payment id
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                KoalaDataRepository repo = KoalaDataRepository.getInstance();

                WinkelMandje mandje = repo.mWinkelMandje.getValue();
                mandje.mPayPalPaymentId = paymentId;
                Date currentDate = new Date();
                mandje.mPayedOnDate = new Timestamp(currentDate.getTime());
                repo.mWinkelMandje.setValue(mandje);

                // order opslaan in db
                Task_SendOrderToDB taak = new Task_SendOrderToDB();
                taak.execute(mandje);

                // 3 seconden wachten vooraleer naar fragment te springen om tijd te geven order op te slaan
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //redirect back to home page of app
                        Intent intent = new Intent(CheckoutActivity.this, MainActivity.class);
                        intent.putExtra("JumpToFragment","WinkelMandjeSucces");
                        intent.putExtra("AfgeslotenOrderId",orderID);
                        intent.putExtra("AfgeslotenPaymentId",mandje.mPayPalPaymentId);

                        progressbar.setVisibility(View.INVISIBLE);

                        startActivity(intent);

                    }

                }, 3000); // 3000ms delay
            }
        });
    }
    public void returnToOrderZonderConfirm() {
        Intent intent = new Intent(CheckoutActivity.this, MainActivity.class);
        intent.putExtra("JumpToFragment","WinkelMandjeAnnuleren");
        startActivity(intent);
    }

}