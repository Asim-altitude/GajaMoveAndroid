package customer.gajamove.com.gajamove_customer.bank;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
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

public class AddBankInfoScreen extends AppCompatActivity implements NotifyClick {
    private static final String TAG = "AddBankInfoScreen";

    EditText account_number_input,account_title_input;
    Spinner bank_name_spinner;
    Button save_btn;

    String[] bank_array = {"Bank 1","Bank 2","Bank 3"};
    String[] bank_id_array = {"1","2","3"};
    int selected_index = 0;
    ArrayAdapter<String> bank_list_adapter;
    ImageView back;


    ArrayList<BankObj> bankObjArrayList;
    MemberBankAdapter memberBankAdapter;
    ListView bankListview;


    String member_id="";
    LinearLayout next_btn,bank_info_lay;

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
        title.setText("Add Bank");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bank_info_screen);

        includeActionBar();
        member_id = getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE).getString(Constants.PREFS_CUSTOMER_ID,"0");

        account_number_input = (EditText) findViewById(R.id.account_number_text);
        account_title_input = (EditText) findViewById(R.id.account_title_text);
        bank_name_spinner = (Spinner) findViewById(R.id.bank_name_spinner);
        save_btn = (Button) findViewById(R.id.save_bank_button);
        next_btn =  findViewById(R.id.next_btn);
        bankListview = findViewById(R.id.bank_listview);
        bank_info_lay = findViewById(R.id.bank_info_lay);

        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 finish();
            }
        });

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!account_number_input.getText().toString().trim().isEmpty() &&
                        !account_title_input.getText().toString().trim().isEmpty()){
                    savebankInfo();
                }else {
                    Toast.makeText(AddBankInfoScreen.this,"Provide account title and number",Toast.LENGTH_SHORT).show();
                }
            }
        });

        next_btn.setVisibility(View.GONE);
       // bank_info_lay.setVisibility(View.GONE);


    }

    @Override
    protected void onResume() {
        super.onResume();
        //getBankInfo();
        getBankList();
    }

    private void getBankList() {

        asyncHttpClient.get(Constants.Host_Address+"members/get_banks/"+ UtilsManager.getApiKey(AddBankInfoScreen.this)+"", new AsyncHttpResponseHandler() {


            @Override
            public void onStart() {
                super.onStart();
                showDialog("Getting Bank List");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    hideDialog();
                    String response = new String(responseBody);
                    Log.e(TAG, "onSuccess: "+response);
                    JSONObject jsonObject = new JSONObject(response);

                    JSONArray dataArray = jsonObject.getJSONArray("data");

                    bank_id_array = new String[dataArray.length()];
                    bank_array = new String[dataArray.length()];
                    for (int i=0;i<dataArray.length();i++){
                        JSONObject jsonObject1 = dataArray.getJSONObject(i);

                        String id = jsonObject1.getString("id");
                        String bank = jsonObject1.getString("bank");

                        bank_id_array[i] = id;
                        bank_array[i] = bank;

                    }

                    bank_list_adapter = new ArrayAdapter<String>(AddBankInfoScreen.this,android.R.layout.simple_list_item_1,bank_array);
                    bank_name_spinner.setAdapter(bank_list_adapter);
                    bank_name_spinner.setOnItemSelectedListener(onItemSelectedListener);

                }
                catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(AddBankInfoScreen.this,"Unable to get bank list",Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    hideDialog();
                    String response = new String(responseBody);
                    Log.e(TAG, "onFailed: "+response);
                    Toast.makeText(AddBankInfoScreen.this,"Internet/Server Error", Toast.LENGTH_SHORT).show();

                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Log.e(TAG, "onItemSelected: "+bank_array[position]);
            selected_index = position;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    private void savebankInfo() {

        RequestParams params = new RequestParams();
        params.put("member_id",member_id);
        params.put("bank_id",bank_id_array[selected_index]);
        params.put("acc_number",account_number_input.getText().toString());
        params.put("acc_title",account_title_input.getText().toString());
        params.put("key",UtilsManager.getApiKey(AddBankInfoScreen.this));
        params.put("user_type","1");


        asyncHttpClient.post(Constants.Host_Address+"members/add_bank_detail",params, new AsyncHttpResponseHandler() {


            @Override
            public void onStart() {
                super.onStart();
                showDialog("Saving Bank Info");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    hideDialog();
                    String response = new String(responseBody);
                    Log.e(TAG, "onSuccess: "+response);
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if (status.equalsIgnoreCase("success")){
                        Toast.makeText(AddBankInfoScreen.this,jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        finish();
                    }else {
                        Toast.makeText(AddBankInfoScreen.this,jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

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
                    Toast.makeText(AddBankInfoScreen.this,"Internet/Server Error.", Toast.LENGTH_SHORT).show();

                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }



    private void getBankInfo(){

        asyncHttpClient.get(Constants.Host_Address + "members/get_bank_detail/"+member_id+"/"+UtilsManager.getApiKey(AddBankInfoScreen.this)+"", new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showDialog("Getting Bank List...");
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


                    memberBankAdapter = new MemberBankAdapter(bankObjArrayList,AddBankInfoScreen.this);
                    memberBankAdapter.setNotifyClick(AddBankInfoScreen.this);
                    bankListview.setAdapter(memberBankAdapter);

                    bank_info_lay.setVisibility(View.GONE);
                    next_btn.setVisibility(View.VISIBLE);

                }
                catch (Exception e){
                    e.printStackTrace();
                    getBankList();
                    bank_info_lay.setVisibility(View.VISIBLE);
                    next_btn.setVisibility(View.GONE);

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    hideDialog();
                    String response = new String(responseBody);
                    Log.e(TAG, "onFailure: "+response);
                    bank_info_lay.setVisibility(View.VISIBLE);
                    next_btn.setVisibility(View.GONE);
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

        alertDialog = new AlertDialog.Builder(AddBankInfoScreen.this)
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


    private void deleteInfoApi(final int pos , String id) {
        RequestParams params = new RequestParams();
        params.put("bank_detail_id",id);
        params.put("key",UtilsManager.getApiKey(AddBankInfoScreen.this));

        asyncHttpClient.post(Constants.Host_Address+"members/delete_bank_detail", params,new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showDialog("Deleting Bank Info");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    hideDialog();
                    JSONObject jsonObject = new JSONObject(new String(responseBody));
                    if (jsonObject.getString("status").equalsIgnoreCase("success")){
                        Toast.makeText(AddBankInfoScreen.this,jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        bankObjArrayList.remove(pos);
                        memberBankAdapter.notifyDataSetChanged();
                        if (bankObjArrayList.size()==0){
                           next_btn.setVisibility(View.GONE);
                           bank_info_lay.setVisibility(View.VISIBLE);
                           getBankList();
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
                Toast.makeText(AddBankInfoScreen.this,"Internet/Server Error", Toast.LENGTH_SHORT).show();

            }
        });
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

}
