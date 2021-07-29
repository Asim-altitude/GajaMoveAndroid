package customer.gajamove.com.gajamove_customer.bank;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import customer.gajamove.com.gajamove_customer.MyApp;
import customer.gajamove.com.gajamove_customer.R;
import customer.gajamove.com.gajamove_customer.adapter.MemberBankAdapter;
import customer.gajamove.com.gajamove_customer.models.BankObj;
import customer.gajamove.com.gajamove_customer.utils.Constants;
import customer.gajamove.com.gajamove_customer.utils.UtilsManager;
import cz.msebera.android.httpclient.Header;

public class BankScreen extends AppCompatActivity implements NotifyClick {
    private static final String TAG = "BankScreen";



    LinearLayout bank_info_lay,empty_message;
    Button add_btn,delete_btn,next_btn;
    String member_id;

    ArrayList<BankObj> bankObjArrayList;
    MemberBankAdapter memberBankAdapter;
    ListView bankListview;

    boolean is_registartion = false;
    boolean is_edit = false;

    ImageView back;


    private void includeActionBar(){
        ImageView back = findViewById(R.id.backBtn);
        TextView title = findViewById(R.id.title);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //redirect_login.performClick();
                finish();
            }
        });

        title.setVisibility(View.VISIBLE);
        title.setText("My Bank");
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_screen);

        includeActionBar();
        member_id = getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE).getString(Constants.PREFS_CUSTOMER_ID,"0");

        empty_message = findViewById(R.id.empty_message);
        bank_info_lay = findViewById(R.id.bank_info_lay);
        bankListview = findViewById(R.id.bank_list);
        is_registartion = false;

        back = findViewById(R.id.backBtn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        next_btn = findViewById(R.id.next_button);
        add_btn = findViewById(R.id.add_bank_button);


        if (!is_registartion){
            next_btn.setVisibility(View.GONE);
        }

        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 finish();
            }
        });

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BankScreen.this,AddBankInfoScreen.class));
            }
        });


        getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE).edit().putInt(Constants.REGISTRATION_STEP,3).apply();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (is_edit){
            finish();
        }else {
            finish();
        }
    }

    private void deleteInfoApi(final int pos , String id) {
        RequestParams params = new RequestParams();
        params.put("bank_detail_id",id);
        params.put("key", UtilsManager.getApiKey(BankScreen.this));

        asyncHttpClient.post(Constants.Host_Address+"members/delete_bank_detail", params,new AsyncHttpResponseHandler() {


            @Override
            public void onStart() {
                super.onStart();
                showDialog("Deleting bank info...");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    hideDialog();
                    JSONObject jsonObject = new JSONObject(new String(responseBody));
                    if (jsonObject.getString("status").equalsIgnoreCase("success")){
                        Toast.makeText(BankScreen.this,jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        bankObjArrayList.remove(pos);
                        memberBankAdapter.notifyDataSetChanged();
                        if (bankObjArrayList.size()==0){
                            empty_message.getChildAt(0).setVisibility(View.VISIBLE);
                            add_btn.setVisibility(View.VISIBLE);
                        }
                    }


                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                hideDialog();
                Toast.makeText(BankScreen.this,"Internet/Server Error", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getBankInfo();
    }

    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    private void getBankInfo(){

        Log.e(TAG, "getBankInfo: "+Constants.Host_Address + "members/get_bank_detail/"+member_id+"/tgs_appkey_amin/1");
        asyncHttpClient.get(Constants.Host_Address + "members/get_bank_detail/"+member_id+"/"+UtilsManager.getApiKey(BankScreen.this)+"/1", new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showDialog("Please wait");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    hideDialog();
                    String response = new String(responseBody);
                    Log.e(TAG, "onSuccess: "+response);

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");

                    bankObjArrayList = new ArrayList<>();
                    for (int i=0;i<jsonArray.length();i++){
                        String bank_name_ = jsonArray.getJSONObject(i).getString("bank");
                        String bank_id = jsonArray.getJSONObject(i).getString("bank_detail_id");
                        String account_number_ = jsonArray.getJSONObject(i).getString("account_number");
                        String account_title_ = jsonArray.getJSONObject(i).getString("acc_title");

                        BankObj bankObj = new BankObj();
                        bankObj.setAccount_number(account_number_);
                        bankObj.setName(bank_name_);
                        bankObj.setAcc_title(account_title_);
                        bankObj.setBank_id(bank_id);

                        bankObjArrayList.add(bankObj);

                    }

                    memberBankAdapter = new MemberBankAdapter(bankObjArrayList,BankScreen.this);
                    memberBankAdapter.setNotifyClick(BankScreen.this);
                    bankListview.setAdapter(memberBankAdapter);
                    empty_message.getChildAt(0).setVisibility(View.GONE);
                    if (!is_edit){
                        add_btn.setVisibility(View.GONE);
                    }


                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    hideDialog();
                    String response = new String(responseBody);
                    Log.e(TAG, "onFailure: "+response);

                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onNotify(int pos) {
        confirmDelete(pos);
    }

    AlertDialog alertDialog = null;
    private void confirmDelete(final int pos) {

        alertDialog = new AlertDialog.Builder(BankScreen.this)
                .setTitle("Confirmation")
                .setMessage("Are you sure to delete this bank info?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteInfoApi(pos,bankObjArrayList.get(pos).getBank_id());
                        alertDialog.dismiss();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                })
                .create();

        alertDialog.show();

    }
}
