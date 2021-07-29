package customer.gajamove.com.gajamove_customer.sos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import customer.gajamove.com.gajamove_customer.MyApp;
import customer.gajamove.com.gajamove_customer.R;
import customer.gajamove.com.gajamove_customer.models.PhoneContact;
import customer.gajamove.com.gajamove_customer.utils.Constants;
import customer.gajamove.com.gajamove_customer.utils.UtilsManager;
import cz.msebera.android.httpclient.Header;

public class SoS_Call_Screen extends AppCompatActivity {
    private static final String TAG = "SoS_Call_Screen";


    ListView listView;
    SOSCallAdapter sosCallAdapter;
    Button emergency_btn;


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
        title.setText("SOS Settings");
    }


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_so_s__call__screen);
        setupToolBar();
        SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        String customerId = settings.getString(Constants.PREFS_CUSTOMER_ID, "");

        listView = findViewById(R.id.contacts_list);
        emergency_btn = findViewById(R.id.call_btn);
        emergency_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "999"));
                startActivity(intent);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + savedContactList.get(position).getNumber()));
                startActivity(intent);
            }
        });

        String url = Constants.Host_Address+"sos/get_customer_sos_contacts_api/"+customerId+"/"+""+ UtilsManager.getApiKey(getApplicationContext())+"/1";
        Log.e(TAG, "getAllSavedContacts: "+url);
        getAllContacts(url);
    }

    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    public void getAllContacts(String url){

        asyncHttpClient.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try
                {
                    String s = new String(responseBody);
                    JSONObject object = new JSONObject(s);

                    if (object.has("data"))
                        parseData(object);


                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    Toast.makeText(getApplicationContext(), "Internet/Server Error", Toast.LENGTH_SHORT).show();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

    }
    private List<PhoneContact> savedContactList;

    private void parseData(JSONObject response) {

        try {

            JSONArray contacts = response.getJSONArray("data");

            if (contacts!=null) {

                savedContactList = new ArrayList<>();

                String name, phone, id, email;
                for (int i = 0; i < contacts.length(); i++) {
                    name = contacts.getJSONObject(i).getString("contact_name");
                    phone = contacts.getJSONObject(i).getString("phone");
                    email = contacts.getJSONObject(i).getString("email");
                    id = contacts.getJSONObject(i).getString("id");

                    if (!name.equalsIgnoreCase("") && !name.equalsIgnoreCase("null")) {
                        PhoneContact my_contact = new PhoneContact(name, phone, false, id);
                        savedContactList.add(my_contact);
                    }

                }

                sosCallAdapter = new SOSCallAdapter(SoS_Call_Screen.this,savedContactList);
                listView.setAdapter(sosCallAdapter);

            }
            else
            {

            }
        }
        catch (Exception e)
        {
            // UtilsManager.showAlertMessage(getContext(),"",e.getMessage());
        }

    }


}

