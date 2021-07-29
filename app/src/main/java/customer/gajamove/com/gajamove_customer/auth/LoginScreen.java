package customer.gajamove.com.gajamove_customer.auth;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import customer.gajamove.com.gajamove_customer.BaseActivity;
import customer.gajamove.com.gajamove_customer.HomeScreen;
import customer.gajamove.com.gajamove_customer.MainActivity;
import customer.gajamove.com.gajamove_customer.R;
import customer.gajamove.com.gajamove_customer.models.ErrorCodes;
import customer.gajamove.com.gajamove_customer.models.MOLoginResponse;

import customer.gajamove.com.gajamove_customer.utils.Constants;
import customer.gajamove.com.gajamove_customer.utils.UtilsManager;
import cz.msebera.android.httpclient.Header;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class LoginScreen extends BaseActivity {
    private static final String TAG = "LoginScreen";

    EditText mUserNameView,mPasswordView;
    TextView forgot_password;
    LinearLayout redirect_signup;
    Button sign_in_btn;
    SharedPreferences sharedPreferences;
    ImageView show_hide_icon_btn,gmail_btn,facebook_btn;
    private boolean isShown = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE);
        if (sharedPreferences.getBoolean(Constants.PREFS_LOGIN_STATE,false)){
            startActivity(new Intent(LoginScreen.this,HomeScreen.class));
            finish();
        }


        Constants.enableSSL(asyncHttpClient);

        show_hide_icon_btn = findViewById(R.id.show_hide_icon_btn);
        show_hide_icon_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isShown){
                    mPasswordView.setTransformationMethod(new PasswordTransformationMethod());
                    isShown = false;
                    show_hide_icon_btn.setImageResource(R.drawable.hide_pass_icon);
                }else {

                    mPasswordView.setTransformationMethod(null);
                    isShown = true;
                    show_hide_icon_btn.setImageResource(R.drawable.show_pass_icon);
                }
            }
        });

        facebook_btn = findViewById(R.id.facebook_btn);
        gmail_btn = findViewById(R.id.gmail_btn);
        mUserNameView = findViewById(R.id.username);
        mPasswordView = findViewById(R.id.password);
        forgot_password = findViewById(R.id.btnForgotPassword);
        redirect_signup = findViewById(R.id.redirect_sign_up);
        sign_in_btn = findViewById(R.id.email_sign_in_button);



        sign_in_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mUserNameView.getText().toString().equalsIgnoreCase("")
                        &&
                        !mPasswordView.getText().toString().equalsIgnoreCase("")
                        ) {
                    loginAPI(mUserNameView.getText().toString(), mPasswordView.getText().toString(), "test");
                }
                else {
                    Toast.makeText(LoginScreen.this,"Enter email and password",Toast.LENGTH_SHORT).show();
                }
            }
        });

        redirect_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginScreen.this,SignUp_Screen.class));
            }
        });

        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginScreen.this,ForgotPassword.class));
            }
        });


        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //Toast.makeText(HelloFormStuff.this, edittext.getText(), Toast.LENGTH_SHORT).show();
                    sign_in_btn.performClick();
                    return true;
                }
                return false;
            }
        });



        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestId()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        gmail_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isAccepted)
                    openDialoge("google");
                else
                    startGoogleLogin();
            }
        });




        //Facebook Login

        facebook_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isAccepted)
                   openDialoge("facebook");
                else
                    startFacebookLogin();
            }
        });


        try {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M)
                requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.RECEIVE_SMS},00);
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }


    private void startGoogleLogin(){
        try {

            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(LoginScreen.this);


            if (account == null) {
                showDialog();
                signIn();
            } else {

                updateUI(account);
            }

        }
        catch (Exception e){
            Toast.makeText(LoginScreen.this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    private void startFacebookLogin(){

        try {
            LoginManager.getInstance().logInWithReadPermissions(LoginScreen.this, Arrays.asList("email", "public_profile"));
            callbackManager = CallbackManager.Factory.create();

            LoginManager.getInstance().registerCallback(callbackManager,
                    new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            // App code
                            Log.e(TAG, "onSuccess: " + loginResult.getAccessToken());

                            GraphRequest.newMeRequest(
                                    loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                                        @Override
                                        public void onCompleted(JSONObject json, GraphResponse response) {
                                            if (response.getError() != null) {
                                                // handle error
                                                Toast.makeText(LoginScreen.this, UtilsManager.makeInitialCapital(response.getError().getErrorMessage()), Toast.LENGTH_LONG).show();
                                            } else {
                                                try {
                                                    if (json.has("email")) {
                                                        // Toast.makeText(LoginScreen.this, json.getString("email"), Toast.LENGTH_LONG).show();
                                                        String email = json.getString("email");
                                                        String name = json.getString("name");

                                                        socialLoginAPI(email, name);

                                                    } else {
                                                        Toast.makeText(LoginScreen.this, "Email not found " + json.toString(), Toast.LENGTH_LONG).show();
                                                        LoginManager.getInstance().logOut();
                                                    }

                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }

                                    }).executeAsync();
                        }

                        @Override
                        public void onCancel() {
                            // App code
                            Log.e(TAG, "CANCELLED");
                        }

                        @Override
                        public void onError(FacebookException exception) {
                            // App code
                            try {
                                if (exception instanceof FacebookAuthorizationException) {
                                    if (AccessToken.getCurrentAccessToken() != null) {
                                        LoginManager.getInstance().logOut();
                                        startFacebookLogin();
                                    }
                                }
                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }
                            Log.e(TAG, "Failed: " + exception.getLocalizedMessage());
                        }
                    });

        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(LoginScreen.this,e.getMessage(),Toast.LENGTH_LONG).show();
        }

    }

    boolean isAccepted = false;
    int index = 0;
    private void openDialoge(String str){

        String title = "Terms & Conditions";
        String path = "https://gajamove.com/terms-conditions-user/";
        String privacy = "http://gajamove.com/privacy-policy/";

        Dialog dialog = new Dialog(LoginScreen.this,R.style.full_screen_dialog);
        dialog.setContentView(R.layout.dialoge_webview);

        TextView titleView = dialog.findViewById(R.id.title);
        Button done = dialog.findViewById(R.id.done_btn);
        WebView webView = dialog.findViewById(R.id.webview);
        CheckBox checkBox = dialog.findViewById(R.id.checkbox);

        checkBox.setText(getResources().getString(R.string.agree)+" "+getResources().getString(R.string.terms_conds));
        done.setBackgroundResource(R.drawable.back_light_btn);



        if (index==0)
            done.setText("Next");


        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    done.setBackgroundResource(R.drawable.next_btn_drawable);
                } else {
                    done.setBackgroundResource(R.drawable.back_light_btn);
                }
            }
        });

        titleView.setText(title);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(path);


        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkBox.isChecked() ) {

                    if (index==0){
                        checkBox.setText(getResources().getString(R.string.agree)+" "+getResources().getString(R.string.privacy));
                        checkBox.setChecked(false);
                        done.setText("Done");
                        index++;
                        webView.loadUrl(privacy);
                        titleView.setText(getResources().getString(R.string.privacy));


                    }else {
                        isAccepted = true;

                        if (str.contains("google"))
                            startGoogleLogin();
                        else
                            startFacebookLogin();


                        dialog.dismiss();
                    }


                }
            }
        });

        dialog.show();


    }


    //FB
    CallbackManager callbackManager;

    //GOOgle
    GoogleSignInClient mGoogleSignInClient;


    public static final  int RC_SIGN_IN = 0011;
    private void signIn() {
        try {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }
        catch (Exception e){
            Toast.makeText(LoginScreen.this,e.getMessage(),Toast.LENGTH_LONG).show();
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == RC_SIGN_IN) {
                if (progressDialog!=null)
                    progressDialog.dismiss();
                // The Task returned from this call is always completed, no need to attach
                // a listener.
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                handleSignInResult(task);
            }else {
                callbackManager.onActivityResult(requestCode, resultCode, data);
            }
        }
        catch (Exception e){
            Toast.makeText(LoginScreen.this,e.getMessage(),Toast.LENGTH_LONG).show();
        }

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(..);

    }


    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            Toast.makeText(LoginScreen.this,e.getMessage(),Toast.LENGTH_LONG).show();
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void updateUI(GoogleSignInAccount account){

        try {
            if (account == null) {
                Toast.makeText(LoginScreen.this, "Sign In Failed", Toast.LENGTH_LONG).show();
                return;
            }


            String name = account.getDisplayName();
            String email = account.getEmail();


            authenticateUser(name, email, true);

        }
        catch (Exception e){
            Toast.makeText(LoginScreen.this,e.getMessage(),Toast.LENGTH_LONG).show();
        }

      //  Toast.makeText(LoginScreen.this,"Name "+name+" email "+email,Toast.LENGTH_LONG).show();



    }

    private void authenticateUser(String name, String email, boolean b) {

        boolean isGoogle = b;
        getDeviceInfo();
        socialLoginAPI(email,name);
    }


    private String token = "token";
    private void getDeviceInfo() {
        try {

            SharedPreferences sharedPreferences = getApplicationContext().
                    getSharedPreferences(getString(R.string.FCM_PREF), MODE_PRIVATE);
            token = sharedPreferences.getString(getString(R.string.FCM_TOKEN), "");
            // token(token);
            Log.e("token", token);

            if (getIntent().getExtras() != null) {
                for (String key : getIntent().getExtras().keySet()) {
                    Object value = getIntent().getExtras().get(key);
                    Log.d("TAG", "Key: " + key + " Value: " + value);
                    //   FcmMessagingService.sendNotification( value.toString());
                }
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.e("Error getting Key",e.getMessage());
        }
    }

    ProgressDialog progressDialog;
    private void showDialog(){
        progressDialog = new ProgressDialog(LoginScreen.this);
        progressDialog.setMessage("Please wait..");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }
    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    private void loginAPI(String email,String pass,String device_id){

        getDeviceInfo();

        RequestParams params = new RequestParams();
        params.put("email",email);
        params.put("password",pass);
        params.put("device_info",token);


        asyncHttpClient.post(Constants.Host_Address + "login",params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showDialog();

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {

                    if (progressDialog!=null)
                        progressDialog.dismiss();

                    String respose =  new String(responseBody);
                    Log.e(TAG, "onSuccess: "+respose);
                    Gson g = new Gson();
                    MOLoginResponse loginResponse = g.fromJson(respose, MOLoginResponse.class);


                    switch (loginResponse.getErrorCode()) {

                        case ErrorCodes.NO_ERROR:

                            SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString(Constants.PREFS_ACCESS_TOKEN, loginResponse.getToken());
                            editor.putString(Constants.PREFS_CUSTOMER_ID, loginResponse.getCustomer_id());
                            editor.putString(Constants.PREFS_USER_NAME, loginResponse.getName());
                            editor.putString(Constants.PREFS_E_RECEIPT, loginResponse.getE_receipt());
                            editor.putString(Constants.PREFS_USER_EMAIL, mUserNameView.getText().toString());

                            editor.putString(Constants.PREFS_USER_IMAGE, loginResponse.getCustomer_full_img());
                            editor.putString(Constants.PREFS_USER_MOBILE, loginResponse.getMobile());
                            editor.putString(Constants.INDUSTRY_NAME, loginResponse.getIndustry_name());
                            editor.putString(Constants.INDUSTRY_ID, loginResponse.getIndustry_id());

                            editor.putString(Constants.PREFS_SOS_STATUS, loginResponse.getSos_enable_disable());

                            String password = mPasswordView.getText().toString();

                            editor.putString(Constants.PREFS_USER_PASSWORD, password);

                          //  loadLocation();

                            editor.putBoolean(Constants.PREFS_LOGIN_STATE, true);
                            editor.apply();

                           /* startActivity(new Intent(LoginScreen.this, HomePage_.class));
                            finish();*/

                         //  Toast.makeText(LoginScreen.this,"Login Successful",Toast.LENGTH_SHORT).show();

                            String check_key=settings.getString(Constants.PREFS_ACCESS_TOKEN, "");
                            Log.e("check_key",check_key);

                            String check_img=settings.getString(Constants.PREFS_USER_IMAGE, "");
                            Log.e("check_img",check_img);

                            startActivity(new Intent(LoginScreen.this,HomeScreen.class));
                            finish();

                            break;

                        case ErrorCodes.ACCOUNT_NOT_VERIFIED:

                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginScreen.this);
                            builder.setMessage(loginResponse.getMessage());
                            // builder.setNegativeButton("Cancel", null);
                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                       /* Intent verificationIntent = new Intent(LoginActivity.this, VerificationActivity_.class);
                        verificationIntent.putExtra("email", mUsernameView.getText().toString());
                        startActivity(verificationIntent);*/
                                    dialogInterface.dismiss();
                                }
                            });
                            builder.show();

                            break;

                        default:

                            UtilsManager.showAlertMessage(LoginScreen.this, "", loginResponse.getMessage());

                            break;
                    }


                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    if (progressDialog!=null)
                        progressDialog.dismiss();

                    String respose =  new String(responseBody);
                    Log.e(TAG, "onFailed: "+respose);

                    Toast.makeText(LoginScreen.this,respose,Toast.LENGTH_LONG).show();



                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

    }

    private void socialLoginAPI(String email,String name){

        getDeviceInfo();

        RequestParams params = new RequestParams();
        params.put("email",email);
        params.put("name",name);
        params.put("device_info",token);


        asyncHttpClient.post(Constants.Host_Address + "Login_with_google",params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showDialog();

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {

                    if (progressDialog!=null)
                        progressDialog.dismiss();

                    String respose =  new String(responseBody);
                    Log.e(TAG, "onSuccess: "+respose);
                    Gson g = new Gson();
                    MOLoginResponse loginResponse = g.fromJson(respose, MOLoginResponse.class);


                    switch (loginResponse.getErrorCode()) {

                        case ErrorCodes.NO_ERROR:

                            SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString(Constants.PREFS_ACCESS_TOKEN, loginResponse.getToken());
                            editor.putString(Constants.PREFS_CUSTOMER_ID, loginResponse.getCustomer_id());
                            editor.putString(Constants.PREFS_USER_NAME, loginResponse.getName());
                            editor.putString(Constants.PREFS_USER_EMAIL, email);

                            editor.putString(Constants.PREFS_USER_IMAGE, loginResponse.getCustomer_full_img());
                            editor.putString(Constants.PREFS_USER_MOBILE, loginResponse.getMobile());
                            editor.putString(Constants.INDUSTRY_ID, loginResponse.getIndustry_id());
                            editor.putString(Constants.INDUSTRY_NAME, loginResponse.getIndustry_name());

                            String password = mPasswordView.getText().toString();

                            editor.putString(Constants.PREFS_USER_PASSWORD, password);
                            editor.putBoolean(Constants.SOCIAL_LOGIN,true);
                            //  loadLocation();

                            editor.putBoolean(Constants.PREFS_LOGIN_STATE, true);
                            editor.apply();

                            startActivity(new Intent(LoginScreen.this, HomeScreen.class));
                            finish();

                            //  Toast.makeText(LoginScreen.this,"Login Successful",Toast.LENGTH_SHORT).show();

                            String check_key=settings.getString(Constants.PREFS_ACCESS_TOKEN, "");
                            Log.e("check_key",check_key);

                            String check_img=settings.getString(Constants.PREFS_USER_IMAGE, "");
                            Log.e("check_img",check_img);


                            Toast.makeText(LoginScreen.this,"Dear User, You are successfully registered",Toast.LENGTH_LONG).show();
                            startActivity(new Intent(LoginScreen.this,HomeScreen.class));
                            finish();

                            break;

                        case ErrorCodes.ACCOUNT_NOT_VERIFIED:

                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginScreen.this);
                            builder.setMessage(loginResponse.getMessage());
                            // builder.setNegativeButton("Cancel", null);
                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                      /*  Intent verificationIntent = new Intent(LoginScreen.this, VerificationActivity_.class);
                        verificationIntent.putExtra("email", mUsernameView.getText().toString());
                        startActivity(verificationIntent);*/
                                    dialogInterface.dismiss();
                                }
                            });
                            builder.show();

                            break;

                        default:

                            UtilsManager.showAlertMessage(LoginScreen.this, "", loginResponse.getMessage());

                            break;
                    }

                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    if (progressDialog!=null)
                        progressDialog.dismiss();

                    String respose =  new String(responseBody);
                    Log.e(TAG, "onFailed: "+respose);

                    Toast.makeText(LoginScreen.this,respose,Toast.LENGTH_LONG).show();



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
       // updateBaseContextLocale(LoginScreen.this);
    }


    /*//Handle Language Change

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(updateBaseContextLocale(base));
    }

    private Context updateBaseContextLocale(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.FCM_PREF),MODE_PRIVATE);
        String language = sharedPreferences.getString(Constants.LANGUAGE_CODE, Locale.getDefault().getLanguage());//.getSavedLanguage(); // Helper method to get saved language from SharedPreferences
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            return updateResourcesLocale(context, locale);
        }

        return updateResourcesLocaleLegacy(context, locale);
    }

    @TargetApi(Build.VERSION_CODES.N_MR1)
    private Context updateResourcesLocale(Context context, Locale locale) {
        Configuration configuration = new Configuration(context.getResources().getConfiguration());
        configuration.setLocale(locale);
        return context.createConfigurationContext(configuration);
    }

    @SuppressWarnings("deprecation")
    private Context updateResourcesLocaleLegacy(Context context, Locale locale) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        return context;
    }

    @Override
    public void applyOverrideConfiguration(Configuration overrideConfiguration) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1) {
            // update overrideConfiguration with your locale
            // setLocale(overrideConfiguration); // you will need to implement this

            createConfigurationContext(overrideConfiguration);
        }
        super.applyOverrideConfiguration(overrideConfiguration);
    }*/

    @Override
    public void onBackPressed() {
        try {
            finishAffinity();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
