package customer.gajamove.com.gajamove_customer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import customer.gajamove.com.gajamove_customer.auth.LoginScreen;
import customer.gajamove.com.gajamove_customer.fragment.HomeUIMap;
import customer.gajamove.com.gajamove_customer.models.FireBaseChatHead;
import customer.gajamove.com.gajamove_customer.models.Member;
import customer.gajamove.com.gajamove_customer.utils.Constants;
import customer.gajamove.com.gajamove_customer.utils.RideDirectionPointsDB;
import customer.gajamove.com.gajamove_customer.utils.UtilsManager;
import cz.msebera.android.httpclient.Header;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.skyfishjy.library.RippleBackground;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class FindDriverScreen extends BaseActivity {

    private static final String TAG = "FindDriverScreen";

    FragmentManager fragmentManager;
    ImageView back_btn;
    RippleBackground rippleBackground;
    String customer_id,order_id;
    Button cancel_order_btn;
    AlertDialog alertDialog = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_driver_screen);


        customer_id = getIntent().getStringExtra("customer_id");
        order_id = getIntent().getStringExtra("order_id");
        Constants.enableSSL(asyncHttpClient);
        cancel_order_btn = findViewById(R.id.cancel_order_btn);
        rippleBackground = findViewById(R.id.rippleBg);
        rippleBackground.startRippleAnimation();
        back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        cancel_order_btn.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                alertDialog = new AlertDialog.Builder(FindDriverScreen.this)
                        .setTitle("Cancel Job")
                        .setMessage("Are you sure you want to cancel job?")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation
                                CancelOrderApi(order_id);
                                alertDialog.dismiss();
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                alertDialog.dismiss();
                            }
                        })
                        .show();
            }
        });

        fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction().replace(R.id.frame_content,new HomeUIMap()).commit();



    }

    @Override
    public void onBackPressed() {
        try{
            startActivity(new Intent(FindDriverScreen.this,HomeScreen.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finishAffinity();
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    private void CancelOrderApi(String order_number)
    {
        asyncHttpClient.setConnectTimeout(30000);
        Log.e("CANCEL_CURRENT_JOB", Constants.Host_Address + "orders/cancel_current_order/tgs_appkey_amin/" + order_number);
        asyncHttpClient.get(FindDriverScreen.this, Constants.Host_Address + "orders/cancel_current_order/"+UtilsManager.getApiKey(FindDriverScreen.this)+"/" + order_number, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showDialog("Cancelling job");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                try {
                    hideDialog();
                    String response = new String(responseBody);
                    Log.e("success response",response);
                    startActivity(new Intent(FindDriverScreen.this,HomeScreen.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    finish();


                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    hideDialog();
                    String response = new String(responseBody);
                    Log.e("failure response",response);
                    Toast.makeText(FindDriverScreen.this,"Unable to cancel order/Server Error",Toast.LENGTH_LONG).show();
                    //UtilsManager.showAlertMessage(FindingDriver.this,"","Could not cancel order.");
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    private ProgressDialog progressDialog;
    private void showDialog(String message)
    {
        progressDialog = new ProgressDialog(FindDriverScreen.this);
        progressDialog.setMessage(message);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

    }

    private void hideDialog()
    {
        if (progressDialog!=null)
            progressDialog.dismiss();
    }

    CountDownTimer countDownTimer;
    private void startWaitingTimer(){
        countDownTimer = new CountDownTimer(3000 * 60 * 5,5000) {
            @Override
            public void onTick(long l) {
                try {
                    Log.e("TIMER", "onTick: TICK");
                    getMembers();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {
                Log.e("TIMER", "onTick: FINISHED");
            }
        };

        countDownTimer.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (countDownTimer==null){
                startWaitingTimer();
            }else {
                countDownTimer.cancel();
                startWaitingTimer();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (countDownTimer!=null){
                countDownTimer.cancel();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (countDownTimer!=null){
                countDownTimer.cancel();
                countDownTimer = null;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    private void getMembers(){
        asyncHttpClient.setConnectTimeout(30000);
        Log.e("url", Constants.Host_Address + "members/get_members/" + customer_id + "/" + order_id + "/tgs_appkey_amin");


        asyncHttpClient.get(FindDriverScreen.this, Constants.Host_Address + "members/get_members/" + customer_id + "/" + order_id + "/"+UtilsManager.getApiKey(FindDriverScreen.this)+"", new AsyncHttpResponseHandler() {


            @Override
            public void onStart() {
                super.onStart();

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {

                    String response = new String(responseBody);

                    Log.e(TAG, "onSuccess: "+response);

                    try {
                        JSONObject object = new JSONObject(response);
                        JSONObject data = object.getJSONObject("data");
                        String path = data.getString("image_path");
                        JSONArray array = data.getJSONArray("members");


                        if (array.length() > 0) {
                            Toast.makeText(FindDriverScreen.this, "We found a driver for you", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(FindDriverScreen.this, HomeScreen.class)
                                    .putExtra("order_id", order_id)
                                    .putExtra("customer_id", customer_id)

                            );
                            finishAffinity();

                        }


                    } catch (Exception e) {
                        e.printStackTrace();
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

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

    }



}
