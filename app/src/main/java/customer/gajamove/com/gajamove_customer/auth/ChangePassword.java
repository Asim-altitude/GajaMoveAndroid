package customer.gajamove.com.gajamove_customer.auth;

import androidx.appcompat.app.AppCompatActivity;
import customer.gajamove.com.gajamove_customer.BaseActivity;
import customer.gajamove.com.gajamove_customer.HomeScreen;
import customer.gajamove.com.gajamove_customer.R;
import customer.gajamove.com.gajamove_customer.utils.Constants;
import customer.gajamove.com.gajamove_customer.utils.UtilsManager;
import cz.msebera.android.httpclient.Header;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import static java.security.AccessController.getContext;

public class ChangePassword extends BaseActivity {



    private EditText txtOldPassword;
    private EditText txtPassword;
    private EditText txtCPassword;
    private ProgressDialog progressDialog;
    private Button btnChangePassword;

    private String accessToken;
    private String customerId;
    private String userPassword;

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
        title.setText(getResources().getString(R.string.change_pass));
    }


    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        includeActionBar();
        SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        accessToken = settings.getString(Constants.PREFS_ACCESS_TOKEN, "");
        customerId = settings.getString(Constants.PREFS_CUSTOMER_ID, "");
        userPassword = settings.getString(Constants.PREFS_USER_PASSWORD, "");

        Log.e("password",userPassword);


        txtPassword = (EditText) findViewById(R.id.password);
        txtCPassword = (EditText) findViewById(R.id.confirm_pass);
        txtOldPassword = findViewById(R.id.old_password);


        btnChangePassword = (Button) findViewById(R.id.create_btn);
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                txtPassword.setError(null);
                txtCPassword.setError(null);


                boolean cancel = false;
                View focusView = null;

                if (txtOldPassword.getText().length() == 0){
                    txtOldPassword.setError("Provide Old Password");
                    focusView = txtOldPassword;
                    cancel = true;

                }

                if (txtOldPassword.getText().toString().equalsIgnoreCase("")){
                    txtOldPassword.setError("Provide old password");
                    cancel = true;
                }

                if (txtPassword.getText().length() < 6) {
                    txtPassword.setError("Password should be atleast 6 digits");
                    focusView = txtPassword;
                    cancel = true;
                } else if (txtCPassword.getText().length() < 6) {
                    txtCPassword.setError("Password should be atleast 6 digits");
                    focusView = txtCPassword;
                    cancel = true;
                } else if (!txtPassword.getText().toString().equals(txtCPassword.getText().toString())) {
                    txtCPassword.setError("Passwords do not match");
                    focusView = txtCPassword;
                    cancel = true;
                }

                if(cancel) {
                    focusView.requestFocus();
                } else {

                    changePassword();
                }


            }
        });

    }


    private void showDialog(){
        progressDialog = new ProgressDialog(ChangePassword.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }
    private void hideDialog(){
        if (progressDialog!=null)
            progressDialog.dismiss();
    }


    private void changePassword() {

        // restServiceClient.callService(this, Constants.Host_Address + "change", JsonResponse.class, "POST", map, true);

        RequestParams params = new RequestParams();
        params.put("password_1", txtPassword.getText().toString());
        params.put("password_2", txtCPassword.getText().toString());
        params.put("old_password", txtOldPassword.getText().toString().trim());
        params.put("key", accessToken);
        params.put("id", customerId);


        asyncHttpClient.post(ChangePassword.this, Constants.Host_Address + "change", params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showDialog();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {


                hideDialog();
                try
                {
                    JSONObject object = new JSONObject(new String(responseBody));
                    if (object.has("status"))
                    {
                        if (object.getString("status").equals("success")) {
                            UtilsManager.showAlertMessage(ChangePassword.this, "", "Password Updated Successfully");
                            getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE).edit().clear().apply();
                            startActivity(new Intent(ChangePassword.this, LoginScreen.class));
                            finish();

                        }
                        else
                            UtilsManager.showAlertMessage(ChangePassword.this,"","Could not update Password");

                    }

                }
                catch (Exception e)
                {
                    Log.e("response",new String(responseBody));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                hideDialog();
                Log.e("response",new String(responseBody));
                UtilsManager.showAlertMessage(ChangePassword.this,"","Error Updating Password");
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
