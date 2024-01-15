package be.ehb.koalaexpress.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import be.ehb.koalaexpress.ConnectionInfo;
import be.ehb.koalaexpress.JSONhelper;
import be.ehb.koalaexpress.KoalaDataRepository;
import be.ehb.koalaexpress.models.Customer;
import be.ehb.koalaexpress.models.ProductCategoryList;
import be.ehb.koalaexpress.returnMessage;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Task_LoginUser extends AsyncTask<KoalaDataRepository, Object, Customer> {
    private String TAG = "Task_LoginUser";
    private String loginName;
    private String loginPassword;
    private KoalaDataRepository mRepository;
    private returnMessage returned_msg_fromCall;

    public Task_LoginUser(String loginName, String loginPassword) {
        this.loginName = loginName;
        this.loginPassword = loginPassword;
    }

    @Override
    protected Customer doInBackground(KoalaDataRepository... koalaDataZwembaden) {
        mRepository = koalaDataZwembaden[0];

        OkHttpClient client = new OkHttpClient();
        String url= ConnectionInfo.ServerUrl() + "/customers?action=login&login=" + loginName + "&password=" + loginPassword;
        final Request request = new Request.Builder()
                .url(url)
                .build();
        String resultStrFromCall="";
        Customer myCustomer = null;
        try {
            // nu doen we de eigenlijke servlet call
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful())
                return null;
            resultStrFromCall = (response.body().string());

            // now process return message from URL call of
            try {
                returned_msg_fromCall = JSONhelper.extractmsgFromJSONAnswer(resultStrFromCall);
                // objectmapper zet string om in object via json (jackson library)
                if(returned_msg_fromCall.m_header.m_Succes) {
                    ObjectMapper objmap = new ObjectMapper();
                    objmap.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
                    // zet content om in object using json
                    myCustomer = objmap.readValue(returned_msg_fromCall.m_Content, Customer.class);
                }
                else {
                    myCustomer = null;
                }
            }
            catch (JsonProcessingException e) {
                Log.d(TAG, e.toString());
            }
        }
        catch (Exception e){
            resultStrFromCall = e.getMessage().toString();
        }
        return myCustomer;
    }


    @Override
    protected void onPostExecute(Customer c){
        super.onPostExecute(c);
        if(returned_msg_fromCall.m_header.m_Succes == false){
            mRepository.mCustomer.setValue(null);

            mRepository.repoInfoMessage.setValue(returned_msg_fromCall.getErrorMessage());
        }
        else{
            mRepository.mCustomer.setValue(c);
        }
    }
}
