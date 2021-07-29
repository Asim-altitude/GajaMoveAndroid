package customer.gajamove.com.gajamove_customer;

import androidx.appcompat.app.AppCompatActivity;
import customer.gajamove.com.gajamove_customer.adapter.CreditHistoryAdapter;
import customer.gajamove.com.gajamove_customer.models.LedgerItem;
import customer.gajamove.com.gajamove_customer.utils.Constants;
import customer.gajamove.com.gajamove_customer.utils.UtilsManager;
import cz.msebera.android.httpclient.Header;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FinanceScreen extends BaseActivity {

    private static final String TAG = "FinanceScreen";

    Button pay_btn;
    LinearLayout refund_btn;
    String credit = "00";

    String user_id;
    TextView user_credit;
    CreditHistoryAdapter creditHistoryAdapter;
    List<LedgerItem> ledgerItemList;
    ListView creditListview;
    int preLast = 0;

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
        title.setText(getResources().getString(R.string.finance));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finance_screen);

        setupToolBar();
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE);
        user_id = sharedPreferences.getString(Constants.PREFS_CUSTOMER_ID,"0");

        pay_btn = findViewById(R.id.pay_btn);
        user_credit = findViewById(R.id.user_credit);
        refund_btn = findViewById(R.id.refund_btn);
        creditListview = findViewById(R.id.credit_list);
        ledgerItemList = new ArrayList<>();
        creditHistoryAdapter = new CreditHistoryAdapter(this,ledgerItemList);
        creditListview.setAdapter(creditHistoryAdapter);

        refund_btn.setOnClickListener(refundClickListener);
        pay_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FinanceScreen.this,TopUpScreen.class);
                startActivity(intent);
            }
        });

        creditListview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                final int lastItem = firstVisibleItem + visibleItemCount;

                if(lastItem == totalItemCount)
                {
                    if(preLast!=lastItem)
                    {
                        //to avoid multiple calls for last item
                        Log.d("Last", "bottom reached");
                        preLast = lastItem;
                        index++;
                        getCreditHistory();
                    }
                }
            }
        });


    }

    private int index = 0;
    private void getCreditHistory() {
        asyncHttpClient.setConnectTimeout(20000);
        asyncHttpClient.get(FinanceScreen.this, Constants.Host_Address+"customers/history/"+user_id+"/1/"+UtilsManager.getApiKey(FinanceScreen.this)+"/"+index+"", new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                try {

                    String response = new String(responseBody);
                    Log.e(TAG, "onSuccess: "+response);
                    JSONObject jsonObject = new JSONObject(response);

                    JSONArray jsonArray = jsonObject.getJSONArray("data");

                    for (int i=0;i<jsonArray.length();i++){
                        String pay_id  = jsonArray.getJSONObject(i).getString("order_id_or_payment_id");
                        String id  = jsonArray.getJSONObject(i).getString("id");
                        String created_date  = jsonArray.getJSONObject(i).getString("date_time");
                        String type  = jsonArray.getJSONObject(i).getString("type");
                        String amount  = jsonArray.getJSONObject(i).getString("amount");
                        String desc  = jsonArray.getJSONObject(i).getString("description");

                        LedgerItem ledgerItem = new LedgerItem();
                        ledgerItem.setId(id);
                        ledgerItem.setAmount(amount);
                        ledgerItem.setDate(created_date);
                        ledgerItem.setPay_id(pay_id);
                        ledgerItem.setIn_out(type);
                        ledgerItem.setDesc(desc);

                        ledgerItemList.add(ledgerItem);

                    }


                    creditHistoryAdapter.notifyDataSetChanged();



                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    String response = new String(responseBody);
                    Log.e(TAG, "onSuccess: "+response);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private View.OnClickListener refundClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            OpenRefundDialog();
        }
    };

    private void setupToolbar(){
        // Show menu icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.white_color), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        getSupportActionBar().setTitle("Finance");
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDriverCreditInfoAPI();
        getCreditHistory();
    }

    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    private void getDriverCreditInfoAPI(){

        asyncHttpClient.setConnectTimeout(20000);
        Log.e(TAG, "getDriverCreditInfoAPI: "+Constants.Host_Address+"customers/calculate_balance/"+user_id+"/1/"+UtilsManager.getApiKey(FinanceScreen.this)+"");
        asyncHttpClient.get(FinanceScreen.this, Constants.Host_Address+"customers/calculate_balance/"+user_id+"/1/"+UtilsManager.getApiKey(FinanceScreen.this)+"", new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                try {

                    String response = new String(responseBody);
                    Log.e(TAG, "onSuccess: "+response);
                    JSONObject jsonObject = new JSONObject(response);
                    String data = jsonObject.getString("data");
                    credit = data;

                    if (data.equalsIgnoreCase("null"))
                        data = "0.0";
                    user_credit.setText("RM"+data);

                    double amount = Double.parseDouble(data);
                    if (amount < 0)
                        refund_btn.setVisibility(View.GONE);
                    else
                        refund_btn.setVisibility(View.VISIBLE);

                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    String response = new String(responseBody);
                    Log.e(TAG, "onSuccess: "+response);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }


    Dialog refund_dialog;
    private void OpenRefundDialog()
    {
        refund_dialog = new Dialog(FinanceScreen.this,R.style.Theme_Dialog);
        refund_dialog.setContentView(R.layout.refund_layout);

        final EditText amount_input = refund_dialog.findViewById(R.id.amount_input);
        final TextView tot_credit = refund_dialog.findViewById(R.id.refund_total_credit);
        final TextView driver_share_ = refund_dialog.findViewById(R.id.refund_driver_credit);

        tot_credit.setText(credit);
        driver_share_.setText(credit);
        TextView send_request = refund_dialog.findViewById(R.id.request_btn);


        send_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!amount_input.getText().toString().equalsIgnoreCase("")) {

                        if (amount_input.getText().toString().contains(".")) {

                            double amount = Double.parseDouble(amount_input.getText().toString());
                            double credit_ = Double.parseDouble(credit);

                            if (amount > credit_) {
                                Toast.makeText(FinanceScreen.this, "You cannot withdraw more than your current balance", Toast.LENGTH_SHORT).show();
                            } else {
                                SendRefundRequest(amount_input.getText().toString());
                            }

                        }else {

                            int amount = Integer.parseInt(amount_input.getText().toString());
                            double credit_ = Double.parseDouble(credit);

                            if (amount > credit_) {
                                Toast.makeText(FinanceScreen.this, "You cannot withdraw more than your current balance", Toast.LENGTH_SHORT).show();
                            } else {
                                SendRefundRequest(amount_input.getText().toString());
                            }

                        }

                    }

                }
                catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(FinanceScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });

        refund_dialog.show();
    }

    private ProgressDialog progressDialog = null;

    private void showDialog(String message){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    private void hideDialog(){
        if (progressDialog!=null)
            progressDialog.dismiss();
    }

    private void SendRefundRequest(String s) {
        refund_dialog.dismiss();

        asyncHttpClient.setTimeout(20*1000);

        RequestParams params = new RequestParams();

        params.put("user_id",user_id);
        params.put("user_type","1");
        params.put("key",UtilsManager.getApiKey(FinanceScreen.this));
        params.put("amount",s);

        Log.e(TAG, "SendRefundRequest: USER_ID "+user_id);
        Log.e(TAG, "SendRefundRequest: USER_AMOUNT "+s);
        Log.e(TAG, "SendRefundRequest: URL "+Constants.Host_Address + "customers/customer_withdraw_request" );

        asyncHttpClient.post(Constants.Host_Address + "customers/customer_withdraw_request", params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showDialog("Requesting withdrawl");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {

                    hideDialog();
                    String res = new String(responseBody);
                    Log.e(TAG, "onSuccess: "+res);
                    JSONObject jsonObject = new JSONObject(res);
                    Toast.makeText(getApplicationContext(), UtilsManager.makeInitialCapital(jsonObject.getString("message")),Toast.LENGTH_LONG).show();


                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    hideDialog();
                    String res = new String(responseBody);
                    Log.e(TAG, "onFailed: "+res);
                    Toast.makeText(getApplicationContext(),"Internet/Server Error",Toast.LENGTH_LONG).show();

                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });


    }


}
