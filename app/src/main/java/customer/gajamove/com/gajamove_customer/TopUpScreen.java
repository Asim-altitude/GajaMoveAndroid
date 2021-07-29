package customer.gajamove.com.gajamove_customer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;
import customer.gajamove.com.gajamove_customer.adapter.CreditPickAdapter;
import customer.gajamove.com.gajamove_customer.utils.Constants;
import customer.gajamove.com.gajamove_customer.utils.UtilsManager;
import cz.msebera.android.httpclient.Header;

public class TopUpScreen extends AppCompatActivity {
    private static final String TAG = "TopUpScreen";

    String[] amount_values = {"50","100","200","300","500","1000","2000","5000","10000"};
    int selected_amount = 50;
    double total = 0.0;
    double pending=0.0;

    GridView amountListGrid;
    CreditPickAdapter creditPickAdapter;
    TextView pay_btn;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

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
        title.setText(getResources().getString(R.string.top_up));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up_screen);
        //setupToolbar();
        setupToolBar();
        Constants.enableSSL(async);
        amountListGrid = findViewById(R.id.topupList);
        pay_btn = findViewById(R.id.pay_btn);
        pay_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                savePaymentInfo();
            }
        });

        creditPickAdapter = new CreditPickAdapter(amount_values,this);
        amountListGrid.setAdapter(creditPickAdapter);
        amountListGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selected_amount = Integer.parseInt(amount_values[position]);
                Log.e(TAG, "onItemClick: "+selected_amount);
                creditPickAdapter.setSelected(position);
                creditPickAdapter.notifyDataSetChanged();
            }
        });


    }




    AsyncHttpClient async = new AsyncHttpClient();
    private void savePaymentInfo() {

        SharedPreferences sha_prefs = getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE);
        String driverId = sha_prefs.getString(Constants.PREFS_CUSTOMER_ID,"12");
        final String driverName = sha_prefs.getString(Constants.PREFS_USER_NAME,"Customer");
        final String driveremail = sha_prefs.getString(Constants.PREFS_USER_EMAIL,"abc@ridingpink.com");
        // final String driverPhone = sha_prefs.getString(PrefConstantsClass.PREF,"Driver");
//        String amount_ = amount_text.getText().toString().trim().replace("RM","");

        async.setConnectTimeout(20000);

        RequestParams params = new RequestParams();
        params.put("amount",selected_amount+"");
        params.put("user_id",driverId);
        params.put("user_type","1");
        params.put("key", UtilsManager.getApiKey(TopUpScreen.this));


        async.post(TopUpScreen.this, Constants.Host_Address+"customers/add_topup" ,params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showDialog("Please wait...");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try
                {

                    hideDialoge();
                    String response = new String(responseBody);
                    Log.e(TAG, "onSuccess: "+response);
                    JSONObject json = new JSONObject(response);
                    JSONObject data = json.getJSONObject("data");//.getJSONObject(0);

                    String url = data.getString("url");
                    SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.FCM_PREF),MODE_PRIVATE);
                    String seleted_language = sharedPreferences.getString(Constants.LANGUAGE_CODE,"en");


                    Intent intent = new Intent(TopUpScreen.this, PaymentScreen.class)
                            .putExtra("url",url+"&lang="+seleted_language)
                            .putExtra("top_up",true)
                            .putExtra("order_id","");

                    startActivity(intent);
                    finish();


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
                    hideDialoge();
                    String response = new String();
                    Log.e(TAG, "onSuccess: "+response);
                    Log.e("response",response);
                    Toast.makeText(TopUpScreen.this,"Could not redirect to MOLPay "+response,Toast.LENGTH_LONG).show();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    ProgressDialog progressDialog;
    private void showDialog(String message){
        progressDialog = new ProgressDialog(TopUpScreen.this);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void hideDialoge(){
        if (progressDialog!=null)
            progressDialog.dismiss();
    }
}
