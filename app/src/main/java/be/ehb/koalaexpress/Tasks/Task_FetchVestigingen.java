package be.ehb.koalaexpress.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import be.ehb.koalaexpress.ConnectionInfo;
import be.ehb.koalaexpress.JSONhelper;
import be.ehb.koalaexpress.KoalaDataRepository;
import be.ehb.koalaexpress.models.LocationList;
import be.ehb.koalaexpress.models.ProductCategoryList;
import be.ehb.koalaexpress.returnMessage;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Task_FetchVestigingen extends AsyncTask<KoalaDataRepository, Object, LocationList> {
    private String TAG = "Task_FetchVestigingen";
    private KoalaDataRepository mRepository;
    @Override
    protected LocationList doInBackground(KoalaDataRepository... KoalaRepo) {
        mRepository = KoalaRepo[0];

        OkHttpClient client = new OkHttpClient();
        String url= ConnectionInfo.ServerUrl() + "/locations?action=listLocations";
        final Request request = new Request.Builder()
                .url(url)
                .build();
        String s="";
        LocationList myList = null;
        try {
            // nu doen we de eigenlijke servlet call naar servlet om de vestigingen op te halen, resultaat in response met header en body
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful())
                return null;
            s = (response.body().string());

            // now process return message from URL call of
            try {
                //zet de response body om in header en content in returnmessage
                returnMessage msg = JSONhelper.extractmsgFromJSONAnswer(s);
                // objectmapper zet string om in object via json (jackson library)
                ObjectMapper objmap = new ObjectMapper();
                objmap.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
                // zet content om in object met json
                myList = objmap.readValue(msg.m_Content, LocationList.class);
            }
            catch (JsonProcessingException e) {
                Log.d(TAG, e.toString());
            }
        }
        catch (Exception e){
            s = e.getMessage().toString();
        }
        return myList;
    }


    @Override
    protected void onPostExecute(LocationList list){
        super.onPostExecute(list);
        mRepository.koalaLocations = list;
    }
}
