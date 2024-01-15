package be.ehb.koalaexpress.Tasks;

import android.os.AsyncTask;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import be.ehb.koalaexpress.ConnectionInfo;
import be.ehb.koalaexpress.JSONhelper;
import be.ehb.koalaexpress.KoalaDataRepository;
import be.ehb.koalaexpress.models.WinkelMandje;
import be.ehb.koalaexpress.returnMessage;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Task_SendOrderToDB extends AsyncTask<WinkelMandje, Object, returnMessage> {
    private String TAG = "Task_SendOrderToDB";
    public WinkelMandje teVerzendenMandje;
    @Override
    protected returnMessage doInBackground(WinkelMandje... winkelMandjes) {
        teVerzendenMandje = winkelMandjes[0];

        OkHttpClient client = new OkHttpClient();
        String url= ConnectionInfo.ServerUrl() + "/orders?action=storeOrder";
        //omzetten winkelmandje naar JSON string
        ObjectMapper defmapper = new ObjectMapper();
        defmapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        String winkelmandjeJSON = "";
        try {
            winkelmandjeJSON = defmapper.writeValueAsString(teVerzendenMandje);
            winkelmandjeJSON = JSONhelper.EncodeStringGoingOut(winkelmandjeJSON);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        url = url + "&payload=" + winkelmandjeJSON;

        final Request request = new Request.Builder()
                .url(url)
                .build();
        String s="";
        returnMessage msg = new returnMessage();
        try {
            // nu doen we de eigenlijke servlet call
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                msg.setSucces(false, "Server call not succesfull");
                return msg;
            }
            s = (response.body().string());

            msg = JSONhelper.extractmsgFromJSONAnswer(s);
            // er is geen payload, is enkel true of false in succes in msg.header
            // in message zit order nummer indien true
        }
        catch (Exception e){
            s = e.getMessage().toString();
            msg.setSucces(false, "Server not online");
        }
        return msg;
    }


    @Override
    protected void onPostExecute(returnMessage r){
        super.onPostExecute(r);
        if(r.isSucces()){
            teVerzendenMandje.mOrderId = Integer.parseInt(r.m_header.m_Message);
        }
    }
}
