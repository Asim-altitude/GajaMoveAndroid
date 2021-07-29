package customer.gajamove.com.gajamove_customer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import customer.gajamove.com.gajamove_customer.adapter.NotificationAdapter;
import customer.gajamove.com.gajamove_customer.models.Notification_Data;
import customer.gajamove.com.gajamove_customer.utils.Constants;
import customer.gajamove.com.gajamove_customer.utils.UtilsManager;
import cz.msebera.android.httpclient.Header;

public class NotificationsScreen extends BaseActivity {
    private static final String TAG = "NotificationsScreen";

    ListView listView;
    NotificationAdapter notificationAdapter;

    private void setupToolBar(){

        ImageView back = findViewById(R.id.backBtn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        TextView title = findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        title.setText("Notifications");
    }

    String customer_id;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications_screen);

        setupToolBar();
        Constants.enableSSL(asyncHttpClient);
        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE);
        customer_id = sharedPreferences.getString(Constants.PREFS_CUSTOMER_ID,"");

        listView = findViewById(R.id.notification_list);
        list = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(list,NotificationsScreen.this);
        listView.setAdapter(notificationAdapter);

        //loadSavedNofications();
        findViewById(R.id.no_data_lay).setVisibility(View.VISIBLE);

    }

    ArrayList<Notification_Data> list;
    private void loadSavedNofications() {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Notification_Data>>() {
        }.getType();
        list.clear();
        String json = getSharedPreferences(Constants.USER_NOTIFICATION, MODE_PRIVATE).getString("gen_notifications", "");
        if (json != null) {
            if (!json.equals("")) {
                ArrayList<Notification_Data> temp = gson.fromJson(json, type);
                for (int i = 0; i < temp.size(); i++) {
                    temp.get(i).setShown(true);
                    list.add(temp.get(i));

                }

                notificationAdapter.notifyDataSetChanged();

            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        getNotifications();
    }

    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    private void getNotifications() {

        String url = Constants.Host_Address+"get_push_notifications/"+customer_id+"/0/"+ UtilsManager.getApiKey(NotificationsScreen.this)+"";
        Log.e(TAG, "getNotifications: "+url);

        asyncHttpClient.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String response = new String(responseBody);
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsArray = jsonObject.getJSONArray("data");

                    list.clear();
                    for (int i=0;i<jsArray.length();i++){

                        Notification_Data notification_data = new Notification_Data();
                        String order_id = jsArray.getJSONObject(i).getString("order_id");
                        String title = jsArray.getJSONObject(i).getString("title");
                        String message = jsArray.getJSONObject(i).getString("message");
                        String datetime = jsArray.getJSONObject(i).getString("datetime");
                        String notification_read = jsArray.getJSONObject(i).getString("notification_read");

                        if (!title.toLowerCase().equalsIgnoreCase("")
                                || !title.toLowerCase().equalsIgnoreCase("0")
                        ){
                            notification_data.setOrder_id(order_id);
                            notification_data.setTitle(title);
                            notification_data.setBody(message);
                            notification_data.setDate_time(datetime);

                            if (notification_read.equalsIgnoreCase("0"))
                                notification_data.setRead(false);
                            else
                                notification_data.setRead(true);


                            list.add(notification_data);

                        }

                    }
                    findViewById(R.id.no_data_lay).setVisibility(View.GONE);
                    notificationAdapter.notifyDataSetChanged();

                }
                catch (Exception e){
                    e.printStackTrace();
                    findViewById(R.id.no_data_lay).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    Toast.makeText(NotificationsScreen.this,"Internet/Server Error",Toast.LENGTH_SHORT).show();
                    findViewById(R.id.no_data_lay).setVisibility(View.VISIBLE);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }


}
