package customer.gajamove.com.gajamove_customer.auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import customer.gajamove.com.gajamove_customer.BaseActivity;
import customer.gajamove.com.gajamove_customer.R;
import customer.gajamove.com.gajamove_customer.custom.PinView;
import customer.gajamove.com.gajamove_customer.utils.Constants;
import customer.gajamove.com.gajamove_customer.utils.UtilsManager;
import cz.msebera.android.httpclient.Header;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jkb.vcedittext.VerificationAction;
import com.jkb.vcedittext.VerificationCodeEditText;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VerificationScreen extends BaseActivity {
    private static final String TAG = "VerificationScreen";

    private void includeActionBar(){
        ImageView back = findViewById(R.id.backBtn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    VerificationCodeEditText verificationCodeEditText;
    PinView pinView;
    Button verify_btn;
    TextView resend_code_btn,message;
    String code = "",password="";
    boolean iscreate_account = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_screen);

        includeActionBar();
        code = getIntent().getStringExtra("code");
        password = getIntent().getStringExtra("pass");
        iscreate_account = getIntent().getBooleanExtra("is_create",true);
        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE);

        pinView = findViewById(R.id.firstPinView);
        pinView.setItemCount(code.length());
        message = findViewById(R.id.message);
        message.setText("Enter "+code.length()+" Digit Number that was sent to your email and mobile.");
        verificationCodeEditText = findViewById(R.id.verification_code_input);


        verificationCodeEditText.setOnVerificationCodeChangedListener(new VerificationAction.OnVerificationCodeChangedListener() {
            @Override
            public void onVerCodeChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void onInputCompleted(CharSequence s) {
                if (iscreate_account)
                    verifyOTP();
                else {

                    Intent intent = new Intent(VerificationScreen.this,CreatePassword.class);
                    intent.putExtra("code",code);
                    startActivity(intent);
                    finish();
                }
            }
        });
        verificationCodeEditText.setFigures(code.length());
        verificationCodeEditText.setVisibility(View.GONE);
        resend_code_btn = findViewById(R.id.resend_code);
        verify_btn = findViewById(R.id.verify);
        verify_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!pinView.getText().toString().equalsIgnoreCase("")){
                    if (iscreate_account)
                        verifyOTP();
                    else {
                        if (code.equalsIgnoreCase(pinView.getText().toString())) {
                            Intent intent = new Intent(VerificationScreen.this, CreatePassword.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                }
            }
        });

        resend_code_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canResend)
                    resendOTP();
            }
        });


        startCounter();
    }

    int time_passed = 0;


    private void stopCounter() {
        if (countDownTimer!=null)
            countDownTimer.cancel();
    }

    private void disableResendBtn(){
        resend_code_btn.setTextColor(ContextCompat.getColor(VerificationScreen.this,R.color.dark_gray_color));
        canResend = false;
    }

    private void enableResendBtn(){
        resend_code_btn.setTextColor(ContextCompat.getColor(VerificationScreen.this,R.color.theme_primary));
        canResend = true;
    }

    CountDownTimer countDownTimer;
    long sec = 0,min=0;
    String resend_text = "";
    boolean canResend = false;
    int tot_min = 10;
    private void startCounter() {

        try {
            if (countDownTimer == null) {
                disableResendBtn();
                countDownTimer = new CountDownTimer(((tot_min * 60 * 1000) - time_passed), 1000) {
                    @Override
                    public void onTick(long l) {
                        long sec = l / 1000;
                        time_passed = (int) (tot_min * 60 * 1000 - l);
                        if (sec > 60) {
                            min = sec / 60;
                            sec = sec % 60;

                            resend_text = getResources().getString(R.string.resend_code);
                            resend_code_btn.setText(resend_text + " (" + min + ":" + sec + ")");
                        } else {

                            resend_text = getResources().getString(R.string.resend_code);
                            resend_code_btn.setText(resend_text + " (00:" + sec + ")");
                        }
                        Log.e(TAG, "onTick: ");
                    }

                    @Override
                    public void onFinish() {
                        try{
                            resend_code_btn.setTextColor(ContextCompat.getColor(VerificationScreen.this,R.color.theme_primary_text));
                            enableResendBtn();
                            canResend = true;
                            resend_text = getResources().getString(R.string.resend_code);
                            resend_code_btn.setText(resend_text);
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                };

                countDownTimer.start();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopCounter();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            stopCounter();
        }
        catch (Exception e){

        }
    }

    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    SharedPreferences sharedPreferences;
    private void verifyOTP() {
        asyncHttpClient.setConnectTimeout(20000);

        RequestParams params = new RequestParams();
        try {
            params.put("otp", pinView.getText().toString());

        }
        catch (Exception e){
            e.printStackTrace();
        }

        asyncHttpClient.post(VerificationScreen.this, Constants.Host_Address + "check_otp_customer", params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showProgressDialog("Verifying..");
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
                        if (iscreate_account) {
                            setupPassword(password, sharedPreferences.getString(Constants.PREFS_CUSTOMER_ID, "0011"));
                        }


                    }else {
                        hideDialoge();
                        String message = jsonObject.getString("message");
                        Toast.makeText(VerificationScreen.this, UtilsManager.makeInitialCapital(message),Toast.LENGTH_LONG).show();
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                    hideDialoge();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    hideDialoge();
                    String response = new String(responseBody);
                    JSONObject jsonObject = new JSONObject(response);
                    Log.e(TAG, "onFailure: "+jsonObject);
                    String message = jsonObject.getString("message");
                    Toast.makeText(VerificationScreen.this,UtilsManager.makeInitialCapital(message),Toast.LENGTH_LONG).show();
                }
                catch (Exception e){
                    e.printStackTrace();

                    Toast.makeText(VerificationScreen.this,"Server Error",Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    private void resendOTP() {
        asyncHttpClient.setConnectTimeout(20000);

        RequestParams params = new RequestParams();
        try {
            params.put("phone", sharedPreferences.getString(Constants.PREFS_USER_MOBILE,""));

        }
        catch (Exception e){
            e.printStackTrace();
        }

        asyncHttpClient.post(VerificationScreen.this, Constants.Host_Address + "resend_otp_customer", params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showProgressDialog("Sending OTP");
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
                        Toast.makeText(VerificationScreen.this,UtilsManager.makeInitialCapital(message),Toast.LENGTH_SHORT).show();
                        code = jsonObject.getString("otp_code");
                        pinView.setItemCount(code.length());
                        startCounter();

                    }else {
                        String message = jsonObject.getString("message");
                        Toast.makeText(VerificationScreen.this,UtilsManager.makeInitialCapital(message),Toast.LENGTH_LONG).show();
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
                    JSONObject jsonObject = new JSONObject(response);
                    Log.e(TAG, "onFailure: "+jsonObject);
                    String message = jsonObject.getString("message");
                    Toast.makeText(VerificationScreen.this,UtilsManager.makeInitialCapital(message),Toast.LENGTH_LONG).show();
                }
                catch (Exception e){
                    e.printStackTrace();

                    Toast.makeText(VerificationScreen.this,"Server Error",Toast.LENGTH_LONG).show();
                }
            }
        });


    }


    private ProgressDialog progress;
    private void showProgressDialog(String message)
    {

        progress = new ProgressDialog(VerificationScreen.this);
        progress.setMessage(message);
        progress.setCanceledOnTouchOutside(false);
        progress.show();
    }

    private void hideDialoge()
    {
        if (progress!=null)
            progress.dismiss();
    }




    private void setupPassword(String password_,String cust_id)
    {
        asyncHttpClient.setConnectTimeout(20000);

        RequestParams params = new RequestParams();
        params.put("customer_id",cust_id);
        params.put("password",password_);
        params.put("key",UtilsManager.getApiKey(VerificationScreen.this));

        asyncHttpClient.post(VerificationScreen.this, Constants.Host_Address + "customers/set_customer_password", params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {

                    String response = new String(responseBody);
                    Log.e("response",response);
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if (status.equalsIgnoreCase("success")) {
                      //  Toast.makeText(VerificationScreen.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        //Toast.makeText(SetupPassword.this,"Your can login now.",Toast.LENGTH_LONG).show();
                        startActivity(new Intent(VerificationScreen.this, ThankYouScreen.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                    }
                    else
                    {
                        Toast.makeText(VerificationScreen.this,UtilsManager.makeInitialCapital(jsonObject.getString("message")), Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {

                    String response = new String(responseBody);
                    Log.e("response",response);
                    Toast.makeText(VerificationScreen.this, "Server Error ", Toast.LENGTH_SHORT).show();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

    }



    private static final Pattern p = Pattern.compile("[^\\d]*[\\d]+[^\\d]+([\\d]+)");

    private BroadcastReceiver mServiceReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent)
        {
            try{
                String IncomingSms=intent.getStringExtra("incomingSms");
               /* Matcher m = p.matcher(IncomingSms);

                // if an occurrence if a pattern was found in a given string..
                if (m.find()) {
                    verificationCodeEditText.setText(m.group(1)+"");
                    // second matched digits
                }*/

               if (IncomingSms.contains(code) && IncomingSms.toLowerCase().contains("gaja move")){
                   pinView.setText(code);

               }

            }
            catch (Exception e){
                e.printStackTrace();
            }
            //Extract your data - better to use constants..

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        try {
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.intent.action.SmsReceiver");
            registerReceiver(mServiceReceiver , filter);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if(mServiceReceiver != null){
                unregisterReceiver(mServiceReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
