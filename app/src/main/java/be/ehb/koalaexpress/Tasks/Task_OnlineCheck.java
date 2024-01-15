package be.ehb.koalaexpress.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import be.ehb.koalaexpress.ConnectionInfo;
import be.ehb.koalaexpress.JSONhelper;
import be.ehb.koalaexpress.KoalaDataRepository;
import be.ehb.koalaexpress.models.ProductList;
import be.ehb.koalaexpress.returnMessage;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Task_OnlineCheck extends AsyncTask<KoalaDataRepository, Object, returnMessage> {
    private String TAG = "Task_OnlineCheck";
    private KoalaDataRepository mRepository;
    @Override
    protected returnMessage doInBackground(KoalaDataRepository... koalaRepo) {
        mRepository = koalaRepo[0];

        OkHttpClient client = new OkHttpClient();
        String url= ConnectionInfo.ServerUrl() + "/versions?action=onlinecheck";
        final Request request = new Request.Builder()
                .url(url)
                .build();
        String s="";
        returnMessage msg = new returnMessage();
        try {
            // nu doen we de eigenlijke servlet call
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                msg.setSucces(false, "Server not online");
                return msg;
            }
            s = (response.body().string());

            msg = JSONhelper.extractmsgFromJSONAnswer(s);
            // er is geen payload, is enkel true of false in succes in msg.header
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
        mRepository.mServerIsOnline = r.isSucces();
        mRepository.mServerStatusGechecked = true;
    }
}