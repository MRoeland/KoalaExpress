package be.ehb.koalaexpress.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import be.ehb.koalaexpress.ConnectionInfo;
import be.ehb.koalaexpress.JSONhelper;
import be.ehb.koalaexpress.KoalaDataRepository;
import be.ehb.koalaexpress.models.ProductCategoryList;
import be.ehb.koalaexpress.returnMessage;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Task_FetchCategories extends AsyncTask<KoalaDataRepository, Object, ProductCategoryList> {
    private String TAG = "Task_FetchCategories";
    private KoalaDataRepository mRepository;
    @Override
    protected ProductCategoryList doInBackground(KoalaDataRepository... koalaDataZwembaden) {
        mRepository = koalaDataZwembaden[0];

        OkHttpClient client = new OkHttpClient();
        String url= ConnectionInfo.ServerUrl() + "/products?action=listCategories";
        final Request request = new Request.Builder()
                .url(url)
                .build();
        String s="";
        ProductCategoryList myList = null;
        try {
            // nu doen we de eigenlijke servlet call
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful())
                return null;
            s = (response.body().string());

            // now process return message from URL call of
            try {
                returnMessage msg = JSONhelper.extractmsgFromJSONAnswer(s);
                // objectmapper zet string om in object via json (jackson library)
                ObjectMapper objmap = new ObjectMapper();
                objmap.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
                // zet content om in object using json
                myList = objmap.readValue(msg.m_Content, ProductCategoryList.class);
                /*for (ProductCategory g: myList.mList) {
                    Log.d(TAG, g.toString());
                }*/
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
    protected void onPostExecute(ProductCategoryList l){
        super.onPostExecute(l);
        mRepository.koalaCategorieen.setValue(l);
    }
}
