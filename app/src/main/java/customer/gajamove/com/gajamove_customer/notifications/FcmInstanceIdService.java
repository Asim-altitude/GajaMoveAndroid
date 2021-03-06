package customer.gajamove.com.gajamove_customer.notifications;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import customer.gajamove.com.gajamove_customer.MyApp;
import customer.gajamove.com.gajamove_customer.R;
import customer.gajamove.com.gajamove_customer.utils.Constants;
import customer.gajamove.com.gajamove_customer.utils.UtilsManager;
import cz.msebera.android.httpclient.Header;

/**
 * Created by PC-GetRanked on 12/9/2017.
 */

public class FcmInstanceIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {

        loadMemberData();
        String recent_token = FirebaseInstanceId.getInstance().getToken();
        Log.e("recent_token", recent_token);
        if (!customerId.equalsIgnoreCase(""))
             saveDeviceIDServer(recent_token);

       // saveDeviceIDServer(recent_token);
        SharedPreferences sharedPreferences = getApplicationContext().
                getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.FCM_TOKEN), recent_token);
        editor.commit();


    }
    SharedPreferences settings;
    String customerId="";
    private void loadMemberData()
    {
        settings = getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE);
        customerId = settings.getString(Constants.PREFS_CUSTOMER_ID, "");

    }

    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    private void saveDeviceIDServer(String device_info)
    {
        asyncHttpClient.setConnectTimeout(40000);
        Log.e("device id customer",Constants.Host_Address + "members/update_customer_device_info/"+customerId+"/"+device_info+"/tgs_appkey_amin");
        asyncHttpClient.get(getApplicationContext(), Constants.Host_Address + "members/update_customer_device_info/"+customerId+"/"+device_info+"/"+UtilsManager.getApiKey(getApplicationContext())+"", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try
                {
                    String s = new String(responseBody);
                    Log.e("respnse",s);

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try
                {
                    String s = new String(responseBody);
                    Log.e("respnse",s);

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }
}
