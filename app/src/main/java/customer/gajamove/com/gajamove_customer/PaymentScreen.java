package customer.gajamove.com.gajamove_customer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import customer.gajamove.com.gajamove_customer.utils.Constants;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ipay.Ipay;
import com.ipay.IpayPayment;
import com.ipay.IpayResultDelegate;

import org.json.JSONObject;

import java.io.Serializable;

public class PaymentScreen extends BaseActivity  {
    private static final String TAG = "PaymentScreen";

    public static boolean isInProgress = false;
    public static int     r_status;
    public static String  r_transactionId;
    public static String  r_referenceNo;
    public static String  r_amount;
    public static String  r_remarks;
    public static String  r_authCode;
    public static String  r_err;


    private JSONObject iPayArgObj;


    SharedPreferences sharedPreferences;

    private void includeActionBar(){
        ImageView back = findViewById(R.id.backBtn);
        TextView title = findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        title.setText(getResources().getString(R.string.payment_screen));
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    WebView webView;
    String base_payment_url,order_id,customer_id;
    boolean isTopup = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_screen);

        includeActionBar();
        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE);
        customer_id = getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE).getString(Constants.PREFS_CUSTOMER_ID,"20");

        webView = findViewById(R.id.webview);

        isTopup = getIntent().getBooleanExtra("top_up",false);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new CustomWebViewClient());

        base_payment_url = getIntent().getStringExtra("url");
        order_id = getIntent().getStringExtra("order_id");

        webView.addJavascriptInterface(new MyJavaScriptInterface(this),"HtmlViewer");
        webView.loadUrl(base_payment_url);



        /*try {

            String amount = "30.0";//getIntent().getStringExtra("amount");
            String refno = "ref123";//getIntent().getStringExtra("refno");
            String payID = "1";//getIntent().getStringExtra("payID");

            IpayPayment payment = new IpayPayment();
            payment.setMerchantKey(Constants.MERCHANT_KEY);
            payment.setMerchantCode(Constants.MERCHANT_CODE);
            payment.setPaymentId(payID);//there are many payment id i attach image for it
            payment.setCurrency("MYR");
            payment.setRefNo(refno); //pass string value as a reference
            payment.setAmount(amount); //amount in MYR
            payment.setProdDesc("Gaja Move Order Payment");//product description
            payment.setUserName(sharedPreferences.getString(Constants.PREFS_USER_NAME, ""));
            payment.setUserEmail(sharedPreferences.getString(Constants.PREFS_USER_EMAIL, ""));
            payment.setUserContact(sharedPreferences.getString(Constants.PREFS_USER_MOBILE, ""));
            payment.setRemark("Gaja Move Order Payment");
            payment.setCountry("MY");


            //  payment.setBackendPostURL("payment url of backend ex. http://xyz.payment.php");
            Intent checkoutIntent = Ipay.getInstance().checkout(payment, PaymentScreen.this, this);
            startActivityForResult(checkoutIntent, 1);

        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(PaymentScreen.this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
*/



    }


    class MyJavaScriptInterface {

        private Context ctx;

        MyJavaScriptInterface(Context ctx) {
            this.ctx = ctx;
        }

        @android.webkit.JavascriptInterface
        public void showHTML(String html) {
            Log.e(TAG, "showHTML: "+html);
        }

    }

    class CustomWebViewClient extends WebViewClient{

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            try
            {
                Log.e(TAG, "onPageFinished: "+url);

                if (url.contains("Ipay88/failed")){
                    Toast.makeText(PaymentScreen.this,"Payment Failed.",Toast.LENGTH_LONG).show();
                    finish();
                }else if (url.contains("Ipay88/success")){

                    if (isTopup){
                        Toast.makeText(PaymentScreen.this,"Top up successful",Toast.LENGTH_LONG).show();
                        finish();
                    }else {
                        if (order_id.equalsIgnoreCase("")) {
                            finish();
                        } else {
                            Toast.makeText(PaymentScreen.this,"Order Payment Successful",Toast.LENGTH_LONG).show();

                            startActivity(new Intent(PaymentScreen.this, FindDriverScreen.class)
                                    .putExtra("order_id", order_id)
                                    .putExtra("customer_id", customer_id)

                            );
                        }
                    }
                }else {
                    webView.loadUrl("javascript:window.HtmlViewer.showHTML" +
                            "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");

                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }


}
