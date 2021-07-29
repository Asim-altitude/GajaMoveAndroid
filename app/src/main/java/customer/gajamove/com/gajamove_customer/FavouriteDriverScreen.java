package customer.gajamove.com.gajamove_customer;

import androidx.appcompat.app.AppCompatActivity;
import customer.gajamove.com.gajamove_customer.adapter.FavDriverAdapter;
import customer.gajamove.com.gajamove_customer.adapter.MemberBankAdapter;
import customer.gajamove.com.gajamove_customer.bank.BankScreen;
import customer.gajamove.com.gajamove_customer.models.BankObj;
import customer.gajamove.com.gajamove_customer.models.Member;
import customer.gajamove.com.gajamove_customer.utils.Constants;
import customer.gajamove.com.gajamove_customer.utils.UtilsManager;
import cz.msebera.android.httpclient.Header;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class FavouriteDriverScreen extends BaseActivity {
    private static final String TAG = "FavouriteDriverScreen";

    String customer_id;
    SharedPreferences sharedPreferences;

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
        title.setText(getResources().getString(R.string.my_driver));
    }

    FavDriverAdapter favDriverAdapter;
    ListView fav_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_driver_screen);

        setupToolBar();
        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE);
        customer_id = sharedPreferences.getString(Constants.PREFS_CUSTOMER_ID,"");

        fav_list = findViewById(R.id.fav_driver_list);




    }

    @Override
    protected void onResume() {
        super.onResume();
        getFavDrivers();
    }

    ArrayList<Member> members;
    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    private void getFavDrivers(){

        Log.e(TAG, "getBankInfo: "+ Constants.Host_Address + "customers/get_favorite_drivers/"+customer_id+"/tgs_appkey_amin");
        asyncHttpClient.get(Constants.Host_Address + "customers/get_favorite_drivers/"+customer_id+"/"+ UtilsManager.getApiKey(FavouriteDriverScreen.this)+"", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String response = new String(responseBody);
                    Log.e(TAG, "onSuccess: "+response);

                    members = new ArrayList<>();
                    JSONObject jsonObject = new JSONObject(response);

                    JSONArray jsonArray = jsonObject.getJSONArray("data");

                    for (int i=0;i<jsonArray.length();i++){
                        Member member = new Member();


                        String id = jsonArray.getJSONObject(i).getString("favorite_id");
                        String full_name = jsonArray.getJSONObject(i).getString("full_name");
                        String display_name = jsonArray.getJSONObject(i).getString("display_name");
                        String profile_img = jsonArray.getJSONObject(i).getString("profile_img");
                        String email = jsonArray.getJSONObject(i).getString("email");

                        member.setItem_id(id);
                        member.setMem_image(profile_img);
                        member.setMem_name(full_name);
                        member.setMem_email(email);

                        members.add(member);
                    }

                    favDriverAdapter = new FavDriverAdapter(members,FavouriteDriverScreen.this);
                    fav_list.setAdapter(favDriverAdapter);

                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    String response = new String(responseBody);
                    Log.e(TAG, "onFailure: "+response);

                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
}
