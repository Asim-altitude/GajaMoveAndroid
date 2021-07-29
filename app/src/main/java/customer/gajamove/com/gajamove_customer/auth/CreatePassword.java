package customer.gajamove.com.gajamove_customer.auth;

import androidx.appcompat.app.AppCompatActivity;
import customer.gajamove.com.gajamove_customer.BaseActivity;
import customer.gajamove.com.gajamove_customer.R;
import customer.gajamove.com.gajamove_customer.utils.Constants;
import customer.gajamove.com.gajamove_customer.utils.UtilsManager;
import cz.msebera.android.httpclient.Header;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

public class CreatePassword extends BaseActivity {
    private static final String TAG = "CreatePassword";

    EditText pasword,confrim_password;
    Button create_btn;
    String code="",customer_id="";
    SharedPreferences sharedPreferences;

    private void includeActionBar(){
        ImageView back = findViewById(R.id.backBtn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_password);

        includeActionBar();
        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE);
        customer_id = sharedPreferences.getString(Constants.PREFS_CUSTOMER_ID,"");
        code = sharedPreferences.getString("code","");
        pasword = findViewById(R.id.password);
        confrim_password = findViewById(R.id.confirm_pass);

        create_btn = findViewById(R.id.create_btn);
        create_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (pasword.getText().toString().equalsIgnoreCase(confrim_password.getText().toString())) {

                    if (pasword.getText().toString().length() < 6)
                    {
                        pasword.setError("Minimum 6 digits");
                        return;
                    }

                    if (confrim_password.getText().toString().length() < 6)
                    {
                        confrim_password.setError("Minimum 6 digits");
                        return;
                    }


                    createPassword();
                }
                else{
                    confrim_password.setError("Passwords do not match");
                   // return;
                }
            }
        });
    }

    private ProgressDialog progress;
    private void showProgressDialog(String message)
    {

        progress = new ProgressDialog(CreatePassword.this);
        progress.setMessage(message);
        progress.setCanceledOnTouchOutside(false);
        progress.show();
    }

    private void hideDialoge()
    {
        if (progress!=null)
            progress.dismiss();
    }

    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    private void createPassword() {
        RequestParams params = new RequestParams();
        params.put("customer_id",customer_id);
        params.put("password",pasword.getText().toString());
        params.put("key",UtilsManager.getApiKey(CreatePassword.this));

        asyncHttpClient.post(Constants.Host_Address + "customers/set_customer_password", params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showProgressDialog("Creating Password");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    hideDialoge();
                    String response = new String(responseBody);
                    Log.e(TAG, "onSuccess: "+response);
                    JSONObject jsonObject = new JSONObject(response);

                    int errorcode = jsonObject.getInt("errorCode");
                    if (errorcode==00){
                        String message = jsonObject.getString("message");
                        Toast.makeText(CreatePassword.this, UtilsManager.makeInitialCapital(message),Toast.LENGTH_LONG).show();
                        startActivity(new Intent(CreatePassword.this,LoginScreen.class));
                        sharedPreferences.edit().putBoolean(Constants.SOCIAL_LOGIN,false).apply();
                        finish();
                    }else {
                        hideDialoge();
                        String message = jsonObject.getString("message");
                        Toast.makeText(CreatePassword.this,UtilsManager.makeInitialCapital(message),Toast.LENGTH_LONG).show();
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                    hideDialoge();
                    Toast.makeText(CreatePassword.this,e.getMessage(),Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    hideDialoge();
                    String response = new String(responseBody);
                    Toast.makeText(CreatePassword.this,"Internet/Server Error",Toast.LENGTH_SHORT).show();

                }
                catch (Exception e){
                    e.printStackTrace();
                    hideDialoge();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
