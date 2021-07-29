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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

public class ForgotPassword extends BaseActivity {
    private static final String TAG = "ForgotPassword";

    LinearLayout redirect_login;

    private void includeActionBar(){
        ImageView back = findViewById(R.id.backBtn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirect_login.performClick();
            }
        });
    }

    SharedPreferences sharedPreferences;
    Button forgot_btn;
    EditText mEmailText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        includeActionBar();
        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE);
        Constants.enableSSL(asyncHttpClient);
        mEmailText = findViewById(R.id.username);
        redirect_login = findViewById(R.id.redirect_sign_in);
        forgot_btn = findViewById(R.id.forgot_btn);

        forgot_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean valid = true;
                if (mEmailText.getText().toString().equalsIgnoreCase("")){
                    mEmailText.setError("Enter Phone Number");
                    valid = false;
                }else if (mEmailText.getText().toString().trim().length()<9){
                    mEmailText.setError("Incomplete Number(min 9 digits)");
                    valid = false;
                }else if (mEmailText.getText().toString().trim().startsWith("0")){
                    mEmailText.setError("Number cannot start with 0");
                    valid = false;
                }

                if (valid)
                   forgotPassword();
            }
        });

        redirect_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ForgotPassword.this,LoginScreen.class));
            }
        });
    }

    private ProgressDialog progress;
    private void showProgressDialog()
    {

        progress = new ProgressDialog(ForgotPassword.this);
        progress.setMessage("Please wait...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
    }

    private void hideDialoge()
    {
        if (progress!=null)
            progress.dismiss();
    }

    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    private void forgotPassword(){
        RequestParams params = new RequestParams();
        params.put("phone","0060"+mEmailText.getText().toString());

        asyncHttpClient.post(ForgotPassword.this, Constants.Host_Address + "forgot", params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showProgressDialog();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    hideDialoge();
                    String response = new String(responseBody);
                    Log.e(TAG, "onSuccess: "+response);
                    JSONObject responseObj = new JSONObject(response);
                    if (responseObj.getString("status").equalsIgnoreCase("success")){
                        String code = responseObj.getString("code");
                        String member_id = responseObj.getString("customer_id");
                        sharedPreferences.edit().putString(Constants.PREFS_USER_MOBILE,"0060"+mEmailText.getText().toString()).apply();
                        sharedPreferences.edit().putString(Constants.PREFS_CUSTOMER_ID,member_id).apply();
                        sharedPreferences.edit().putString("code",code).apply();
                        startActivity(new Intent(ForgotPassword.this,VerificationScreen.class)
                                .putExtra("code",code)
                                .putExtra("is_create",false));
                        finish();
                    }else {
                        Toast.makeText(ForgotPassword.this, UtilsManager.makeInitialCapital(responseObj.getString("message")),Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    hideDialoge();
                    String response = new String(responseBody);
                    Log.e(TAG, "onFailed: "+response);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
